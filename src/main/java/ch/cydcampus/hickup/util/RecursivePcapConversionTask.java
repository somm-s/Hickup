package ch.cydcampus.hickup.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
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
import ch.cydcampus.hickup.pipeline.feature.Feature.Protocol;
import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.abstraction.AbstractionFactory;

public class RecursivePcapConversionTask extends RecursiveTask<Void>{
    
    private final String outputPath;
    private final String filter;

    private File[] pcapFiles; 

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
        File[] listOfFiles = folder.listFiles();

        // remove files that are not pcap files / pcap.gz files
        List<File> pcapFiles = new ArrayList<>();

        for(File file : listOfFiles) {
            if(file.isFile() && hasAllowedEnding(file, new String[] {"pcap", "pcap.gz"})) {
                pcapFiles.add(file);
            }
        }

        listOfFiles = pcapFiles.toArray(new File[0]);
        java.util.Arrays.sort(listOfFiles);
        RecursivePcapConversionTask task = new RecursivePcapConversionTask(outputPath, filter, listOfFiles, 0, listOfFiles.length);
        forkJoinPool.invoke(task);
        forkJoinPool.shutdown();

        File[] outputFolders = new File(outputPath).listFiles();
        for(File outputFolder : outputFolders) {
            if(outputFolder.isDirectory() && outputFolder.listFiles().length == 0) {
                outputFolder.delete();
            }
        }
        File[] outputFiles = new File(outputPath).listFiles();
        File[] oldFiles = outputFiles[0].listFiles();

        // delete all files that do not end with .csv
        for(File outputFile : oldFiles) {
            if(outputFile.isFile() && !hasFileEnding(outputFile, "csv")) {
                outputFile.delete();
            }
        }

        FolderCompressor.compressFolder(outputFiles[0].getAbsolutePath());
        for(File outputFile : oldFiles) {
            if(outputFile.isFile()) {
                outputFile.delete();
            }
        }
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
        PcapHandle handle;
        try {
            handle = Pcaps.openOffline(pcapFilePath, TimestampPrecision.MICRO);
            // add filter
            handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);
        } catch (PcapNativeException | NotOpenException e) {
            System.out.println("Couldn't open pcap file: " + pcapFilePath + ", skipping...");
            return;
        }
        PacketListener pl = new PacketListener() {
            @SuppressWarnings("resource")
            @Override
            public void gotPacket(Packet packet) {
                PacketAbstraction abstraction = null;
                try {
                    byte[] rawData = packet.getPayload().getRawData();
                    if(rawData.length <= 50) {
                        return;
                    }

                    byte[] ipData = new byte[rawData.length - 50];
                    // make rawData human readable:
                    for (int i = 50; i < rawData.length; i++) {
                        ipData[i - 50] = rawData[i];
                    }

                    Protocol protocol = null;
                    long bytes = 0;
                    InetAddress srcAddr = null;
                    InetAddress dstAddr = null;
                    int srcPort = 0;
                    int dstPort = 0;
                    if(ipData[0] == 0x45) { // ipv4

                        if(ipData.length < 20) {
                            return;
                        }

                        srcAddr = InetAddress.getByAddress(new byte[] {ipData[12], ipData[13], ipData[14], ipData[15]});
                        dstAddr = InetAddress.getByAddress(new byte[] {ipData[16], ipData[17], ipData[18], ipData[19]});
                        bytes = ipData.length - 20;
                        if(ipData[9] == 0x06) {
                            if(ipData.length < 24) {
                                return;
                            }
                            protocol = Protocol.TCP;
                            srcPort = (ipData[20] & 0xFF) << 8 | (ipData[21] & 0xFF);
                            dstPort = (ipData[22] & 0xFF) << 8 | (ipData[23] & 0xFF);
                        } else if(ipData[9] == 0x11) {
                            if(ipData.length < 24) {
                                return;
                            }
                            protocol = Protocol.UDP;
                            bytes -= 8;
                            srcPort = (ipData[20] & 0xFF) << 8 | (ipData[21] & 0xFF);
                            dstPort = (ipData[22] & 0xFF) << 8 | (ipData[23] & 0xFF);
                        } else {
                            protocol = Protocol.ANY;
                        }
                    } else if((ipData[0] & 0xF0) == 0x60) { // ipv6
                        if(ipData.length < 40) {
                            return;
                        }
                        srcAddr = InetAddress.getByAddress(new byte[] {ipData[8], ipData[9], ipData[10], ipData[11], ipData[12], ipData[13], ipData[14], ipData[15], ipData[16], ipData[17], ipData[18], ipData[19], ipData[20], ipData[21], ipData[22], ipData[23]});
                        dstAddr = InetAddress.getByAddress(new byte[] {ipData[24], ipData[25], ipData[26], ipData[27], ipData[28], ipData[29], ipData[30], ipData[31], ipData[32], ipData[33], ipData[34], ipData[35], ipData[36], ipData[37], ipData[38], ipData[39]});
                        bytes = ipData.length - 40;
                        if(ipData[6] == 0x06) {
                            if(ipData.length < 44) {
                                return;
                            }
                            protocol = Protocol.TCP;
                            srcPort = (ipData[40] & 0xFF) << 8 | (ipData[41] & 0xFF);
                            dstPort = (ipData[42] & 0xFF) << 8 | (ipData[43] & 0xFF);
                        } else if(ipData[6] == 0x11) {
                            if(ipData.length < 44) {
                                return;
                            }
                            protocol = Protocol.UDP;
                            srcPort = (ipData[40] & 0xFF) << 8 | (ipData[41] & 0xFF);
                            dstPort = (ipData[42] & 0xFF) << 8 | (ipData[43] & 0xFF);
                            bytes -= 8;
                        } else {
                            protocol = Protocol.ANY;
                        }
                    } else {
                        return;
                    }
                    
                    abstraction = AbstractionFactory.getInstance().allocateFromFields(srcAddr, dstAddr, srcPort, dstPort, protocol, bytes, TimeInterval.timeToMicro(handle.getTimestamp()));


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
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(outFile, true));
                    writer.write(abstraction.serializeString() + "\n");
                    writer.close();
                } catch (IOException e) {
                    System.out.println("Couldn't write point to: " + outFile.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        };
        try {
            handle.loop(-1, pl);
        } catch (PcapNativeException | InterruptedException | NotOpenException e) {
            System.out.println("Couldn't loop over pcap file: " + pcapFilePath);
            e.printStackTrace();
        }
        handle.close();
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
                File file1 = new File(Paths.get(firstDirectory.getAbsolutePath()).resolve(conflictFile.getName()).toString());
                BufferedReader reader1 = new BufferedReader(new FileReader(file1));
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

    private static boolean hasFileEnding(File file, String ending) {
        String fileName = file.getName();
        if(fileName.length() <= ending.length() + 1) {
            return false;
        }
        String lastPart = fileName.substring(fileName.length() - ending.length() - 1, fileName.length());
        return lastPart.equals("." + ending);
    }

    private static boolean hasAllowedEnding(File file, String[] allowedEndings) {
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
        if(args.length < 2) {
            System.out.println("Usage: java -jar pcap-converter.jar <pcapFolderPath> <outputPath> [filter]");
            return;
        }
        String pcapFolderPath = args[0];
        String outputPath = args[1];
        String filter = "";
        if(args.length > 2) {
            filter = args[2];
        }
        int numThreads = Runtime.getRuntime().availableProcessors();
        if(args.length > 3) {
            numThreads = Integer.parseInt(args[3]);
        }
        convertPcaps(pcapFolderPath, outputPath, filter, numThreads);
    }
}