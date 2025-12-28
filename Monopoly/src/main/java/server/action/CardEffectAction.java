package server.action;

import server.model.Player;
import server.model.Card;
import server.model.EffectType;

public class CardEffectAction extends Action {

    private final Card card;

    private final int previousBalance;
    private final int newBalance;
    private final int previousPosition;
    private final int newPosition;
    private final boolean wasInJail;
    private final boolean goToJail;

    private final Player[] allPlayers;
    private final int[] previousBalances;
    private final int[] newBalances;

    public CardEffectAction(int actionId, Player player, Card card, int prevBalance, int newBalance, int prevPosition, int newPosition, boolean wasInJail, boolean goToJail, Player[] allPlayers, int[] previousBalances, int[] newBalances) {
        super(actionId, ActionType.CARD_EFFECT, player.getPlayerID(), player);
        this.card = card;
        this.previousBalance = prevBalance;
        this.newBalance = newBalance;
        this.previousPosition = prevPosition;
        this.newPosition = newPosition;
        this.wasInJail = wasInJail;
        this.goToJail = goToJail;

        this.allPlayers = allPlayers;
        this.previousBalances = previousBalances;
        this.newBalances = newBalances;
    }

    @Override
    public void redo() {
        Player player = (Player) affectedEntities;

        switch (card.getEffectType()) {
            case MOVE:
                player.setCurrentPosition(newPosition);
                break;

            case GET_MONEY:
            case LOSE_MONEY:
                player.setBalance(newBalance);
                break;

            case PAY_TO_PLAYERS:
                player.setBalance(newBalance);
                for (int i = 0; i < allPlayers.length; i++) {
                    if (allPlayers[i].getPlayerID() != player.getPlayerID()) {
                        allPlayers[i].setBalance(newBalances[i]);
                    }
                }
                break;

            case GO_TO_JAIL:
                player.setCurrentPosition(newPosition);
                if (goToJail) {
                    player.sendToJail();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void undo() {
        Player player = (Player) affectedEntities;

        switch (card.getEffectType()) {
            case MOVE:
                player.setCurrentPosition(previousPosition);
                break;

            case GET_MONEY:
            case LOSE_MONEY:
                player.setBalance(previousBalance);
                break;

            case PAY_TO_PLAYERS:
                player.setBalance(previousBalance);
                for (int i = 0; i < allPlayers.length; i++) {
                    if (allPlayers[i].getPlayerID() != player.getPlayerID()) {
                        allPlayers[i].setBalance(previousBalances[i]);
                    }
                }
                break;

            case GO_TO_JAIL:
                player.setCurrentPosition(previousPosition);
                if (!wasInJail) {
                    player.releaseFromJail();
                }
                break;

            default:
                break;
        }
    }
}
