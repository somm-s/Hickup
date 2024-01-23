package ch.cydcampus.hickup.pipeline.tokenizer;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

public class NumericTokenizer {

    public static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private double min;
    private double max;
    private boolean useLogScale;
    private int idx;

    public NumericTokenizer(double min, double max, boolean useLogScale, int idx) {
        this.min = min;
        this.max = max;
        this.useLogScale = useLogScale;
        this.idx = idx;
    }

    private char tokenize(double value) {
        if (value < min) {
            return CHARACTERS.charAt(0);
        } else if (value > max) {
            return CHARACTERS.charAt(CHARACTERS.length() - 1);
        } else {
            int numBuckets = CHARACTERS.length();
            value = useLogScale ? Math.log(value) : value;
            double min = useLogScale ? Math.log(this.min) : this.min;
            double max = useLogScale ? Math.log(this.max) : this.max;
            int bucketIndex = (int) ((value - min) * numBuckets / ((max - min)));
            char token = CHARACTERS.charAt(bucketIndex);
            return token;
        }
    }

    public char tokenize(Abstraction abstraction) {
        return tokenize(abstraction.getFeature(idx).asLong());
    }

}
