package ch.cydcampus.hickup.pipeline.source;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.abstraction.AbstractionDeque;
import ch.cydcampus.hickup.pipeline.filter.Filter;
import ch.cydcampus.hickup.pipeline.filter.Filter.FilterType;

public abstract class DataSource implements AbstractionDeque {

    private ConcurrentLinkedQueue<Abstraction> queue = new ConcurrentLinkedQueue<>();

    protected boolean produce(Abstraction abstraction) {
        return queue.offer(abstraction);
    }

    public abstract void setFilter(Filter filter);

    public abstract Set<FilterType> getSupportedFilters();

    public abstract void start();

    @Override
    public Abstraction getFirstAbstraction(long currentTime) {
        return queue.poll();
    }

    @Override
    public void addAbstraction(Abstraction abstraction) {
        throw new UnsupportedOperationException("Not supported for data source.");
    }
    
}
