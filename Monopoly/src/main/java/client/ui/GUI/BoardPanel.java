package client.ui.GUI;

import javax.swing.*;
import java.awt.*;

import server.model.Board;
import server.model.Tile;
import server.model.Player;
import server.model.TileType;

public class BoardPanel extends JPanel {
    private SquareTile[] guiTiles = new SquareTile[40];
    private int selectedPropertyId = -1;

    public BoardPanel(Board board) {
        this.setLayout(new GridLayout(11, 11));
        this.setBackground(new Color(188, 234, 188));

        for (int i = 0; i < 40; i++) {
            Tile t = board.getAllTiles().getTileAtPosition(i);

            String name = t.getName();
            String typeStr = t.getType().toString();
            String price = "";
            String colorGroup = getGroupColor(i);

            if (t.getType() == TileType.PROPERTY) {
                price = "ID: " + t.getData();
            }

            guiTiles[i] = new SquareTile(name, price, typeStr, colorGroup);
            final int tileIndex = i;

            guiTiles[i].addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {

                    Tile tile = board.getAllTiles().getTileAtPosition(tileIndex);

                    if (tile.getType() == TileType.PROPERTY) {

                        selectedPropertyId = (int) tile.getData();

                        highlightSelectedTile(tileIndex);
                    }
                }
            });

        }

        assembleGrid();
    }
    private void highlightSelectedTile(int tileIndex) {
        for (int i = 0; i < guiTiles.length; i++) {
            if (i == tileIndex) {
                guiTiles[i].setBorder(
                        BorderFactory.createLineBorder(Color.RED, 3)
                );
            } else {
                guiTiles[i].setBorder(
                        BorderFactory.createLineBorder(Color.BLACK, 1)
                );
            }
        }
        repaint();
    }


    private void assembleGrid() {
        Component[][] grid = new Component[11][11];

        for (int i = 0; i <= 10; i++) grid[10][10 - i] = guiTiles[i];

        for (int i = 1; i <= 9; i++) grid[10 - i][0] = guiTiles[10 + i];

        for (int i = 0; i <= 10; i++) grid[0][i] = guiTiles[20 + i];

        for (int i = 1; i <= 9; i++) grid[i][10] = guiTiles[30 + i];

        for (int r = 0; r < 11; r++) {
            for (int c = 0; c < 11; c++) {
                if (grid[r][c] != null) {
                    this.add(grid[r][c]);
                } else {
                    JPanel emptyPanel = new JPanel();
                    emptyPanel.setOpaque(false);
                    if (r == 5 && c == 5) {
                        emptyPanel.setLayout(new GridBagLayout());

                        JLabel label = new JLabel("MONOPOLY");
                        label.setFont(new Font("Arial Black", Font.BOLD, 10));
                        label.setForeground(new Color(180, 0, 0));

                        emptyPanel.add(label);
                    }
                    this.add(emptyPanel);
                }
            }
        }
    }
    private String getGroupColor(int i) {
        if (i == 1 || i == 3 || i == 6) return "BROWN";
        if (i == 5 || i == 15 || i == 25) return "PURPLE";
        if(i==8 || i==9 )return "BLUE";

        if (i == 16 || i == 18 || i == 19) return "ORANGE";
        if (i == 21 || i == 23 || i == 24) return "RED";
        if(i==11 || i==13 || i==14) return "PINK";

        if (i == 31 || i == 32 || i == 34) return "GREEN";
        if (i == 35 || i == 37 || i == 39) return "NAVY_BLUE";
        if (i == 26 || i == 27 || i == 29) return "YELLOW";

        return "";
    }
    public void updateTokens(Player[] players) {
        for (SquareTile tile : guiTiles) tile.clearTokens();

        if (players != null) {
            for (Player p : players) {
                if (p != null && !p.isBankrupt()) {
                    int pos = p.getCurrentPosition() % 40;
                    guiTiles[pos].addToken(getPlayerColor(p.getPlayerID()));
                }
            }
        }
        revalidate();
        repaint();
    }

    private Color getPlayerColor(int id) {
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.ORANGE};
        return colors[id % colors.length];
    }
    public void clearAllTokens() {
        for (SquareTile tile : guiTiles) {
            if (tile != null) {
                tile.clearTokens();
            }
        }
    }

    public void moveToken(int playerId, int position) {
        int pos = position % 40;
        if (guiTiles[pos] != null) {
            guiTiles[pos].addToken(getPlayerColor(playerId));
        }
        revalidate();
        repaint();
    }

    public int getSelectedPropertyId() {
        return selectedPropertyId;
    }
}

