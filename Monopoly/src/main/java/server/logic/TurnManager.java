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
        int diceValue = dice.roll();
        Player player = getCurrentPlayer();

        if (player.isBankrupt()) {
            nextTurn();
            return;
        }

        if (player.getStatus() == PlayerStatus.IN_JAIL) {
            handleJail(player);
            nextTurn();
            return;
        }

        transactionManager.applyMove(
                gameState.nextActionId(),
                player,
                diceValue
        );

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

    private void handleJail(Player player) {
        player.incrementJailTurn();
        int jailTurns = player.getJailTurns();

        if (jailTurns > 3) {
            player.releaseFromJail();
            return;
        }
        int diceValue = dice.roll();

        if (dice.isDouble()) {
            player.releaseFromJail();
            transactionManager.applyMove(
                    gameState.nextActionId(),
                    player,
                    diceValue
            );
            handleLanding(player);
            return;
        }

        boolean wantsToPay = wantsToPayFine(player);

        if (wantsToPay && player.getBalance() >= 50) {
            Tax jailFine = new Tax(50);
            transactionManager.applyPayTax(
                    gameState.nextActionId(),
                    player,
                    jailFine
            );
            player.releaseFromJail();
            transactionManager.applyMove(
                    gameState.nextActionId(),
                    player,
                    diceValue
            );
            handleLanding(player);
        }
    }

    //this is not complete it should combine with UI
    private boolean wantsToPayFine(Player player) {
        return true;
    }

    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % turnOrder.length;
    }

}
