package ch.cydcampus.hickup.pipeline.tokenizer;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

public interface Tokenizer {
    /**
     * Tokenizes the feature at the defined index of the abstraction.
     * @param abstraction
     * @return the character that the feature is mapped to
     */
    public char tokenize(Abstraction abstraction);
}
