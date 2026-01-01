package client.network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import server.network.Message;

public class ClientSocketManager {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientSocketManager(String host, int port) throws Exception {
        socket = new Socket(host, port);

        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public synchronized void send(Message msg) throws Exception {
        out.writeObject(msg);
        out.flush();
    }

    public Message receive() throws Exception {
        return (Message) in.readObject();
    }
}
