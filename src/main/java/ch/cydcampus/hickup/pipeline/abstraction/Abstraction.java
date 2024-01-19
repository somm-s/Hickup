package ch.cydcampus.hickup.pipeline.abstraction;

import java.util.List;

import ch.cydcampus.hickup.pipeline.feature.Feature;

public interface Abstraction {
    
    public boolean isSealed();
    public void seal();

    // returns the time of last update in microseconds
    public long getLastUpdateTime();
    public void setLastUpdateTime(long time);
    public long getFirstUpdateTime();

    public int getLevel();
    public List<Abstraction> getChildren();
    public void addChild(Abstraction abstraction);
    public Abstraction getActiveAbstraction();

    // Interface for abstraction deque
    public Abstraction getNext();
    public Abstraction getPrev();
    public void setNext(Abstraction abstraction);
    public void setPrev(Abstraction abstraction);

    // Feature interface
    public void addFeatures(Feature[] feature);
    public Feature[] getFeatures();
    public Feature getFeature(int index);

}
