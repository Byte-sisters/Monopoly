package server.network;

import server.core.GameServer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final GameServer gameServer;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private int playerId = -1;
    private boolean running = true;

    public ClientHandler(Socket socket, GameServer gameServer) {
        this.socket = socket;
        this.gameServer = gameServer;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            while(running) {
                Message msg = (Message) in.readObject();
                gameServer.handleMessage(this, msg);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Client disconnected (Player " + playerId + ")");
        } finally {
            close();
        }
    }

    public synchronized void send(Message msg) throws Exception {
        out.writeObject(msg);
        out.flush();
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    private void close() {
        running = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            socket.close();
        } catch (Exception ignored) {
        }
    }
}
