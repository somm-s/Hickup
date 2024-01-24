package ch.cydcampus.hickup.pipeline.feature;

import java.net.InetAddress;

/**
 * IntFeature is a feature that stores an integer value.
 */
public class IntFeature implements Feature {

    int value;
    String name;

    public IntFeature(int value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int asInt() {
        return value;
    }

    @Override
    public void set(int value) {
        this.value = value;
    }

    @Override
    public int compareTo(Feature otherFeature) {
        return value - otherFeature.asInt();
    }

    @Override
    public FeatureType getType() {
        return FeatureType.INT;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public void cloneTo(Feature otherFeature) {
        otherFeature.set(value);
    }

    @Override
    public boolean equals(Feature otherFeature) {
        return value == otherFeature.asInt();
    }

    @Override
    public String asString() {
        throw new RuntimeException("Type mismatch: IntFeature.asString()");
    }

    @Override
    public void set(String value) {
        throw new RuntimeException("Type mismatch: IntFeature.set(String)");
    }

    @Override
    public boolean asBoolean() {
        throw new RuntimeException("Type mismatch: IntFeature.asBoolean()");
    }

    @Override
    public void set(boolean value) {
        throw new RuntimeException("Type mismatch: IntFeature.set(boolean)");
    }

    @Override
    public double asDouble() {
        throw new RuntimeException("Type mismatch: IntFeature.asDouble()");
    }

    @Override
    public void set(double value) {
        throw new RuntimeException("Type mismatch: IntFeature.set(double)");
    }

    @Override
    public float asFloat() {
        throw new RuntimeException("Type mismatch: IntFeature.asFloat()");
    }

    @Override
    public void set(float value) {
        throw new RuntimeException("Type mismatch: IntFeature.set(float)");
    }

    @Override
    public long asLong() {
        throw new RuntimeException("Type mismatch: IntFeature.asLong()");
    }

    @Override
    public void set(long value) {
        throw new RuntimeException("Type mismatch: IntFeature.set(long)");
    }

    @Override
    public short asShort() {
        throw new RuntimeException("Type mismatch: IntFeature.asShort()");
    }

    @Override
    public void set(short value) {
        throw new RuntimeException("Type mismatch: IntFeature.set(short)");
    }

    @Override
    public byte asByte() {
        throw new RuntimeException("Type mismatch: IntFeature.asByte()");
    }

    @Override
    public void set(byte value) {
        throw new RuntimeException("Type mismatch: IntFeature.set(byte)");
    }

    @Override
    public char asChar() {
        throw new RuntimeException("Type mismatch: IntFeature.asChar()");
    }

    @Override
    public void set(char value) {
        throw new RuntimeException("Type mismatch: IntFeature.set(char)");
    }

    @Override
    public Object asObject() {
        throw new RuntimeException("Type mismatch: IntFeature.asObject()");
    }

    @Override
    public void set(Object value) {
        throw new RuntimeException("Type mismatch: IntFeature.set(Object)");
    }

    @Override
    public InetAddress asIP() {
        throw new RuntimeException("Type mismatch: IntFeature.asIP()");
    }

    @Override
    public void set(InetAddress value) {
        throw new RuntimeException("Type mismatch: IntFeature.set(InetAddress)");
    }

    @Override
    public Protocol asProtocol() {
        throw new RuntimeException("Type mismatch: IntFeature.asProtocol()");
    }

    @Override
    public void set(Protocol value) {
        throw new RuntimeException("Type mismatch: IntFeature.set(Protocol)");
    }
    
}
