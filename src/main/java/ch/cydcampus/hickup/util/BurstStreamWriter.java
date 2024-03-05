package ch.cydcampus.hickup.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

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
    }

    /**
     * Writes the given abstraction to the file.
     *
     * @param abstraction The abstraction to write.
     * @throws IOException
     */
    public void writeAbstraction(Abstraction abstraction) throws IOException {

        String hostPairId = abstraction.getFeature(0).asString();
        String fileName = path + "/" + hostPairId + ".csv";
        FileWriter fileWriter = new FileWriter(fileName, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        // abstraction is interaction. Bytes is in 2 and numChildren is in 3
        for(Abstraction child : abstraction.getChildren()) {
            String logBytes = String.format("%.2f", getLogBytes(child.getFeature(2).asLong()));
            bufferedWriter.write(logBytes + " ");
        }
        bufferedWriter.newLine();
        bufferedWriter.flush();
        bufferedWriter.close();
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