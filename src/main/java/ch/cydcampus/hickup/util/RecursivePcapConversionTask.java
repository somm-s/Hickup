package ch.cydcampus.hickup.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import org.pcap4j.core.BpfProgram;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapHandle.TimestampPrecision;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;

import ch.cydcampus.hickup.pipeline.abstraction.PacketAbstraction;
import ch.cydcampus.hickup.pipeline.abstraction.AbstractionFactory;

public class RecursivePcapConversionTask extends RecursiveTask<Void>{
    
    private final String outputPath;
    private final String filter;

    private File[] pcapFiles; 
    private BufferedWriter writer;

    private int start;
    private int end;

    /**
     * Recursive task to convert pcap files to csv files containing IPPoints.
     * 
     * @param outputPath absolute path to folder where output files will be written
     * @param filter filter to apply to pcap files
     * @param pcapFiles array of pcap files to process
     * @param start index of first pcap file to process
     * @param end index past last pcap file to process
     */
    public RecursivePcapConversionTask(String outputPath, String filter, File[] pcapFiles, int start, int end) {
        this.outputPath = outputPath;
        this.filter = filter;
        this.pcapFiles = pcapFiles;
        this.start = start;
        this.end = end;
    }

    /**
     * Converts all pcap files in a folder to csv files containing IPPoints.
     * 
     * @param pcapFolderPath absolute path to folder containing pcap files
     * @param outputPath absolute path to folder where output files will be written
     * @param filter filter to apply to pcap files
     * @throws IOException 
     */
    public static void convertPcaps(String pcapFolderPath, String outputPath, String filter, int numThreads) throws IOException {
        ForkJoinPool forkJoinPool = new ForkJoinPool(numThreads);
        File folder = new File(pcapFolderPath);
        System.out.println(folder.listFiles());
        File[] listOfFiles = folder.listFiles();

        java.util.Arrays.sort(listOfFiles);

        RecursivePcapConversionTask task = new RecursivePcapConversionTask(outputPath, filter, listOfFiles, 0, listOfFiles.length);

        forkJoinPool.invoke(task);
        forkJoinPool.shutdown();

        // delete empty folders
        File[] outputFolders = new File(outputPath).listFiles();
        for(File outputFolder : outputFolders) {
            if(outputFolder.isDirectory() && outputFolder.listFiles().length == 0) {
                outputFolder.delete();
            }
        }

        // list all files in output path
        File[] outputFiles = new File(outputPath).listFiles();
        File[] oldFiles = outputFiles[0].listFiles();
        FolderCompressor.compressFolder(outputFiles[0].getAbsolutePath());

        // delete uncompressed files
        for(File outputFile : oldFiles) {
            if(outputFile.isFile()) {
                outputFile.delete();
            }
        }

        // build index
        FileIndexer indexer = new FileIndexer(outputFiles[0].getAbsolutePath());
        indexer.index();
    }

    @Override
    protected Void compute() {
        if(end - start > 1) {
            reduce(); // split into two tasks and combine results
        } else {
            map(); // process pcap file
        }
        return null;
    }

    private void map() {

        int pcapIndex = this.start;
        System.out.println("Worker " + Thread.currentThread().getId() + " processing " + pcapFiles[pcapIndex].getName());

        String pcapFilePath = pcapFiles[pcapIndex].getAbsolutePath();

        if(!hasAllowedEnding(pcapFiles[pcapIndex], new String[] {"pcap", "pcap.gz"})) {
            System.out.println("File " + pcapFiles[pcapIndex].getName() + " has wrong file ending, skipping...");
            return;
        }

        File pcapOutputDirectory = new File(combinePaths(outputPath, Integer.toString(pcapIndex)));
        pcapOutputDirectory.mkdirs();

        if(pcapFiles[pcapIndex].getName().endsWith(".pcap.gz")) {
            String tempFilePath = combinePaths(pcapOutputDirectory.getAbsolutePath(), "temp.pcap");
            try {
                PcapDecompressor.decompress(pcapFilePath, tempFilePath);
            } catch(Exception e) {
                System.out.println("Couldn't decompress pcap file: " + pcapFiles[pcapIndex].getName() + ", skipping...");
                return;
            }
            pcapFilePath = tempFilePath;
        }

        // Open the pcap file
        PcapHandle handle;

        try {
            handle = Pcaps.openOffline(pcapFilePath, TimestampPrecision.MICRO);
            // add filter
            handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);
        } catch (PcapNativeException | NotOpenException e) {
            System.out.println("Couldn't open pcap file: " + pcapFilePath + ", skipping...");
            return;
        }
        
        // create a packet listener
        PacketListener pl = new PacketListener() {
            @Override
            public void gotPacket(Packet packet) {

                // parse packet to IPPoint
                PacketAbstraction abstraction = null;
                try {
                    abstraction = AbstractionFactory.getInstance().allocateFromNetwork(packet, handle.getTimestamp());
                } catch (UnknownHostException e) {
                    System.out.println("Unknown host exception, skipping...");
                }
                if(abstraction == null) {
                    System.out.println("Could not parse abstraction, skipping...");
                    return;
                }

                String timeString = TimeInterval.microToTime(abstraction.getLastUpdateTime());
                int colonIndex = timeString.indexOf(':');
                String outFileName = timeString.substring(0, colonIndex + 3) + ".csv";
                File outFile = new File(Paths.get(pcapOutputDirectory.getAbsolutePath()).resolve(outFileName).toString());

                // check if file exists, create new filewriter and close previous one if not null
                try {
                    if(!outFile.exists()) {
                        if(writer != null) {
                            writer.flush();
                            writer.close();
                        }
                        writer = new BufferedWriter(new FileWriter(outFile, true));
                    }

                    writer.write(abstraction.serializeString() + "\n");
                } catch (IOException e) {
                    System.out.println("Couldn't write point to: " + outFile.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        };

        // loop over all packets in the pcap file
        try {
            handle.loop(-1, pl);
        } catch (PcapNativeException | InterruptedException | NotOpenException e) {
            System.out.println("Couldn't loop over pcap file: " + pcapFilePath);
            e.printStackTrace();
        }

        // Close the handle
        handle.close();

        // close writer if not null
        if(writer != null) {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // delete the temporary file
        File file = new File(pcapFilePath);
        file.delete();

        System.out.println("Worker " + Thread.currentThread().getId() + " Finished " + pcapFiles[start].getName());

    }

    private void reduce() {

        int mid = (start + end) / 2;
        RecursivePcapConversionTask leftTask = new RecursivePcapConversionTask(outputPath, filter, pcapFiles, start, mid);
        RecursivePcapConversionTask rightTask = new RecursivePcapConversionTask(outputPath, filter, pcapFiles, mid, end);
        
        leftTask.fork();
        rightTask.fork();
        leftTask.join();
        rightTask.join();

        System.out.println("Worker " + Thread.currentThread().getId() + " Combining files " + start + " and " + mid);

        // the results will be in start and mid, respectively. 
        File firstDirectory = new File(Paths.get(outputPath).resolve(Integer.toString(start)).toString());
        File secondDirectory = new File(Paths.get(outputPath).resolve(Integer.toString(mid)).toString());

        // Start by identifying conflicting files.
        File[] firstFiles = firstDirectory.listFiles();
        File[] secondFiles = secondDirectory.listFiles();

        List<File> conflictingFiles = new ArrayList<>();
        if(firstFiles != null && secondFiles != null) {
            java.util.Arrays.sort(firstFiles);
            java.util.Arrays.sort(secondFiles);
    
            int i = 0; int j = 0;
            while(i < firstFiles.length && j < secondFiles.length) {
                String a = firstFiles[i].getName();
                String b = secondFiles[j].getName();
                if(a.equals(b)) {
                    conflictingFiles.add(firstFiles[i]);
                    i++; j++;
                } else if(a.compareTo(b) < 0) {
                    i++;
                } else  {
                    j++;
                }
            }
        }


        // conflicting files are opened simultaneously and iterated over at the same time (merge)
        for(File conflictFile : conflictingFiles) {
            // open writer to write into start folder:
            File newFilePath = new File(Paths.get(firstDirectory.getAbsolutePath()).resolve("resolved " + conflictFile.getName()).toString());
            try {
                BufferedWriter combineWriter = new BufferedWriter(new FileWriter(newFilePath));

                // file 1
                File file1 = new File(Paths.get(firstDirectory.getAbsolutePath()).resolve(conflictFile.getName()).toString());
                BufferedReader reader1 = new BufferedReader(new FileReader(file1));
                
                // file 2
                File file2 = new File(Paths.get(secondDirectory.getAbsolutePath()).resolve(conflictFile.getName()).toString());
                BufferedReader reader2 = new BufferedReader(new FileReader(file2));

                TimeString[] lines1 = reader1.lines()
                        .map(TimeString::new)
                        .toArray(TimeString[]::new);
                
                TimeString[] lines2 = reader2.lines()
                        .map(TimeString::new)
                        .toArray(TimeString[]::new);

                System.out.println("started sorting " + conflictFile.getName() + " with " + lines1.length + " and " + lines2.length);
                Arrays.sort(lines1);
                Arrays.sort(lines2);
                System.out.println("finished sorting " + conflictFile.getName());

                int i = 0;
                int j = 0;
                while(i < lines1.length && j < lines2.length) {
                    if(lines1[i].compareTo(lines2[j]) < 0) {
                        // write p1
                        combineWriter.write(lines1[i].line + "\n");
                        i++;
                    } else {
                        // write p2
                        combineWriter.write(lines2[j].line + "\n");
                        j++;
                    }
                }

                combineWriter.flush();
                combineWriter.close();
                reader1.close();
                reader2.close();
            } catch (IOException e) {
                System.out.println("Merging failed. Skipping...");
                e.printStackTrace();
                return;
            }
        }
        
        // non-conflicting files are simply moved to the start directory
        for(File secondFile : secondFiles) {
            try {
                Files.move(Paths.get(secondFile.getAbsolutePath()), 
                Paths.get(firstDirectory.getAbsolutePath()).resolve(secondFile.getName()), 
                StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("coudn't replace " + secondFile.getAbsolutePath());
            }
        }

        // conflict files are moved to the directory, also replacing:
        for(File conflictFile : conflictingFiles) {
            try {
                Files.move(Paths.get(firstDirectory.getAbsolutePath()).resolve("resolved " + conflictFile.getName()), 
                Paths.get(conflictFile.getAbsolutePath()), 
                StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("coudn't replace resolved to " + conflictFile.getAbsolutePath());
            }
        }

        System.out.println("Worker " + Thread.currentThread().getId() + " Finished Combining " + start + " and " + mid);

        return;
    }

    private long timeFromLine(String line) {
        String[] splits = line.split(",");
        return Long.parseLong(splits[splits.length - 1]);
    }

    private boolean hasFileEnding(File file, String ending) {
        String fileName = file.getName();

        if(fileName.length() < ending.length()) {
            return false;
        }

        String lastPart = fileName.substring(fileName.length() - ending.length() - 1, fileName.length());
        return lastPart.equals("." + ending);
    }

    private boolean hasAllowedEnding(File file, String[] allowedEndings) {
        for(String ending : allowedEndings) {
            if(hasFileEnding(file, ending)) {
                return true;
            }
        }
        return false;
    }

    private String combinePaths(String directory, String file) {
        return Paths.get(directory).resolve(file).toString();
    }

    private class TimeString implements Comparable<TimeString> {
        public String line;
        public long time;

        public TimeString(String line) {
            this.line = line;
            this.time = timeFromLine(line);
        }

        @Override
        public int compareTo(TimeString o) {
            if(this.time < o.time) {
                return -1;
            } else if(this.time > o.time) {
                return 1;
            } else {
                return 0;
            }
        }
    }


    public static void main(String[] args) throws IOException {
        // String pcapFolderPath = "/home/lab/Documents/networking/hickup-net/pcaps_diverse";
        // String outputPath = "/home/lab/Documents/networking/hickup-net/output";
        String pcapFolderPath = "/home/sosi/ls22/2022/BT03-CHE/pcaps";
        String outputPath = "/home/sosi/ls22/2022/BT03-CHE/abstractions";

        String filter = "ip";

        convertPcaps(pcapFolderPath, outputPath, filter, 16);
    }

}
