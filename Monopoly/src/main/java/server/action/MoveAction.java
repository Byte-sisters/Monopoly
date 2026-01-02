package server.action;

import server.model.Player;

public class MoveAction extends Action {

    private final int previousPosition;
    private final int newPosition;

    private final int previousBalance;
    private final int newBalance;

    public MoveAction(int actionId, Player player, int prevPos, int newPos, int prevBalance, int newBalance) {

        super(actionId, ActionType.MOVE, player.getPlayerID(), player);

        this.previousPosition = prevPos;
        this.newPosition = newPos;
        this.previousBalance = prevBalance;
        this.newBalance = newBalance;
    }
    @Override
    public void redo() {
        Player player = (Player) affectedEntities;
        player.setCurrentPosition(newPosition);
        player.setBalance(newBalance);
    }

    @Override
    public void undo() {
        Player player = (Player) affectedEntities;
        player.setCurrentPosition(previousPosition);
        player.setBalance(previousBalance);
    }
}
