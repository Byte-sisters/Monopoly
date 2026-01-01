package client.ui;

import java.util.Scanner;

import client.ClientApp;
import server.network.Message;
import server.network.MessageType;

public class ConsoleUI {

    private ClientApp app;
    private Scanner scanner = new Scanner(System.in);

    public ConsoleUI(ClientApp app) {
        this.app = app;
    }

    public void start() {
        System.out.println("Commands: roll | end | exit");

        while (true) {
            System.out.print("> ");
            String cmd = scanner.nextLine();

            try {
                switch (cmd) {
                    case "roll":
                        app.send(new Message(MessageType.ROLL_DICE, ""));
                        break;

                    case "end":
                        app.send(new Message(MessageType.END_TURN, ""));
                        break;

                    case "exit":
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Unknown command");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
