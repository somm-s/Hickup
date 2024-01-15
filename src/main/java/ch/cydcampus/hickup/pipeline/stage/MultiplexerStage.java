package ch.cydcampus.hickup.pipeline.stage;

import java.util.HashMap;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

/*
 * When an abstraction is passed to be added, it calculates its identifier based on the rule that is passed. 
 * Afterwards, it retreives the child stages. If not present, it creates all child stages for this node. 
 * It then prepends the childStages to the nextStages field of the abstraction. 
 * (Note that before an abstraction is passed to a stage the stage it is passed to is popped from the nextStages list).
 */
public class MultiplexerStage {

    private MultiplexerRule multiplexerRule;
    private AbstractionRule abstractionRule;
    private HashMap<String, AbstractionStage> childStages;
    private int level;

    public MultiplexerStage(int level, MultiplexerRule multiplexerRule, AbstractionRule abstractionRule) {
        this.level = level;
        this.multiplexerRule = multiplexerRule;
        this.abstractionRule = abstractionRule;
        childStages = new HashMap<>();
    }

    public AbstractionStage getAbstractionStage(Abstraction abstraction) {
        
        String identifier = multiplexerRule.getIdentifier(abstraction);
        AbstractionStage abstractionStage = childStages.get(identifier);
        if(abstractionStage == null) {
            abstractionStage = new AbstractionStage(level, abstractionRule);
            childStages.put(identifier, abstractionStage);
        }
        return abstractionStage;
    }
}
