package ch.cydcampus.hickup.pipeline.abstraction;

import java.util.List;

import ch.cydcampus.hickup.pipeline.feature.Feature;

/**
 * This interface provides the abstraction for the basic data container that is processed by the pipeline.
 * There are two types of abstractions: HighOrderAbstraction and PacketAbstraction. A HighOrderAbstraction
 * contains multiple abstractions as children. Each abstraction only contains children that are exactly one level lower.
 * Packet abstractions always have a level of 0. Each abstraction contains a set of features that can be used to
 * extract higher order features from lower order features.
 */
public interface Abstraction {

    /**
     * Get the time of the last update of this abstraction. This time is equal to the timestamp of the last packet 
     * that was added to this abstraction or a descendant abstraction.
     * @return The time of the last update of this abstraction.
     */
    public long getLastUpdateTime();

    /**
     * Set the time of the last update of this abstraction.
     * @param time
     */
    public void setLastUpdateTime(long time);


    /**
     * Timer used to determine whether the abstraction is ready to be consumed from the queue.
     * @param time
     */
    public void setRefreshTime(long time);

    /**
     * Get the last time a packet arrived with the same multiplexer attribute. This means that the packet could potentially
     * be added to this abstraction.
     */
    public long getRefreshTime();

    /**
     * Get the time of the first update of this abstraction. This time is equal to the timestamp of the first packet 
     * that was added to this abstraction or a descendant abstraction. This time is not updated once initialized.
     * @return The time of the first update of this abstraction.
     */
    public long getFirstUpdateTime();

    /**
     * Get the level of the abstraction. The level is 0 for packet abstractions and increases by 1 for each level of
     * abstraction that is added.
     * @return The level of the abstraction.
     */
    public int getLevel();

    /**
     * Get the children of this abstraction. The children are abstractions that are exactly one level lower.
     * Furthermore, the children are always sorted by their first update time.
     * @return Sorted list of children.
     */
    public List<Abstraction> getChildren();

    /**
     * Add a child to this abstraction. The child must be exactly one level lower than this abstraction.
     * An abstraction may be child to at most one abstraction. Only abstractions that are not sealed may be added as children.
     * @param abstraction The abstraction to add as a child.
     */
    public void addChild(Abstraction abstraction);

    /**
     * Return the seal status of this abstraction. An abstraction is sealed if it is not allowed to be modified anymore.
     * This means that no children may be added and no features and update times may be altered. Sealed abstractions
     * can be added to other abstractions as children.
     * @return True if the abstraction is sealed, false otherwise.
     */
    public boolean isSealed();

    /**
     * Seal this abstraction. Once sealed, the abstraction may not be modified anymore.
     */
    public void seal();

    /**
     * Add all features to this abstraction. The features are statically initialized with default values.
     * It is statically determined which index in the feature array corresponds to which feature.
     * @param features to add to this abstraction.
     */
    public void addFeatures(Feature[] features);

    /**
     * Get the features of this abstraction.
     * @return The features of this abstraction.
     */
    public Feature[] getFeatures();

    /**
     * Get the feature at the specified index.
     * @param index The index of the feature to get.
     * @return The feature at the specified index.
     */
    public Feature getFeature(int index);

    /**
     * Get the csv string representation of this abstraction.
     */
    public String toCsvString();

    /**
     * Get the csv header for the csv string representation of this abstraction.
     */
    public String getCsvHeader();
}
