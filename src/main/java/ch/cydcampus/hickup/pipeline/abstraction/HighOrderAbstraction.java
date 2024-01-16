package ch.cydcampus.hickup.pipeline.abstraction;

import java.util.LinkedList;
import java.util.List;

import ch.cydcampus.hickup.pipeline.feature.Feature;

public class HighOrderAbstraction implements Abstraction {

    private int level;
    private long lastUpdateTime;
    private long firstUpdateTime;
    private List<Abstraction> children;
    private Abstraction activeAbstraction;
    private Abstraction next;
    private Abstraction prev;
    private Feature[] features;
    private boolean sealed = false;

    public HighOrderAbstraction(int level) {
        this.level = level;
        this.lastUpdateTime = 0;
        this.firstUpdateTime = 0;
        this.children = new LinkedList<>();
        this.activeAbstraction = null;
        this.next = null;
        this.prev = null;
    }

    @Override
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Override
    public long getFirstUpdateTime() {
        return firstUpdateTime;
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
        }
        if(firstUpdateTime == 0) {
            firstUpdateTime = updateTime;
        }
        children.add(abstraction);
    }

    @Override
    public Abstraction getActiveAbstraction() {
        return activeAbstraction;
    }

    @Override
    public Abstraction getNext() {
        return next;
    }

    @Override
    public Abstraction getPrev() {
        return prev;
    }

    @Override
    public void setNext(Abstraction abstraction) {
        next = abstraction;
    }

    @Override
    public void setPrev(Abstraction abstraction) {
        prev = abstraction;
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
        sb.append("Features: \n");
        for(Feature f : features) {
            sb.append(f.getName() + ": " + f.toString() + "\n");
        }
        return sb.toString();
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
    public Feature getFeature(int index) {
        return features[index];
    }
}
