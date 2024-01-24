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

/**
 * This class provides a factory for creating abstractions.
 */
public class AbstractionFactory {

    private static AbstractionFactory instance = null;
    private AbstractionFactory() {}

    /**
     * Get the singleton instance of the abstraction factory.
     * @return The singleton instance of the abstraction factory.
     */
    public static AbstractionFactory getInstance() {
        if(instance == null) {
            instance = new AbstractionFactory();
        }
        return instance;
    }

    /**
     * Create a new HighOrderAbstraction from a lower order abstraction. The lower level abstraction specifies the
     * level of the new abstraction. Furthermore, the level specifies which features are copied from the lower level
     * by the feature copy rules.
     * @param lowerAbstraction
     * @return The new abstraction.
     */
    public Abstraction createHighOrderAbstraction(Abstraction lowerAbstraction) {
        int level = lowerAbstraction.getLevel() + 1;
        HighOrderAbstraction highOrderAbstraction = new HighOrderAbstraction(level);
        Feature[] features = new Feature[PipelineConfig.FEATURE_NAMES[level].length];

        for(int i = 0; i < PipelineConfig.FEATURE_NAMES[level].length; i++) {
            features[i] = FeatureFactory.createFeature(PipelineConfig.FEATURE_TYPES[level][i], null, PipelineConfig.FEATURE_NAMES[level][i]);
        }
        highOrderAbstraction.addFeatures(features);

        for(FeatureCopyRule rule : PipelineConfig.FEATURE_COPY_RULES[level]) {
            rule.copy(lowerAbstraction, highOrderAbstraction);
        }
        return highOrderAbstraction;
    }

    /**
     * Allocate a packet abstraction and populate the features array with the correct values. It uses a network packet to
     * extract the values for the features. The timestamp is used to set the update times.
     * @param networkPacket pcap4j packet to allocate the token from. Returns null if the packet does not contain an IP packet.
     * @param timestamp Timestamp of the packet.
     */
    public PacketAbstraction allocateFromNetwork(org.pcap4j.packet.Packet networkPacket, Timestamp timestamp) throws UnknownHostException {
        if(!networkPacket.contains(IpPacket.class)) {
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

    /**
     * Allocate a packet abstraction and populate the features array with the correct values. Additional features that are extracted
     * in the pipeline for this level are added and initialized with null.
     * @param srcAddr Source IP address.
     * @param dstAddr Destination IP address.
     * @param srcPort Source port.
     * @param dstPort Destination port.
     * @param protocol Protocol.
     * @param bytes Number of bytes.
     * @param time Timestamp.
     */
    public PacketAbstraction allocateFromFields(InetAddress srcAddr, InetAddress dstAddr, int srcPort, int dstPort, Protocol protocol, long bytes, long time) throws UnknownHostException {
        Feature[] features = new Feature[PipelineConfig.LEVEL_0_FEATURES.length];

        features[PipelineConfig.SRC_IP_INDEX] = FeatureFactory.createFeature(
            PipelineConfig.LEVEL_0_FEATURE_TYPES[PipelineConfig.SRC_IP_INDEX], srcAddr, PipelineConfig.LEVEL_0_FEATURES[PipelineConfig.SRC_IP_INDEX]);
        features[PipelineConfig.DST_IP_INDEX] = FeatureFactory.createFeature(
            PipelineConfig.LEVEL_0_FEATURE_TYPES[PipelineConfig.DST_IP_INDEX], dstAddr, PipelineConfig.LEVEL_0_FEATURES[PipelineConfig.DST_IP_INDEX]);
        features[PipelineConfig.SRC_PORT_INDEX] = FeatureFactory.createFeature(
            PipelineConfig.LEVEL_0_FEATURE_TYPES[PipelineConfig.SRC_PORT_INDEX], srcPort, PipelineConfig.LEVEL_0_FEATURES[PipelineConfig.SRC_PORT_INDEX]);
        features[PipelineConfig.DST_PORT_INDEX] = FeatureFactory.createFeature(
            PipelineConfig.LEVEL_0_FEATURE_TYPES[PipelineConfig.DST_PORT_INDEX], dstPort, PipelineConfig.LEVEL_0_FEATURES[PipelineConfig.DST_PORT_INDEX]);
        features[PipelineConfig.PROTOCOL_INDEX] = FeatureFactory.createFeature(
            PipelineConfig.LEVEL_0_FEATURE_TYPES[PipelineConfig.PROTOCOL_INDEX], protocol, PipelineConfig.LEVEL_0_FEATURES[PipelineConfig.PROTOCOL_INDEX]);
        features[PipelineConfig.BYTES_INDEX] = FeatureFactory.createFeature(
            PipelineConfig.LEVEL_0_FEATURE_TYPES[PipelineConfig.BYTES_INDEX], bytes, PipelineConfig.LEVEL_0_FEATURES[PipelineConfig.BYTES_INDEX]);
        features[PipelineConfig.TIME_INDEX] = FeatureFactory.createFeature(
            PipelineConfig.LEVEL_0_FEATURE_TYPES[PipelineConfig.TIME_INDEX], time, PipelineConfig.LEVEL_0_FEATURES[PipelineConfig.TIME_INDEX]);

        // Add the remaining features with their statically known types and names.
        for(int i = 7; i < PipelineConfig.LEVEL_0_FEATURES.length; i++) {
            features[i] = FeatureFactory.createFeature(PipelineConfig.LEVEL_0_FEATURE_TYPES[i], null, PipelineConfig.LEVEL_0_FEATURES[i]);
        }

        PacketAbstraction packetAbstraction = new PacketAbstraction();
        packetAbstraction.addFeatures(features);
        return packetAbstraction;
    }
}
