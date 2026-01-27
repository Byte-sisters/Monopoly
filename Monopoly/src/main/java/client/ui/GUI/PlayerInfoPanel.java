package client.ui.GUI;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

public class PlayerInfoPanel extends JPanel {
    private int playerID;
    private JLabel nameLabel;
    private JLabel balanceLabel;
    private JLabel statusLabel;
    private JLabel positionLabel;
    private JLabel assetsCountLabel;
    private Color playerColor;
    private DefaultTreeModel treeModel;

    public PlayerInfoPanel(int playerID, String playerName, Color color) {
        this.playerID = playerID;
        this.playerColor = color;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        setPreferredSize(new Dimension(150, 180));

        initComponents(playerName);
    }

    private void initComponents(String playerName) {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(playerColor);
        headerPanel.setMaximumSize(new Dimension(150, 30));

        nameLabel = new JLabel("  " + playerName + "  ");
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        headerPanel.add(nameLabel);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        balanceLabel = new JLabel("BALANCE: 0");
        statusLabel = new JLabel("STATUS: -");
        positionLabel = new JLabel("POSITION: 0");
        assetsCountLabel = new JLabel("ASSETS: 0");

        Font font = new Font("Tahoma", Font.PLAIN, 11);
        balanceLabel.setFont(font);
        statusLabel.setFont(font);
        positionLabel.setFont(font);
        assetsCountLabel.setFont(font);

        infoPanel.add(balanceLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(statusLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(positionLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(assetsCountLabel);

        add(headerPanel);
        add(infoPanel);
    }

    public void updateInfo(
            String playerName,
            int balance,
            String status,
            int position,
            int assetsCount
    ) {
        nameLabel.setText("NAME: " + playerName + "  ");
        balanceLabel.setText("BALANCE: " + balance);
        statusLabel.setText("STATUS: " + status);
        positionLabel.setText("POSITION: " + position);
        assetsCountLabel.setText("ASSETS: " + assetsCount + "  ");

        if (status.contains("ACTIVE")) {
            statusLabel.setForeground(Color.GREEN.darker());
        } else if (status.contains("IN_JAIL")) {
            statusLabel.setForeground(Color.ORANGE.darker());
        } else if (status.contains("BANKRUPT")) {
            statusLabel.setForeground(Color.RED.darker());
        } else {
            statusLabel.setForeground(Color.BLACK);
        }
    }

    public void setMyTurn(boolean isMyTurn) {
        if (isMyTurn) {
            setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
        } else {
            setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        }
    }

    public int getPlayerID() {
        return playerID;
    }

    public String getStatus() {
        return statusLabel.getText().replace("STATUS: ", "").trim();
    }

}