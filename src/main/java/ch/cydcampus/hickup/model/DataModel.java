package ch.cydcampus.hickup.model;

/*
 * Token tree in memory that is used to store tokens. Thread-safe.
 */
public class DataModel {    
    private Token root = new ParallelToken();
    private TokenPool tokenPool = TokenPool.getPool();

    /*
     * Inserts a token into the token tree.
     * 
     * @param token The token to insert.
     * @param combinationRule The combination rule to use.
     */
    public synchronized void insertToken(Token packetToken, CombinationRule combinationRule) {
        Token current = root;
        Token child = null;
        do {
            current.addSubToken(packetToken);
            child = current.getDecidingChild(packetToken);

            if(child == null) {
            } else {
            }

            if(child == null || !combinationRule.belongsToToken(child, packetToken)) {
                child = current.createNewSubToken(packetToken, tokenPool);
            } else if(child.getLevel() == Token.PACKET_LAYER) {
                child.addSubToken(packetToken);
            }

            current = child;
        } while(child != null && child.getLevel() < Token.PACKET_LAYER);
    }

    public synchronized String toString() {
        return root.deepToString();
    }
}
