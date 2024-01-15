package ch.cydcampus.hickup.pipeline.abstraction;

public interface AbstractionDeque {
    
    public Abstraction getFirstAbstraction(long currentTime);
    public void addAbstraction(Abstraction abstraction);

}
