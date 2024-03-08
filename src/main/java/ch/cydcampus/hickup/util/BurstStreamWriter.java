package ch.cydcampus.hickup.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import ch.cydcampus.hickup.pipeline.PipelineConfig;
import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.abstraction.PacketAbstraction;

/**
 * Write the abstractions that are on the tokenization level to a JSON Lines file.
 * Each abstraction is written to a new line and represented as a JSON object.
 * The fields of the JSON object are the features of the abstraction as well as the timestamp and the level of the abstraction.
 * The children of the abstraction are represented as an array of JSON objects.
 */
public class BurstStreamWriter {

    String path;

    /**
     * Constructs a new abstraction writer that writes to the file at the given path.
     *
     * @param path The path to the file to write the abstractions to.
     * @throws IOException
     */
    public BurstStreamWriter(String path) throws IOException {
        this.path = path;
        new java.io.File(path).mkdir();
    }

    /**
     * Writes the given abstraction to the file.
     *
     * @param abstraction The abstraction to write.
     * @throws IOException
     */
    public void writeAbstraction(Abstraction abstraction) throws IOException {

        String logBytesDir = path + "/log_bytes";
        String avgBytesDir = path + "/avg_bytes";
        new java.io.File(logBytesDir).mkdir();
        new java.io.File(avgBytesDir).mkdir();

        String hostPairId = abstraction.getFeature(0).asString();
        String fileNameLogBytes = path + "/log_bytes/" + hostPairId + ".csv";
        FileWriter fileWriterLogBytes = new FileWriter(fileNameLogBytes, true);
        BufferedWriter bufferedWriterLogBytes = new BufferedWriter(fileWriterLogBytes);

        String fileNameAvgBytes = path + "/avg_bytes/" + hostPairId + ".csv";
        FileWriter fileWriterAvgBytes = new FileWriter(fileNameAvgBytes, true);
        BufferedWriter bufferedWriterAvgBytes = new BufferedWriter(fileWriterAvgBytes);

        // abstraction is interaction. Bytes is in 2 and numChildren is in 3
        for(Abstraction child : abstraction.getChildren()) {

            // if its a packet abstraction, take PipelineConfig.BYTES_INDEX and 1 for numChildren
            String logBytes = "";
            String avgBytes = "";

            if(child instanceof PacketAbstraction) {
                logBytes = String.format("%.2f", getLogBytes(child.getFeature(PipelineConfig.BYTES_INDEX).asLong()));
                avgBytes = "" + child.getFeature(PipelineConfig.BYTES_INDEX).asLong();
            } else {
                logBytes = String.format("%.2f", getLogBytes(child.getFeature(2).asLong()));
                avgBytes = "" + getAverageBytes(child.getFeature(2).asLong(), child.getFeature(3).asInt());
            }
            bufferedWriterLogBytes.write(logBytes + " ");
            bufferedWriterAvgBytes.write(avgBytes + " ");
        }

        bufferedWriterLogBytes.newLine();
        bufferedWriterLogBytes.flush();
        bufferedWriterLogBytes.close();

        bufferedWriterAvgBytes.newLine();
        bufferedWriterAvgBytes.flush();
        bufferedWriterAvgBytes.close();
    }

    public void close() {
        // do nothing
    }

    /**
     * Return the average bytes of the burst
     * @param bytes
     * @param numChildren
     * @return average bytes per child in the burst
     */
    private long getAverageBytes(long bytes, int numChildren) {
        return bytes / numChildren;
    }

    /**
     * Retun log of bytes in float (base 2)
     * @param bytes
     * @return log of bytes
     */
    private float getLogBytes(long bytes) {
        return (float) (Math.log(bytes) / Math.log(2));
    }
}