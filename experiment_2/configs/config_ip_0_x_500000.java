package ch.cydcampus.hickup.pipeline;

import ch.cydcampus.hickup.pipeline.feature.Feature.FeatureType;
import ch.cydcampus.hickup.pipeline.feature.aggregationrules.FeatureAggregationRule;
import ch.cydcampus.hickup.pipeline.feature.aggregationrules.FeatureSumRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.ChildrenCountCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.FeatureCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.FlowIdCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.HostPairIdCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.IntervalLengthCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.copyrules.FeatureCopyRule;
import ch.cydcampus.hickup.pipeline.feature.differentialrules.FeatureDifferentialRule;
import ch.cydcampus.hickup.pipeline.filter.FilterRule;
import ch.cydcampus.hickup.pipeline.filter.HighPassSizeFilter;
import ch.cydcampus.hickup.pipeline.stage.AbstractionRule;
import ch.cydcampus.hickup.pipeline.stage.MultiplexerRule;

public class PipelineConfig {

    private PipelineConfig() {
        throw new IllegalStateException("Utility class");
    }

    public static final int NUM_ABSTRACTION_LEVELS = 2;
    public static final int MAX_ABSTRACTION_LEVEL = NUM_ABSTRACTION_LEVELS - 1;
    public static final int MIN_TOKENIZATION_LEVEL = 0;
    public static final int MAX_TOKENIZATION_LEVEL = 1;
    public static final long[] TIMEOUTS = { 0, 500000 };
    public static final int SRC_IP_INDEX = 0;
    public static final int DST_IP_INDEX = 1;
    public static final int SRC_PORT_INDEX = 2;
    public static final int DST_PORT_INDEX = 3;
    public static final int PROTOCOL_INDEX = 4;
    public static final int BYTES_INDEX = 5;
    public static final int TIME_INDEX = 6;
    public static final int NUM_DEFAULT_FEATURES = 7;
    public static final String[] LEVEL_0_FEATURES = new String[] {"srcIP", "dstIP", "srcPort", "dstPort", "protocol", "bytes", "time", "flowID", "hostPairID"};
    public static final FeatureType[] LEVEL_0_FEATURE_TYPES = new FeatureType[] {FeatureType.IP, FeatureType.IP, FeatureType.INT, FeatureType.INT, FeatureType.PROTOCOL, FeatureType.LONG, FeatureType.LONG, FeatureType.STRING, FeatureType.STRING};
    public static final String[] LEVEL_1_FEATURES = new String[] {"hostPairID"};
    public static final FeatureType[] LEVEL_1_FEATURE_TYPES = new FeatureType[] {FeatureType.STRING};
    public static final String[][] FEATURE_NAMES = { LEVEL_0_FEATURES, LEVEL_1_FEATURES };
    public static final FeatureType[][] FEATURE_TYPES = { LEVEL_0_FEATURE_TYPES, LEVEL_1_FEATURE_TYPES };
    public static final AbstractionRule[] ABSTRACTION_RULES = {
        new AbstractionRule(new int[] { }, TIMEOUTS[1]),
    };
    public static final MultiplexerRule[] MULTIPLEXER_RULES = {
        new MultiplexerRule(8, 8),
    };
    public static final FeatureCombinationRule[] MULTIPLEXER_ID_RULES = {
        new FlowIdCombinationRule().setOutputIndex(7), new HostPairIdCombinationRule().setOutputIndex(8)
    };
    public static final FeatureCombinationRule[][] FEATURE_COMBINATION_RULES = {
        {},
        {}
    };
    public static final FeatureAggregationRule[][] FEATURE_AGGREGATION_RULES = {
        {},
        {}
    };
    public static final FeatureCopyRule[][] FEATURE_COPY_RULES = {
        {}, // always empty
        {new FeatureCopyRule().setInputIndex(8).setOutputIndex(0)}
    };
    public static final FilterRule[][] FILTER_RULES = {
        {},
        {}
    };
    public static final FeatureDifferentialRule[][] FEATURE_DIFFERENTIAL_RULES = {
        {}
    };
}
