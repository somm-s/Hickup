package ch.cydcampus.hickup.pipeline;

import ch.cydcampus.hickup.pipeline.feature.Feature.FeatureType;
import ch.cydcampus.hickup.pipeline.feature.aggregationrules.FeatureAggregationRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.FeatureCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.FlowIdCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.HostPairIdCombinationRule;
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

    /**
     * Change number of abstraction levels here.
     */
    public static final int NUM_ABSTRACTION_LEVELS = 2;
    public static final int MAX_ABSTRACTION_LEVEL = NUM_ABSTRACTION_LEVELS - 1;

    /**
     * The maximum tokenization level that is considered for tokenization. There can be more levels in the definition.
     * This facilitates experimentation with different levels of hierarchy in a specific network language definition.
     */
    public static final int MAX_TOKENIZATION_LEVEL = 1;

    /**
     * Timeouts of the abstraction rules in macro-seconds. Level 0 corresponds to the packet level timeout which is disregarded.
     */
    public static final long[] TIMEOUTS = { 0, 15000000 };

    /**
     * Feature array indices of the default packet level features.
     */
    public static final int SRC_IP_INDEX = 0;
    public static final int DST_IP_INDEX = 1;
    public static final int SRC_PORT_INDEX = 2;
    public static final int DST_PORT_INDEX = 3;
    public static final int PROTOCOL_INDEX = 4;
    public static final int BYTES_INDEX = 5;
    public static final int TIME_INDEX = 6;
    public static final int NUM_DEFAULT_FEATURES = 7;

    /**
     * Packet level Feature names and types.
     */
    public static final String[] LEVEL_0_FEATURES = new String[] {"srcIP", "dstIP", "srcPort", "dstPort", "protocol", "bytes", "time", "flowID", "hostPairID", "internalFeature"};
    public static final FeatureType[] LEVEL_0_FEATURE_TYPES = new FeatureType[] {FeatureType.IP, FeatureType.IP, FeatureType.INT, FeatureType.INT, FeatureType.PROTOCOL, FeatureType.LONG, FeatureType.LONG, FeatureType.STRING, FeatureType.STRING, FeatureType.INT};
    
    /**
     * Higher level Feature names and types.
     */
    public static final String[] LEVEL_1_FEATURES = new String[] {"hostPairID", "internalFeature"};
    public static final FeatureType[] LEVEL_1_FEATURE_TYPES = new FeatureType[] {FeatureType.STRING, FeatureType.INT};
    public static final String[][] FEATURE_NAMES = { LEVEL_0_FEATURES, LEVEL_1_FEATURES };
    public static final FeatureType[][] FEATURE_TYPES = { LEVEL_0_FEATURE_TYPES, LEVEL_1_FEATURE_TYPES };

    /**
     * Abstraction rules for each level of abstraction, starting at level 1 (level 0 does not have an abstraction rule).
     */
    public static final AbstractionRule[] ABSTRACTION_RULES = {
        new AbstractionRule(new int[] { }, TIMEOUTS[1]),
    };

    /**
     * Multiplexer rules for each level of abstraction, starting at level 1.
     */
    public static final MultiplexerRule[] MULTIPLEXER_RULES = {
        new MultiplexerRule(7, 7),
    };

    /**
     * Multiplexer ID rules for each higher level of abstraction (sentence and document level). This is executed for each packet abstraction upon arrival.
     */
    public static final FeatureCombinationRule[] MULTIPLEXER_ID_RULES = {
        new FlowIdCombinationRule().setOutputIndex(7), new HostPairIdCombinationRule().setOutputIndex(8),
    };

    /**
     * Feature combination rules for each level of abstraction, starting at level 0. This is executed after Multiplexer ID rules.
     */
    public static final FeatureCombinationRule[][] FEATURE_COMBINATION_RULES = {
        {},
        {}
    };

    /**
     * Feature aggregation rules for each level of abstraction, starting at level 1. This is invoked upon arrival of a new child abstraction.
     */
    public static final FeatureAggregationRule[][] FEATURE_AGGREGATION_RULES = {
        {},
        {}
    };

    /**
     * Feature copy rules for each level of abstraction, starting at level 1. This is invoked upon arrival of a new child abstraction.
     */
    public static final FeatureCopyRule[][] FEATURE_COPY_RULES = {
        {}, // always empty
        {new FeatureCopyRule().setInputIndex(8).setOutputIndex(0), new FeatureCopyRule().setInputIndex(9).setOutputIndex(1)}
    };

    /**
     * Filter rules for each level of abstraction, starting at level 0. This is executed before tokenization.
     */
    public static final FilterRule[][] FILTER_RULES = {
        {new HighPassSizeFilter(100)},
        {}
    };

    /**
     * Feature differential rules for each level of abstraction, starting at level 1. This is executed when a new child abstraction is added, 
     * with respect to its previously added child.
     */
    public static final FeatureDifferentialRule[][] FEATURE_DIFFERENTIAL_RULES = {
        {}
    };

    /**
     * Tokenizer configurations.
     */
    public static final boolean IS_LOG_SCALE = false; // Transform before linear bucketization
    public static final int NUM_BUCKETS = 10; // Number of buckets for linear bucketization of BYTE feature
    public static final int HEART_BEAT_INTERVAL = 100000; // Interval in micro-seconds. Interleaves heartbeats in each sentence.
    public static final boolean USE_HEART_BEATS = true; // Use heartbeats in tokenization
    public static final boolean IS_BIDIRECTIONAL = false; // Use bidirectional tokenization. If false, vocabulary is twice as large and token streams are produced for both directions.
    public static final int MAX_VALUE = 1500; // Max value for linear bucketization
    public static final int MIN_VALUE = 0; // Min value for linear bucketization
    public static final int BUCKET_OFFSET = 10; // 0 - 9 is reserved for BERT tokens and heartbeat tokens. Buckets thus start at 10.
    public static final int HEART_BEAT_TOKEN_ID = 5; // ID of the heartbeat token in the tokenization.
}
