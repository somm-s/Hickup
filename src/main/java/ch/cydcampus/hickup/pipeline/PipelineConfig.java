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
import ch.cydcampus.hickup.pipeline.tokenizer.NumericTokenizer;
import ch.cydcampus.hickup.pipeline.tokenizer.Tokenizer;
import ch.cydcampus.hickup.pipeline.tokenizer.ValueTokenizer;

public class PipelineConfig {
    
    public static final int NUM_ABSTRACTION_LEVELS = 4;
    public static final int MAX_ABSTRACTION_LEVEL = NUM_ABSTRACTION_LEVELS - 1;
    public static final int TOKENIZATION_LAYER = 3;
    public static final long[] TIMEOUTS = { 0, 30000, 1000000, 0 };
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
    public static final String[] LEVEL_1_FEATURES = new String[] {"hostPairID", "bytes", "numChildren"};
    public static final FeatureType[] LEVEL_1_FEATURE_TYPES = new FeatureType[] {FeatureType.STRING, FeatureType.LONG, FeatureType.INT};
    public static final String[] LEVEL_2_FEATURES = new String[] {"bytes", "intervalLength", "numChildren"};
    public static final FeatureType[] LEVEL_2_FEATURE_TYPES = new FeatureType[] {FeatureType.LONG, FeatureType.LONG, FeatureType.INT};
    public static final String[] LEVEL_3_FEATURES = new String[] {"bytes"};
    public static final FeatureType[] LEVEL_3_FEATURE_TYPES = new FeatureType[] {FeatureType.LONG};
    public static final String[][] FEATURE_NAMES = { LEVEL_0_FEATURES, LEVEL_1_FEATURES, LEVEL_2_FEATURES, LEVEL_3_FEATURES };
    public static final FeatureType[][] FEATURE_TYPES = { LEVEL_0_FEATURE_TYPES, LEVEL_1_FEATURE_TYPES, LEVEL_2_FEATURE_TYPES, LEVEL_3_FEATURE_TYPES };
    public static final AbstractionRule[] ABSTRACTION_RULES = {
        new AbstractionRule(new int[] { SRC_IP_INDEX }, TIMEOUTS[1]),
        new AbstractionRule(new int[] { }, TIMEOUTS[2]),
        new AbstractionRule(new int[] { }, TIMEOUTS[3])
    };
    public static final MultiplexerRule[] MULTIPLEXER_RULES = {
        new MultiplexerRule(7, 7),
        new MultiplexerRule(0, 8),
        new MultiplexerRule() // static multiplexer
    };
    public static final FeatureCombinationRule[] MULTIPLEXER_ID_RULES = {
        new FlowIdCombinationRule().setOutputIndex(7), new HostPairIdCombinationRule().setOutputIndex(8)
    };
    public static final FeatureCombinationRule[][] FEATURE_COMBINATION_RULES = {
        {},
        { new ChildrenCountCombinationRule().setOutputIndex(2) },
        { new IntervalLengthCombinationRule().setOutputIndex(1), new ChildrenCountCombinationRule().setOutputIndex(2)},
        {}
    };
    public static final FeatureAggregationRule[][] FEATURE_AGGREGATION_RULES = {
        {new FeatureSumRule().setInputIndex(BYTES_INDEX).setOutputIndex(1)},
        {new FeatureSumRule().setInputIndex(1).setOutputIndex(0)},
        {new FeatureSumRule().setInputIndex(0).setOutputIndex(0)},
        {}
    };
    public static final FeatureCopyRule[][] FEATURE_COPY_RULES = {
        {}, // always empty
        {new FeatureCopyRule().setInputIndex(8).setOutputIndex(0)},
        {},
        {}
    };
    public static final FilterRule[][] FILTER_RULES = {
        {new HighPassSizeFilter(150)},
        {},
        {},
        {}
    };
    public static final FeatureDifferentialRule[][] FEATURE_DIFFERENTIAL_RULES = {
        {},
        {},
        {}
    };

    public static final Tokenizer[][] TOKENIZERS = { // Use this tokenizer for running the pipeline on real data
        {}, //{ new NumericTokenizer(150, 20000, true, BYTES_INDEX)}, // level 0
        { new NumericTokenizer(150, 80000, true, 1) , new NumericTokenizer(0, 100, false, 2)}, // level 1
        { new NumericTokenizer(1000, 20000000, true, 0), new NumericTokenizer(-1, 10000000, false, 1), new NumericTokenizer(0, 100, false, 2) }, // level 2
        { new NumericTokenizer(1000, 20000000, true, 0) } // level 3
    };

    // public static final Tokenizer[][] TOKENIZERS = { // This tokenizer is used for testing the pipeline (actual values instead of buckets)
    //     {},
    //     { new ValueTokenizer(1) },
    //     { new ValueTokenizer(0) },
    //     { new ValueTokenizer(0) }
    // };
}
