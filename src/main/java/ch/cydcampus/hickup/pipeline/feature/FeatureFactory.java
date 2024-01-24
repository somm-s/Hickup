package ch.cydcampus.hickup.pipeline.feature;

import ch.cydcampus.hickup.pipeline.feature.Feature.FeatureType;
import ch.cydcampus.hickup.pipeline.feature.Feature.Protocol;

/**
 * Factory for creating features. Initializes the features with the correct initial values.
 */
public class FeatureFactory {

    /**
     * Create a feature of the given type with the given value and name.
     * @param type The type of the feature.
     * @param value The value of the feature. Can be null to initialize with default value.
     * @param name The name of the feature.
     * @return The created feature.
     */
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
