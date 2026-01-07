package client.ui.GUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class GameReportPanel extends JPanel {

    private JLabel richestPlayerLabel;
    private JLabel rentKingLabel;

    public GameReportPanel() {
        setLayout(new GridLayout(1, 2, 20, 0));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(120, 120, 120)),
                "📊 Game Reports",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14),
                new Color(30, 30, 30)
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
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(new Color(70, 70, 70));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return label;
    }

    public void updateRichest(String name, int balance) {
        SwingUtilities.invokeLater(() -> {
            richestPlayerLabel.setText(String.format("👑 Richest Player: %s ($%d)", name, balance));
            richestPlayerLabel.setForeground(new Color(0, 102, 0));
        });
    }

    public void updateRentKing(String name, int totalRent) {
        SwingUtilities.invokeLater(() -> {
            rentKingLabel.setText(String.format("🏠 Top Rent Earner: %s ($%d)", name, totalRent));
            rentKingLabel.setForeground(new Color(0, 51, 153));
        });
    }

    public void reset() {
        richestPlayerLabel.setText("👑 Richest Player: -");
        rentKingLabel.setText("🏠 Top Rent Earner: -");
        richestPlayerLabel.setForeground(Color.GRAY);
        rentKingLabel.setForeground(Color.GRAY);
    }
}