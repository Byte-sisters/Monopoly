package server.network;

import java.io.*;
import java.net.Socket;

import server.core.GameServer;

public class ClientHandler implements Runnable {

    private Socket socket;
    private GameServer gameServer;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private int playerId = -1;

    public ClientHandler(Socket socket, GameServer gameServer) {
        this.socket = socket;
        this.gameServer = gameServer;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                Message msg = (Message) in.readObject();
                gameServer.handleMessage(this, msg);
            }

        } catch (Exception e) {
            System.out.println("Client disconnected");
        }
    }
    public void send(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            System.out.println("Failed to send to player " + playerId);
            close();
        }
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }
    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int id) {
        this.playerId = id;
    }
}
