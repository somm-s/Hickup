package ch.cydcampus.hickup.pipeline.stage;

import java.util.concurrent.ConcurrentLinkedQueue;

import ch.cydcampus.hickup.pipeline.PipelineConfig;
import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.abstraction.AbstractionFactory;
import ch.cydcampus.hickup.pipeline.feature.aggregationrules.FeatureAggregationRule;
import ch.cydcampus.hickup.pipeline.feature.differentialrules.FeatureDifferentialRule;

public class AbstractionStage {
    
    private ConcurrentLinkedQueue<Abstraction> sealedChildren;
    private Abstraction activeAbstraction; // abstraction where abstractions are being added to
    private Abstraction prevChildAbstraction; // abstraction that was added last to the stage
    private AbstractionRule rule;
    private int level;

    public AbstractionStage(int level, AbstractionRule rule) {
        this.rule = rule;
        this.level = level;
        sealedChildren = new ConcurrentLinkedQueue<>();
    }

    /*
     * Add an abstraction of same level to the stage. Return abstraction of level + 1.
     */
    public Abstraction addAbstraction(Abstraction newAbstraction) {
        if(activeAbstraction == null) { // first abstraction
            createActiveAbstraction(newAbstraction);
            prevChildAbstraction = newAbstraction;
            activeAbstraction.addChild(newAbstraction);
            return activeAbstraction;
        }

        for(FeatureDifferentialRule rule : PipelineConfig.FEATURE_DIFFERENTIAL_RULES[level]) {
            rule.differential(prevChildAbstraction, newAbstraction);
        }

        if(rule.belongsToActiveAbstraction(newAbstraction, activeAbstraction, prevChildAbstraction)) {
            prevChildAbstraction = newAbstraction;
            activeAbstraction.addChild(newAbstraction);

            for(FeatureAggregationRule rule : PipelineConfig.FEATURE_AGGREGATION_RULES[level]) {
                rule.aggregate(activeAbstraction, newAbstraction);
            }

            return activeAbstraction;
        } else {
            activeAbstraction.seal();
            Abstraction sealedAbstraction = activeAbstraction;
            sealedChildren.add(activeAbstraction);
            createActiveAbstraction(newAbstraction);
            prevChildAbstraction = newAbstraction;
            activeAbstraction.addChild(newAbstraction);
            return sealedAbstraction;
        }
    }

    private void createActiveAbstraction(Abstraction childAbstraction) {
        this.activeAbstraction = AbstractionFactory.getInstance().createHighOrderAbstraction(level + 1, childAbstraction); // TODO: check if level or level + 1
    }

}
