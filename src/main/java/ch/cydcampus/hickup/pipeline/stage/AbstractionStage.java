package ch.cydcampus.hickup.pipeline.stage;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.abstraction.AbstractionFactory;

/**
 * A stage that is responsible for creating and managing abstractions.
 */
public class AbstractionStage {
    
    private Abstraction activeAbstraction;
    private Abstraction prevChildAbstraction;
    private AbstractionRule rule;

    /**
     * Creates a new abstraction stage.
     * @param rule the rule that defines when a new abstraction belongs to an active abstraction
     * and when a new active abstraction must be created.
     */
    public AbstractionStage(AbstractionRule rule) {
        this.rule = rule;
    }

    /**
     * Checks if the abstraction belongs to the active abstraction.
     * @param abstraction the abstraction to check
     * @return true if the abstraction belongs to the active abstraction
     */
    public boolean applyRule(Abstraction abstraction) {
        return rule.belongsToActiveAbstraction(abstraction, activeAbstraction, prevChildAbstraction);
    }

    /**
     * Creates a new active abstraction.
     * @param childAbstraction the abstraction that belongs to the active abstraction
     * @return the new active abstraction
     */
    public Abstraction createActiveAbstraction(Abstraction childAbstraction) {
        return AbstractionFactory.getInstance().createHighOrderAbstraction(childAbstraction);
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
}
