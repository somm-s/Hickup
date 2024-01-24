package ch.cydcampus.hickup.pipeline.feature;

import java.net.InetAddress;

/**
 * Interface for features. Features are the basic building blocks of the
 * pipeline. They are used to store data and to pass data between stages.
 * Features can store any type of data with the same interface.
 * The type is statically determined at pipeline construction time.
 */
public interface Feature {

    /**
     * Enumeration of the different protocol types.
     */
    public static enum Protocol {
        TCP, UDP, ANY;
        public static Protocol fromInt(int parseInt) {
            switch (parseInt) {
                case 0:
                    return TCP;
                case 1:
                    return UDP;
                default:
                    return ANY;
            }
        }

        public static String toString(Protocol protocol) {
            switch (protocol) {
                case TCP:
                    return "TCP";
                case UDP:
                    return "UDP";
                default:
                    return "ANY";
            }
        }
    }

    /**
     * Enumeration of the different types of features.
     */
    public static enum FeatureType {
        IP, PROTOCOL, INT, STRING, BOOLEAN, DOUBLE, LONG, SHORT, BYTE, CHAR, OBJECT, LONG_INTERVAL, INT_INTERVAL, DOUBLE_INTERVAL
    }

    /**
     * Get the name of the feature. The name is set at pipeline construction.
     * @return The name of the feature.
     */
    public String getName();

    /**
     * Get the type of the feature. The type is set at pipeline construction.
     * @return The type of the feature.
     */
    public FeatureType getType();

    /*
     * Get and set the value of the feature. The type of the value is
     * determined by the instance of the feature.
     */
    public InetAddress asIP();
    public void set(InetAddress value);
    public Protocol asProtocol();
    public void set(Protocol value);
    public int asInt();
    public void set(int value);
    public String asString();
    public void set(String value);
    public boolean asBoolean();
    public void set(boolean value);
    public double asDouble();
    public void set(double value);
    public float asFloat();
    public void set(float value);
    public long asLong();
    public void set(long value);
    public short asShort();
    public void set(short value);
    public byte asByte();
    public void set(byte value);
    public char asChar();
    public void set(char value);
    public Object asObject();
    public void set(Object value);
    
    /**
     * Used for features using the MAX or MIN aggregation strategy.
     * @param otherFeature The feature to compare to.
     * @return 1 if this feature is greater than the other feature, -1 if this feature is smaller 
     * than the other feature, 0 if they are equal.
     */
    public int compareTo(Feature otherFeature);

    /**
     * Equality test for abstraction combination rules.
     * @param otherFeature The feature to compare to.
     * @return True if the features are equal, false otherwise.
     */
    public boolean equals(Feature otherFeature);

    /**
     * Each feature must be able to clone itself to a feature with the same type.
     * @param otherFeature The feature to clone to.
     * @return The cloned feature.
     */
    public void cloneTo(Feature otherFeature);

    /**
     * Each feature must be able to represent its value as a string to be uesd as a key in a map.
     * @return The string representation of the feature.
     */
    public String toString();
}
