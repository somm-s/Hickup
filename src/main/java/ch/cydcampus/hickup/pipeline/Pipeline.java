package ch.cydcampus.hickup.pipeline;

import java.io.FileWriter;
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
import ch.cydcampus.hickup.pipeline.stage.AbstractionStage;
import ch.cydcampus.hickup.pipeline.stage.MultiplexerStage;
import ch.cydcampus.hickup.pipeline.tokenizer.Tokenizer;

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
    private String[] result;
    private FileWriter[] outputFileWriter;

    /**
     * Constructs a new pipeline according to the configuration.
     * @throws PcapNativeException
     * @throws NotOpenException
     * @throws IOException 
     */
    public Pipeline() throws PcapNativeException, NotOpenException, IOException {
        abstractionQueues = new AbstractionQueue[PipelineConfig.NUM_ABSTRACTION_LEVELS];
        // dataSource = new DataBaseSource("localhost", 5432,"ls22", "lab", "lab", "capture");
        dataSource = new FileSource("/home/sosi/ls22/2022/BT03-CHE/abstractions/0", "10.3.8.38"); // 10.3.8.38 // /home/sosi/ls22/2022/BT03-CHE/abstractions/0 // integration_tests
        // dataSource = new NetworkSource("wlp0s20f3", "");
        abstractionQueues[0] = dataSource;
        for(int i = 1; i < PipelineConfig.NUM_ABSTRACTION_LEVELS; i++) {
            abstractionQueues[i] = new HighOrderAbstractionQueue(i, PipelineConfig.TIMEOUTS);
        }
        multiplexerStages = new MultiplexerStage[PipelineConfig.NUM_ABSTRACTION_LEVELS - 1];
        for(int i = 0; i < PipelineConfig.NUM_ABSTRACTION_LEVELS - 1; i++) {
            multiplexerStages[i] = new MultiplexerStage(PipelineConfig.MULTIPLEXER_RULES[i], PipelineConfig.ABSTRACTION_RULES[i]);
        }
        this.finished = false;
        this.logicClock = 0;
        // use buffered writer
        this.outputFileWriter = new FileWriter[PipelineConfig.NUM_ABSTRACTION_LEVELS];
        result = new String[PipelineConfig.NUM_ABSTRACTION_LEVELS];
        for(int i = 0; i < PipelineConfig.NUM_ABSTRACTION_LEVELS; i++) {
            outputFileWriter[i] = new FileWriter("output" + i + ".txt");
            result[i] = "";
        }
    }

    /**
     * Runs the pipeline until the data source is finished and all abstractions are processed.
     */
    public void runPipeline() {
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
            processAbstraction(abstraction, idx);
            if(idx == 0) {
                idx = (idx + 1) % PipelineConfig.NUM_ABSTRACTION_LEVELS;
            }
        }
        try {
            for(int i = 0; i < PipelineConfig.NUM_ABSTRACTION_LEVELS; i++) {
                outputFileWriter[i].write(result[i]);
                outputFileWriter[i].close();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                stage.getActiveAbstraction().setLastUpdateTime(packetAbstraction.getLastUpdateTime());
            }
        }
    }

    private void processAbstraction(Abstraction abstraction, int level) {
        for(FeatureCombinationRule rule : PipelineConfig.FEATURE_COMBINATION_RULES[level]) {
            rule.combine(abstraction);
        }
        for(FilterRule rule : PipelineConfig.FILTER_RULES[level]) {
            if(rule.filter(abstraction)) {
                return;
            }
        }
        if(level <= PipelineConfig.TOKENIZATION_LAYER) {
            outputTokenStream(abstraction);
            if(result[level].length() > 1000) {
                try {
                    outputFileWriter[level].write(result[level]);
                    outputFileWriter[level].flush();
                    result[level] = "";
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(level == PipelineConfig.TOKENIZATION_LAYER)
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

    private String outputTokenStream(Abstraction abstraction) {
        for(Tokenizer tokenizer : PipelineConfig.TOKENIZERS[abstraction.getLevel()]) {
            result[abstraction.getLevel()] += tokenizer.tokenize(abstraction);
        }
        result[abstraction.getLevel()] += " ";
        return result[abstraction.getLevel()];
    }

    public static void main(String[] args) throws PcapNativeException, NotOpenException, IOException {
        Pipeline pipeline = new Pipeline();
        pipeline.runPipeline();
    }
}
