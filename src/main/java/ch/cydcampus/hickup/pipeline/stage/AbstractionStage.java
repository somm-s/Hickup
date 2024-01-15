package ch.cydcampus.hickup.pipeline.stage;

import java.util.concurrent.ConcurrentLinkedQueue;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.abstraction.AbstractionFactory;

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
     * 
     */
    public Abstraction addAbstraction(Abstraction newAbstraction) {
        if(activeAbstraction == null) { // first abstraction
            createActiveAbstraction(newAbstraction);
            prevChildAbstraction = newAbstraction;
            activeAbstraction.addChild(newAbstraction);
            return null;
        }

        // TODO: update differential features (if prevChildAbstraction != null)

        if(rule.belongsToActiveAbstraction(newAbstraction, activeAbstraction, prevChildAbstraction)) {
            prevChildAbstraction = newAbstraction;
            activeAbstraction.addChild(newAbstraction);

            // TODO: update streaming features of activeAbstraction

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
