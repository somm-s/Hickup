package ch.cydcampus.hickup.pipeline.source;

import java.io.EOFException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import org.pcap4j.core.BpfProgram;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.abstraction.AbstractionFactory;
public class NetworkSource extends DataSource {

    private PcapHandle pcapHandle;
    boolean isRunning = true;

    public NetworkSource(String networkInterface, String berkleyPacketFilter) throws PcapNativeException, NotOpenException {
        BpfProgram.BpfCompileMode mode = BpfProgram.BpfCompileMode.OPTIMIZE;
        PcapNetworkInterface device = Pcaps.getDevByName(networkInterface);
        this.pcapHandle = device.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);
        pcapHandle.setFilter(berkleyPacketFilter, mode);
    }

    public void capture() {
        try {
            while (isRunning) {
                Packet packet = pcapHandle.getNextPacketEx();
                Abstraction packetAbstraction = AbstractionFactory.getInstance().allocateFromNetwork(packet, pcapHandle.getTimestamp());
                if (packetAbstraction != null) {
                    this.produce(packetAbstraction);
                }
            }
        } catch (PcapNativeException | NotOpenException | EOFException | TimeoutException | UnknownHostException e) {
            e.printStackTrace();
        } finally {
            pcapHandle.close();
        }

    }

    @Override
    public void start() {
        // start thread that runs capture
        Thread t = new Thread(() -> {
            capture();
        });
        t.start();
    }
    
}