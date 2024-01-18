package ch.cydcampus.hickup.util;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipFileReader {

    public static void readZipFile(String zipFilePath) {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipInputStream.getNextEntry();

            System.out.println("Contents of " + entry.getName() + ":");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
                
            zipInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        readZipFile("output/0/2022-04-18 22:41.csv.zip");
    }
}
