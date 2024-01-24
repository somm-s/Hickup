package ch.cydcampus.hickup.pipeline.feature;

import java.net.InetAddress;

/**
 * ProtocolFeature is a feature that stores a protocol value.
 */
public class ProtocolFeature implements Feature {

    Protocol value;
    String name;

    public ProtocolFeature(Protocol value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Protocol asProtocol() {
        return value;
    }

    @Override
    public void set(Protocol value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public FeatureType getType() {
        return FeatureType.PROTOCOL;
    }

    @Override
    public void cloneTo(Feature otherFeature) {
        otherFeature.set(value);
    }

    @Override
    public boolean equals(Feature otherFeature) {
        return value == otherFeature.asProtocol();
    }

    @Override
    public String asString() {
        throw new RuntimeException("Type mismatch: ProtocolFeature.asString()");
    }

    @Override
    public int compareTo(Feature otherFeature) {
        return value.compareTo(otherFeature.asProtocol());
    }

    @Override
    public int asInt() {
        throw new RuntimeException("Type mismatch: ProtocolFeature.asInt()");
    }

    @Override
    public void set(int value) {
        throw new RuntimeException("Type mismatch: ProtocolFeature.set(int)");
    }

    @Override
    public long asLong() {
        throw new RuntimeException("Type mismatch: ProtocolFeature.asLong()");
    }

    @Override
    public void set(long value) {
        throw new RuntimeException("Type mismatch: ProtocolFeature.set(long)");
    }

    @Override
    public InetAddress asIP() {
        throw new RuntimeException("Type mismatch: ProtocolFeature.asIP()");
    }

    @Override
    public void set(InetAddress value) {
        throw new RuntimeException("Type mismatch: ProtocolFeature.set(InetAddress)");
    }

    @Override
    public void set(String value) {
        throw new RuntimeException("Type mismatch: ProtocolFeature.set(String)");
    }

    @Override
    public boolean asBoolean() {
        throw new RuntimeException("Type mismatch: ProtocolFeature.asBoolean()");
    }

    @Override
    public void set(boolean value) {
        throw new RuntimeException("Type mismatch: ProtocolFeature.set(boolean)");
    }

    @Override
    public double asDouble() {
        throw new RuntimeException("Type mismatch: ProtocolFeature.asDouble()");
    }

    @Override
    public void set(double value) {
        throw new RuntimeException("Type mismatch: ProtocolFeature.set(double)");
    }

    @Override
    public float asFloat() {
        throw new RuntimeException("Type mismatch: ProtocolFeature.asFloat()");
    }

    @Override
    public void set(float value) {
        throw new RuntimeException("Type mismatch: ProtocolFeature.set(float)");
    }

    @Override
    public short asShort() {
        throw new RuntimeException("Type mismatch: ProtocolFeature.asShort()");
    }

    @Override
    public void set(short value) {
        throw new RuntimeException("Type mismatch: ProtocolFeature.set(short)");
    }

    @Override
    public void set(byte value) {
        throw new RuntimeException("Type mismatch: ProtocolFeature.set(byte)");
    }

    @Override
    public byte asByte() {
        throw new RuntimeException("Type mismatch: ProtocolFeature.asByte()");
    }

    @Override
    public void set(char value) {
        throw new RuntimeException("Type mismatch: ProtocolFeature.set(char)");
    }

    @Override
    public char asChar() {
        throw new RuntimeException("Type mismatch: ProtocolFeature.asChar()");
    }

    @Override
    public Object asObject() {
        throw new RuntimeException("Type mismatch: ProtocolFeature.asObject()");
    }

    @Override
    public void set(Object value) {
        throw new RuntimeException("Type mismatch: ProtocolFeature.set(Object)");
    }
}
