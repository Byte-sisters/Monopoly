package server.core;

import server.logic.*;
import server.model.*;
import server.network.*;

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
            handleHello(client);
            return;
        }

        if (client.getPlayerId() == -1) return;

        if (isTurnBasedAction(msg.getType()) && !validateTurn(client)) {
            client.send(new Message(MessageType.ERROR, "Not your turn!"));
            return;
        }

        Player p = turnManager.getCurrentPlayer();

        switch (msg.getType()) {
            case ROLL_DICE -> handleRollDice(client);
            case BUY_PROPERTY -> handleBuyProperty(p);
            case BUILD -> handleBuild(p, msg.getData());
            case MORTGAGE -> handleMortgage(p, msg.getData(), true);
            case UNMORTGAGE -> handleMortgage(p, msg.getData(), false);
            case JAIL_PAY_FINE -> {
                turnManager.applyJailPaymentLogic(p);
                broadcastUpdate("Player paid jail fine.");
            }
            case JAIL_TRY_DOUBLE -> {
                turnManager.applyJailDoubleLogic(p);
                broadcastUpdate("Player tried for double.");
            }
            case PROPOSE_TRADE -> handleTradeProposal(p, msg.getData());
            case ACCEPT_TRADE -> handleTradeResponse(client, true);
            case REJECT_TRADE -> handleTradeResponse(client, false);
            case END_TURN -> {
                turnManager.nextTurn();
                broadcastUpdate("Turn ended.");
            }
            case UNDO -> { gameState.undo(); broadcastUpdate("Undo performed."); }
            case REDO -> { gameState.redo(); broadcastUpdate("Redo performed."); }
        }
        checkWinCondition();
    }

    private void handleHello(ClientHandler client) throws Exception {
        if (gameState.getPlayers().size() >= 4) {
            client.send(new Message(MessageType.ERROR, "Game is full!"));
            return;
        }
        int pid = nextPlayerId++;
        client.setPlayerId(pid);
        Player newPlayer = new Player(pid, "Player " + pid, 1500);
        gameState.addPlayer(newPlayer);
        client.send(new Message(MessageType.WELCOME, "ID:" + pid));
        broadcastUpdate("Player " + pid + " joined.");
    }

    private void handleRollDice(ClientHandler client) {
        Player p = turnManager.getCurrentPlayer();
        if (p.getStatus() == PlayerStatus.IN_JAIL) {
            turnManager.applyJailDoubleLogic(p);
        } else {
            turnManager.playTurn();
        }
        broadcastUpdate("Dice rolled.");
    }

    private void handleBuyProperty(Player p) {
        Tile tile = gameState.getBoard().getAllTiles().getTileAtPosition(p.getCurrentPosition());
        if (tile.getType() == TileType.PROPERTY) {
            Property prop = (Property) tile.getData();
            if (transactionManager.buyProperty(gameState.nextActionId(), p, prop)) {
                broadcastUpdate("Bought " + prop.getName());
            }
        }
    }

    private void handleBuild(Player p, String data) {
        try {
            String[] parts = data.split(":");
            int propId = Integer.parseInt(parts[0]);
            BuildingType type = BuildingType.valueOf(parts[1]);
            Property prop = gameState.getProperties().get(propId);

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
        if (transactionManager.applyMortgage(gameState.nextActionId(), p.getPlayerID(), prop, isMortgaging)) {
            broadcastUpdate(isMortgaging ? "Property mortgaged" : "Property unmortgaged");
        }
    }

    private void handleTradeProposal(Player p, String data) {
        network.broadcast(new Message(MessageType.PROPOSE_TRADE, data));
    }

    private void handleTradeResponse(ClientHandler client, boolean accepted) {
        if (accepted) {
            broadcastUpdate("Trade completed!");
        } else {
            broadcastUpdate("Trade rejected.");
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
}