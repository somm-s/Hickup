package ch.cydcampus.hickup.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipInputStream;

/**
 * Indexes the contents of a folder with compressed or uncompressed csv files containing packet
 * abstraction information. The index is written to a file called index.txt in the same folder.
 * The index contains a mapping from each ip address contained in any file to all files where it
 * occurs.
 */
public class FileIndexer {

    private HashMap<String, Set<File>> index = new HashMap<>();
    private File[] listOfFiles;
    private String folderPath;

    /**
     * Creates a new FileIndexer.
     * @param folderPath the path to the folder containing the csv files
     */
    public FileIndexer(String folderPath) {
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        this.folderPath = folderPath;
        this.listOfFiles = listOfFiles;
    }

    /**
     * Indexes the contents of the folder and writes the index to a file called index.txt.
     */
    public void index() {
        for (File file : listOfFiles) {
            if (file.isFile()) {
                indexFile(file);
            }
        }
        ArrayList<String> sortedKeys = new ArrayList<>(index.keySet());
        Collections.sort(sortedKeys);
        for (String key : sortedKeys) {
            Set<File> files = index.get(key);
            writeToFile(key, files, folderPath + "/index.txt");
        }
    }

    private void writeToFile(String key, Set<File> files, String outputFileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName, true))) {
            writer.write(key + "," + files.size());
            List<File> sortedFiles = new ArrayList<>(files);
            Collections.sort(sortedFiles, (o1, o2) -> o1.getName().compareTo(o2.getName()));
            for (File file : sortedFiles) {
                writer.write("," + file.getName());
            }
            writer.write("\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
        }
    }

    private void indexFile(File file) {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file.getAbsolutePath()))) {
            System.out.println("Indexing contents of " + file.getName());
            if(zipInputStream.available() == 0) {
                return;
            }
            zipInputStream.getNextEntry();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if(line.length() <= 2) {
                        continue;
                    }
                    String[] parts = line.split(",");
                    String src = parts[0];
                    String dest = parts[1];
                    if(index.containsKey(src)) {
                        index.get(src).add(file);
                    } else {
                        index.put(src, new HashSet<>(Set.of(file)));
                    }
                    if(index.containsKey(dest)) {
                        index.get(dest).add(file);
                    } else {
                        index.put(dest, new HashSet<>(Set.of(file)));
                    }
                }
            }
            zipInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
