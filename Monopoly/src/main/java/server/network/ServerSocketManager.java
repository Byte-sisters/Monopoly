package server.network;

import java.net.ServerSocket;
import java.net.Socket;

import server.core.GameServer;

public class ServerSocketManager {

    private int port;

    private ClientHandler[] clients = new ClientHandler[10];
    private int clientCount = 0;

    public ServerSocketManager(int port) {
        this.port = port;
    }

    public void start(GameServer gameServer) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");

                ClientHandler handler =
                        new ClientHandler(socket, gameServer);

                clients[clientCount] = handler;
                clientCount++;

                new Thread(handler).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void broadcast(Message msg) {
        for (int i = 0; i < clientCount; i++) {
            clients[i].send(msg);
        }
    }
}
