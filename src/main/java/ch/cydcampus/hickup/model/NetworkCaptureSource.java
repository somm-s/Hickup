package ch.cydcampus.hickup.model;

import java.io.EOFException;
import java.util.concurrent.TimeoutException;

import org.pcap4j.core.BpfProgram;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import ch.cydcampus.hickup.util.MultipleReaderRingBuffer;

public class NetworkCaptureSource extends Thread implements DataSource {
    
    public static final int BUFFER_SIZE = 1000;
    private PcapHandle pcapHandle;
    private MultipleReaderRingBuffer buffer;
    private volatile boolean isRunning = true;

    public NetworkCaptureSource(String networkInterface, String berkleyPacketFilter) throws PcapNativeException, NotOpenException {
        BpfProgram.BpfCompileMode mode = BpfProgram.BpfCompileMode.OPTIMIZE;
        PcapNetworkInterface device = Pcaps.getDevByName(networkInterface);
        this.pcapHandle = device.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);
        pcapHandle.setFilter(berkleyPacketFilter, mode);
        this.buffer = new MultipleReaderRingBuffer(BUFFER_SIZE);
        this.start();
    }

    @Override
    public void run() {
        System.out.println("NetworkCaptureThread started");
        try {
            while (isRunning) {
                Packet packet = pcapHandle.getNextPacketEx();
                Token ipPacket = TokenPool.getPool().allocateFromPacket(packet, pcapHandle.getTimestamp());
                if (ipPacket != null) {
                    if(buffer.produce(ipPacket)) {
                        System.out.println("Buffer is full, overwriting!");
                    }
                }
            }
        } catch (PcapNativeException | NotOpenException | EOFException | TimeoutException e) {
            e.printStackTrace();
        } finally {
            pcapHandle.close();
        }
    }
    
    public void stopThread() {
        isRunning = false;
    }

    @Override
    public void registerReader() {
        buffer.registerReader();
    }

    @Override
    public Token consume() throws InterruptedException {
        if(!isRunning) {
            throw new InterruptedException("Consuming even though is not running");
        }
        return (Token) buffer.consume();
    }

    @Override
    public void stopProducer() {
        stopThread();
    }

    public Token peek() {
        return (Token) buffer.peek();
    }
}
