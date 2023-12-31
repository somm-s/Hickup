package com.hickup.services;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.packet.Packet;
import com.hickup.points.IPPoint;
import javafx.application.Platform;
import java.io.FileWriter;
import java.io.IOException;


// unused imports
// import java.text.SimpleDateFormat;
// import java.util.Date;
// import org.pcap4j.core.PcapNativeException;
// import org.pcap4j.core.PcapNetworkInterface;
// import org.pcap4j.core.Pcaps;

public class PacketCSVTask extends PacketTask {


    FileWriter fileWriter;

    public PacketCSVTask(final String fileName, final String filter, final String networkInterfaceName, final String receiverIP) {
        super(filter, networkInterfaceName, receiverIP);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        this.fileWriter = fileWriter;
        // csvWriter.writeNext(new String[]{"Time", "Source IP", "Destination IP", "Source Port", "Destination Port", "Protocol", "Payload Length", "Fin", "Syn", "Rst", "Psh", "Ack", "Urg"});
    }

    public void onCancel(PcapHandle handle) {
        try {
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();

            Platform.runLater(() -> {
                System.out.println("Failed");
            });
        }
        handle.close();
    }

    @Override
    public void processPacket(Packet packet, PcapHandle handle, boolean isSent, String ip) {

        // use IPPoint to parse packet and write to the file
        IPPoint p = IPPoint.parsePacket(packet, handle.getTimestamp());

        // write to file
        try {
            fileWriter.write(p.toString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}