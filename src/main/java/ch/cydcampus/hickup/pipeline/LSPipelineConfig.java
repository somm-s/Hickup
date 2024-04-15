package ch.cydcampus.hickup.pipeline;

import ch.cydcampus.hickup.pipeline.feature.Feature.FeatureType;
import ch.cydcampus.hickup.pipeline.feature.aggregationrules.FeatureAggregationRule;
import ch.cydcampus.hickup.pipeline.feature.aggregationrules.FeatureSumRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.ChildrenCountCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.FeatureCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.FlowIdCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.HostPairIdCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.IntervalLengthCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.PacketTokenIDCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.copyrules.FeatureCopyRule;
import ch.cydcampus.hickup.pipeline.feature.differentialrules.FeatureDifferentialRule;
import ch.cydcampus.hickup.pipeline.filter.FilterRule;
import ch.cydcampus.hickup.pipeline.filter.HighPassSizeFilter;
import ch.cydcampus.hickup.pipeline.stage.AbstractionRule;
import ch.cydcampus.hickup.pipeline.stage.MultiplexerRule;

public class LSPipelineConfig {

    private LSPipelineConfig() {
        throw new IllegalStateException("Utility class");
    }

    // Tokenizer configuration
    public static final boolean IS_LOG_SCALE = false;
    public static final int NUM_BUCKETS = 30;
    public static final int HEART_BEAT_INTERVAL = 100000;
    public static final boolean IS_BIDIRECTIONAL = false;
    public static final int MAX_LOG_VALUE = 11;
    public static final int MAX_LINEAR_VALUE = 1500;
    public static final int MIN_LOG_VALUE = 0;
    public static final int MIN_LINEAR_VALUE = 0;
    public static final int BUCKET_OFFSET = 10;
    public static final int HEART_BEAT_TOKEN_ID = 5;
    public static final int HEART_BEAT_REP_ID = 8;
    public static final int REP_TOKEN_ID = 9;
    public static final int MIN_REPETITION_COUNT = 4;

    // Abstraction configuration
    public static final int NUM_ABSTRACTION_LEVELS = 2;
    public static final int MAX_ABSTRACTION_LEVEL = NUM_ABSTRACTION_LEVELS - 1;
    public static final int MIN_TOKENIZATION_LEVEL = 0;
    public static final int MAX_TOKENIZATION_LEVEL = 1;
    public static final long[] TIMEOUTS = { 0, 0, 15000000 };
    public static final int SRC_IP_INDEX = 0;
    public static final int DST_IP_INDEX = 1;
    public static final int SRC_PORT_INDEX = 2;
    public static final int DST_PORT_INDEX = 3;
    public static final int PROTOCOL_INDEX = 4;
    public static final int BYTES_INDEX = 5;
    public static final int TIME_INDEX = 6;
    public static final int NUM_DEFAULT_FEATURES = 7;
    public static final String[] LEVEL_0_FEATURES = new String[] {"srcIP", "dstIP", "srcPort", "dstPort", "protocol", "bytes", "time", "flowID", "hostPairID", "tokenID"};
    public static final FeatureType[] LEVEL_0_FEATURE_TYPES = new FeatureType[] {FeatureType.IP, FeatureType.IP, FeatureType.INT, FeatureType.INT, FeatureType.PROTOCOL, FeatureType.LONG, FeatureType.LONG, FeatureType.STRING, FeatureType.STRING};
    public static final String[] LEVEL_1_FEATURES = new String[] {"flowID", "hostPairID", "leftTokenID", "rightTokenID", "timeout"};
    public static final FeatureType[] LEVEL_1_FEATURE_TYPES = new FeatureType[] {FeatureType.STRING, FeatureType.STRING, FeatureType.INT, FeatureType.INT, FeatureType.BOOLEAN};
    public static final String[] Level_2_FEATURES = new String[] {"hostPairID"};
    public static final FeatureType[] LEVEL_2_FEATURE_TYPES = new FeatureType[] {FeatureType.STRING};
    public static final String[][] FEATURE_NAMES = { LEVEL_0_FEATURES, LEVEL_1_FEATURES, Level_2_FEATURES};
    public static final FeatureType[][] FEATURE_TYPES = { LEVEL_0_FEATURE_TYPES, LEVEL_1_FEATURE_TYPES, LEVEL_2_FEATURE_TYPES};
    public static final AbstractionRule[] ABSTRACTION_RULES = {
        new AbstractionRule(new int[] { 2, 3, 4}, TIMEOUTS[1]),
        new AbstractionRule(new int[] { }, TIMEOUTS[2])
    };
    public static final MultiplexerRule[] MULTIPLEXER_RULES = {
        new MultiplexerRule(7, 7),
        new MultiplexerRule(0, 7)
    };
    public static final FeatureCombinationRule[] MULTIPLEXER_ID_RULES = {
        new FlowIdCombinationRule().setOutputIndex(7), new HostPairIdCombinationRule().setOutputIndex(8)
    };
    public static final FeatureCombinationRule[][] FEATURE_COMBINATION_RULES = {
        {new PacketTokenIDCombinationRule(NUM_BUCKETS, BUCKET_OFFSET, IS_LOG_SCALE, IS_LOG_SCALE ? MIN_LOG_VALUE : MIN_LINEAR_VALUE, IS_LOG_SCALE ? MAX_LOG_VALUE : MAX_LINEAR_VALUE).setOutputIndex(9)},
        {}
    };
    public static final FeatureAggregationRule[][] FEATURE_AGGREGATION_RULES = {
        {},
        {}
    };
    public static final FeatureCopyRule[][] FEATURE_COPY_RULES = {
        {}, // always empty
        {new FeatureCopyRule().setInputIndex(7).setOutputIndex(0), new FeatureCopyRule().setInputIndex(8).setOutputIndex(1)},
        {new FeatureCopyRule().setInputIndex(1).setOutputIndex(0)}
    };
    public static final FilterRule[][] FILTER_RULES = {
        {new HighPassSizeFilter(100)},
        {}
    };
    public static final FeatureDifferentialRule[][] FEATURE_DIFFERENTIAL_RULES = {
        {}
    };
}
