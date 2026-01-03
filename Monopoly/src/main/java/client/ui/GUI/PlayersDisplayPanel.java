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
                new Color(255, 200, 200),
                new Color(200, 200, 255),
                new Color(200, 255, 200),
                new Color(255, 255, 200),
        };

        for (int i = 0; i < 4; i++) {
            String name = "بازیکن " + (i + 1);
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
}