package ch.cydcampus.hickup.pipeline.feature;

import java.net.InetAddress;

/*
 * Interface for features. Features are the basic building blocks of the
 * pipeline. They are used to store data and to pass data between stages.
 * Features are weakly typed, meaning that they can store any type of data.
 * The type is statically checked at pipeline construction time.
 * All instances of features implement this interface as well as one of the
 * interfaces specifying the general type of the feature.
 */
public interface Feature {

    /*
     * Enumeration of the different protocols.
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

    /*
     * Enumeration of the different types of features.
     */
    public static enum FeatureType {
        IP, PROTOCOL, INT, STRING, BOOLEAN, DOUBLE, LONG, SHORT, BYTE, CHAR, OBJECT, LONG_INTERVAL, INT_INTERVAL, DOUBLE_INTERVAL
    }

    /*
     * Get the name of the feature. The name is set at pipeline construction.
     */
    public String getName();

    /*
     * Get the type of the feature. The type is set at pipeline construction.
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
    
    /*
     * Used for features using the MAX or MIN aggregation strategy.
     */
    public int compareTo(Feature otherFeature);

    /*
     * Equality test for abstraction combination rules.
     */
    public boolean equals(Feature otherFeature);

    /*
     * Each feature must be able to clone itself to a feature with the same type.
     */
    public void cloneTo(Feature otherFeature);

    /*
     * Used for multiplexing according to the value of this feature.
     */
    public String toString();
}
