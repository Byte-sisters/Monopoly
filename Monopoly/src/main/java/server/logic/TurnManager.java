package server.logic;

import server.core.GameState;
import server.model.*;

public class TurnManager {

    private final GameState gameState;
    private final TransactionManager transactionManager;
    private final Dice dice;

    private int currentPlayerIndex;
    private int[] turnOrder;

    public TurnManager(GameState gameState, TransactionManager tm) {
        this.gameState = gameState;
        this.transactionManager = tm;
        this.dice = new Dice();
        initializeTurnOrder();
    }

    private void initializeTurnOrder() {
        int size = gameState.getPlayers().size();
        turnOrder = new int[size];

        int idx = 0;
        for (int i = 1; i <= size; i++) {
            Player p = gameState.getPlayers().get(i);
            if (p != null) {
                turnOrder[idx++] = p.getPlayerID();
            }
        }
        currentPlayerIndex = 0;
    }

    public Player getCurrentPlayer() {
        return gameState.getPlayers()
                .get(turnOrder[currentPlayerIndex]);
    }

    public void playTurn() {
        Player player = getCurrentPlayer();

        if (player.isBankrupt()) {
            nextTurn();
            return;
        }

        if (player.getStatus() == PlayerStatus.IN_JAIL) {
            handleJailAuto(player);
            return;
        }
        int diceValue = dice.roll();
        transactionManager.applyMove(gameState.nextActionId(), player, diceValue);
        handleLanding(player);
        nextTurn();
    }

    private void handleLanding(Player player) {
        Tile tile = gameState.getBoard()
                .getAllTiles()
                .getTileAtPosition(player.getCurrentPosition());

        switch (tile.getType()) {

            case PROPERTY:
                Property p = (Property) tile.getData();
                if (p.getOwnerID() == null) {
                    transactionManager.buyProperty(
                            gameState.nextActionId(),
                            player,
                            p
                    );
                } else {
                    transactionManager.applyPayRent(
                            gameState.nextActionId(),
                            player.getPlayerID(),
                            p.getPropertyID()
                    );
                }
                break;

            case TAX:
                transactionManager.applyPayTax(
                        gameState.nextActionId(),
                        player,
                        (Tax) tile.getData()
                );
                break;

            case CARD:
                transactionManager.applyCardEffect(
                        gameState.nextActionId(),
                        player.getPlayerID(),
                        (CardType) tile.getData()
                );
                break;

            case JAIL:
                if (tile.getData() == JailStatus.GO_TO_JAIL) {
                    player.sendToJail();
                }
                break;
        }
    }


    private void handleJailAuto(Player player) {
        player.incrementJailTurn();
        if (player.getJailTurns() > 3) {
            applyJailPaymentLogic(player);
        }
    }

    public void applyJailPaymentLogic(Player player) {
        if (player.getBalance() >= 50) {
            Tax jailFine = new Tax(50);
            transactionManager.applyPayTax(gameState.nextActionId(), player, jailFine);
            player.releaseFromJail();

            int diceValue = dice.roll();
            transactionManager.applyMove(gameState.nextActionId(), player, diceValue);
            handleLanding(player);
            nextTurn();
        }
    }

    public void applyJailDoubleLogic(Player player) {
        int diceValue = dice.roll();
        if (dice.isDouble()) {
            player.releaseFromJail();
            transactionManager.applyMove(gameState.nextActionId(), player, diceValue);
            handleLanding(player);
        }
        nextTurn();
    }
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % turnOrder.length;
    }

}
