package ch.cydcampus.hickup.pipeline.tokenizer;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.feature.Feature;

/**
 * Numeric Tokenizer Class used to map a number feature to a character.
 */
public class NumericTokenizer implements Tokenizer {

    public static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private double min;
    private double max;
    private boolean useLogScale;
    private int idx;

    /*
     * Creates a new NumericTokenizer.
     * @param min the minimum value of the feature that is mapped to the first character
     * @param max the maximum value of the feature that is mapped to the last character
     * @param useLogScale whether to use a logarithmic scale (apply logarithm to values before mapping to characters)
     * @param idx the index of the feature in the abstraction
     */
    public NumericTokenizer(double min, double max, boolean useLogScale, int idx) {
        this.min = min;
        this.max = max;
        this.useLogScale = useLogScale;
        this.idx = idx;
    }

    @Override
    public char tokenize(Abstraction abstraction) {
        Feature feature = abstraction.getFeature(idx);
        switch(feature.getType()) {
            case INT:
                return tokenize(abstraction.getFeature(idx).asInt());
            case DOUBLE:
                return tokenize(abstraction.getFeature(idx).asDouble());
            case LONG:
                return tokenize(abstraction.getFeature(idx).asLong());
            case SHORT:
                return tokenize(abstraction.getFeature(idx).asShort());
            default:
                throw new IllegalArgumentException("Feature type not supported");
        }
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
}
