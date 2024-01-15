package ch.cydcampus.hickup.pipeline;

import ch.cydcampus.hickup.pipeline.feature.Feature.FeatureType;

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
        FeatureType.IP, FeatureType.STRING, FeatureType.STRING, FeatureType.LONG, FeatureType.LONG, FeatureType.LONG
    };
    public static final int[] LEVEL_1_INIT_FEATURES = { 8 };

    // LEVEL 2
    public static final String[] LEVEL_2_FEATURES = new String[] {
        "hostPairID", "bytes", "startTime", "endTime"
    };

    public static final FeatureType[] LEVEL_2_FEATURE_TYPES = new FeatureType[] {
        FeatureType.STRING, FeatureType.LONG, FeatureType.LONG, FeatureType.LONG
    };
    public static final int[] LEVEL_2_INIT_FEATURES = { 0 };


    // LEVEL 3
    public static final String[] LEVEL_3_FEATURES = new String[] {
        "bytes", "startTime", "endTime"
    };

    public static final FeatureType[] LEVEL_3_FEATURE_TYPES = new FeatureType[] {
        FeatureType.STRING, FeatureType.LONG, FeatureType.LONG, FeatureType.LONG
    };
    public static final int[] LEVEL_3_INIT_FEATURES = {};


    public static final String[][] FEATURE_NAMES = { LEVEL_0_FEATURES, LEVEL_1_FEATURES, LEVEL_2_FEATURES, LEVEL_3_FEATURES };
    public static final FeatureType[][] FEATURE_TYPES = { LEVEL_0_FEATURE_TYPES, LEVEL_1_FEATURE_TYPES, LEVEL_2_FEATURE_TYPES, LEVEL_3_FEATURE_TYPES };
    public static final int[][] INIT_FEATURES = { null, LEVEL_1_INIT_FEATURES, LEVEL_2_INIT_FEATURES, LEVEL_3_INIT_FEATURES };



    // Abstraction rules
    public static final int[][] ABSRACTION_RULE_SAME_FEATURES = { { 0 }, {}, {}};
    

    // Multiplexer stage
    public static final int[] MULTIPLEXER_FEATURES = { 7, 0, 0 };
}
