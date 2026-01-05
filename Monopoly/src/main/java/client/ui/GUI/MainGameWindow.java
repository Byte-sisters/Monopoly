package client.ui.GUI;

import server.model.Board;

import javax.swing.*;
import java.awt.*;

public class MainGameWindow extends JFrame {

    public MainGameWindow(
            BoardPanel boardPanel,
            PlayerControlPanel controlPanel,
            PlayersDisplayPanel playersPanel,
            GameReportPanel reportPanel
    ) {
        setTitle("Monopoly");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 900);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        add(playersPanel, BorderLayout.WEST);
        add(reportPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            server.core.GameState gameState = new server.core.GameState();

            BoardPanel boardPanel = new BoardPanel(gameState.getBoard());
            PlayersDisplayPanel playersPanel = new PlayersDisplayPanel();
            GameReportPanel reportPanel = new GameReportPanel();

            PlayerControlPanel controlPanel = new PlayerControlPanel(e -> {
                reportPanel.updateReports(gameState);
            });

            MainGameWindow window = new MainGameWindow(
                    boardPanel,
                    controlPanel,
                    playersPanel,
                    reportPanel
            );

            window.setVisible(true);
        });
    }
}