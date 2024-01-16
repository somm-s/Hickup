package ch.cydcampus.hickup.pipeline;

import ch.cydcampus.hickup.pipeline.feature.FeatureAggregationRule;
import ch.cydcampus.hickup.pipeline.feature.FeatureCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.FeatureCopyRule;
import ch.cydcampus.hickup.pipeline.feature.FeatureDifferentialRule;
import ch.cydcampus.hickup.pipeline.feature.FeatureSumRule;
import ch.cydcampus.hickup.pipeline.feature.FlowIdCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.HostPairIdCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.Feature.FeatureType;
import ch.cydcampus.hickup.pipeline.stage.AbstractionRule;
import ch.cydcampus.hickup.pipeline.stage.MultiplexerRule;

public class PipelineConfig {
    
    public static final int NUM_ABSTRACTION_LEVELS = 4;
    public static final int MAX_ABSTRACTION_LEVEL = NUM_ABSTRACTION_LEVELS - 1;

    public static final int[] TIMEOUTS = { 0, 30000, 1000000, 1000000 };


    // Always order Init Features - Combination Features - Differential Features - Streaming Features

    // LEVEL 0
    public static final int SRC_IP_INDEX = 0;
    public static final int DST_IP_INDEX = 1;
    public static final int SRC_PORT_INDEX = 2;
    public static final int DST_PORT_INDEX = 3;
    public static final int PROTOCOL_INDEX = 4;
    public static final int BYTES_INDEX = 5;
    public static final int TIME_INDEX = 6;
    public static final String[] LEVEL_0_FEATURES = new String[] {
        "srcIP", "dstIP", "srcPort", "dstPort", "protocol", "bytes", "time", "flowID", "hostPairID"
    };
    public static final FeatureType[] LEVEL_0_FEATURE_TYPES = new FeatureType[] {
        FeatureType.IP, FeatureType.IP, FeatureType.INT, FeatureType.INT, FeatureType.PROTOCOL, FeatureType.LONG, FeatureType.LONG, FeatureType.STRING, FeatureType.STRING
    };

    // LEVEL 1
    public static final String[] LEVEL_1_FEATURES = new String[] {
        "hostPairID", "bytes", "startTime", "endTime"
    };
    public static final FeatureType[] LEVEL_1_FEATURE_TYPES = new FeatureType[] {
        FeatureType.STRING, FeatureType.LONG, FeatureType.LONG, FeatureType.LONG
    };

    // LEVEL 2
    public static final String[] LEVEL_2_FEATURES = new String[] {
        "hostPairID", "bytes", "startTime", "endTime"
    };

    public static final FeatureType[] LEVEL_2_FEATURE_TYPES = new FeatureType[] {
        FeatureType.STRING, FeatureType.LONG, FeatureType.LONG, FeatureType.LONG
    };

    // LEVEL 3
    public static final String[] LEVEL_3_FEATURES = new String[] {
        "bytes", "startTime", "endTime"
    };

    public static final FeatureType[] LEVEL_3_FEATURE_TYPES = new FeatureType[] {
        FeatureType.LONG, FeatureType.LONG, FeatureType.LONG
    };


    // all levels of abstraction
    public static final String[][] FEATURE_NAMES = { LEVEL_0_FEATURES, LEVEL_1_FEATURES, LEVEL_2_FEATURES, LEVEL_3_FEATURES };
    public static final FeatureType[][] FEATURE_TYPES = { LEVEL_0_FEATURE_TYPES, LEVEL_1_FEATURE_TYPES, LEVEL_2_FEATURE_TYPES, LEVEL_3_FEATURE_TYPES };



    // Abstraction rules
    public static final AbstractionRule[] ABSTRACTION_RULES = {
        new AbstractionRule(new int[] { SRC_IP_INDEX }, TIMEOUTS[1]),
        new AbstractionRule(new int[] { }, TIMEOUTS[2]),
        new AbstractionRule(new int[] { }, TIMEOUTS[3])
    };

    public static final MultiplexerRule[] MULTIPLEXER_RULES = {
        new MultiplexerRule(0, 7),
        new MultiplexerRule(1, 0),
        new MultiplexerRule(2, 0)
    };

    // Combination rules. Each level of abstraction has an array of combination rules.
    public static final FeatureCombinationRule[][] FEATURE_COMBINATION_RULES = {
        {new FlowIdCombinationRule().setOutputIndex(7), new HostPairIdCombinationRule().setOutputIndex(8)},
        {},
        {},
        {} // can be non-empty
    };

    // Aggregation rules. Each level of abstraction has an array of streaming rules.
    public static final FeatureAggregationRule[][] FEATURE_AGGREGATION_RULES = {
        {new FeatureSumRule().setInputIndex(BYTES_INDEX).setOutputIndex(1)},
        {new FeatureSumRule().setInputIndex(1).setOutputIndex(1)},
        {new FeatureSumRule().setInputIndex(1).setOutputIndex(0)},
        {} // always empty
    };

    // Initial features. Each level of abstraction has an array of initial features.
    public static final FeatureCopyRule[][] FEATURE_COPY_RULES = {
        {}, // always empty
        {new FeatureCopyRule().setInputIndex(8).setOutputIndex(0)},
        {new FeatureCopyRule().setInputIndex(0).setOutputIndex(0)},
        {}
    };

    // Differential rules. Each level of abstraction has an array of differential rules.
    public static final FeatureDifferentialRule[][] FEATURE_DIFFERENTIAL_RULES = {
        {},
        {},
        {},
        {} // always empty.
    };

}
