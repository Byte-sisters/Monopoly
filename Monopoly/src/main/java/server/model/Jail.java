package server.model;

public class Jail {
    private int remainingTurns;

    public Jail() {
        this.remainingTurns = 0;
    }
    public void sendToJail(){
        remainingTurns=3;
    }
    public boolean isInJail(){
        return remainingTurns>0;
    }
}
