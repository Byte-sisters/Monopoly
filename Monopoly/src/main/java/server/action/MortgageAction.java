package server.action;

import server.model.Player;
import server.model.Property;

public class MortgageAction extends Action {
    private final Property property;
    private final int mortgageValue;
    private final boolean isMortgaging;
    private final int previousBalance;
    private final int newBalance;
    private final boolean previousMortgageStatus;

    public MortgageAction(int actionId, Player player, Property property, boolean isMortgaging) {
        super(actionId, ActionType.MORTGAGE, player.getPlayerID(), player);

        this.property = property;
        this.isMortgaging = isMortgaging;
        this.mortgageValue = property.getMortgageValue();
        this.previousBalance = player.getBalance();
        this.previousMortgageStatus = property.isMortgaged();

        if (isMortgaging) {
            this.newBalance = previousBalance + mortgageValue;
        } else {
            int redemptionCost = (int) (mortgageValue * 1.1);
            this.newBalance = previousBalance - redemptionCost;
        }
    }

    @Override
    public void redo() {
        Player player = (Player) affectedEntities;
        player.setBalance(newBalance);

        property.setMortgaged(isMortgaging);
    }

    @Override
    public void undo() {
        Player player = (Player) affectedEntities;
        player.setBalance(previousBalance);
        property.setMortgaged(previousMortgageStatus);
    }
}