package ch.cydcampus.hickup.pipeline.abstraction;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;

import org.pcap4j.packet.IpPacket;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

import ch.cydcampus.hickup.pipeline.PipelineConfig;
import ch.cydcampus.hickup.pipeline.feature.Feature.Protocol;
import ch.cydcampus.hickup.pipeline.feature.copyrules.FeatureCopyRule;
import ch.cydcampus.hickup.pipeline.feature.Feature;
import ch.cydcampus.hickup.pipeline.feature.FeatureFactory;
import ch.cydcampus.hickup.util.TimeInterval;

public class AbstractionFactory {

    private static AbstractionFactory instance = null;

    private AbstractionFactory() {
    }

    public static AbstractionFactory getInstance() {
        if(instance == null) {
            instance = new AbstractionFactory();
        }
        return instance;
    }

    /* Level is the level of the abstraction that is added, i.e. of the lower level abstraction */
    public Abstraction createHighOrderAbstraction(int level, Abstraction lowerAbstraction) {
        assert level == lowerAbstraction.getLevel() + 1;
        assert level > 0;
        HighOrderAbstraction highOrderAbstraction = new HighOrderAbstraction(level);
        Feature[] features = new Feature[PipelineConfig.FEATURE_NAMES[level].length];

        // initialize all
        for(int i = 0; i < PipelineConfig.FEATURE_NAMES[level].length; i++) {
            features[i] = FeatureFactory.createFeature(PipelineConfig.FEATURE_TYPES[level][i], null, PipelineConfig.FEATURE_NAMES[level][i]);
        }
        highOrderAbstraction.addFeatures(features);

        for(FeatureCopyRule rule : PipelineConfig.FEATURE_COPY_RULES[level]) {
            rule.copy(lowerAbstraction, highOrderAbstraction);
        }

        return highOrderAbstraction;
    }

    /*
     * Allocate a sequential token and populate it with the data from the network packet.
     * 
     * @param packet The packet to allocate the token from. Returns null if the packet does not contain an IP packet.
     */
    public PacketAbstraction allocateFromNetwork(org.pcap4j.packet.Packet networkPacket, Timestamp timestamp) throws UnknownHostException {
        if(!networkPacket.contains(IpPacket.class)) {
            System.out.println("Packet does not contain IP packet");
            return null;
        }

        InetAddress srcAddr = networkPacket.get(IpPacket.class).getHeader().getSrcAddr();
        InetAddress dstAddr  = networkPacket.get(IpPacket.class).getHeader().getDstAddr();
        Protocol protocol = Protocol.ANY;
        long bytes = networkPacket.length();
        int srcPort = 0;
        int dstPort = 0;
        long time = TimeInterval.timeToMicro(timestamp);

        if(networkPacket.contains(TcpPacket.class)) {
            TcpPacket tcpPacket = networkPacket.get(TcpPacket.class);
            srcPort = tcpPacket.getHeader().getSrcPort().valueAsInt();
            dstPort = tcpPacket.getHeader().getDstPort().valueAsInt();
            if(tcpPacket.getPayload() != null) {
                bytes = tcpPacket.getPayload().length();
            }
            protocol = Protocol.TCP;
        } else if(networkPacket.contains(UdpPacket.class)) {
            UdpPacket udpPacket = networkPacket.get(UdpPacket.class);
            if(udpPacket.getPayload() != null) {
                bytes = udpPacket.getPayload().length();
            }
            srcPort = udpPacket.getHeader().getSrcPort().valueAsInt();
            dstPort = udpPacket.getHeader().getDstPort().valueAsInt();
            protocol = Protocol.UDP;
        }
        return allocateFromFields(srcAddr, dstAddr, srcPort, dstPort, protocol, bytes, time);
    }

    public PacketAbstraction allocateFromFields(InetAddress srcAddr, InetAddress dstAddr, int srcPort, int dstPort, Protocol protocol, long bytes, long time) throws UnknownHostException {
        Feature[] features = new Feature[PipelineConfig.LEVEL_0_FEATURES.length];

        features[PipelineConfig.SRC_IP_INDEX] = FeatureFactory.createFeature(PipelineConfig.LEVEL_0_FEATURE_TYPES[0], srcAddr, PipelineConfig.LEVEL_0_FEATURES[0]);
        features[PipelineConfig.DST_IP_INDEX] = FeatureFactory.createFeature(PipelineConfig.LEVEL_0_FEATURE_TYPES[1], dstAddr, PipelineConfig.LEVEL_0_FEATURES[1]);
        features[PipelineConfig.SRC_PORT_INDEX] = FeatureFactory.createFeature(PipelineConfig.LEVEL_0_FEATURE_TYPES[2], srcPort, PipelineConfig.LEVEL_0_FEATURES[2]);
        features[PipelineConfig.DST_PORT_INDEX] = FeatureFactory.createFeature(PipelineConfig.LEVEL_0_FEATURE_TYPES[3], dstPort, PipelineConfig.LEVEL_0_FEATURES[3]);
        features[PipelineConfig.PROTOCOL_INDEX] = FeatureFactory.createFeature(PipelineConfig.LEVEL_0_FEATURE_TYPES[4], protocol, PipelineConfig.LEVEL_0_FEATURES[4]);
        features[PipelineConfig.BYTES_INDEX] = FeatureFactory.createFeature(PipelineConfig.LEVEL_0_FEATURE_TYPES[5], bytes, PipelineConfig.LEVEL_0_FEATURES[5]);
        features[PipelineConfig.TIME_INDEX] = FeatureFactory.createFeature(PipelineConfig.LEVEL_0_FEATURE_TYPES[6], time, PipelineConfig.LEVEL_0_FEATURES[6]);

        // Add the rest of the features
        for(int i = 7; i < PipelineConfig.LEVEL_0_FEATURES.length; i++) {
            features[i] = FeatureFactory.createFeature(PipelineConfig.LEVEL_0_FEATURE_TYPES[i], null, PipelineConfig.LEVEL_0_FEATURES[i]);
        }

        PacketAbstraction packetAbstraction = new PacketAbstraction();
        packetAbstraction.addFeatures(features);
        return packetAbstraction;

    }

}
