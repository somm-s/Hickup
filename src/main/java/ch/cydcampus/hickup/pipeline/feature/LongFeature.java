package ch.cydcampus.hickup.pipeline.feature;

import java.net.InetAddress;

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
    public InetAddress asIP() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'asIP'");
    }

    @Override
    public void set(InetAddress value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'set'");
    }

    @Override
    public Protocol asProtocol() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'asProtocol'");
    }

    @Override
    public void set(Protocol value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'set'");
    }

    @Override
    public int asInt() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'asInt'");
    }

    @Override
    public void set(int value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'set'");
    }

    @Override
    public String asString() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'asString'");
    }

    @Override
    public void set(String value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'set'");
    }

    @Override
    public boolean asBoolean() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'asBoolean'");
    }

    @Override
    public void set(boolean value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'set'");
    }

    @Override
    public double asDouble() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'asDouble'");
    }

    @Override
    public void set(double value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'set'");
    }

    @Override
    public float asFloat() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'asFloat'");
    }

    @Override
    public void set(float value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'set'");
    }

    @Override
    public short asShort() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'asShort'");
    }

    @Override
    public void set(short value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'set'");
    }

    @Override
    public byte asByte() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'asByte'");
    }

    @Override
    public void set(byte value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'set'");
    }

    @Override
    public char asChar() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'asChar'");
    }

    @Override
    public void set(char value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'set'");
    }

    @Override
    public Object asObject() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'asObject'");
    }

    @Override
    public void set(Object value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'set'");
    }

    @Override
    public void cloneTo(Feature otherFeature) {
        otherFeature.set(value);
    }

    @Override
    public boolean equals(Feature otherFeature) {
        return value == otherFeature.asLong();
    }
}
