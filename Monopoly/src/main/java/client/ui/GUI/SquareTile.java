package client.ui.GUI;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class SquareTile extends JPanel {
    private JPanel playersPanel;
    private JLabel nameLabel;
    private JLabel priceLabel;
    private JLabel iconLabel;

    public SquareTile(String name, String price, String type, String colorGroupName) {
        this.setLayout(new BorderLayout());
        this.setBorder(new LineBorder(Color.BLACK, 1));
        this.setPreferredSize(new Dimension(85, 85));
        this.setBackground(Color.WHITE);

        JPanel centerPanel = new JPanel(new GridLayout(3, 1));
        centerPanel.setOpaque(false);

        iconLabel = new JLabel("", SwingConstants.CENTER);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));

        nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 9));

        priceLabel = new JLabel(price, SwingConstants.CENTER);
        priceLabel.setFont(new Font("SansSerif", Font.PLAIN, 9));

        switch (type.toUpperCase()) {
            case "PROPERTY":
                setupPropertyUI(colorGroupName);
                break;
            case "GO":
                this.setBackground(new Color(200, 255, 200));
                iconLabel.setText("🏁");
                nameLabel.setForeground(Color.RED);
                break;
            case "JAIL":
                this.setBackground(new Color(255, 230, 180));
                iconLabel.setText("⛓️");
                break;
            case "TAX":
                this.setBackground(new Color(240, 240, 240));
                iconLabel.setText("💰");
                nameLabel.setText("TAX");
                break;
            case "CARD":
                if (name.toUpperCase().contains("COMMUNITY")) {
                    iconLabel.setText("📦");
                    this.setBackground(new Color(230, 240, 255));
                } else {
                    iconLabel.setText("❓");
                    this.setBackground(new Color(255, 230, 230));
                }
                break;
        }

        centerPanel.add(iconLabel);
        centerPanel.add(nameLabel);
        centerPanel.add(priceLabel);
        add(centerPanel, BorderLayout.CENTER);

        playersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        playersPanel.setOpaque(false);
        playersPanel.setPreferredSize(new Dimension(0, 20));
        add(playersPanel, BorderLayout.SOUTH);
    }

    private void setupPropertyUI(String colorGroup) {
        JPanel colorBar = new JPanel();
        colorBar.setPreferredSize(new Dimension(0, 18));
        colorBar.setBackground(getRealColor(colorGroup));
        colorBar.setBorder(new LineBorder(Color.BLACK, 1));
        add(colorBar, BorderLayout.NORTH);
    }

    private Color getRealColor(String name) {
        if (name == null) return Color.WHITE;
        switch (name.toUpperCase()) {
            case "BROWN": return new Color(139, 69, 19);
            case "PURPLE": return new Color(170, 14, 149);
            case "NAVY_BLUE": return new Color(141, 208, 230);
            case "PINK": return new Color(255, 20, 147);
            case "ORANGE": return Color.ORANGE;
            case "RED": return Color.RED;
            case "YELLOW": return Color.YELLOW;
            case "GREEN": return new Color(34, 139, 34);
            case "BLUE": return Color.BLUE;
            default: return Color.WHITE;
        }
    }

    public void addToken(Color c) {
        JPanel token = new JPanel();
        token.setPreferredSize(new Dimension(12, 12));
        token.setBackground(c);
        token.setBorder(new LineBorder(Color.BLACK, 1));
        playersPanel.add(token);
        playersPanel.revalidate();
    }

    public void clearTokens() {
        playersPanel.removeAll();
        playersPanel.revalidate();
    }
}