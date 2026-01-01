package server.core;

import server.logic.TurnManager;
import server.network.ClientHandler;
import server.network.Message;
import server.network.MessageType;
import server.network.ServerSocketManager;

public class GameServer {

    private final ServerSocketManager network;
    private final GameState gameState;
    private final TurnManager turnManager;

    private int nextPlayerId = 1;

    public GameServer(ServerSocketManager network,
                      GameState gameState,
                      TurnManager turnManager) {

        this.network = network;
        this.gameState = gameState;
        this.turnManager = turnManager;
    }

    public synchronized void handleMessage(ClientHandler client, Message msg) {

        switch (msg.getType()) {

            case HELLO -> handleHello(client);

            case ROLL_DICE -> handleRollDice(client);

            case END_TURN -> handleEndTurn(client);

            default -> client.send(new Message(
                    MessageType.ERROR,
                    "Unsupported message type"
            ));
        }
    }

    // ---------------- HANDLERS ----------------

    private void handleHello(ClientHandler client) {
        int pid = nextPlayerId++;
        client.setPlayerId(pid);

        client.send(new Message(
                MessageType.WELCOME,
                "PLAYER_ID=" + pid
        ));

        network.broadcast(new Message(
                MessageType.EVENT_LOG,
                "Player " + pid + " joined the game"
        ));

        network.broadcast(new Message(
                MessageType.STATE_UPDATE,
                gameState.toString()
        ));
    }

    private void handleRollDice(ClientHandler client) {
        int pid = client.getPlayerId();

        if (pid == -1) {
            client.send(new Message(
                    MessageType.ERROR,
                    "Player not registered"
            ));
            return;
        }

        // بررسی نوبت
        if (turnManager.getCurrentPlayer().getPlayerID() != pid) {
            client.send(new Message(
                    MessageType.ERROR,
                    "Not your turn"
            ));
            return;
        }

        // اجرای کامل منطق بازی
        turnManager.playTurn();

        // ارسال وضعیت جدید برای همه
        network.broadcast(new Message(
                MessageType.STATE_UPDATE,
                gameState.toString()
        ));
    }

    private void handleEndTurn(ClientHandler client) {
        client.send(new Message(
                MessageType.ERROR,
                "END_TURN is handled automatically"
        ));
    }
}
