package client.ui.GUI;

import server.core.GameState;
import server.model.Player;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class GameReportPanel extends JPanel {

    private JLabel richestPlayerLabel;
    private JLabel rentKingLabel;
    private GameState gameState;

    public GameReportPanel() {
        setLayout(new GridLayout(2, 1, 5, 5));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                "📊 Game Reports",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14)
        ));
        setBackground(new Color(245, 245, 245));

        initComponents();
    }

    private void initComponents() {
        richestPlayerLabel = createReportLabel("👑 Richest Player: -");
        rentKingLabel = createReportLabel("🏠 Top Rent Earner: -");

        add(richestPlayerLabel);
        add(rentKingLabel);
    }

    private JLabel createReportLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return label;
    }

    public void updateReports(GameState gameState) {
        updateRichestPlayer(gameState);
        updateTopRentEarner(gameState);
    }

    private void updateRichestPlayer(GameState gameState) {
        Player p = gameState.getRichestPlayerSafe();

        if (p != null) {
            richestPlayerLabel.setText(
                    String.format("👑 Richest Player: P%d | 💰 $%d",
                            p.getPlayerID(),
                            p.getBalance())
            );

            richestPlayerLabel.setForeground(new Color(0, 102, 0));
        } else {
            richestPlayerLabel.setText("👑 Richest Player: No Active Player");
            richestPlayerLabel.setForeground(Color.GRAY);
        }
    }
    private void updateTopRentEarner(GameState gameState) {
        Player p = gameState.getTopRentEarnerSafe();

        if (p != null) {
            rentKingLabel.setText(
                    String.format("🏠 Top Rent Earner: P%d | 💵 $%d",
                            p.getPlayerID(),
                            p.getRentIncome())
            );
            rentKingLabel.setForeground(new Color(0, 51, 153));
        } else {
            rentKingLabel.setText("🏠 Top Rent Earner: -");
            rentKingLabel.setForeground(Color.GRAY);
        }
    }

}
