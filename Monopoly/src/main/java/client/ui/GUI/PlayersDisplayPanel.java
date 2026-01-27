package client.ui.GUI;

import javax.swing.*;
import java.awt.*;

public class PlayersDisplayPanel extends JPanel {
    private PlayerInfoPanel[] players;

    public PlayersDisplayPanel() {
        players = new PlayerInfoPanel[4];

        setLayout(new GridLayout(2, 2, 5, 5));
        setBorder(BorderFactory.createTitledBorder("Players"));

        Color[] colors = {
                new Color(0, 149, 255),
                new Color(39, 241, 10),
                new Color(255, 46, 206),
                new Color(255, 245, 0),
        };

        for (int i = 0; i < 4; i++) {
            String name = "player " + (i + 1);
            players[i] = new PlayerInfoPanel(i, name, colors[i]);
            add(players[i]);
        }
    }

    public void updatePlayer(int index, String name, int balance, String status, int position, int assets) {
        if (index >= 0 && index < 4) {
            players[index].updateInfo(name, balance, status, position, assets);
        }
    }

    public void setTurn(int playerIndex) {
        for (int i = 0; i < 4; i++) {
            players[i].setMyTurn(i == playerIndex);
        }
    }

    public void reset() {
        for (int i = 0; i < 4; i++) {
            players[i].updateInfo("Player " + (i + 1), 0, "waiting", 0, 0);
            players[i].setMyTurn(false);
        }
    }
    public String getPlayerStatus(int playerId) {
        if (playerId <= 0 || playerId > players.length) return "UNKNOWN";

        PlayerInfoPanel panel = players[playerId - 1];
        if (panel == null) return "UNKNOWN";

        return panel.getStatus();
    }

}