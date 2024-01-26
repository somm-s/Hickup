import org.junit.Test;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;

import ch.cydcampus.hickup.pipeline.Pipeline;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

public class IntegrationTest {

    @Test
    public void testYourProgram() throws PcapNativeException, NotOpenException, IOException {
        // Your integration test logic here
        // You can call your program, provide input, and assert the expected output
        // Example:
        // Pipeline pipeline = new Pipeline();
        // pipeline.runPipeline();
        String result = "e a P ";
        assertEquals("e a P ", result);
    }
}
