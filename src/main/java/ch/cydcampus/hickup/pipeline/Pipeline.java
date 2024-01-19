package ch.cydcampus.hickup.pipeline;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.abstraction.AbstractionDeque;
import ch.cydcampus.hickup.pipeline.abstraction.HighOrderAbstractionDeque;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.FeatureCombinationRule;
import ch.cydcampus.hickup.pipeline.source.DataSource;
import ch.cydcampus.hickup.pipeline.source.FileSource;
import ch.cydcampus.hickup.pipeline.stage.AbstractionStage;
import ch.cydcampus.hickup.pipeline.stage.MultiplexerStage;

public class Pipeline {

    private AbstractionDeque[] abstractionDeques;
    private DataSource dataSource;
    private long logicalTime; // TODO: add suport for real time. Measure the delta time since last packet and add it to logical time when spinning. Do it with slight delay.
    private MultiplexerStage[] multiplexerStages;

    public Pipeline() {
        abstractionDeques = new AbstractionDeque[4];
        // dataSource = new DataBaseSource("localhost", 5432,"ls22", "lab", "lab", "capture");
        dataSource = new FileSource("/home/sosi/ls22/2022/BT03-CHE/abstractions/0", "10.3.8.38", "150");
        abstractionDeques[0] = dataSource;
        for(int i = 1; i < PipelineConfig.NUM_ABSTRACTION_LEVELS; i++) {
            abstractionDeques[i] = new HighOrderAbstractionDeque(i, PipelineConfig.TIMEOUTS[i]);
        }
        multiplexerStages = new MultiplexerStage[PipelineConfig.NUM_ABSTRACTION_LEVELS - 1];
        for(int i = 0; i < PipelineConfig.NUM_ABSTRACTION_LEVELS - 1; i++) {
            multiplexerStages[i] = new MultiplexerStage(i, PipelineConfig.MULTIPLEXER_RULES[i], PipelineConfig.ABSTRACTION_RULES[i]);
        }
    }

    public void run() {

        dataSource.start();

        while(true) { // main loop. TODO: Exit condition.
            for(int i = 0; i < abstractionDeques.length; i++) {
                Abstraction abstraction = abstractionDeques[i].getFirstAbstraction(logicalTime); // logical time no influence on level 0
                if(abstraction == null) {
                    // System.out.println("No abstraction available...");
                    continue;
                }

                if(abstraction.getLevel() == 0) {
                    logicalTime = Math.max(abstraction.getLastUpdateTime(), logicalTime);
                }
                processAbstraction(abstraction, i);
            }
        }
    }

    private void processAbstraction(Abstraction abstraction, int level) {

        for(FeatureCombinationRule rule : PipelineConfig.FEATURE_COMBINATION_RULES[level]) {
            rule.combine(abstraction);
        }
        if(level >= PipelineConfig.NUM_ABSTRACTION_LEVELS - 1) {
            // System.out.println(dataSource.getQueueSize());
            // System.out.println("Finished processing abstraction at level " + level + " " + abstraction);
            return;
        }

        AbstractionStage abstractionStage = multiplexerStages[level].getAbstractionStage(abstraction);
        Abstraction parentAbstraction = abstractionStage.addAbstraction(abstraction);

        assert parentAbstraction != null; // should not happen as new abstraction is created if there is no active abstraction

        if(!parentAbstraction.isSealed()) {
            abstractionDeques[level + 1].addAbstraction(parentAbstraction);
        }
    }
    

    public static void main(String[] args) {
        Pipeline pipeline = new Pipeline();
        pipeline.run();
    }
}
