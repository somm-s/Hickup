package ch.cydcampus.hickup.pipeline.feature;

import ch.cydcampus.hickup.pipeline.feature.Feature.FeatureType;
import ch.cydcampus.hickup.pipeline.feature.Feature.Protocol;

public class FeatureFactory {
    public static Feature createFeature(FeatureType type, Object value, String name) {
        switch (type) {
            case INT:
                if(value == null) {
                    return new IntFeature(0, name);
                }
                return new IntFeature((int) value, name);
            case IP:
                return new IPFeature((java.net.InetAddress) value, name);
            case PROTOCOL:
                if(value == null) {
                    return new ProtocolFeature(Protocol.ANY, name);
                }
                return new ProtocolFeature((Protocol) value, name);
            case LONG:
                if(value == null) {
                    return new LongFeature(0, name);
                }
                return new LongFeature((long) value, name);
            case STRING:
                if(value == null) {
                    return new StringFeature("", name);
                }
                return new StringFeature((String) value, name);
            default:
                throw new UnsupportedOperationException("Unimplemented method 'createFeature'");
        }
    }
}
