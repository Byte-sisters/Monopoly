package client;

import client.network.ClientSocketManager;
import client.ui.GUI.DealViewModel;
import client.ui.GUI.MainGameWindow;
import server.network.Message;
import server.network.MessageType;

import javax.swing.*;

public class ClientApp {

    private ClientSocketManager network;
    private MainGameWindow mainWindow;
    private int myPlayerId = -1;

    public void start() {
        try {
            network = new ClientSocketManager("localhost", 5000);
            String playerName = JOptionPane.showInputDialog(
                    null,
                    "Enter your player name:",
                    "Player Name",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Player";
            }

            send(new Message(MessageType.HELLO, playerName.trim()));
            new Thread(this::listenToServer).start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Cannot connect to server:\n" + e.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void listenToServer() {
        try {
            while (true) {
                Message msg = network.receive();
                handleServerMessage(msg);
            }
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                if (mainWindow != null) {
                    mainWindow.getLogPanel()
                            .addLog("❌ Connection lost with server");
                }
            });
        }
    }

    private void handleServerMessage(Message msg) {
        SwingUtilities.invokeLater(() -> {
            switch (msg.getType()) {

                case WELCOME -> handleWelcome(msg.getData());

                case STATE_UPDATE -> {
                    if (mainWindow != null)
                        mainWindow.updateGameState(msg.getData());
                }

                case TURN_INFO -> handleTurnInfo(msg.getData());

                case EVENT_LOG -> {
                    if (mainWindow != null)
                        mainWindow.getLogPanel().addLog(msg.getData());
                }

                case REPORT_UPDATE -> handleReportUpdate(msg.getData());

                case PROPOSE_TRADE -> handleIncomingTrade(msg.getData());

                case ERROR -> JOptionPane.showMessageDialog(
                        mainWindow,
                        msg.getData(),
                        "Game Error",
                        JOptionPane.WARNING_MESSAGE
                );

                case GAME_OVER -> {
                    JOptionPane.showMessageDialog(
                            mainWindow,
                            "GAME OVER!\nWinner: " + msg.getData()
                    );
                    System.exit(0);
                }
            }
        });
    }


    private void handleWelcome(String data) {
        myPlayerId = Integer.parseInt(data.split(":")[1]);
        mainWindow = new MainGameWindow(this);
        mainWindow.setVisible(true);
    }

    private void handleTurnInfo(String data) {
        int currentTurn = Integer.parseInt(data);

        if (mainWindow == null) return;

        boolean myTurn = currentTurn == myPlayerId;
        mainWindow.getControlPanel()
                .setTurnStatus(myTurn, "P" + currentTurn);
        mainWindow.getControlPanel()
                .enableMainControls(myTurn);
    }

    private void handleReportUpdate(String data) {
        if (mainWindow == null) return;

        String[] parts = data.split("\\|");

        if (parts.length < 2) return;

        String[] richest = parts[0].split(":");
        String[] rentKing = parts[1].split(":");

        mainWindow.getGameReportPanel()
                .updateRichest(richest[0], Integer.parseInt(richest[1]));

        mainWindow.getGameReportPanel()
                .updateRentKing(rentKing[0], Integer.parseInt(rentKing[1]));
    }

    private void handleIncomingTrade(String data) {
        DealViewModel deal = parseDeal(data);

        if (deal.isIncoming) {
            showDealDialog(deal);
        }
    }

    private DealViewModel parseDeal(String data) {
        String[] parts = data.split(",");

        if (parts.length < 5) {
            System.err.println("Trade data incomplete: " + data);
            return null;
        }

        DealViewModel deal = new DealViewModel(5);
        deal.proposerId = Integer.parseInt(parts[0].trim());
        int targetId = Integer.parseInt(parts[1].trim());
        deal.isIncoming = (targetId == myPlayerId);
        if (!parts[2].trim().isEmpty()) {
            deal.offeredProperties[deal.offeredPropertiesCount++] = parts[2].trim();
        }
        if (!parts[3].trim().isEmpty()) {
            deal.requestedProperties[deal.requestedPropertiesCount++] = parts[3].trim();
        }
        deal.offeredMoney = Integer.parseInt(parts[4].trim());
        deal.requestedMoney = 0;

        return deal;
    }

    private void showDealDialog(DealViewModel deal) {
        StringBuilder sb = new StringBuilder();

        sb.append("🤝 Trade Proposal from Player ")
                .append(deal.proposerId)
                .append("\n\n");

        sb.append("Offers:\n");
        for (int i = 0; i < deal.offeredPropertiesCount; i++)
            sb.append("• ").append(deal.offeredProperties[i]).append("\n");

        sb.append("Money: $").append(deal.offeredMoney).append("\n\n");

        sb.append("Requests:\n");
        for (int i = 0; i < deal.requestedPropertiesCount; i++)
            sb.append("• ").append(deal.requestedProperties[i]).append("\n");

        sb.append("Money: $").append(deal.requestedMoney);

        int choice = JOptionPane.showConfirmDialog(
                mainWindow,
                sb.toString(),
                "Deal Proposal",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION)
            send(new Message(MessageType.ACCEPT_TRADE, ""));
        else
            send(new Message(MessageType.REJECT_TRADE, ""));
    }


    public void rollDice() { send(new Message(MessageType.ROLL_DICE, "")); }
    public void buyProperty() { send(new Message(MessageType.BUY_PROPERTY, "")); }
    public void payJailFine() { send(new Message(MessageType.JAIL_PAY_FINE, "")); }
    public void tryDouble() { send(new Message(MessageType. JAIL_TRY_DOUBLE, "")); }
    public void undo() { send(new Message(MessageType.UNDO, "")); }
    public void redo() { send(new Message(MessageType.REDO, "")); }
    public void endTurn() { send(new Message(MessageType.END_TURN, "")); }

    public void proposeTrade(String targetId, String details) {
        send(new Message(MessageType.PROPOSE_TRADE, targetId + "|" + details));
    }


    public void send(Message msg) {
        try {
            network.send(msg);
        } catch (Exception e) {
            System.err.println("Send failed: " + e.getMessage());
        }
    }

    public int getMyPlayerId() {
        return myPlayerId;
    }
}
