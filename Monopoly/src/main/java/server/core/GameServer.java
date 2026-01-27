package server.core;

import server.logic.*;
import server.model.*;
import server.network.*;

import javax.swing.*;

public class GameServer {
    private final ServerSocketManager network;
    private final GameState gameState;
    private final TransactionManager transactionManager;
    private final TurnManager turnManager;
    private int nextPlayerId = 1;

    public GameServer(ServerSocketManager network) {
        this.network = network;
        this.gameState = new GameState();
        this.transactionManager = new TransactionManager(gameState);
        this.turnManager = new TurnManager(gameState, transactionManager);
    }

    public synchronized void handleMessage(ClientHandler client, Message msg) throws Exception {
        if (msg.getType() == MessageType.HELLO) {
            handleHello(client, msg.getData());
            return;
        }

        if (client.getPlayerId() == -1) return;

        if (isTurnBasedAction(msg.getType()) && !validateTurn(client)) {
            client.send(new Message(MessageType.ERROR, "Not your turn!"));
            return;
        }

        Player p = turnManager.getCurrentPlayer();

        switch (msg.getType()) {
            case ROLL_DICE -> {
                p.setHasRolledThisTurn(true);
                handleRollDice(client);
            }
            case BUY_PROPERTY -> handleBuyProperty(p);
            case BUILD -> handleBuild(p, msg.getData());
            case MORTGAGE -> handleMortgage(p, msg.getData(), true);
            case UNMORTGAGE -> handleMortgage(p, msg.getData(), false);
            case JAIL_PAY_FINE -> {
                turnManager.applyJailPaymentLogic(p);
                p.setHasRolledThisTurn(false);
                broadcastUpdate("Player paid jail fine.");
            }
            case JAIL_TRY_DOUBLE -> {
                turnManager.applyJailDoubleLogic(p);
                broadcastUpdate("Player tried for double.");
            }
            case PROPOSE_TRADE -> handleTradeProposal(client, msg.getData());
            case ACCEPT_TRADE -> handleTradeResponse(client, true);
            case REJECT_TRADE -> handleTradeResponse(client, false);
            case END_TURN -> {
                p.setHasRolledThisTurn(false);
                turnManager.nextTurn();
                broadcastUpdate("Turn ended.");
            }
            case UNDO -> { gameState.undo(); broadcastUpdate("Undo performed."); }
            case REDO -> { gameState.redo(); broadcastUpdate("Redo performed."); }
        }
        checkWinCondition();
    }
    private void handleHello(ClientHandler client, String playerName) throws Exception {
        if (gameState.getPlayers().size() >= 4) {
            client.send(new Message(MessageType.ERROR, "Game is full!"));
            return;
        }

        int pid = nextPlayerId++;
        client.setPlayerId(pid);

        String name = (playerName == null || playerName.trim().isEmpty()) ? "Player " + pid : playerName.trim();

        Player newPlayer = new Player(pid, name, 1500);
        gameState.addPlayer(newPlayer);

        client.send(new Message(MessageType.WELCOME, "ID:" + pid));
        broadcastUpdate("Player " + name + " joined.");
    }

    private void handleRollDice(ClientHandler client) {
        Player p = turnManager.getCurrentPlayer();
        if (p.getStatus() == PlayerStatus.IN_JAIL) {
            return;
        }else {
            turnManager.playTurn();
        }
        broadcastUpdate("Dice rolled.");
    }

    private void handleBuyProperty(Player p) {

        if (!gameState.canBuyProperty(p.getPlayerID())) {
            return;
        }

        int propId = gameState.getPendingBuyPropertyId();
        Property prop = gameState.getProperties().get(propId);

        if (transactionManager.buyProperty(
                gameState.nextActionId(),
                p,
                prop
        )) {
            broadcastUpdate("Bought " + prop.getName());
            gameState.clearPendingBuy();
        }
    }

    private void handleBuild(Player p, String data) {
        try {
            Property prop = gameState.getPendingBuildProperty(p.getPlayerID());
            if (prop == null) {
                network.broadcast(new Message(MessageType.ERROR, "No property available to build!"));
                return;
            }

            BuildingType type = BuildingType.valueOf(data);

            if (transactionManager.build(gameState.nextActionId(), p, prop, type, 100)) {
                broadcastUpdate("Building created on " + prop.getName());
            }
        } catch (Exception e) {
            network.broadcast(new Message(MessageType.ERROR, "BUILD_ERROR: " + e.getMessage()));
            System.err.println("Error processing build: " + e.getMessage());
        }
    }


    private void handleMortgage(Player p, String data, boolean isMortgaging) {
        int propId = Integer.parseInt(data);
        Property prop = gameState.getProperties().get(propId);
        if (prop==null){
            System.out.println("Mortgage property not found");
        }else{
            System.out.println("Mortgage property found: " + prop.getOwnerID());
        }
        if (transactionManager.applyMortgage(gameState.nextActionId(), p.getPlayerID(), prop, isMortgaging)) {
            broadcastUpdate(isMortgaging ? "Property mortgaged" : "Property unmortgaged");
        }
    }

    private void handleTradeResponse(ClientHandler client, boolean accepted) {
        int aId = gameState.getPendingTradePlayerA();
        int bId = gameState.getPendingTradePlayerB();
        int propA = gameState.getPendingTradePropertyA();
        Integer propB = gameState.getPendingTradePropertyB();
        int amount = gameState.getPendingTradeAmount();

        if (aId == -1 || bId == -1) {
            try {
                client.send(new Message(MessageType.ERROR, "No pending trade."));
            }catch (Exception e){
                System.err.println("Error processing trade: " + e.getMessage());
            }
            return;
        }

        if (accepted) {
            boolean success = transactionManager.applyTrade(
                    gameState.nextActionId(),
                    aId,
                    bId,
                    propA,
                    propB,
                    amount
            );
            if (success) {
                broadcastUpdate("Trade executed successfully!");
            } else {
                try {
                    client.send(new Message(MessageType.ERROR, "Trade failed."));
                }catch (Exception e){
                    System.err.println("Error processing trade: " + e.getMessage());
                }
            }
        } else {
            broadcastUpdate("Trade rejected.");
        }

        gameState.clearPendingTrade();
        broadcastUpdate(accepted ? "Trade executed successfully!" : "Trade rejected.");
    }

    private void handleTradeProposal(ClientHandler client, String data) {
        try {
            String[] parts = data.split(",");
            if (parts.length < 5) {
                client.send(new Message(MessageType.ERROR, "Trade data incomplete."));
                return;
            }

            int playerAId = Integer.parseInt(parts[0].trim());
            int playerBId = Integer.parseInt(parts[1].trim());
            int propertyAId = Integer.parseInt(parts[2].trim());
            Integer propertyBId = null;
            if (!parts[3].trim().isEmpty()) propertyBId = Integer.parseInt(parts[3].trim());
            int amount = 0;
            if (!parts[4].trim().isEmpty()) amount = Integer.parseInt(parts[4].trim());

            gameState.setPendingTrade(playerAId, playerBId, propertyAId, propertyBId, amount);

            String tradeMessage = playerAId + "," + playerBId + "," + propertyAId + "," +
                    (propertyBId != null ? propertyBId : "") + "," + amount;
            sendToPlayer(playerBId, new Message(MessageType.PROPOSE_TRADE, tradeMessage));

            broadcastUpdate("Player " + playerAId + " proposed a trade to Player " + playerBId);

        } catch (Exception e) {
            try { client.send(new Message(MessageType.ERROR, "Trade parsing/processing error: " + e.getMessage())); }
            catch (Exception ex) { System.err.println("Error sending trade error: " + ex.getMessage()); }
        }
    }

    private void checkWinCondition() {
        int active = 0;
        Player winner = null;

        for (int i = 1; i <= gameState.getPlayers().size(); i++) {
            Player p = gameState.getPlayers().get(i);
            if (p != null && !p.isBankrupt()) {
                active++;
                winner = p;
            }
        }

        if (active == 1 && winner != null) {
            network.broadcast(new Message(MessageType.GAME_OVER, "Winner: " + winner.getName()));
        }
    }

    private void broadcastUpdate(String logMsg) {
        network.broadcast(new Message(MessageType.EVENT_LOG, logMsg));
        network.broadcast(new Message(MessageType.STATE_UPDATE, gameState.toString()));
        network.broadcast(new Message(
                MessageType.TURN_INFO,
                String.valueOf(turnManager.getCurrentPlayer().getPlayerID())
        ));
        Player richest = gameState.getRichestPlayerSafe();
        Player rentKing = gameState.getTopRentEarnerSafe();

        if (richest != null && rentKing != null) {
            String reportData =
                    richest.getName() + ":" + richest.getBalance()
                            + "|"
                            + rentKing.getName() + ":" + rentKing.getRentIncome();

            network.broadcast(new Message(
                    MessageType.REPORT_UPDATE,
                    reportData
            ));
        }
    }


    private boolean validateTurn(ClientHandler client) {
        return turnManager.getCurrentPlayer().getPlayerID() == client.getPlayerId();
    }

    private boolean isTurnBasedAction(MessageType type) {
        return type != MessageType.ACCEPT_TRADE && type != MessageType.REJECT_TRADE;
    }

    private void sendToPlayer(int playerId, Message msg) {
        for (ClientHandler ch : network.getClients()) {
            if (ch.getPlayerId() == playerId) {
                try { ch.send(msg); } catch (Exception e) { System.err.println("Send failed: " + e.getMessage()); }
                break;
            }
        }
    }

}