package ch.cydcampus.hickup;

import java.io.IOException;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;

import ch.cydcampus.hickup.pipeline.Pipeline;
import ch.cydcampus.hickup.util.RecursivePcapConversionTask;

/**
 * Main class of the application.
 * Wraps pipeline and data pre-processing.
 * 
 * Input arguments:
 * - path to the input directory
 * - path to the output directory
 * - execution mode (pre-processing, pipeline)
 */
public class Main {
    
    public static void main(String[] args) throws PcapNativeException, NotOpenException, IOException {

        if (args.length != 3) {
            System.err.println("Usage: (converter | pipeline) <inputDir> <outputDir>");
            System.exit(1);
        }

        String mode = args[0];
        String inputDir = args[1];
        String outputDir = args[2];

        if (mode.equals("converter")) {
            int numThreads = Runtime.getRuntime().availableProcessors();
            RecursivePcapConversionTask.convertPcaps(inputDir, outputDir, "", numThreads);
        } else if (mode.equals("pipeline")) {
            Pipeline pipeline = new Pipeline(inputDir, "", outputDir);
            pipeline.runPipeline();
        } else {
            System.err.println("Invalid mode: " + mode);
            System.exit(1);
        }
    }

}
