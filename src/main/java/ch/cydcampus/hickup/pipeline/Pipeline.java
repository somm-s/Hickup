package ch.cydcampus.hickup.pipeline;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.abstraction.AbstractionDeque;
import ch.cydcampus.hickup.pipeline.abstraction.HighOrderAbstractionDeque;
import ch.cydcampus.hickup.pipeline.source.DataBaseSource;
import ch.cydcampus.hickup.pipeline.source.DataSource;
import ch.cydcampus.hickup.pipeline.stage.AbstractionRule;
import ch.cydcampus.hickup.pipeline.stage.MultiplexerRule;
import ch.cydcampus.hickup.pipeline.stage.MultiplexerStage;

public class Pipeline {

    private AbstractionDeque[] abstractionDeques;
    private DataSource dataSource;
    private long logicalTime; // TODO: add suport for real time
    private MultiplexerStage[] multiplexerStages;

    public Pipeline() {
        abstractionDeques = new AbstractionDeque[4];
        dataSource = new DataBaseSource("localhost", 5432,"ls22", "lab", "lab", "capture");
        abstractionDeques[0] = dataSource;
        for(int i = 1; i < PipelineConfig.NUM_ABSTRACTION_LEVELS; i++) {
            abstractionDeques[i] = new HighOrderAbstractionDeque(i, PipelineConfig.TIMEOUTS[i]);
        }
        multiplexerStages = new MultiplexerStage[PipelineConfig.NUM_ABSTRACTION_LEVELS - 1];
        for(int i = 0; i < PipelineConfig.NUM_ABSTRACTION_LEVELS - 1; i++) {
            AbstractionRule abstractionRule = new AbstractionRule(PipelineConfig.ABSRACTION_RULE_SAME_FEATURES[i], PipelineConfig.TIMEOUTS[i + 1]);
            MultiplexerRule multiplexerRule = new MultiplexerRule(i, PipelineConfig.MULTIPLEXER_FEATURES[i]);
            multiplexerStages[i] = new MultiplexerStage(i, multiplexerRule, abstractionRule);
        }
    }

    public void run() {

        dataSource.start();

        while(true) { // main loop. TODO: Exit condition.
            for(int i = 0; i < abstractionDeques.length; i++) {
                Abstraction abstraction = abstractionDeques[i].getFirstAbstraction(logicalTime); // logical time no influence on level 0
                if(abstraction == null) {
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

        // calculate combination features

        // extract attribute for 

    }
    

    public static void main(String[] args) {
        Pipeline pipeline = new Pipeline();
        pipeline.run();
    }
}
