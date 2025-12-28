package server.action;

import server.model.Player;

public class PayTaxAction extends Action {
    private final int amount;
    private final int previousBalance;
    private final int newBalance;

    public PayTaxAction(int actionId, Player player, int amount) {
        super(actionId, ActionType.PAY_TAX, player.getPlayerID(), player);
        this.amount = amount;
        this.previousBalance = player.getBalance();
        this.newBalance = previousBalance - amount;
    }

    @Override
    public void redo() {
        Player player = (Player) affectedEntities;
        player.setBalance(newBalance);
    }

    @Override
    public void undo() {
        Player player = (Player) affectedEntities;
        player.setBalance(previousBalance);
    }
}