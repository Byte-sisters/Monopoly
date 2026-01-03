package client.ui.GUI;

import javax.swing.*;
import java.awt.*;

import server.model.Board;
import server.model.Tile;
import server.model.Player;
import server.model.TileType;

public class BoardPanel extends JPanel {
    private SquareTile[] guiTiles = new SquareTile[40];

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
                price = "ID: " + t.getId();
            }

            guiTiles[i] = new SquareTile(name, price, typeStr, colorGroup);
        }

        assembleGrid();
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
        public static void main(String[] args) {
            Board board = new Board();

            Player[] testPlayers = new Player[4];
            testPlayers[0] = new Player(0, "Player 1", 1500);
            testPlayers[1] = new Player(1, "Player 2", 1500);
            testPlayers[2] = new Player(2, "Player 3", 1500);
            testPlayers[3] = new Player(3, "Player 4", 1500);
            testPlayers[0].moveTo(0);
            testPlayers[1].moveTo(10);
            testPlayers[2].moveTo(20);
            testPlayers[3].moveTo(39);

            JFrame frame = new JFrame("Monopoly Board Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            BoardPanel boardPanel = new BoardPanel(board);

            frame.add(boardPanel, BorderLayout.CENTER);

            boardPanel.updateTokens(testPlayers);

            frame.setSize(900, 900);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            System.out.println("Board UI initialized successfully.");
        }
    }

