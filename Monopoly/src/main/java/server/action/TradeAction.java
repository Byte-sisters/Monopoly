package server.action;

import server.model.Player;
import server.model.Property;

public class TradeAction extends Action {

    private final Player playerA;
    private final Player playerB;
    private final Property propertyA;
    private final Property propertyB;
    private final int amount;

    private final Integer prevOwnerA;
    private final Integer prevOwnerB;
    private final int prevBalanceA;
    private final int prevBalanceB;

    public TradeAction(int actionId, Player playerA, Player playerB, Property propertyA, Property propertyB, int amount) {
        super(actionId, ActionType.TRADE, playerA.getPlayerID(), playerA);

        this.playerA = playerA;
        this.playerB = playerB;
        this.propertyA = propertyA;
        this.propertyB = propertyB;
        this.amount = amount;

        this.prevOwnerA = propertyA.getOwnerID();
        this.prevOwnerB = propertyB != null ? propertyB.getOwnerID() : null;

        this.prevBalanceA = playerA.getBalance();
        this.prevBalanceB = playerB != null ? playerB.getBalance() : 0;
    }

    @Override
    public void redo() {
        if (propertyB != null && playerB != null) {
            propertyA.setOwnerID(playerB.getPlayerID());
            propertyB.setOwnerID(playerA.getPlayerID());

            playerA.removeProperty(propertyA.getPropertyID());
            playerB.removeProperty(propertyB.getPropertyID());

            playerA.addProperty(propertyB);
            playerB.addProperty(propertyA);

            playerA.setBalance(playerA.getBalance() - amount);
            playerB.setBalance(playerB.getBalance() + amount);

        } else {
            propertyA.setOwnerID(playerB != null ? playerB.getPlayerID() : null);
            playerA.removeProperty(propertyA.getPropertyID());

            if (playerB != null) {
                playerA.setBalance(playerA.getBalance() - amount);
                playerB.setBalance(playerB.getBalance() + amount);
                playerB.addProperty(propertyA);
            } else {
                playerA.setBalance(playerA.getBalance() + amount);
            }
        }
    }
    @Override
    public void undo() {
        propertyA.setOwnerID(prevOwnerA);
        if (propertyB != null && playerB != null) {
            propertyB.setOwnerID(prevOwnerB);

            playerA.removeProperty(propertyB.getPropertyID());
            playerB.removeProperty(propertyA.getPropertyID());

            playerA.addProperty(propertyA);
            playerB.addProperty(propertyB);

            playerA.setBalance(prevBalanceA);
            playerB.setBalance(prevBalanceB);

        } else {
            if (playerB != null) {
                playerA.setBalance(prevBalanceA);
                playerB.setBalance(prevBalanceB);
                playerB.removeProperty(propertyA.getPropertyID());
                playerA.addProperty(propertyA);
            } else {
                playerA.setBalance(prevBalanceA);
                playerA.addProperty(propertyA);
            }
        }
    }

}
