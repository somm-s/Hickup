package ch.cydcampus.hickup.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class LineCounter {
    
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the directory path:");
        String directoryPath = scanner.nextLine();
        
        File directory = new File(directoryPath);

        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Invalid directory provided");
            return;
        }

        try {
            int lineCount = countLines(directory);
            System.out.println("Total lines: " + lineCount);
        } catch (IOException e) {
            System.out.println("An error occurred while reading the files");
            e.printStackTrace();
        }
    }

    private static int countLines(File directory) throws IOException {
        int lineCount = 0;

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    lineCount += countLinesInFile(file);
                } else if (file.isDirectory()) {
                    lineCount += countLines(file);
                }
            }
        }

        return lineCount;
    }

    private static int countLinesInFile(File file) throws IOException {
        int lines = 0;
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines++;
                }
            }
        }
        return lines;
    }
}
