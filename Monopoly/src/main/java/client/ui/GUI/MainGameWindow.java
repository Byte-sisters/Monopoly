package client.ui.GUI;

import server.model.Board;

import javax.swing.*;
import java.awt.*;

public class MainGameWindow extends JFrame {

    public MainGameWindow(
            BoardPanel boardPanel,
            PlayerControlPanel controlPanel,
            PlayersDisplayPanel playersPanel
    ) {
        setTitle("Monopoly");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 900);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        add(playersPanel, BorderLayout.WEST);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            Board board = new Board();

            BoardPanel boardPanel = new BoardPanel(board);

            PlayerControlPanel controlPanel =
                    new PlayerControlPanel(e ->
                            System.out.println("Clicked: " + e.getActionCommand())
                    );

            PlayersDisplayPanel playersPanel = new PlayersDisplayPanel();

            MainGameWindow window =
                    new MainGameWindow(boardPanel, controlPanel, playersPanel);

            window.setVisible(true);
        });
    }

}
