package ch.cydcampus.hickup.pipeline.source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.zip.ZipInputStream;

import ch.cydcampus.hickup.pipeline.abstraction.AbstractionFactory;
import ch.cydcampus.hickup.pipeline.abstraction.PacketAbstraction;
import ch.cydcampus.hickup.pipeline.feature.Feature.Protocol;

/**
 * Use files as source for packets. Assumes that the files were produced with the
 * RecursivePcapConversionTask and indexed correctly.
 */
public class FileSource extends DataSource {

    private String filterHost;
    private boolean doFilter;
    private long sizeFilter = 0;
    private File[] processFiles;

    /**
     * Creates a new file source.
     * @param path path to the folder containing the files and the index file
     * @param filterHost only process packets from this host
     */
    public FileSource(String path, String filterHost) {
        this(path, filterHost, "");
    }
    
    /**
     * Creates a new file source.
     * @param path path to the folder containing the files and the index file
     * @param filterHost only process packets from this host
     * @param sizeFilter only process packets with a size greater than this value
     */
    public FileSource(String path, String filterHost, String sizeFilter) {
        this.filterHost = filterHost;
        this.doFilter = filterHost != null && !filterHost.equals("") && filterHost.length() > 3;
        if(sizeFilter != null && !sizeFilter.equals("")) {
            this.sizeFilter = Long.parseLong(sizeFilter);
        }
        File folder = new File(path);
        if(filterHost == null || filterHost.equals("")) {
            File[] listOfFiles = folder.listFiles();
            Arrays.sort(listOfFiles, (o1, o2) -> o1.getName().compareTo(o2.getName()));
            processFiles = listOfFiles;
            return;
        }
        File indexFile = new File(path + "/index.txt");
        if(!indexFile.exists()) {
            System.out.println("Index file not found. Please run FileIndexer first.");
            throw new RuntimeException("Index file not found.");
        }
        String[] parts = null;
        try {
            @SuppressWarnings("resource")
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(indexFile.getAbsolutePath())));
            String line;
            while((line = reader.readLine()) != null) {
                String[] currentParts = line.split(",");
                if(currentParts[0].equals(filterHost)) {
                    parts = currentParts;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(parts == null) {
            System.out.println("Host not found in index file.");
            throw new RuntimeException("Host not found in index file.");
        }
        processFiles = new File[parts.length - 2];
        for(int i = 2; i < parts.length; i++) {
            processFiles[i-2] = new File(path + "/" + parts[i]);
        }
    }

    @Override
    public void start() {
        new Thread(() -> {
            loadFromFiles();
        }).start();
    }

    private void loadFromFiles() {
        for (File file : processFiles) {
            if (file.isFile()) {
                if(file.getName().endsWith(".zip")) {
                    System.out.println("Processing file " + file.getName());
                    readZipFile(file);
                }
                else if(file.getName().endsWith(".csv")) {
                    readTextFile(file);
                }
            }
        }
        this.finish();
    }

    private void processLine(String line) {
        String[] parts = line.split(",");
        String src = parts[0];
        String dst = parts[1];
        if(doFilter && !src.equals(filterHost) && !dst.equals(filterHost)) {
            return;
        }
        int srcPort = Integer.parseInt(parts[2]);
        int dstPort = Integer.parseInt(parts[3]);
        Protocol protocol = Protocol.valueOf(parts[4]);
        long bytes = Long.parseLong(parts[5]);

        if(bytes <= sizeFilter) {
            return;
        }
        long timestamp = Long.parseLong(parts[6]);

        PacketAbstraction packet = null;
        try {
            packet = AbstractionFactory.getInstance().allocateFromFields(InetAddress.getByName(src), InetAddress.getByName(dst), srcPort, dstPort, protocol, bytes, timestamp);
        } catch (UnknownHostException e) {
            return;
        }
        if(packet != null) {
            this.produce(packet);
        }
        if(this.queueLimitReached()) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void readTextFile(File file) {
        try (FileInputStream inputStream = new FileInputStream(file.getAbsolutePath())) {
            if(inputStream.available() == 0) {
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    processLine(line);
                }
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void readZipFile(File zipFile) {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile.getAbsolutePath()))) {
            if(zipInputStream.available() == 0) {
                return;
            }
            zipInputStream.getNextEntry();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    processLine(line);
                }
            }
            zipInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
