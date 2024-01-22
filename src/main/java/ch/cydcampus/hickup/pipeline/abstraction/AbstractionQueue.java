package ch.cydcampus.hickup.pipeline.abstraction;

public interface AbstractionQueue {
    
    public Abstraction getFirstAbstraction(long currentTime);
    public void addAbstraction(Abstraction abstraction);

}
