package server;

import server.core.GameServer;
import server.core.GameState;
import server.logic.TurnManager;
import server.logic.TransactionManager;
import server.network.ServerSocketManager;

public class MainServer {

    public static void main(String[] args) {

        int port = 5000;

        GameState gameState = new GameState();
        TransactionManager tm = new TransactionManager(gameState);
        TurnManager turnManager = new TurnManager(gameState, tm);

        ServerSocketManager network =
                new ServerSocketManager(port);

        GameServer gameServer =
                new GameServer(network, gameState, turnManager);

        network.start(gameServer);
    }
}
