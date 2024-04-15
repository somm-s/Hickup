package ch.cydcampus.hickup.pipeline.tokenizer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import ch.cydcampus.hickup.pipeline.PipelineConfig;
import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.util.CrossProductIterator;

/*
 * Tokenizes single packets either from flows or interactions. 
 * This class is not compatible with other abstraction trees.
 */
public class PacketTokenizer {

    private int[] isLogScaleOptions = {0};
    private int[] numBucketsOptions = {30};
    private int[] heartBeatIntervalOptions = {100000};
    private int[] isBidirectionalOptions = {0};
    private int[] maxLogValueOptions = {11};
    private int[] maxLinearValueOptions = {1500}; // needs to be same length as maxLogValueOptions

    private int minLogValue = 0;
    private int minLinearValue = 0;
    private int bucketOffset = 10; // 0 - 9 is reserved for BERT tokens and heartbeat tokens
    private int heartBeatTokenID = 5;
    private String outputPath;

    List<List<Integer>> crossProductIndices;

    

    public PacketTokenizer(String outputPath) {
        this.outputPath = outputPath;
        crossProductIndices = CrossProductIterator.crossProductIndices(
            isLogScaleOptions, numBucketsOptions, heartBeatIntervalOptions, isBidirectionalOptions, maxLogValueOptions);
    }

    public void tokenize(Abstraction abstraction) throws IOException {

        for(List<Integer> indices : crossProductIndices) {
            boolean isLogScale = indices.get(0) == 1;
            int numBuckets = numBucketsOptions[indices.get(1)];
            int heartBeatInterval = heartBeatIntervalOptions[indices.get(2)];
            boolean useHeartBeats = heartBeatInterval != 0;
            boolean isBidirectional = indices.get(3) == 1;
            int maxValue = isLogScale ? maxLogValueOptions[indices.get(4)] : maxLinearValueOptions[indices.get(4)];
            int minValue = isLogScale ? minLogValue : minLinearValue;

            String configSuffix = "/";
            if (isLogScale) {
                configSuffix += "log_";
            } else {
                configSuffix += "lin_";
            }
            if (useHeartBeats) {
                configSuffix += "hb" + heartBeatInterval + "_";
            } else {
                configSuffix += "no-hb_";
            }
            if (isBidirectional) {
                configSuffix += "bi_";
            } else {
                configSuffix += "uni_";
            }

    
            configSuffix += numBuckets + "-buck_";
            configSuffix += minValue + "-" + maxValue;

            String configDir = outputPath + configSuffix;
            new java.io.File(configDir).mkdir();

            tokenizeWithConfig(isLogScale, numBuckets, useHeartBeats, heartBeatInterval, isBidirectional, maxValue, minValue, configDir, abstraction);
        }
    }

    private void tokenizeWithConfig(boolean isLogScale, int numBuckets, boolean useHeartBeats, int heartBeatInterval, boolean isBidirectional, int maxValue, int minValue, String configDir, Abstraction abstraction) throws IOException {
        String hostPairId = abstraction.getFeature(0).asString();
        String[] ips = hostPairId.split("-");
        String fileNameLeft = configDir + "/" + ips[0] + ".csv";
        String fileNameRight = configDir + "/" + ips[1] + ".csv";
        String hostFileName = configDir + "/" + hostPairId + ".csv";

        BufferedWriter leftBufferedWriter = null;
        BufferedWriter rightBufferedWriter = null;
        BufferedWriter hostBufferedWriter = null;
        if(isBidirectional) {
            FileWriter hostWriter = new FileWriter(hostFileName, true);
            hostBufferedWriter = new BufferedWriter(hostWriter);
        } else {
            FileWriter leftWriter = new FileWriter(fileNameLeft, true);
            leftBufferedWriter = new BufferedWriter(leftWriter);
            leftBufferedWriter.write(ips[1] + ",");
            FileWriter rightWriter = new FileWriter(fileNameRight, true);
            rightBufferedWriter = new BufferedWriter(rightWriter);
            rightBufferedWriter.write(ips[0] + ",");
        }

        long lastHeartBeatTime = abstraction.getFirstUpdateTime();
        for(Abstraction child : abstraction.getChildren()) {
            long bytes = child.getFeature(PipelineConfig.BYTES_INDEX).asLong();

            if(useHeartBeats && child.getFeature(PipelineConfig.TIME_INDEX).asLong() - lastHeartBeatTime > heartBeatInterval) {
                int numHeartBeats = (int) ((child.getFeature(PipelineConfig.TIME_INDEX).asLong() - lastHeartBeatTime) / heartBeatInterval);
                for(int i = 0; i < numHeartBeats; i++) {
                    lastHeartBeatTime += heartBeatInterval;
                    if(isBidirectional) {
                        hostBufferedWriter.write(heartBeatTokenID + " ");
                    } else {
                        leftBufferedWriter.write(heartBeatTokenID + " ");
                        rightBufferedWriter.write(heartBeatTokenID + " ");
                    }
                }
            }

            int tokenID = getTokenID(isLogScale, numBuckets, minValue, maxValue, bytes, bucketOffset);
            if(!isBidirectional) {
                boolean leftToRight = false; // from left IP to right IP
                String src = child.getFeature(PipelineConfig.SRC_IP_INDEX).toString();
                leftToRight = src.equals(ips[0]);
                int leftID = leftToRight ? tokenID : (tokenID + numBuckets); // receiving tokens are in the second half
                int rightID = leftToRight ? (tokenID + numBuckets) : tokenID;
                tokenID = leftID; // take left to track if similar token is received

                rightBufferedWriter.write(rightID + " ");
                leftBufferedWriter.write(leftID + " ");
            } else {
                hostBufferedWriter.write(tokenID + " ");
            }
        }

        if(isBidirectional) {
            hostBufferedWriter.newLine();
            hostBufferedWriter.flush();
            hostBufferedWriter.close();
        } else {
            leftBufferedWriter.newLine();
            leftBufferedWriter.flush();
            leftBufferedWriter.close();
            rightBufferedWriter.newLine();
            rightBufferedWriter.flush();
            rightBufferedWriter.close();
        }
    }

    private int getTokenID(boolean isLogScale, int numBuckets, int minValue, int maxValue, long bytes, int offset) {

        double bytesDouble = bytes;
        if(isLogScale) {
            bytesDouble = (long) getLogBytes(bytes);
        }

        if(bytesDouble <= minValue) {
            return offset;
        }

        if(bytesDouble >= maxValue) {
            return numBuckets + offset - 1;
        }

        int bucket = (int) (bytesDouble * (((double) (numBuckets - 1)) / (maxValue - minValue)));
        return bucket + offset;
    }

    private double getLogBytes(long bytes) {
        return (Math.log(bytes) / Math.log(2));
    }

}