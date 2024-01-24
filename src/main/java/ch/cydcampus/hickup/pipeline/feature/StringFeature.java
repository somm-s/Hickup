package ch.cydcampus.hickup.pipeline.feature;

import java.net.InetAddress;

public class StringFeature implements Feature {

    private String value;
    private String name;

    public StringFeature(String value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public void cloneTo(Feature feature) {
        if(feature instanceof StringFeature) {
            ((StringFeature) feature).value = this.value;
        }
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public FeatureType getType() {
        return FeatureType.STRING;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void set(String value) {
        this.value = value;
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public boolean equals(Feature otherFeature) {
        return value.equals(otherFeature.asString());
    }

    @Override
    public int compareTo(Feature otherFeature) {
        return value.compareTo(otherFeature.asString());
    }

    @Override
    public InetAddress asIP() {
        throw new RuntimeException("Type mismatch: StringFeature.asIP()");
    }

    @Override
    public int asInt() {
        throw new RuntimeException("Type mismatch: StringFeature.asInt()");
    }

    @Override
    public long asLong() {
        throw new RuntimeException("Type mismatch: StringFeature.asLong()");
    }

    @Override
    public Protocol asProtocol() {
        throw new RuntimeException("Type mismatch: StringFeature.asProtocol()");
    }

    @Override
    public void set(InetAddress value) {
        throw new RuntimeException("Type mismatch: StringFeature.set(InetAddress)");
    }

    @Override
    public void set(int value) {
        throw new RuntimeException("Type mismatch: StringFeature.set(int)");
    }

    @Override
    public void set(long value) {
        throw new RuntimeException("Type mismatch: StringFeature.set(long)");
    }

    @Override
    public void set(Protocol value) {
        throw new RuntimeException("Type mismatch: StringFeature.set(Protocol)");
    }

    @Override
    public boolean asBoolean() {
        throw new RuntimeException("Type mismatch: StringFeature.asBoolean()");
    }

    @Override
    public void set(boolean value) {
        throw new RuntimeException("Type mismatch: StringFeature.set(boolean)");
    }

    @Override
    public double asDouble() {
        throw new RuntimeException("Type mismatch: StringFeature.asDouble()");
    }

    @Override
    public void set(double value) {
        throw new RuntimeException("Type mismatch: StringFeature.set(double)");
    }

    @Override
    public float asFloat() {
        throw new RuntimeException("Type mismatch: StringFeature.asFloat()");
    }

    @Override
    public void set(float value) {
        throw new RuntimeException("Type mismatch: StringFeature.set(float)");
    }

    @Override
    public short asShort() {
        throw new RuntimeException("Type mismatch: StringFeature.asShort()");
    }

    @Override
    public void set(short value) {
        throw new RuntimeException("Type mismatch: StringFeature.set(short)");
    }

    @Override
    public byte asByte() {
        throw new RuntimeException("Type mismatch: StringFeature.asByte()");
    }

    @Override
    public void set(byte value) {
        throw new RuntimeException("Type mismatch: StringFeature.set(byte)");
    }

    @Override
    public char asChar() {
        throw new RuntimeException("Type mismatch: StringFeature.asChar()");
    }

    @Override
    public void set(char value) {
        throw new RuntimeException("Type mismatch: StringFeature.set(char)");
    }

    @Override
    public Object asObject() {
        throw new RuntimeException("Type mismatch: StringFeature.asObject()");
    }

    @Override
    public void set(Object value) {
        throw new RuntimeException("Type mismatch: StringFeature.set(Object)");
    }

}
