package ch.cydcampus.hickup.pipeline.abstraction;

import java.util.LinkedList;
import java.util.List;

import ch.cydcampus.hickup.pipeline.feature.Feature;

/**
 * This class provides an implementation of the Abstraction interface for high order abstractions.
 * A high order abstraction contains multiple abstractions as children. Each abstraction only contains children that are
 * exactly one level lower.
 */
public class HighOrderAbstraction implements Abstraction {

    private int level;
    private long lastUpdateTime;
    private long firstUpdateTime;
    private long refreshTime;
    private List<Abstraction> children;
    private Feature[] features;
    private boolean sealed;

    /**
     * Create a new high order abstraction.
     * @param level The level of the abstraction.
     */
    public HighOrderAbstraction(int level) {
        this.level = level;
        this.lastUpdateTime = -1;
        this.firstUpdateTime = -1;
        this.children = new LinkedList<>();
        this.sealed = false;
    }

    @Override
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Override
    public void setLastUpdateTime(long time) {
        lastUpdateTime = time;
    }

    @Override
    public long getFirstUpdateTime() {
        return firstUpdateTime;
    }

    @Override
    public boolean isSealed() {
        return sealed;
    }

    @Override
    public void seal() {
        sealed = true;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public List<Abstraction> getChildren() {
        return children;
    }

    @Override
    public void addChild(Abstraction abstraction) {
        long updateTime = abstraction.getLastUpdateTime();
        if(updateTime > lastUpdateTime) {
            lastUpdateTime = updateTime;
            refreshTime = Math.max(refreshTime, updateTime);
        }
        if(firstUpdateTime == -1) {
            firstUpdateTime = abstraction.getFirstUpdateTime();
            refreshTime = abstraction.getLastUpdateTime();
        }
        children.add(abstraction);
    }

    @Override
    public void addFeatures(Feature[] features) {
        this.features = features;
    }

    @Override
    public Feature[] getFeatures() {
        return features;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Abstraction Level: " + level + "\n");
        sb.append("Last Update Time: " + lastUpdateTime + "\n");
        sb.append("Number of Children: " + children.size() + "\n");
        sb.append("Features: \n");
        for(Feature f : features) {
            sb.append(f.getName() + ": " + f.toString() + "\n");
        }
        return sb.toString();
    }

    @Override
    public Feature getFeature(int index) {
        return features[index];
    }

    @Override
    public String toCsvString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getFirstUpdateTime());
        sb.append(",");
        sb.append(this.getLastUpdateTime());
        sb.append(",");
        for(Feature f : features) {
            sb.append(f.toString() + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public String getCsvHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("startTime");
        sb.append(",");
        sb.append("endTime");
        sb.append(",");
        for(Feature f : features) {
            sb.append(f.getName() + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public void setRefreshTime(long time) {
        refreshTime = time;
    }

    @Override
    public long getRefreshTime() {
        return refreshTime;
    }
}
