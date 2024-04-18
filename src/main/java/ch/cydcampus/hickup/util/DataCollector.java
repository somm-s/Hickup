package ch.cydcampus.hickup.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeoutException;

import org.pcap4j.core.BpfProgram;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import ch.cydcampus.hickup.pipeline.feature.Feature.Protocol;

public class DataCollector {

    private PcapHandle pcapHandle;
    private BufferedWriter bufferedWriter;
    long lastWriteTime = 0;
    String outputPath = "";
    String fileName = "";
    boolean isRunning = true;

    /**
     * Creates a new network source.
     * @param networkInterface name of the network interface to use
     * @param berkleyPacketFilter filter for packets
     * @throws IOException 
     * @throws Exception 
     */
    public DataCollector(String networkInterface, String outputPath) throws PcapNativeException, NotOpenException, IOException {
        BpfProgram.BpfCompileMode mode = BpfProgram.BpfCompileMode.OPTIMIZE;
        PcapNetworkInterface device = Pcaps.getDevByName(networkInterface);
        this.pcapHandle = device.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);
        pcapHandle.setFilter("", mode);

        this.lastWriteTime = (System.currentTimeMillis() * 1000);
        this.outputPath = outputPath;
        String filePath = outputPath + "/" + TimeInterval.microToTime(lastWriteTime) + ".csv";
        FileWriter fileWriter = new FileWriter(filePath);
        bufferedWriter = new BufferedWriter(fileWriter);
    }

    public void capture() throws PcapNativeException, TimeoutException, NotOpenException, IOException {
        while (isRunning) {
            Packet packet = pcapHandle.getNextPacketEx();
            byte[] rawData = packet.getPayload().getRawData();
            if(rawData.length <= 50) {
                continue;
            }

            byte[] ipData = new byte[rawData.length - 50];

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
                    continue;
                }

                srcAddr = InetAddress.getByAddress(new byte[] {ipData[12], ipData[13], ipData[14], ipData[15]});
                dstAddr = InetAddress.getByAddress(new byte[] {ipData[16], ipData[17], ipData[18], ipData[19]});
                bytes = ipData.length - 20;
                if(ipData[9] == 0x06) {
                    if(ipData.length < 24) {
                        continue;
                    }
                    protocol = Protocol.TCP;
                    srcPort = (ipData[20] & 0xFF) << 8 | (ipData[21] & 0xFF);
                    dstPort = (ipData[22] & 0xFF) << 8 | (ipData[23] & 0xFF);
                } else if(ipData[9] == 0x11) {
                    if(ipData.length < 24) {
                        continue;
                    }
                    protocol = Protocol.UDP;
                    srcPort = (ipData[20] & 0xFF) << 8 | (ipData[21] & 0xFF);
                    dstPort = (ipData[22] & 0xFF) << 8 | (ipData[23] & 0xFF);
                } else {
                    protocol = Protocol.ANY;
                }
            } else if(ipData[0] == 0x60) { // ipv6
                if(ipData.length < 40) {
                    continue;
                }
                srcAddr = InetAddress.getByAddress(new byte[] {ipData[8], ipData[9], ipData[10], ipData[11], ipData[12], ipData[13], ipData[14], ipData[15], ipData[16], ipData[17], ipData[18], ipData[19], ipData[20], ipData[21], ipData[22], ipData[23]});
                dstAddr = InetAddress.getByAddress(new byte[] {ipData[24], ipData[25], ipData[26], ipData[27], ipData[28], ipData[29], ipData[30], ipData[31], ipData[32], ipData[33], ipData[34], ipData[35], ipData[36], ipData[37], ipData[38], ipData[39]});
                bytes = ipData.length - 40;
                if(ipData[6] == 0x06) {
                    if(ipData.length < 44) {
                        continue;
                    }
                    protocol = Protocol.TCP;
                    srcPort = (ipData[40] & 0xFF) << 8 | (ipData[41] & 0xFF);
                    dstPort = (ipData[42] & 0xFF) << 8 | (ipData[43] & 0xFF);
                } else if(ipData[6] == 0x11) {
                    if(ipData.length < 44) {
                        continue;
                    }
                    protocol = Protocol.UDP;
                    srcPort = (ipData[40] & 0xFF) << 8 | (ipData[41] & 0xFF);
                    dstPort = (ipData[42] & 0xFF) << 8 | (ipData[43] & 0xFF);
                } else {
                    protocol = Protocol.ANY;
                }
            } else {
                continue;
            }

            long time = TimeInterval.timeToMicro(pcapHandle.getTimestamp());
            bufferedWriter.write(srcAddr.getHostAddress() + "," + dstAddr.getHostAddress() + "," + srcPort + "," + dstPort + "," + protocol + "," + bytes + "," + time + ",\n");

            if(time - lastWriteTime > 20000000) {
                bufferedWriter.flush();
                bufferedWriter.close();
                lastWriteTime = time;
                fileName = TimeInterval.microToTime(lastWriteTime) + ".csv";
                FileWriter fileWriter = new FileWriter(outputPath + "/" + fileName);
                bufferedWriter = new BufferedWriter(fileWriter);
            }
        }
    }


    public static void main(String[] args) throws PcapNativeException, NotOpenException, IOException, TimeoutException {
        // get the network interface from command line
        String networkInterface = args[0];
        String outputPath = args[1];

        DataCollector collector = new DataCollector(networkInterface, outputPath);
        collector.capture();
    }


}