package server.action;

import server.model.Player;

public class PayRentAction extends Action {

    private final Player receiver;

    private final int payerPrevBalance;
    private final int payerNewBalance;

    private final Integer receiverPrevBalance;
    private final Integer receiverNewBalance;
    private final int rent;

    public PayRentAction(int actionId, Player payer, Player receiver, int payerPrevBalance, int payerNewBalance, Integer receiverPrevBalance, Integer receiverNewBalance, int rent) {

        super(actionId, ActionType.PAY_RENT, payer.getPlayerID(), payer);
        this.rent = rent;
        this.receiver = receiver;
        this.payerPrevBalance = payerPrevBalance;
        this.payerNewBalance = payerNewBalance;
        this.receiverPrevBalance = receiverPrevBalance;
        this.receiverNewBalance = receiverNewBalance;
    }

    @Override
    public void redo() {
        Player payer = (Player) affectedEntities;
        payer.setBalance(payerNewBalance);

        if (receiver != null) {
            receiver.setBalance(receiverNewBalance);
            receiver.addRentIncome(rent);
        }
    }

    @Override
    public void undo() {
        Player payer = (Player) affectedEntities;
        payer.setBalance(payerPrevBalance);

        if (receiver != null) {
            receiver.setBalance(receiverPrevBalance);
            receiver.subtractRentIncome(rent);
        }
    }
}
