package ch.cydcampus.hickup.util;

import java.io.FileWriter;
import java.io.IOException;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

public class AbstractionCsvWriter {
    
    private FileWriter[] writers;
    private int[] indices;
    private int minLevel;

    public AbstractionCsvWriter(String outputFilePath, int minLevel, int maxLevel) {
        writers = new FileWriter[maxLevel - minLevel + 1];
        indices = new int[maxLevel - minLevel + 1];
        this.minLevel = minLevel;
        for(int i = 0; i < writers.length; i++) {
            try {
                writers[i] = new FileWriter(outputFilePath + "/level_" + (i + minLevel) + ".csv");
            } catch (Exception e) {
                e.printStackTrace();
            }
            indices[i] = 0;
        }
    }

    public void close() {
        for(int i = 0; i < writers.length; i++) {
            try {
                writers[i].close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void writeAbstraction(Abstraction abstraction, int parentIndex) throws IOException {
        int level = abstraction.getLevel();
        int idx = level - minLevel;
        int abstractionIndex = indices[idx]++;
        String csvString = abstraction.toCsvString();
        if(abstractionIndex == 0 && parentIndex == -1) {
            writers[idx].write("id," + abstraction.getCsvHeader() + "\n");
            writers[idx].write(abstractionIndex + "," + csvString + "\n");
        } else if(abstractionIndex == 0) {
            writers[idx].write("id,parent_id," + abstraction.getCsvHeader() + "\n");
            writers[idx].write(abstractionIndex + "," + parentIndex + "," + csvString + "\n");
        } else if(parentIndex == -1) {
            writers[idx].write(abstractionIndex + "," + csvString + "\n");
        } else {
            writers[idx].write(abstractionIndex + "," + parentIndex + "," + csvString + "\n");
        }

        if(level > minLevel) {
            for(Abstraction child : abstraction.getChildren()) {
                writeAbstraction(child, abstractionIndex);
            }
        }
    }
    
}
