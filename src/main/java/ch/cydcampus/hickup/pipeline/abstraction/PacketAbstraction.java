package ch.cydcampus.hickup.pipeline.abstraction;

import java.util.List;

import ch.cydcampus.hickup.pipeline.PipelineConfig;
import ch.cydcampus.hickup.pipeline.feature.Feature;

/**
 * This class provides an implementation of the Abstraction interface for packet abstractions.
 * A packet abstraction contains the features of a single packet.
 */
public class PacketAbstraction implements Abstraction {

    Feature[] features;
    public static final Abstraction FINISH_PACKET = new FinishPacketAbstraction();

    /**
     * Serialize the features of this abstraction to a string. This is used to write the abstraction to a file.
     * @return The serialized string.
     */
    public String serializeString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < PipelineConfig.NUM_DEFAULT_FEATURES; i++) {
            sb.append(features[i].toString());
            sb.append(",");
        }
        return sb.toString();
    }

    @Override
    public long getLastUpdateTime() {
        return features[PipelineConfig.TIME_INDEX].asLong();
    }

    @Override
    public void setLastUpdateTime(long time) {
        throw new RuntimeException("Cannot change update time of packet abstraction");
    }

    @Override
    public long getFirstUpdateTime() {
        return features[PipelineConfig.TIME_INDEX].asLong();
    }

    @Override
    public boolean isSealed() {
        return true;
    }

    @Override
    public void seal() {
        // do nothing
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public List<Abstraction> getChildren() {
        throw new UnsupportedOperationException("Unimplemented method 'getChildren'");
    }

    @Override
    public void addChild(Abstraction abstraction) {
        throw new UnsupportedOperationException("Unimplemented method 'addChild'");
    }

    @Override
    public void addFeatures(Feature[] features) {
        this.features = features;
    }

    @Override
    public Feature[] getFeatures() {
        return this.features;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PacketAbstraction: \n");
        for(Feature feature : features) {
            sb.append(feature.getName() + ": " + feature.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public Feature getFeature(int index) {
        return features[index];
    }
}