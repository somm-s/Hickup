package ch.cydcampus.hickup.pipeline;

import java.io.IOException;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.abstraction.AbstractionQueue;
import ch.cydcampus.hickup.pipeline.abstraction.HighOrderAbstractionQueue;
import ch.cydcampus.hickup.pipeline.abstraction.PacketAbstraction;
import ch.cydcampus.hickup.pipeline.feature.aggregationrules.FeatureAggregationRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.FeatureCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.differentialrules.FeatureDifferentialRule;
import ch.cydcampus.hickup.pipeline.filter.FilterRule;
import ch.cydcampus.hickup.pipeline.source.DataSource;
import ch.cydcampus.hickup.pipeline.source.FileSource;
import ch.cydcampus.hickup.pipeline.source.NetworkSource;
import ch.cydcampus.hickup.pipeline.stage.AbstractionStage;
import ch.cydcampus.hickup.pipeline.stage.MultiplexerStage;
import ch.cydcampus.hickup.pipeline.tokenizer.PacketTokenizer;
import ch.cydcampus.hickup.util.AbstractionCsvWriter;
import ch.cydcampus.hickup.util.BurstStreamWriter;

/**
 * The Pipeline class is responsible to construct a pipeline according to the configuration and run it.
 * The pipeline consists of a data source, abstraction queues, multiplexer stages and feature rules.
 */
public class Pipeline {

    private AbstractionQueue[] abstractionQueues;
    private DataSource dataSource;
    private long logicClock;
    private MultiplexerStage[] multiplexerStages;
    private boolean finished;
    private PacketTokenizer packetTokenizer;

    private Pipeline(String outputFilePath) throws IOException {
        abstractionQueues = new AbstractionQueue[PipelineConfig.NUM_ABSTRACTION_LEVELS];
        for(int i = 1; i < PipelineConfig.NUM_ABSTRACTION_LEVELS; i++) {
            abstractionQueues[i] = new HighOrderAbstractionQueue(i, PipelineConfig.TIMEOUTS);
        }
        multiplexerStages = new MultiplexerStage[PipelineConfig.NUM_ABSTRACTION_LEVELS - 1];
        for(int i = 0; i < PipelineConfig.NUM_ABSTRACTION_LEVELS - 1; i++) {
            multiplexerStages[i] = new MultiplexerStage(PipelineConfig.MULTIPLEXER_RULES[i], PipelineConfig.ABSTRACTION_RULES[i]);
        }
        this.finished = false;
        this.logicClock = 0;
        this.packetTokenizer = new PacketTokenizer(outputFilePath);
    }

    /** Constructs a new pipeline from a network interface
     * @param interfaceName The name of the network interface
     * @param outputPath The path to the output file
     */
    public Pipeline(String interfaceName, String outputPath) throws PcapNativeException, NotOpenException, IOException {
        this(outputPath);
        dataSource = new NetworkSource(interfaceName, "");
        abstractionQueues[0] = dataSource;
    }

    /**
     * Constructs a new pipeline according to the configuration.
     * @throws PcapNativeException
     * @throws NotOpenException
     * @throws IOException 
     */
    public Pipeline(String inputPath, String filterHost, String outputPath) throws PcapNativeException, NotOpenException, IOException {
        this(outputPath);
        dataSource = new FileSource(inputPath, filterHost);
        abstractionQueues[0] = dataSource;
    }

    /**
     * Runs the pipeline until the data source is finished and all abstractions are processed.
     * @throws IOException 
     */
    public void runPipeline() throws IOException {
        dataSource.start();
        int idx = 0;
        while(!finished || idx != 0) {
            Abstraction abstraction = abstractionQueues[idx].getFirstAbstraction(logicClock);
            if(abstraction == null) {
                idx = (idx + 1) % PipelineConfig.NUM_ABSTRACTION_LEVELS;
                continue;
            } else if(idx == 0) {
                if(abstraction == PacketAbstraction.FINISH_PACKET) {
                    idx = (idx + 1) % PipelineConfig.NUM_ABSTRACTION_LEVELS;
                    finished = true;
                    logicClock = Long.MAX_VALUE;
                    continue;
                } else {
                    logicClock = abstraction.getLastUpdateTime();
                    updateAbstractions(abstraction);    
                }
            }
            abstraction.seal();

            // remove this abstraction from active abstractions
            if(abstraction.getLevel() > 0) {
                multiplexerStages[abstraction.getLevel() - 1].removeAbstractionStage(abstraction); // TODO: maybe faulty
            }
            
            processAbstraction(abstraction, idx);
            if(idx == 0) {
                idx = (idx + 1) % PipelineConfig.NUM_ABSTRACTION_LEVELS;
            }
        }
    } 

    private void updateAbstractions(Abstraction packetAbstraction) {
        for(FeatureCombinationRule rule : PipelineConfig.MULTIPLEXER_ID_RULES) {
            rule.combine(packetAbstraction);
        }
        for(int i = 1; i < multiplexerStages.length; i++) { // Note: can skip level 0 since packets are directly added to it
            AbstractionStage stage = multiplexerStages[i].getAbstractionStage(packetAbstraction);
            Abstraction activeAbstraction = stage.getActiveAbstraction();
            if(activeAbstraction != null && !activeAbstraction.isSealed()) {
                stage.getActiveAbstraction().setRefreshTime(packetAbstraction.getLastUpdateTime());
            }
        }
    }

    private void processAbstraction(Abstraction abstraction, int level) throws IOException {
        for(FeatureCombinationRule rule : PipelineConfig.FEATURE_COMBINATION_RULES[level]) {
            rule.combine(abstraction);
        }
        for(FilterRule rule : PipelineConfig.FILTER_RULES[level]) {
            if(rule.filter(abstraction)) {
                return;
            }
        }
        if(level == PipelineConfig.MAX_TOKENIZATION_LEVEL) {
            // abstractionWriter.writeAbstraction(abstraction, -1); // use this for CsvAbstractioWriter
            packetTokenizer.tokenize(abstraction);
            return;
        } else if(level >= PipelineConfig.MAX_TOKENIZATION_LEVEL) {
            return;
        }
        AbstractionStage abstractionStage = multiplexerStages[level].getAbstractionStage(abstraction);
        Abstraction activeAbstraction = abstractionStage.getActiveAbstraction();
        Abstraction prevAbstraction = abstractionStage.getPrevChildAbstraction();
        for(FeatureDifferentialRule rule : PipelineConfig.FEATURE_DIFFERENTIAL_RULES[level]) {
            rule.differential(prevAbstraction, abstraction);
        }
        if(activeAbstraction == null || !abstractionStage.applyRule(abstraction) || activeAbstraction.isSealed()) {
            activeAbstraction = abstractionStage.createActiveAbstraction(abstraction);
            abstractionQueues[level + 1].addAbstraction(activeAbstraction);
        }
        for(FeatureAggregationRule rule : PipelineConfig.FEATURE_AGGREGATION_RULES[level]) {
            rule.aggregate(activeAbstraction, abstraction);
        }
        activeAbstraction.addChild(abstraction);
        abstractionStage.setPrevChildAbstraction(abstraction);
        abstractionStage.setActiveAbstraction(activeAbstraction);
    }

    public static void main(String[] args) throws PcapNativeException, NotOpenException, IOException {
        if(args.length < 2) {
            System.out.println("Usage: java -jar pipeline.jar <interface> <outputPath>");
            return;
        }

        if(args.length == 3) {
            String inputPath = args[0];
            String filterHost = args[1];
            String outputPath = args[2];
            Pipeline pipeline = new Pipeline(inputPath, filterHost, outputPath);
            pipeline.runPipeline();
            return;
        }

        String interfaceName = args[0];
        String outputPath = args[1];
        Pipeline pipeline = new Pipeline(interfaceName, outputPath);
        pipeline.runPipeline();
    }
}
