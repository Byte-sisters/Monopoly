package server.action;

import server.model.Player;
import server.model.Property;

public class BuyAction extends Action {

    private final Property property;

    private final int previousBalance;
    private final int newBalance;

    private final Integer previousOwner;
    private final Integer newOwner;

    public BuyAction(int actionId, Player buyer, Property property, int prevBalance, int newBalance) {

        super(actionId, ActionType.BUY_PROPERTY, buyer.getPlayerID(), buyer);

        this.property = property;
        this.previousBalance = prevBalance;
        this.newBalance = newBalance;

        this.previousOwner = property.getOwnerID();
        this.newOwner = buyer.getPlayerID();
    }

    @Override
    public void redo() {
        Player buyer = (Player) affectedEntities;

        buyer.setBalance(newBalance);
        property.setOwnerID(newOwner);
        buyer.addProperty(property);
    }

    @Override
    public void undo() {
        Player buyer = (Player) affectedEntities;

        buyer.setBalance(previousBalance);
        property.setOwnerID(previousOwner);
        buyer.removeProperty(property.getPropertyID());
    }
}
