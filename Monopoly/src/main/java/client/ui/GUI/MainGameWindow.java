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
            app.send(new Message(MessageType.valueOf(command), ""));
        });

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
        if (data == null || data.isEmpty()) return;

        try {
            boardPanel.clearAllTokens();

            String[] lines = data.split("\n");

            for (String line : lines) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split("[^0-9]+");

                java.util.List<String> digits = new java.util.ArrayList<>();
                for (String p : parts) {
                    if (!p.isEmpty()) digits.add(p);
                }

                if (digits.size() >= 3) {
                    int id = Integer.parseInt(digits.get(0));
                    int money = Integer.parseInt(digits.get(1));
                    int pos = Integer.parseInt(digits.get(2));

                    playersPanel.updatePlayer(id - 1, "Player " + id, money, "Active", pos, 0);

                    boardPanel.moveToken(id, pos);
                }
            }

            repaint();

        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
        }
    }
    public LogPanel getLogPanel() { return logPanel; }
    public PlayerControlPanel getControlPanel() { return controlPanel; }
    public GameReportPanel getGameReportPanel() { return reportPanel; }
}