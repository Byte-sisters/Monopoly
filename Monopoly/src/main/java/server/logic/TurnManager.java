package server.logic;

import server.core.GameState;
import server.datastructure.MyHashTable;
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
        turnOrder = new int[4];
        int idx = 0;
        for (int i = 1; i <= 4; i++) {
            turnOrder[idx++] = i;
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

        if (player.getStatus() == PlayerStatus.IN_JAIL &&
                player.getJailTurns() >= 3) {
            applyJailPaymentLogic(player);
        }

        int diceValue = dice.roll();
        transactionManager.applyMove(
                gameState.nextActionId(),
                player,
                diceValue
        );
        handleLanding(player);
        updateBuildAndMortgageState(player);
    }


    private void handleLanding(Player player) {
        Tile tile = gameState.getBoard()
                .getAllTiles()
                .getTileAtPosition(player.getCurrentPosition());

        switch (tile.getType()) {
            case PROPERTY:
                int index = (int) tile.getData();
                Property property = gameState.getProperties().get(index);

                if (property.getOwnerID() == null) {
                    gameState.setPendingBuy(
                            player.getPlayerID(),
                            property.getPropertyID()
                    );
                }
                else if (!property.getOwnerID().equals(player.getPlayerID())) {
                    transactionManager.applyPayRent(
                            gameState.nextActionId(),
                            player.getPlayerID(),
                            property.getPropertyID()
                    );
                }
                break;
            case TAX:
                Tax tax = new Tax((int)tile.getData());
                transactionManager.applyPayTax(
                        gameState.nextActionId(),
                        player,
                        tax
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

    public void applyJailPaymentLogic(Player player) {
        if (player.getBalance() < 50) return;

        Tax jailFine = new Tax(50);
        transactionManager.applyPayTax(
                gameState.nextActionId(),
                player,
                jailFine
        );

        player.releaseFromJail();

        int diceValue = dice.roll();
        transactionManager.applyMove(
                gameState.nextActionId(),
                player,
                diceValue
        );
        handleLanding(player);
        nextTurn();
    }

    public void applyJailDoubleLogic(Player player) {
        int diceValue = dice.roll();
        player.incrementJailTurn();

        if (dice.isDouble()) {
            player.releaseFromJail();

            transactionManager.applyMove(
                    gameState.nextActionId(),
                    player,
                    diceValue
            );
            handleLanding(player);
        }
    }

    private void updateBuildAndMortgageState(Player player) {

        gameState.clearBuildFlags();

        MyHashTable<Property> properties = gameState.getProperties();

        for (int i = 1; i <= properties.size(); i++) {
            Property p = properties.get(i);
            if (p == null) continue;

            if (p.getOwnerID() != null &&
                    p.getOwnerID() == player.getPlayerID()) {

                if (!p.isMortgaged() &&
                        !p.hasHotel() &&
                        p.getHouseCount() < 4 &&
                        gameState.hasMonopoly(player.getPlayerID(), p.getColorGroup())) {

                    gameState.setCanBuildHouse(p.getPropertyID());
                }

                if (!p.isMortgaged() &&
                        p.getHouseCount() == 4 &&
                        !p.hasHotel() &&
                        gameState.hasMonopoly(player.getPlayerID(), p.getColorGroup())) {

                    gameState.setCanBuildHotel(p.getPropertyID());
                }

                if (p.isMortgaged()) {
                    gameState.mortgage(p.getPropertyID());
                }
            }
        }
    }

    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % turnOrder.length;
    }

}
