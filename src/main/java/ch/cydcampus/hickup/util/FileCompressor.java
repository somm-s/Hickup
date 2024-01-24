package ch.cydcampus.hickup.util;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileCompressor {

    public static void compressFile(String filePath) {
        File inputFile = new File(filePath);
        if (!inputFile.exists()) {
            System.out.println("File not found: " + filePath);
            return;
        }
        String outputZipPath = filePath + ".zip";
        try (FileOutputStream fos = new FileOutputStream(outputZipPath);
             ZipOutputStream zipOut = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(inputFile)) {
            ZipEntry zipEntry = new ZipEntry(inputFile.getName());
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            System.out.println("File compressed successfully. Compressed file: " + outputZipPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
