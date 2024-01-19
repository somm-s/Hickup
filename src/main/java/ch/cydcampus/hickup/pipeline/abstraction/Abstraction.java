package ch.cydcampus.hickup.pipeline.abstraction;

import java.util.List;

import ch.cydcampus.hickup.pipeline.feature.Feature;

public interface Abstraction {

    // returns the time of last update in microseconds
    public long getLastUpdateTime();
    public void setLastUpdateTime(long time);
    public long getFirstUpdateTime();

    public int getLevel();
    public List<Abstraction> getChildren();
    public void addChild(Abstraction abstraction);

    // Feature interface
    public void addFeatures(Feature[] feature);
    public Feature[] getFeatures();
    public Feature getFeature(int index);

}
