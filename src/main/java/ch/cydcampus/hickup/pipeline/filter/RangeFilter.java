package ch.cydcampus.hickup.pipeline.filter;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.feature.Feature;

/**
 * Filter abstractions based on a range of values.
 */
public class RangeFilter<T> implements FilterRule {

    private T lowerBound;
    private T upperBound;
    private boolean inclusive;
    private int featureIndex;

    /**
     * Create a new range filter.
     * @param lowerBound the lower bound of the range.
     * @param upperBound the upper bound of the range.
     * @param inclusive true if the bounds are inclusive, false if they are exclusive.
     * @param featureIndex the index of the feature to filter on.
     */
    public RangeFilter(T lowerBound, T upperBound, boolean inclusive, int featureIndex) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.inclusive = inclusive;
        this.featureIndex = featureIndex;
    }

    @Override
    public boolean filter(Abstraction abstraction) {
        Feature feature = abstraction.getFeature(featureIndex);
        switch(feature.getType()) {
            case INT:
                return filterInteger(feature.asInt());
            case DOUBLE:
                return filterDouble(feature.asDouble());
            case LONG:
                return filterLong(feature.asLong());
            default:
                return false;
        }
    }

    private boolean filterInteger(int value) {
        int lower = (Integer) lowerBound;
        int upper = (Integer) upperBound;
        if (inclusive) {
            return value >= lower && value <= upper;
        } else {
            return value > lower && value < upper;
        }
    }

    private boolean filterDouble(double value) {
        double lower = (Double) lowerBound;
        double upper = (Double) upperBound;
        if (inclusive) {
            return value >= lower && value <= upper;
        } else {
            return value > lower && value < upper;
        }
    }

    private boolean filterLong(long value) {
        long lower = (Long) lowerBound;
        long upper = (Long) upperBound;
        if (inclusive) {
            return value >= lower && value <= upper;
        } else {
            return value > lower && value < upper;
        }
    }

}
