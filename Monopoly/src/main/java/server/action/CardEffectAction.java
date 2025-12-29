package server.action;

import server.datastructure.MyHashTable;
import server.model.*;

public class CardEffectAction extends Action {

    private final Card card;

    private final int previousBalance;
    private final int newBalance;

    private final int previousPosition;
    private final int newPosition;

    private final boolean wasInJail;
    private final boolean goToJail;

    private final MyHashTable<Player> players;
    private final MyHashTable<Integer> previousBalances;
    private final MyHashTable<Integer> newBalances;

    public CardEffectAction(
            int actionId,
            Player player,
            Card card,
            int previousBalance,
            int newBalance,
            int previousPosition,
            int newPosition,
            boolean wasInJail,
            boolean goToJail,
            MyHashTable<Player> players,
            MyHashTable<Integer> previousBalances,
            MyHashTable<Integer> newBalances
    ) {
        super(actionId, ActionType.CARD_EFFECT, player.getPlayerID(), player);
        this.card = card;
        this.previousBalance = previousBalance;
        this.newBalance = newBalance;
        this.previousPosition = previousPosition;
        this.newPosition = newPosition;
        this.wasInJail = wasInJail;
        this.goToJail = goToJail;
        this.players = players;
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
                for (int id = 1; id <= players.size(); id++) {
                    Player p = players.get(id);
                    if (p != null) {
                        p.setBalance(newBalances.get(id));
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
                for (int id = 1; id <= players.size(); id++) {
                    Player p = players.get(id);
                    if (p != null) {
                        p.setBalance(previousBalances.get(id));
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
