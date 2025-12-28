package server.action;

import server.model.Player;
import server.model.Property;

public class TradeAction extends Action {

    private final Property property;

    private final int fromPlayerPrevBalance;
    private final int fromPlayerNewBalance;

    private final int toPlayerPrevBalance;
    private final int toPlayerNewBalance;

    private final Integer previousOwner;
    private final Integer newOwner;

    private final Player toPlayer;

    public TradeAction(int actionId, Player fromPlayer, Player toPlayer, Property property, int fromPrevBalance, int fromNewBalance, int toPrevBalance, int toNewBalance) {
        super(actionId, ActionType.TRADE, fromPlayer.getPlayerID(), fromPlayer);
        this.toPlayer = toPlayer;
        this.property = property;

        this.fromPlayerPrevBalance = fromPrevBalance;
        this.fromPlayerNewBalance = fromNewBalance;

        this.toPlayerPrevBalance = toPrevBalance;
        this.toPlayerNewBalance = toNewBalance;

        this.previousOwner = property.getOwnerID();
        this.newOwner = toPlayer.getPlayerID();
    }

    @Override
    public void redo() {
        Player fromPlayer = (Player) affectedEntities;

        fromPlayer.setBalance(fromPlayerNewBalance);
        toPlayer.setBalance(toPlayerNewBalance);

        property.setOwnerID(newOwner);

        fromPlayer.removeProperty(property.getPropertyID());
        toPlayer.addProperty(property);
    }

    @Override
    public void undo() {
        Player fromPlayer = (Player) affectedEntities;

        fromPlayer.setBalance(fromPlayerPrevBalance);
        toPlayer.setBalance(toPlayerPrevBalance);

        property.setOwnerID(previousOwner);

        toPlayer.removeProperty(property.getPropertyID());
        fromPlayer.addProperty(property);
    }
}
