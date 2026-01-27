package client.ui.GUI;

import client.ClientApp;
import server.model.Board;
import server.network.Message;
import server.network.MessageType;
import javax.swing.*;
import java.awt.*;

public class MainGameWindow extends JFrame {
    private BoardPanel boardPanel;
    private PlayerControlPanel controlPanel;
    private PlayersDisplayPanel playersPanel;
    private GameReportPanel reportPanel;
    private LogPanel logPanel;
    private ClientApp app;

    public MainGameWindow(ClientApp app) {
        this.app = app;
        setTitle("Monopoly - Player " + app.getMyPlayerId());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1450, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        boardPanel = new BoardPanel(new Board());
        playersPanel = new PlayersDisplayPanel();
        reportPanel = new GameReportPanel();
        logPanel = new LogPanel();

        controlPanel = new PlayerControlPanel(e -> {
            String command = e.getActionCommand();
            if (   command.equals("UNDO") || command.equals("REDO") ||
                    command.equals("JAIL_PAY_FINE") || command.equals("JAIL_TRY_DOUBLE") ||
                    command.equals("BUY_PROPERTY")) {
                app.send(new Message(MessageType.valueOf(command)));
            }
        });

        setupActionListeners();

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(playersPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(controlPanel, BorderLayout.NORTH);
        rightPanel.add(logPanel, BorderLayout.CENTER);

        add(boardPanel, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
        add(reportPanel, BorderLayout.SOUTH);
    }

    public void updateGameState(String data) {
        if (data == null || data.trim().isEmpty()) return;

        try {
            boardPanel.clearAllTokens();

            String[] lines = data.split("\n");

            boolean readingPlayers = false;
            boolean readingPendingBuy = false;
            boolean readingPendingTrade = false;
            boolean readingBuild = false;
            boolean readingMortgage = false;

            boolean canBuy = false;
            boolean hasPendingTrade = false;
            boolean canBuildHouse = false;
            boolean canBuildHotel = false;
            boolean canUnmortgage = false;
            boolean canMortgage = false;
            boolean hasRolled = false;
            int myId = app.getMyPlayerId();

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                if (line.equals("PLAYERS")) {
                    readingPlayers = true;
                    readingPendingBuy = readingPendingTrade = readingBuild = readingMortgage = false;
                    continue;
                }
                if (line.equals("PENDING_BUY")) {
                    readingPendingBuy = true;
                    readingPlayers = readingPendingTrade = readingBuild = readingMortgage = false;
                    continue;
                }
                if (line.equals("PENDING_TRADE")) {
                    readingPendingTrade = true;
                    readingPlayers = readingPendingBuy = readingBuild = readingMortgage = false;
                    continue;
                }
                if (line.equals("BUILD")) {
                    readingBuild = true;
                    readingPlayers = readingPendingBuy = readingPendingTrade = readingMortgage = false;
                    continue;
                }
                if (line.equals("MORTGAGE")) {
                    readingMortgage = true;
                    readingPlayers = readingPendingBuy = readingPendingTrade = readingBuild = false;
                    continue;
                }

                if (readingPlayers) {
                    String[] p = line.split(",");
                    if (p.length < 7) continue;

                    int id = Integer.parseInt(p[0]);
                    String name = p[1];
                    int balance = Integer.parseInt(p[2]);
                    int position = Integer.parseInt(p[3]);
                    String status = p[4];
                    int assets = Integer.parseInt(p[5]);
                    boolean playerHasRolled = Boolean.parseBoolean(p[6]);
                    if (id == myId) {
                        hasRolled = playerHasRolled;
                    }
                    playersPanel.updatePlayer(
                            id - 1,
                            name,
                            balance,
                            status,
                            position,
                            assets
                    );

                    boardPanel.moveToken(id, position);
                }

                else if (readingPendingBuy) {
                    if (!line.equals("NONE")) {
                        String[] p = line.split(",");
                        int pid = Integer.parseInt(p[0]);
                        canBuy = (pid == myId);
                    }
                }

                else if (readingPendingTrade) {
                    if (!line.equals("NONE")) {
                        String[] t = line.split(",");
                        int proposerId = Integer.parseInt(t[0]);
                        int targetId = Integer.parseInt(t[1]);
                        hasPendingTrade = (targetId == myId);
                        controlPanel.setTradeMode(true, hasPendingTrade);
                    } else {
                        controlPanel.setTradeMode(true, false);
                    }
                }

                else if (readingBuild) {
                    if (line.startsWith("HOUSE:")) {
                        canBuildHouse = line.length() > 6;
                    }
                    if (line.startsWith("HOTEL:")) {
                        canBuildHotel = line.length() > 6;
                    }
                }

                else if (readingMortgage) {
                    if (!line.isEmpty()) {
                        String[] items = line.split(",");
                        for (String it : items) {
                            if (it.isEmpty()) continue;

                            String[] parts = it.split(":");
                            int ownerId = Integer.parseInt(parts[1]);
                            String state = parts[2];

                            if (ownerId == myId) {
                                if (state.equals("U")) canMortgage = true;
                                if (state.equals("M")) canUnmortgage = true;
                            }
                        }
                    }
                }

            }

            String playerStatus = playersPanel.getPlayerStatus(myId);
            boolean inJail = "IN_JAIL".equals(playerStatus);
            boolean finalHasRolled = hasRolled;
            SwingUtilities.invokeLater(() -> {
                controlPanel.enableRollButton(!inJail && !finalHasRolled);
            });
            controlPanel.enableBuyButton(canBuy && !inJail);
            controlPanel.enableTryDouble(inJail);
            controlPanel.enablePayFine(inJail);

            controlPanel.setTradeMode(true, hasPendingTrade);
            controlPanel.setBuildMode(canBuildHouse, canBuildHotel, 100, 100);
            controlPanel.setMortgageMode(canMortgage, canUnmortgage);

            repaint();

        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
        }
    }

    public LogPanel getLogPanel() { return logPanel; }
    public PlayerControlPanel getControlPanel() { return controlPanel; }
    public GameReportPanel getGameReportPanel() { return reportPanel; }
    private void setupActionListeners() {

        controlPanel.getRollDiceBtn().addActionListener(e -> {
            app.send(new Message(MessageType.ROLL_DICE));
            controlPanel.enableRollButton(false);
        });

        controlPanel.getEndTurnBtn().addActionListener(e -> {
            app.send(new Message(MessageType.END_TURN));
        });

        controlPanel.getMortgageBtn().addActionListener(e -> {
            int propId = boardPanel.getSelectedPropertyId();
            if (propId != -1) {
                app.send(new Message(MessageType.MORTGAGE, String.valueOf(propId)));
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select a property first!",
                        "Mortgage Error",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        controlPanel.getUnmortgageBtn().addActionListener(e -> {
            int propId = boardPanel.getSelectedPropertyId();
            if (propId != -1) {
                app.send(new Message(MessageType.UNMORTGAGE, String.valueOf(propId)));
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select a property first!",
                        "UnMortgage Error",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        controlPanel.getBuildHouseBtn().addActionListener(e -> {
            app.send(new Message(MessageType.BUILD, "HOUSE"));
        });

        controlPanel.getBuildHotelBtn().addActionListener(e -> {
            app.send(new Message(MessageType.BUILD, "HOTEL"));
        });

        controlPanel.getProposeDealBtn().addActionListener(e -> {
            String tradeData = promptTradeData();
            if (!tradeData.isEmpty()) {
                app.send(new Message(MessageType.PROPOSE_TRADE, tradeData));
            }
        });

        controlPanel.getAcceptDealBtn().addActionListener(e ->
                app.send(new Message(MessageType.ACCEPT_TRADE))
        );

        controlPanel.getRejectDealBtn().addActionListener(e ->
                app.send(new Message(MessageType.REJECT_TRADE))
        );
    }

    private String promptTradeData() {
        String input = JOptionPane.showInputDialog(this,
                "Enter trade info (format: fromPlayerId,toPlayerId,propertyAId,propertyBId,amount):",
                "Propose Trade",
                JOptionPane.PLAIN_MESSAGE);
        return input != null ? input.trim() : "";
    }
}