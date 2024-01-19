package ch.cydcampus.hickup.pipeline.stage;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.abstraction.AbstractionFactory;

public class AbstractionStage {
    
    private Abstraction activeAbstraction; // abstraction where abstractions are being added to
    private Abstraction prevChildAbstraction; // abstraction that was added last to the stage
    private AbstractionRule rule;
    private int level;

    public AbstractionStage(int level, AbstractionRule rule) {
        this.rule = rule;
        this.level = level;
    }

    public Abstraction getActiveAbstraction() {
        return activeAbstraction;
    }

    public Abstraction getPrevChildAbstraction() {
        return prevChildAbstraction;
    }

    public void setActiveAbstraction(Abstraction abstraction) {
        this.activeAbstraction = abstraction;
    }

    public void setPrevChildAbstraction(Abstraction abstraction) {
        this.prevChildAbstraction = abstraction;
    }

    public boolean applyRule(Abstraction abstraction) {
        return rule.belongsToActiveAbstraction(prevChildAbstraction, activeAbstraction, abstraction);
    }

    public Abstraction createActiveAbstraction(Abstraction childAbstraction) {
        return AbstractionFactory.getInstance().createHighOrderAbstraction(level + 1, childAbstraction); // TODO: check if level or level + 1
    }

}
