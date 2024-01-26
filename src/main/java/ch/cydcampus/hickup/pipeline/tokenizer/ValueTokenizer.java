package ch.cydcampus.hickup.pipeline.tokenizer;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

public class ValueTokenizer implements Tokenizer {

    int idx;

    public ValueTokenizer(int idx) {
        this.idx = idx;
    }

    @Override
    public String tokenize(Abstraction abstraction) {
        return abstraction.getFeature(idx).toString();
    }
    
}
