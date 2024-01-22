package ch.cydcampus.hickup.pipeline;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.abstraction.AbstractionQueue;
import ch.cydcampus.hickup.pipeline.abstraction.HighOrderAbstractionQueue;
import ch.cydcampus.hickup.pipeline.abstraction.PacketAbstraction;
import ch.cydcampus.hickup.pipeline.feature.aggregationrules.FeatureAggregationRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.FeatureCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.differentialrules.FeatureDifferentialRule;
import ch.cydcampus.hickup.pipeline.source.DataSource;
import ch.cydcampus.hickup.pipeline.source.FileSource;
import ch.cydcampus.hickup.pipeline.stage.AbstractionStage;
import ch.cydcampus.hickup.pipeline.stage.MultiplexerStage;

public class Pipeline {

    private AbstractionQueue[] abstractionQueues;
    private DataSource dataSource;
    private long logicClock;
    private MultiplexerStage[] multiplexerStages;
    private boolean finished;

    public Pipeline() {
        abstractionQueues = new AbstractionQueue[4];
        // dataSource = new DataBaseSource("localhost", 5432,"ls22", "lab", "lab", "capture");
        dataSource = new FileSource("/home/sosi/ls22/2022/BT03-CHE/abstractions/0", "", "150"); // 10.3.8.38 // /home/sosi/ls22/2022/BT03-CHE/abstractions/0 // integration_tests
        abstractionQueues[0] = dataSource;
        for(int i = 1; i < PipelineConfig.NUM_ABSTRACTION_LEVELS; i++) {
            abstractionQueues[i] = new HighOrderAbstractionQueue(i, PipelineConfig.TIMEOUTS);
        }
        multiplexerStages = new MultiplexerStage[PipelineConfig.NUM_ABSTRACTION_LEVELS - 1];
        for(int i = 0; i < PipelineConfig.NUM_ABSTRACTION_LEVELS - 1; i++) {
            multiplexerStages[i] = new MultiplexerStage(i, PipelineConfig.MULTIPLEXER_RULES[i], PipelineConfig.ABSTRACTION_RULES[i]);
        }
        this.finished = false;
        this.logicClock = 0;
    }

    public void run() {

        dataSource.start();
        int idx = 0;
        while(!finished || idx != 0) { // main loop. TODO: Exit condition.
            Abstraction abstraction = abstractionQueues[idx].getFirstAbstraction(logicClock);
            if(abstraction == null) {
                idx = (idx + 1) % PipelineConfig.NUM_ABSTRACTION_LEVELS;
                continue;
            } else if(idx == 0) {
                if(abstraction == PacketAbstraction.FINISH_PACKET) {
                    System.out.println("Finish abstraction received");
                    finished = true;
                    idx = idx + 1;
                    logicClock = Long.MAX_VALUE;
                    continue;
                } else {
                    logicClock = abstraction.getLastUpdateTime();
                    updateAbstractions(abstraction);    
                }
            }
            abstraction.seal(); // Cannot add children or update time anymore
            processAbstraction(abstraction, idx);
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

        if(level >= PipelineConfig.NUM_ABSTRACTION_LEVELS - 1) {
            System.out.println(abstraction.getFeature(0) + " " + abstraction.getFirstUpdateTime() + " " + abstraction.getLastUpdateTime() + "(");
            long sum = 0;
            long deepSum = 0;
            for(Abstraction abs : abstraction.getChildren()) {
                sum += abs.getFeature(1).asLong();
                for(Abstraction ab : abs.getChildren()) {
                    deepSum += ab.getFeature(PipelineConfig.BYTES_INDEX).asLong();
                }
            }
            System.out.println(sum + " " + deepSum + ")");
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

    public static void main(String[] args) {
        Pipeline pipeline = new Pipeline();
        pipeline.run();
    }
}
