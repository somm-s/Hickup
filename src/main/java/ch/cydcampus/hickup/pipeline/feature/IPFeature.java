package ch.cydcampus.hickup.pipeline.feature;

import java.net.InetAddress;

/**
 * IPFeature is a feature that stores an IP address.
 */
public class IPFeature implements Feature {

    InetAddress value;
    String name;

    public IPFeature(InetAddress value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public InetAddress asIP() {
        return value;
    }

    @Override
    public void set(InetAddress value) {
        this.value = value;
    }

    @Override
    public int compareTo(Feature otherFeature) {
        return value.hashCode() - otherFeature.asIP().hashCode();
    }

    @Override
    public FeatureType getType() {
        return FeatureType.IP;
    }

    @Override
    public String toString() {
        return value.getHostAddress();
    }

    @Override
    public void cloneTo(Feature otherFeature) {
        otherFeature.set(value);
    }

    @Override
    public boolean equals(Feature otherFeature) {
        return value.equals(otherFeature.asIP());
    }
    
    @Override
    public String asString() {
        throw new RuntimeException("Type mismatch: IPFeature.asString()");
    }

    @Override
    public void set(String value) {
        throw new RuntimeException("Type mismatch: IPFeature.set(String)");
    }
    
    @Override
    public boolean asBoolean() {
        throw new RuntimeException("Type mismatch: IPFeature.asBoolean()");
    }

    @Override
    public void set(boolean value) {
        throw new RuntimeException("Type mismatch: IPFeature.set(boolean)");
    }

    @Override
    public double asDouble() {
        throw new RuntimeException("Type mismatch: IPFeature.asDouble()");
    }

    @Override
    public void set(double value) {
        throw new RuntimeException("Type mismatch: IPFeature.set(double)");
    }

    @Override
    public float asFloat() {
        throw new RuntimeException("Type mismatch: IPFeature.asFloat()");
    }

    @Override
    public void set(float value) {
        throw new RuntimeException("Type mismatch: IPFeature.set(float)");
    }
    
    @Override
    public int asInt() {
        throw new RuntimeException("Type mismatch: IPFeature.asInt()");
    }

    @Override
    public void set(int value) {
        throw new RuntimeException("Type mismatch: IPFeature.set(int)");
    }

    @Override
    public long asLong() {
        throw new RuntimeException("Type mismatch: IPFeature.asLong()");
    }

    @Override
    public void set(long value) {
        throw new RuntimeException("Type mismatch: IPFeature.set(long)");
    }

    @Override
    public short asShort() {
        throw new RuntimeException("Type mismatch: IPFeature.asShort()");
    }

    @Override
    public void set(short value) {
        throw new RuntimeException("Type mismatch: IPFeature.set(short)");
    }

    @Override
    public byte asByte() {
        throw new RuntimeException("Type mismatch: IPFeature.asByte()");
    }

    @Override
    public void set(byte value) {
        throw new RuntimeException("Type mismatch: IPFeature.set(byte)");
    }

    @Override
    public char asChar() {
        throw new RuntimeException("Type mismatch: IPFeature.asChar()");
    }

    @Override
    public void set(char value) {
        throw new RuntimeException("Type mismatch: IPFeature.set(char)");
    }

    @Override
    public Object asObject() {
        throw new RuntimeException("Type mismatch: IPFeature.asObject()");
    }

    @Override
    public void set(Object value) {
        throw new RuntimeException("Type mismatch: IPFeature.set(Object)");
    }

    @Override
    public void set(Protocol value) {
        throw new RuntimeException("Type mismatch: IPFeature.set(Protocol)");
    }

    @Override
    public Protocol asProtocol() {
        throw new RuntimeException("Type mismatch: IPFeature.asProtocol()");
    }

}
