package ch.cydcampus.hickup.util;

import java.io.FileWriter;
import java.io.IOException;

import ch.cydcampus.hickup.pipeline.feature.Feature.FeatureType;
import ch.cydcampus.hickup.pipeline.feature.aggregationrules.FeatureAggregationRule;
import ch.cydcampus.hickup.pipeline.feature.aggregationrules.FeatureSumRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.ChildrenCountCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.FeatureCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.FlowIdCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.combinationrules.HostPairIdCombinationRule;
import ch.cydcampus.hickup.pipeline.feature.copyrules.FeatureCopyRule;
import ch.cydcampus.hickup.pipeline.feature.differentialrules.FeatureDifferentialRule;
import ch.cydcampus.hickup.pipeline.filter.FilterRule;
import ch.cydcampus.hickup.pipeline.filter.HighPassSizeFilter;
import ch.cydcampus.hickup.pipeline.stage.AbstractionRule;
import ch.cydcampus.hickup.pipeline.stage.MultiplexerRule;

public class ConfigGenerator {

    public static String configPath = "experiment_2/configs";

    public static String imports = 
    "package ch.cydcampus.hickup.pipeline;\n" +
    "\n" +
    "import ch.cydcampus.hickup.pipeline.feature.Feature.FeatureType;\n" +
    "import ch.cydcampus.hickup.pipeline.feature.aggregationrules.FeatureAggregationRule;\n" +
    "import ch.cydcampus.hickup.pipeline.feature.aggregationrules.FeatureSumRule;\n" +
    "import ch.cydcampus.hickup.pipeline.feature.combinationrules.ChildrenCountCombinationRule;\n" +
    "import ch.cydcampus.hickup.pipeline.feature.combinationrules.FeatureCombinationRule;\n" +
    "import ch.cydcampus.hickup.pipeline.feature.combinationrules.FlowIdCombinationRule;\n" +
    "import ch.cydcampus.hickup.pipeline.feature.combinationrules.HostPairIdCombinationRule;\n" +
    "import ch.cydcampus.hickup.pipeline.feature.combinationrules.IntervalLengthCombinationRule;\n" +
    "import ch.cydcampus.hickup.pipeline.feature.copyrules.FeatureCopyRule;\n" +
    "import ch.cydcampus.hickup.pipeline.feature.differentialrules.FeatureDifferentialRule;\n" +
    "import ch.cydcampus.hickup.pipeline.filter.FilterRule;\n" +
    "import ch.cydcampus.hickup.pipeline.filter.HighPassSizeFilter;\n" +
    "import ch.cydcampus.hickup.pipeline.stage.AbstractionRule;\n" +
    "import ch.cydcampus.hickup.pipeline.stage.MultiplexerRule;\n" +
    "\n";

    public static void main(String[] args) throws IOException {

        String[] BurstTimeouts = {"1000", "30000", "50000"};
        String[] InteractionTimeouts = {"500000", "1000000", "15000000"};
        String[] Filters = {"", "new HighPassSizeFilter(100)", "new HighPassSizeFilter(150)"};

        for(String filter : Filters) {

            String filterNum = filter.equals("") ? "0" : filter.equals("new HighPassSizeFilter(100)") ? "100" : "150";

            for(String interactionTimeout : InteractionTimeouts) {

                String flowPacketConfig = 
                "public class PipelineConfig {\n" +
                "\n" +
                "    private PipelineConfig() {\n" +
                "        throw new IllegalStateException(\"Utility class\");\n" +
                "    }\n" +
                "\n" +
                "    public static final int NUM_ABSTRACTION_LEVELS = 2;\n" +
                "    public static final int MAX_ABSTRACTION_LEVEL = NUM_ABSTRACTION_LEVELS - 1;\n" +
                "    public static final int MIN_TOKENIZATION_LEVEL = 0;\n" +
                "    public static final int MAX_TOKENIZATION_LEVEL = 1;\n" +
                "    public static final long[] TIMEOUTS = { 0, " + interactionTimeout + " };\n" +
                "    public static final int SRC_IP_INDEX = 0;\n" +
                "    public static final int DST_IP_INDEX = 1;\n" +
                "    public static final int SRC_PORT_INDEX = 2;\n" +
                "    public static final int DST_PORT_INDEX = 3;\n" +
                "    public static final int PROTOCOL_INDEX = 4;\n" +
                "    public static final int BYTES_INDEX = 5;\n" +
                "    public static final int TIME_INDEX = 6;\n" +
                "    public static final int NUM_DEFAULT_FEATURES = 7;\n" +
                "    public static final String[] LEVEL_0_FEATURES = new String[] {\"srcIP\", \"dstIP\", \"srcPort\", \"dstPort\", \"protocol\", \"bytes\", \"time\", \"flowID\", \"hostPairID\"};\n" +
                "    public static final FeatureType[] LEVEL_0_FEATURE_TYPES = new FeatureType[] {FeatureType.IP, FeatureType.IP, FeatureType.INT, FeatureType.INT, FeatureType.PROTOCOL, FeatureType.LONG, FeatureType.LONG, FeatureType.STRING, FeatureType.STRING};\n" +
                "    public static final String[] LEVEL_1_FEATURES = new String[] {\"hostPairID\"};\n" +
                "    public static final FeatureType[] LEVEL_1_FEATURE_TYPES = new FeatureType[] {FeatureType.STRING};\n" +
                "    public static final String[][] FEATURE_NAMES = { LEVEL_0_FEATURES, LEVEL_1_FEATURES };\n" +
                "    public static final FeatureType[][] FEATURE_TYPES = { LEVEL_0_FEATURE_TYPES, LEVEL_1_FEATURE_TYPES };\n" +
                "    public static final AbstractionRule[] ABSTRACTION_RULES = {\n" +
                "        new AbstractionRule(new int[] { }, TIMEOUTS[1]),\n" +
                "    };\n" +
                "    public static final MultiplexerRule[] MULTIPLEXER_RULES = {\n" +
                "        new MultiplexerRule(7, 7),\n" +
                "    };\n" +
                "    public static final FeatureCombinationRule[] MULTIPLEXER_ID_RULES = {\n" +
                "        new FlowIdCombinationRule().setOutputIndex(7), new HostPairIdCombinationRule().setOutputIndex(8)\n" +
                "    };\n" +
                "    public static final FeatureCombinationRule[][] FEATURE_COMBINATION_RULES = {\n" +
                "        {},\n" +
                "        {}\n" +
                "    };\n" +
                "    public static final FeatureAggregationRule[][] FEATURE_AGGREGATION_RULES = {\n" +
                "        {},\n" +
                "        {}\n" +
                "    };\n" +
                "    public static final FeatureCopyRule[][] FEATURE_COPY_RULES = {\n" +
                "        {}, // always empty\n" +
                "        {new FeatureCopyRule().setInputIndex(8).setOutputIndex(0)}\n" +
                "    };\n" +
                "    public static final FilterRule[][] FILTER_RULES = {\n" +
                "        {" + filter + "},\n" +
                "        {}\n" +
                "    };\n" +
                "    public static final FeatureDifferentialRule[][] FEATURE_DIFFERENTIAL_RULES = {\n" +
                "        {}\n" +
                "    };\n" +
                "}\n";

                writeConfig(flowPacketConfig, configPath + "/config_fp_" + filterNum + "_x_" + interactionTimeout + ".java");
                
                String interactionPacketConfig = 
                "public class PipelineConfig {\n" +
                "\n" +
                "    private PipelineConfig() {\n" +
                "        throw new IllegalStateException(\"Utility class\");\n" +
                "    }\n" +
                "\n" +
                "    public static final int NUM_ABSTRACTION_LEVELS = 2;\n" +
                "    public static final int MAX_ABSTRACTION_LEVEL = NUM_ABSTRACTION_LEVELS - 1;\n" +
                "    public static final int MIN_TOKENIZATION_LEVEL = 0;\n" +
                "    public static final int MAX_TOKENIZATION_LEVEL = 1;\n" +
                "    public static final long[] TIMEOUTS = { 0, " + interactionTimeout + " };\n" +
                "    public static final int SRC_IP_INDEX = 0;\n" +
                "    public static final int DST_IP_INDEX = 1;\n" +
                "    public static final int SRC_PORT_INDEX = 2;\n" +
                "    public static final int DST_PORT_INDEX = 3;\n" +
                "    public static final int PROTOCOL_INDEX = 4;\n" +
                "    public static final int BYTES_INDEX = 5;\n" +
                "    public static final int TIME_INDEX = 6;\n" +
                "    public static final int NUM_DEFAULT_FEATURES = 7;\n" +
                "    public static final String[] LEVEL_0_FEATURES = new String[] {\"srcIP\", \"dstIP\", \"srcPort\", \"dstPort\", \"protocol\", \"bytes\", \"time\", \"flowID\", \"hostPairID\"};\n" +
                "    public static final FeatureType[] LEVEL_0_FEATURE_TYPES = new FeatureType[] {FeatureType.IP, FeatureType.IP, FeatureType.INT, FeatureType.INT, FeatureType.PROTOCOL, FeatureType.LONG, FeatureType.LONG, FeatureType.STRING, FeatureType.STRING};\n" +
                "    public static final String[] LEVEL_1_FEATURES = new String[] {\"hostPairID\"};\n" +
                "    public static final FeatureType[] LEVEL_1_FEATURE_TYPES = new FeatureType[] {FeatureType.STRING};\n" +
                "    public static final String[][] FEATURE_NAMES = { LEVEL_0_FEATURES, LEVEL_1_FEATURES };\n" +
                "    public static final FeatureType[][] FEATURE_TYPES = { LEVEL_0_FEATURE_TYPES, LEVEL_1_FEATURE_TYPES };\n" +
                "    public static final AbstractionRule[] ABSTRACTION_RULES = {\n" +
                "        new AbstractionRule(new int[] { }, TIMEOUTS[1]),\n" +
                "    };\n" +
                "    public static final MultiplexerRule[] MULTIPLEXER_RULES = {\n" +
                "        new MultiplexerRule(8, 8),\n" +
                "    };\n" +
                "    public static final FeatureCombinationRule[] MULTIPLEXER_ID_RULES = {\n" +
                "        new FlowIdCombinationRule().setOutputIndex(7), new HostPairIdCombinationRule().setOutputIndex(8)\n" +
                "    };\n" +
                "    public static final FeatureCombinationRule[][] FEATURE_COMBINATION_RULES = {\n" +
                "        {},\n" +
                "        {}\n" +
                "    };\n" +
                "    public static final FeatureAggregationRule[][] FEATURE_AGGREGATION_RULES = {\n" +
                "        {},\n" +
                "        {}\n" +
                "    };\n" +
                "    public static final FeatureCopyRule[][] FEATURE_COPY_RULES = {\n" +
                "        {}, // always empty\n" +
                "        {new FeatureCopyRule().setInputIndex(8).setOutputIndex(0)}\n" +
                "    };\n" +
                "    public static final FilterRule[][] FILTER_RULES = {\n" +
                "        {" + filter + "},\n" +
                "        {}\n" +
                "    };\n" +
                "    public static final FeatureDifferentialRule[][] FEATURE_DIFFERENTIAL_RULES = {\n" +
                "        {}\n" +
                "    };\n" +
                "}\n";

                writeConfig(interactionPacketConfig, configPath + "/config_ip_" + filterNum + "_x_" + interactionTimeout + ".java");


                for(String burstTimeout : BurstTimeouts) {

                    String interactionBurstConfig =
                    "public class PipelineConfig {\n" +
                    "\n" +
                    "    private PipelineConfig() {\n" +
                    "        throw new IllegalStateException(\"Utility class\");\n" +
                    "    }\n" +
                    "\n" +
                    "    public static final int NUM_ABSTRACTION_LEVELS = 3;\n" +
                    "    public static final int MAX_ABSTRACTION_LEVEL = NUM_ABSTRACTION_LEVELS - 1;\n" +
                    "    public static final int MIN_TOKENIZATION_LEVEL = 1;\n" +
                    "    public static final int MAX_TOKENIZATION_LEVEL = 2;\n" +
                    "    public static final long[] TIMEOUTS = { 0, " + burstTimeout + ", " + interactionTimeout + " };\n" +
                    "    public static final int SRC_IP_INDEX = 0;\n" +
                    "    public static final int DST_IP_INDEX = 1;\n" +
                    "    public static final int SRC_PORT_INDEX = 2;\n" +
                    "    public static final int DST_PORT_INDEX = 3;\n" +
                    "    public static final int PROTOCOL_INDEX = 4;\n" +
                    "    public static final int BYTES_INDEX = 5;\n" +
                    "    public static final int TIME_INDEX = 6;\n" +
                    "    public static final int NUM_DEFAULT_FEATURES = 7;\n" +
                    "    public static final String[] LEVEL_0_FEATURES = new String[] {\"srcIP\", \"dstIP\", \"srcPort\", \"dstPort\", \"protocol\", \"bytes\", \"time\", \"flowID\", \"hostPairID\"};\n" +
                    "    public static final FeatureType[] LEVEL_0_FEATURE_TYPES = new FeatureType[] {FeatureType.IP, FeatureType.IP, FeatureType.INT, FeatureType.INT, FeatureType.PROTOCOL, FeatureType.LONG, FeatureType.LONG, FeatureType.STRING, FeatureType.STRING};\n" +
                    "    public static final String[] LEVEL_1_FEATURES = new String[] {\"flowID\", \"hostPairID\", \"bytes\", \"numChildren\"};\n" +
                    "    public static final FeatureType[] LEVEL_1_FEATURE_TYPES = new FeatureType[] {FeatureType.STRING, FeatureType.STRING, FeatureType.LONG, FeatureType.INT};\n" +
                    "    public static final String[] LEVEL_2_FEATURES = new String[] {\"hostPairID\", \"bytes\"};\n" +
                    "    public static final FeatureType[] LEVEL_2_FEATURE_TYPES = new FeatureType[] {FeatureType.STRING, FeatureType.LONG};\n" +
                    "    public static final String[][] FEATURE_NAMES = { LEVEL_0_FEATURES, LEVEL_1_FEATURES, LEVEL_2_FEATURES };\n" +
                    "    public static final FeatureType[][] FEATURE_TYPES = { LEVEL_0_FEATURE_TYPES, LEVEL_1_FEATURE_TYPES, LEVEL_2_FEATURE_TYPES };\n" +
                    "    public static final AbstractionRule[] ABSTRACTION_RULES = {\n" +
                    "        new AbstractionRule(new int[] { SRC_IP_INDEX }, TIMEOUTS[1]),\n" +
                    "        new AbstractionRule(new int[] { }, TIMEOUTS[2]),\n" +
                    "    };\n" +
                    "    public static final MultiplexerRule[] MULTIPLEXER_RULES = {\n" +
                    "        new MultiplexerRule(7, 7),\n" +
                    "        new MultiplexerRule(1, 8),\n" +
                    "    };\n" +
                    "    public static final FeatureCombinationRule[] MULTIPLEXER_ID_RULES = {\n" +
                    "        new FlowIdCombinationRule().setOutputIndex(7), new HostPairIdCombinationRule().setOutputIndex(8)\n" +
                    "    };\n" +
                    "    public static final FeatureCombinationRule[][] FEATURE_COMBINATION_RULES = {\n" +
                    "        {},\n" +
                    "        { new ChildrenCountCombinationRule().setOutputIndex(3) },\n" +
                    "        {}\n" +
                    "    };\n" +
                    "    public static final FeatureAggregationRule[][] FEATURE_AGGREGATION_RULES = {\n" +
                    "        {new FeatureSumRule().setInputIndex(BYTES_INDEX).setOutputIndex(2)},\n" +
                    "        {new FeatureSumRule().setInputIndex(2).setOutputIndex(1)},\n" +
                    "        {},\n" +
                    "    };\n" +
                    "    public static final FeatureCopyRule[][] FEATURE_COPY_RULES = {\n" +
                    "        {}, // always empty\n" +
                    "        {new FeatureCopyRule().setInputIndex(7).setOutputIndex(0), new FeatureCopyRule().setInputIndex(8).setOutputIndex(1)},\n" +
                    "        {new FeatureCopyRule().setInputIndex(1).setOutputIndex(0)}\n" +
                    "    };\n" +
                    "    public static final FilterRule[][] FILTER_RULES = {\n" +
                    "        {" + filter + "},\n" +
                    "        {},\n" +
                    "        {}\n" +
                    "    };\n" +
                    "    public static final FeatureDifferentialRule[][] FEATURE_DIFFERENTIAL_RULES = {\n" +
                    "        {},\n" +
                    "        {}\n" +
                    "    };\n" +
                    "}\n";

                    writeConfig(interactionBurstConfig, configPath + "/config_ib_" + filterNum + "_" + burstTimeout + "_" + interactionTimeout + ".java");
            
                    String flowBurstConfig = 
                    "public class PipelineConfig {\n" +
                    "\n" +
                    "    private PipelineConfig() {\n" +
                    "        throw new IllegalStateException(\"Utility class\");\n" +
                    "    }\n" +
                    "\n" +
                    "    public static final int NUM_ABSTRACTION_LEVELS = 3;\n" +
                    "    public static final int MAX_ABSTRACTION_LEVEL = NUM_ABSTRACTION_LEVELS - 1;\n" +
                    "    public static final int MIN_TOKENIZATION_LEVEL = 1;\n" +
                    "    public static final int MAX_TOKENIZATION_LEVEL = 2;\n" +
                    "    public static final long[] TIMEOUTS = { 0, " + burstTimeout + ", " + interactionTimeout + " };\n" +
                    "    public static final int SRC_IP_INDEX = 0;\n" +
                    "    public static final int DST_IP_INDEX = 1;\n" +
                    "    public static final int SRC_PORT_INDEX = 2;\n" +
                    "    public static final int DST_PORT_INDEX = 3;\n" +
                    "    public static final int PROTOCOL_INDEX = 4;\n" +
                    "    public static final int BYTES_INDEX = 5;\n" +
                    "    public static final int TIME_INDEX = 6;\n" +
                    "    public static final int NUM_DEFAULT_FEATURES = 7;\n" +
                    "    public static final String[] LEVEL_0_FEATURES = new String[] {\"srcIP\", \"dstIP\", \"srcPort\", \"dstPort\", \"protocol\", \"bytes\", \"time\", \"flowID\", \"hostPairID\"};\n" +
                    "    public static final FeatureType[] LEVEL_0_FEATURE_TYPES = new FeatureType[] {FeatureType.IP, FeatureType.IP, FeatureType.INT, FeatureType.INT, FeatureType.PROTOCOL, FeatureType.LONG, FeatureType.LONG, FeatureType.STRING, FeatureType.STRING};\n" +
                    "    public static final String[] LEVEL_1_FEATURES = new String[] {\"flowID\", \"hostPairID\", \"bytes\", \"numChildren\"};\n" +
                    "    public static final FeatureType[] LEVEL_1_FEATURE_TYPES = new FeatureType[] {FeatureType.STRING, FeatureType.STRING, FeatureType.LONG, FeatureType.INT};\n" +
                    "    public static final String[] LEVEL_2_FEATURES = new String[] {\"hostPairID\", \"bytes\"};\n" +
                    "    public static final FeatureType[] LEVEL_2_FEATURE_TYPES = new FeatureType[] {FeatureType.STRING, FeatureType.LONG};\n" +
                    "    public static final String[][] FEATURE_NAMES = { LEVEL_0_FEATURES, LEVEL_1_FEATURES, LEVEL_2_FEATURES };\n" +
                    "    public static final FeatureType[][] FEATURE_TYPES = { LEVEL_0_FEATURE_TYPES, LEVEL_1_FEATURE_TYPES, LEVEL_2_FEATURE_TYPES };\n" +
                    "    public static final AbstractionRule[] ABSTRACTION_RULES = {\n" +
                    "        new AbstractionRule(new int[] { SRC_IP_INDEX }, TIMEOUTS[1]),\n" +
                    "        new AbstractionRule(new int[] { }, TIMEOUTS[2]),\n" +
                    "    };\n" +
                    "    public static final MultiplexerRule[] MULTIPLEXER_RULES = {\n" +
                    "        new MultiplexerRule(7, 7),\n" +
                    "        new MultiplexerRule(0, 7),\n" +
                    "    };\n" +
                    "    public static final FeatureCombinationRule[] MULTIPLEXER_ID_RULES = {\n" +
                    "        new FlowIdCombinationRule().setOutputIndex(7), new HostPairIdCombinationRule().setOutputIndex(8)\n" +
                    "    };\n" +
                    "    public static final FeatureCombinationRule[][] FEATURE_COMBINATION_RULES = {\n" +
                    "        {},\n" +
                    "        { new ChildrenCountCombinationRule().setOutputIndex(3) },\n" +
                    "        {}\n" +
                    "    };\n" +
                    "    public static final FeatureAggregationRule[][] FEATURE_AGGREGATION_RULES = {\n" +
                    "        {new FeatureSumRule().setInputIndex(BYTES_INDEX).setOutputIndex(2)},\n" +
                    "        {new FeatureSumRule().setInputIndex(2).setOutputIndex(1)},\n" +
                    "        {},\n" +
                    "    };\n" +
                    "    public static final FeatureCopyRule[][] FEATURE_COPY_RULES = {\n" +
                    "        {}, // always empty\n" +
                    "        {new FeatureCopyRule().setInputIndex(7).setOutputIndex(0), new FeatureCopyRule().setInputIndex(8).setOutputIndex(1)},\n" +
                    "        {new FeatureCopyRule().setInputIndex(1).setOutputIndex(0)}\n" +
                    "    };\n" +
                    "    public static final FilterRule[][] FILTER_RULES = {\n" +
                    "        {" + filter + "},\n" +
                    "        {},\n" +
                    "        {}\n" +
                    "    };\n" +
                    "    public static final FeatureDifferentialRule[][] FEATURE_DIFFERENTIAL_RULES = {\n" +
                    "        {},\n" +
                    "        {}\n" +
                    "    };\n" +
                    "}\n";

                    writeConfig(flowBurstConfig, configPath + "/config_fb_" + filterNum + "_" + burstTimeout + "_" + interactionTimeout + ".java");
                }
            }
        }
    }

    public static void writeConfig(String config, String fileName) throws IOException {
        FileWriter myWriter = new FileWriter(fileName);
        myWriter.write(imports + config);
        myWriter.close();
        System.out.println("Successfully wrote config to file");
    }
}
