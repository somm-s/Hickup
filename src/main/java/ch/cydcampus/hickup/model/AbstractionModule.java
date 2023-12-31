package ch.cydcampus.hickup.model;

/*
 * Used to insert tokens into the token tree in memory.
 * Includes CombinationRules that are used to combine the tokens.
 */
public class AbstractionModule extends Thread {
    
    private CombinationRule combinationRule;
    private DataModel dataModel;
    private DataSource dataSource;
    private boolean running = true;
    private int sizeThreshold = 150;

    public AbstractionModule(DataModel dataModel, DataSource dataSource, CombinationRule combinationRule) {
        this.dataModel = dataModel;
        this.dataSource = dataSource;
        this.combinationRule = combinationRule;
    }

    @Override
    public void run() {
        System.out.println("AbstractionModule started");
        dataSource.registerReader();
        try {
            Token last = null;
            while (running) {
                Token token = dataSource.consume();
                if(token == null) {
                    System.out.println("Received End of Input Stream, exiting abstraction module.");
                    running = false;
                    break;
                }

                if(token.getState().getBytes() < sizeThreshold) {
                    continue;
                }
                dataModel.insertToken(token, combinationRule);

                if(last != null) {
                    assert last.getTimeInterval().compareTo(token.getTimeInterval()) < 0;
                }
                last = token;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setSizeThreshold(int sizeThreshold) {
        this.sizeThreshold = sizeThreshold;
    } 

    public void stopThread() {
        running = false;
        dataSource.stopProducer();
    }

}
