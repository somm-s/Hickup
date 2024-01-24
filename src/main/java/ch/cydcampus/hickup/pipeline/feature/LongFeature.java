package ch.cydcampus.hickup.pipeline.feature;

import java.net.InetAddress;

/**
 * LongFeature is a feature that stores a long value.
 */
public class LongFeature implements Feature {

    long value;
    String name;

    public LongFeature(long value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long asLong() {
        return value;
    }

    @Override
    public void set(long value) {
        this.value = value;
    }

    @Override
    public int compareTo(Feature otherFeature) {
        return (int) (value - otherFeature.asLong());
    }

    @Override
    public FeatureType getType() {
        return FeatureType.LONG;
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }

    @Override
    public void cloneTo(Feature otherFeature) {
        otherFeature.set(value);
    }

    @Override
    public boolean equals(Feature otherFeature) {
        return value == otherFeature.asLong();
    }

    @Override
    public String asString() {
        throw new RuntimeException("Type mismatch: LongFeature.asString()");
    }

    @Override
    public InetAddress asIP() {
        throw new RuntimeException("Type mismatch: LongFeature.asIP()");
    }

    @Override
    public void set(InetAddress value) {
        throw new RuntimeException("Type mismatch: LongFeature.set(InetAddress)");
    }

    @Override
    public void set(String value) {
        throw new RuntimeException("Type mismatch: LongFeature.set(String)");
    }

    @Override
    public Protocol asProtocol() {
        throw new RuntimeException("Type mismatch: LongFeature.asProtocol()");
    }

    @Override
    public void set(Protocol value) {
        throw new RuntimeException("Type mismatch: LongFeature.set(Protocol)");
    }

    @Override
    public int asInt() {
        throw new RuntimeException("Type mismatch: LongFeature.asInt()");
    }

    @Override
    public void set(int value) {
        throw new RuntimeException("Type mismatch: LongFeature.set(int)");
    }

    @Override
    public boolean asBoolean() {
        throw new RuntimeException("Type mismatch: LongFeature.asBoolean()");
    }

    @Override
    public void set(boolean value) {
        throw new RuntimeException("Type mismatch: LongFeature.set(boolean)");
    }

    @Override
    public double asDouble() {
        throw new RuntimeException("Type mismatch: LongFeature.asDouble()");
    }

    @Override
    public void set(double value) {
        throw new RuntimeException("Type mismatch: LongFeature.set(double)");
    }

    @Override
    public float asFloat() {
        throw new RuntimeException("Type mismatch: LongFeature.asFloat()");
    }

    @Override
    public void set(float value) {
        throw new RuntimeException("Type mismatch: LongFeature.set(float)");
    }

    @Override
    public short asShort() {
        throw new RuntimeException("Type mismatch: LongFeature.asShort()");
    }

    @Override
    public void set(short value) {
        throw new RuntimeException("Type mismatch: LongFeature.set(short)");
    }

    @Override
    public byte asByte() {
        throw new RuntimeException("Type mismatch: LongFeature.asByte()");
    }

    @Override
    public void set(byte value) {
        throw new RuntimeException("Type mismatch: LongFeature.set(byte)");
    }

    @Override
    public char asChar() {
        throw new RuntimeException("Type mismatch: LongFeature.asChar()");
    }

    @Override
    public void set(char value) {
        throw new RuntimeException("Type mismatch: LongFeature.set(char)");
    }

    @Override
    public Object asObject() {
        throw new RuntimeException("Type mismatch: LongFeature.asObject()");
    }

    @Override
    public void set(Object value) {
        throw new RuntimeException("Type mismatch: LongFeature.set(Object)");
    }
}
