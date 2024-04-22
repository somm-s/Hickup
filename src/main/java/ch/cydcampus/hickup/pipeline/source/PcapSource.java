package ch.cydcampus.hickup.pipeline.source;

import java.io.EOFException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import org.pcap4j.core.BpfProgram;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.IpPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.factory.PacketFactories;
import org.pcap4j.packet.factory.PacketFactory;
import org.pcap4j.packet.namednumber.IpNumber;
import org.pcap4j.packet.namednumber.PppDllProtocol;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.abstraction.AbstractionFactory;
import ch.cydcampus.hickup.pipeline.feature.Feature.Protocol;
import ch.cydcampus.hickup.util.PcapDecompressor;
import ch.cydcampus.hickup.util.TimeInterval;

/**
 * Use network interface as source for packets.
 */
public class PcapSource extends DataSource {

    private PcapHandle pcapHandle;
    boolean isRunning = true;

    /**
     * Creates a new network source.
     * @param networkInterface name of the network interface to use
     * @param berkleyPacketFilter filter for packets
     * @throws Exception 
     */
    public PcapSource(String networkInterface, String berkleyPacketFilter) throws PcapNativeException, NotOpenException {
        BpfProgram.BpfCompileMode mode = BpfProgram.BpfCompileMode.OPTIMIZE;
        PcapNetworkInterface device = Pcaps.getDevByName(networkInterface);
        this.pcapHandle = device.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);
        pcapHandle.setFilter(berkleyPacketFilter, mode);
    }

    @Override
    public void start() {
        // start thread that runs capture
        Thread t = new Thread(() -> {
            capture();
        });
        t.start();
    }

    private void capture() {
        while (isRunning) {
            try {
                Packet packet = pcapHandle.getNextPacketEx();
                byte[] rawData = packet.getPayload().getRawData();
                if(rawData.length <= 50) {
                    continue;
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
                } else if((ipData[0] & 0xF0) == 0x60) { // ipv6
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

                if(bytes < 100 || bytes > 1000000) {
                    continue;
                }
                
                Abstraction packetAbstraction = AbstractionFactory.getInstance().allocateFromFields(srcAddr, dstAddr, srcPort, dstPort, protocol, bytes, TimeInterval.timeToMicro(pcapHandle.getTimestamp()));
                this.produce(packetAbstraction);

            } catch (Exception e) {
                System.out.println("Skipping due to exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
        pcapHandle.close();
    }


}