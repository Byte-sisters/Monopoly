package client.ui.GUI;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class PlayerControlPanel extends JPanel {
    private JButton rollDiceBtn;
    private JButton endTurnBtn;
    private JButton undoBtn;
    private JButton redoBtn;

    private JButton buyPropertyBtn;
    private JButton proposeDealBtn;
    private JButton acceptDealBtn;
    private JButton rejectDealBtn;
    private JButton buildHouseBtn;
    private JButton buildHotelBtn;
    private JButton mortgageBtn;
    private JButton unmortgageBtn;

    private JButton useJailCardBtn;
    private JButton payJailFineBtn;
    private JButton tryDoubleBtn;

    private ActionListener actionListener;

    public PlayerControlPanel(ActionListener listener) {
        this.actionListener = listener;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 240, 240));

        initComponents();
        arrangeComponents();
        disableConditionalButtons();
    }

    private void initComponents() {
        rollDiceBtn = createButton("🎲 Roll Dice", "ROLL_DICE", Color.GREEN);
        endTurnBtn = createButton("⏭️ End Turn", "END_TURN", Color.ORANGE);
        undoBtn = createButton("↩️ Undo", "UNDO", Color.CYAN);
        redoBtn = createButton("↪️ Redo", "REDO", Color.CYAN);

        buyPropertyBtn = createButton("💰 Buy Property", "BUY_PROPERTY", new Color(34, 139, 34));
        proposeDealBtn = createButton("🤝 Propose Deal", "PROPOSE_DEAL", new Color(70, 130, 180));
        acceptDealBtn = createButton("✅ Accept Deal", "ACCEPT_DEAL", new Color(50, 205, 50));
        rejectDealBtn = createButton("❌ Reject Deal", "REJECT_DEAL", Color.RED);
        buildHouseBtn = createButton("🏠 Build House", "BUILD_HOUSE", new Color(210, 105, 30));
        buildHotelBtn = createButton("🏨 Build Hotel", "BUILD_HOTEL", new Color(178, 34, 34));
        mortgageBtn = createButton("🏦 Mortgage", "MORTGAGE", new Color(128, 0, 128));
        unmortgageBtn = createButton("💵 Unmortgage", "UNMORTGAGE", new Color(75, 0, 130));

        useJailCardBtn = createButton("🎫 Use Jail Card", "USE_JAIL_CARD", new Color(255, 215, 0));
        payJailFineBtn = createButton("💸 Pay Jail Fine", "PAY_JAIL_FINE", new Color(218, 165, 32));
        tryDoubleBtn = createButton("🎲 Try Double", "TRY_DOUBLE", new Color(255, 140, 0));

        rollDiceBtn.setToolTipText("Roll dice to move");
        endTurnBtn.setToolTipText("End your turn");
        undoBtn.setToolTipText("Undo last action");
        redoBtn.setToolTipText("Redo undone action");
        buyPropertyBtn.setToolTipText("Buy current property");
        buildHouseBtn.setToolTipText("Build house on owned property");
        buildHotelBtn.setToolTipText("Build hotel on owned property");
    }

    private JButton createButton(String text, String actionCommand, Color color) {
        JButton button = new JButton(text);
        button.setActionCommand(actionCommand);
        button.addActionListener(actionListener);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(color.brighter());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(color);
                }
            }
        });

        return button;
    }

    private void arrangeComponents() {
        JPanel mainControlPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        mainControlPanel.setBackground(new Color(220, 220, 220));
        mainControlPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
                "Main Controls",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14),
                Color.BLUE
        ));

        mainControlPanel.add(rollDiceBtn);
        mainControlPanel.add(endTurnBtn);
        mainControlPanel.add(undoBtn);
        mainControlPanel.add(redoBtn);

        JPanel conditionalPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        conditionalPanel.setBackground(new Color(230, 230, 230));
        conditionalPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                "Conditional Actions",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12),
                new Color(0, 100, 0)
        ));

        conditionalPanel.add(buyPropertyBtn);
        conditionalPanel.add(proposeDealBtn);
        conditionalPanel.add(acceptDealBtn);
        conditionalPanel.add(rejectDealBtn);
        conditionalPanel.add(buildHouseBtn);
        conditionalPanel.add(buildHotelBtn);
        conditionalPanel.add(mortgageBtn);
        conditionalPanel.add(unmortgageBtn);

        JPanel jailPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        jailPanel.setBackground(new Color(240, 230, 200));
        jailPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                "Jail Actions",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12),
                new Color(139, 0, 0)
        ));

        jailPanel.add(useJailCardBtn);
        jailPanel.add(payJailFineBtn);
        jailPanel.add(tryDoubleBtn);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(mainControlPanel, BorderLayout.CENTER);

        JPanel middlePanel = new JPanel(new BorderLayout(10, 10));
        middlePanel.add(conditionalPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.add(jailPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void disableConditionalButtons() {
        buyPropertyBtn.setEnabled(false);
        proposeDealBtn.setEnabled(false);
        acceptDealBtn.setEnabled(false);
        rejectDealBtn.setEnabled(false);
        buildHouseBtn.setEnabled(false);
        buildHotelBtn.setEnabled(false);
        mortgageBtn.setEnabled(false);
        unmortgageBtn.setEnabled(false);
        useJailCardBtn.setEnabled(false);
        payJailFineBtn.setEnabled(false);
        tryDoubleBtn.setEnabled(false);
    }

    public void enableMainControls(boolean enable) {
        rollDiceBtn.setEnabled(enable);
        endTurnBtn.setEnabled(enable);
        undoBtn.setEnabled(enable);
        redoBtn.setEnabled(enable);
    }
    public void setBuyPropertyMode(boolean canBuy, int price) {
        buyPropertyBtn.setEnabled(canBuy);
        if (canBuy) {
            buyPropertyBtn.setText("💰 Buy ($" + price + ")");
        } else {
            buyPropertyBtn.setText("💰 Buy Property");
        }
    }
    public void setBuildMode(boolean canBuildHouse, boolean canBuildHotel, int houseCost, int hotelCost) {
        buildHouseBtn.setEnabled(canBuildHouse);
        buildHotelBtn.setEnabled(canBuildHotel);

        if (canBuildHouse) {
            buildHouseBtn.setText("🏠 Build House ($" + houseCost + ")");
        }
        if (canBuildHotel) {
            buildHotelBtn.setText("🏨 Build Hotel ($" + hotelCost + ")");
        }
    }
    public void setMortgageMode(boolean canMortgage, boolean canUnmortgage) {
        mortgageBtn.setEnabled(canMortgage);
        unmortgageBtn.setEnabled(canUnmortgage);
    }
    public void setJailMode(boolean inJail, boolean hasJailCard, int fineAmount) {
        useJailCardBtn.setEnabled(inJail && hasJailCard);
        payJailFineBtn.setEnabled(inJail);
        tryDoubleBtn.setEnabled(inJail);

        if (inJail) {
            payJailFineBtn.setText("💸 Pay Fine ($" + fineAmount + ")");
        }
    }
    public void setTradeMode(boolean canPropose, boolean hasPendingDeal) {
        proposeDealBtn.setEnabled(canPropose);
        acceptDealBtn.setEnabled(hasPendingDeal);
        rejectDealBtn.setEnabled(hasPendingDeal);
    }
    public void setUndoRedoState(boolean canUndo, boolean canRedo) {
        undoBtn.setEnabled(canUndo);
        redoBtn.setEnabled(canRedo);
    }
    public void setTurnStatus(boolean isMyTurn, String playerName) {
        if (isMyTurn) {
            rollDiceBtn.setBackground(new Color(0, 150, 0));
            rollDiceBtn.setText("🎲 YOUR TURN - Roll Dice");
        } else {
            rollDiceBtn.setBackground(new Color(0, 100, 0));
            rollDiceBtn.setText("🎲 Waiting for " + playerName);
            rollDiceBtn.setEnabled(false);
        }
    }

    public JButton getRollDiceBtn() { return rollDiceBtn; }
    public JButton getEndTurnBtn() { return endTurnBtn; }
    public JButton getBuyPropertyBtn() { return buyPropertyBtn; }
    public JButton getProposeDealBtn() { return proposeDealBtn; }
    public JButton getAcceptDealBtn() { return acceptDealBtn; }
    public JButton getRejectDealBtn() { return rejectDealBtn; }
    public JButton getBuildHouseBtn() { return buildHouseBtn; }
    public JButton getBuildHotelBtn() { return buildHotelBtn; }
    public JButton getMortgageBtn() { return mortgageBtn; }
    public JButton getUnmortgageBtn() { return unmortgageBtn; }
    public JButton getUseJailCardBtn() { return useJailCardBtn; }
    public JButton getPayJailFineBtn() { return payJailFineBtn; }
    public JButton getTryDoubleBtn() { return tryDoubleBtn; }
    public JButton getUndoBtn() { return undoBtn; }
    public JButton getRedoBtn() { return redoBtn; }
}