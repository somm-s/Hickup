import org.junit.Test;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;

import ch.cydcampus.hickup.pipeline.Pipeline;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class IntegrationTest {

    @Test
    public void testYourProgram() throws PcapNativeException, NotOpenException, IOException {
        File[] test = new File("integration_tests").listFiles(File::isDirectory);
        for (File folder : test) {
            System.out.println("Running test " + folder.getName());
            Pipeline pipeline = new Pipeline(folder.getAbsolutePath(), "", "outputs");
            pipeline.runPipeline();            

            // load txt files in outputs and txt files in integration_tests/test0 to compare if the same
            File[] files = new File("outputs").listFiles();
            File[] expectedFiles = new File(folder.getAbsolutePath() + "/expected_output").listFiles();
            HashMap<String, File> expectedFilesMap = new HashMap<String, File>();
            for (File file : expectedFiles) {
                expectedFilesMap.put(file.getName(), file);
            }
    
            for (File file : files) {
                if (expectedFilesMap.containsKey(file.getName())) {
                    compareFiles(file, expectedFilesMap.get(file.getName()));
                } else {
                    System.out.println("File " + file.getName() + " not found in expected files");
                    assertEquals(true, false);
                }
            }
        }

    }

    private void compareFiles(File output, File expected) throws FileNotFoundException, IOException {
        try (BufferedReader outputReader = new BufferedReader(new FileReader(output));
            BufferedReader expectedReader = new BufferedReader(new FileReader(expected))) {

            String outputLine, expectedLine;
            int lineNumber = 1;

            while ((outputLine = outputReader.readLine()) != null && (expectedLine = expectedReader.readLine()) != null) {
                // Compare each line from the output and expected files
                assertEquals("Mismatch at line " + lineNumber, outputLine, expectedLine);
                lineNumber++;
            }

            // Check if one file has more lines than the other
            if (outputReader.readLine() != null || expectedReader.readLine() != null) {
                System.out.println("Mismatch in the number of lines between " + output.getName() + " and " + expected.getName());
                assertEquals(true, false);
            }
        }
    }
}
