package server;

import server.core.GameServer;
import server.network.ServerSocketManager;

public class MainServer {

    public static void main(String[] args) {
        int port = 5000;

        ServerSocketManager network =
                new ServerSocketManager(port);

        GameServer gameServer =
                new GameServer(network);

        network.start(gameServer);
    }
}
