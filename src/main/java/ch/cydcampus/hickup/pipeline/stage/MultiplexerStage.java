package ch.cydcampus.hickup.pipeline.stage;

import java.util.HashMap;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

/**
 * A stage that is responsible for creating and managing abstraction stages.
 * When an abstraction is passed to retreive the abstraction stage, it calculates its identifier based on the rule that is passed. 
 * Afterwards, it retreives the corresponding abstraction stage. If not present, it creates a new abstractoin stage for this node. 
 */
public class MultiplexerStage {

    private MultiplexerRule multiplexerRule;
    private AbstractionRule abstractionRule;
    private HashMap<String, AbstractionStage> childStages;

    /**
     * Creates a new multiplexer stage.
     * @param multiplexerRule the rule that defines how to calculate the identifier of an abstraction
     * @param abstractionRule the rule that defines when a new abstraction belongs to an active abstraction
     * and when a new active abstraction must be created.
     */
    public MultiplexerStage(MultiplexerRule multiplexerRule, AbstractionRule abstractionRule) {
        this.multiplexerRule = multiplexerRule;
        this.abstractionRule = abstractionRule;
        childStages = new HashMap<>();
    }

    /**
     * Retreives the abstraction stage for the abstraction.
     * @param abstraction the abstraction
     * @return the abstraction stage
     */
    public AbstractionStage getAbstractionStage(Abstraction abstraction) {
        String identifier = multiplexerRule.getIdentifier(abstraction);
        AbstractionStage abstractionStage = childStages.get(identifier);
        if(abstractionStage == null) {
            abstractionStage = new AbstractionStage(abstractionRule);
            childStages.put(identifier, abstractionStage);
        }
        return abstractionStage;
    }
}
