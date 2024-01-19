package ch.cydcampus.hickup.pipeline;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.abstraction.AbstractionDeque;
import ch.cydcampus.hickup.pipeline.abstraction.HighOrderAbstractionQueue;
import ch.cydcampus.hickup.pipeline.feature.aggregationrules.FeatureAggregationRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.FeatureCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.differentialrules.FeatureDifferentialRule;
import ch.cydcampus.hickup.pipeline.source.DataSource;
import ch.cydcampus.hickup.pipeline.source.FileSource;
import ch.cydcampus.hickup.pipeline.stage.AbstractionStage;
import ch.cydcampus.hickup.pipeline.stage.MultiplexerStage;

public class Pipeline {

    private AbstractionDeque[] abstractionQueues;
    private DataSource dataSource;
    private long logicClock;
    private MultiplexerStage[] multiplexerStages;
    private Abstraction[] nextElements;
    private int nextIndex;

    public Pipeline() {
        abstractionQueues = new AbstractionDeque[4];
        // dataSource = new DataBaseSource("localhost", 5432,"ls22", "lab", "lab", "capture");
        dataSource = new FileSource("/home/sosi/ls22/2022/BT03-CHE/abstractions/0", "10.3.8.38", "150");
        abstractionQueues[0] = dataSource;
        for(int i = 1; i < PipelineConfig.NUM_ABSTRACTION_LEVELS; i++) {
            abstractionQueues[i] = new HighOrderAbstractionQueue(PipelineConfig.TIMEOUTS[i]);
        }
        multiplexerStages = new MultiplexerStage[PipelineConfig.NUM_ABSTRACTION_LEVELS - 1];
        for(int i = 0; i < PipelineConfig.NUM_ABSTRACTION_LEVELS - 1; i++) {
            multiplexerStages[i] = new MultiplexerStage(i, PipelineConfig.MULTIPLEXER_RULES[i], PipelineConfig.ABSTRACTION_RULES[i]);
        }

        this.logicClock = 0;
        this.nextElements = new Abstraction[PipelineConfig.NUM_ABSTRACTION_LEVELS];
    }

    public void run() {

        dataSource.start();

        while(true) { // main loop. TODO: Exit condition.
            getNextIndex();

            if(nextIndex < 0) {
                try {
                    Thread.sleep(5);
                } catch(InterruptedException e) {}
                continue;
            }

            Abstraction abstraction = nextElements[nextIndex];
            nextElements[nextIndex] = null;

            if(nextIndex == 0) { // packet abstraction
                updateAbstractions(abstraction);
            } 

            processAbstraction(abstraction, nextIndex);

        }
    }

    private void getNextIndex() { // TODO check if this is correct
        long minTime = Long.MAX_VALUE;
        int minIndex = -1;
        long delta = 0;
        if(dataSource.getQueueSize() == 0) {
            long currentTime = System.currentTimeMillis() * 1000;
            delta = currentTime - logicClock;
        }

        for(int i = 0; i < nextElements.length; i++) {
            nextElements[i] = abstractionQueues[i].getFirstAbstraction(logicClock);
            if(nextElements[i] == null) {
                continue;
            }
            long temp = nextElements[i].getLastUpdateTime() + PipelineConfig.TIMEOUTS[i];
            if(temp < minTime) {
                minTime = temp;
                minIndex = i;
            }
        }

        if(minIndex == 0) {
            logicClock = minTime;
        } else if(minTime > logicClock + delta) {
            minIndex = -1;
        }
        this.nextIndex = minIndex;
    }

    private void updateAbstractions(Abstraction packetAbstraction) {

        for(FeatureCombinationRule rule : PipelineConfig.MULTIPLEXER_ID_RULES) {
            rule.combine(packetAbstraction);
        }

        for(int i = 0; i < multiplexerStages.length; i++) {
            AbstractionStage stage = multiplexerStages[i].getAbstractionStage(packetAbstraction);
            Abstraction activeAbstraction = stage.getActiveAbstraction();
            if(activeAbstraction != null) {
                stage.getActiveAbstraction().setLastUpdateTime(packetAbstraction.getLastUpdateTime());
            }
        }

    }

    private void processAbstraction(Abstraction abstraction, int level) {

        for(FeatureCombinationRule rule : PipelineConfig.FEATURE_COMBINATION_RULES[level]) {
            rule.combine(abstraction);
        }

        if(level >= PipelineConfig.NUM_ABSTRACTION_LEVELS - 1) {
            // System.out.println("Finished processing abstraction at level " + level + " " + abstraction);
            // TODO send to output
            return;
        }

        AbstractionStage abstractionStage = multiplexerStages[level].getAbstractionStage(abstraction);
        Abstraction activeAbstraction = abstractionStage.getActiveAbstraction();
        Abstraction prevAbstraction = abstractionStage.getPrevChildAbstraction();

        for(FeatureDifferentialRule rule : PipelineConfig.FEATURE_DIFFERENTIAL_RULES[level]) {
            rule.differential(prevAbstraction, abstraction);
        }

        if(activeAbstraction == null || !abstractionStage.applyRule(abstraction)) {
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
