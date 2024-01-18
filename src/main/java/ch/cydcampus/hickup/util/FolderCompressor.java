package ch.cydcampus.hickup.util;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FolderCompressor {

    public static void compressFolder(String folderPath) {
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Invalid folder path: " + folderPath);
            return;
        }

        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            System.out.println("No files found in the folder: " + folderPath);
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                compressFile(file.getPath());
            }
        }

        System.out.println("Folder compression complete.");
    }

    public static void compressFile(String filePath) {
        File inputFile = new File(filePath);

        // Output ZIP file path
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
