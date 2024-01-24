package ch.cydcampus.hickup.util;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;

/**
 * Class to decompress pcap.gz files and store them in a temporary file.
 */
public class PcapDecompressor {

    /**
     * Decompresses a pcap.gz file and stores it in a temporary file.
     * @param filename the name of the pcap.gz file
     * @param tempname the name of the temporary file
     * @throws Exception
     */
    public static void decompress(String filename, String tempname) throws Exception {
        FileInputStream fis = new FileInputStream(filename);
        GZIPInputStream gzis = new GZIPInputStream(fis);
        FileOutputStream fos = new FileOutputStream(tempname);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = gzis.read(buffer)) > 0) {
            fos.write(buffer, 0, len);
        }
        gzis.close();
        fis.close();
        fos.close();
    }
}
