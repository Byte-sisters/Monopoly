package client;

import client.network.ClientSocketManager;
import client.ui.ConsoleUI;
import server.network.Message;
import server.network.MessageType;

public class ClientApp {

    private ClientSocketManager network;
    private ConsoleUI ui;

    public void start() {
        try {
            network = new ClientSocketManager("localhost", 5000);
            ui = new ConsoleUI(this);

            network.send(new Message(MessageType.HELLO, ""));

            Thread listener = new Thread(() -> {
                try {
                    while (true) {
                        Message msg = network.receive();
                        handleServerMessage(msg);
                    }
                } catch (Exception e) {
                    System.out.println("Disconnected from server");
                }
            });

            listener.start();

            ui.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleServerMessage(Message msg) {
        System.out.println(
                "[SERVER] " +
                        msg.getType() +
                        " -> " +
                        msg.getPayload()
        );
    }

    public void send(Message msg) throws Exception {
        network.send(msg);
    }
}
