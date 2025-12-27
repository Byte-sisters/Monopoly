package server.core;

import server.action.Action;
import server.datastructure.MyStack;
import server.model.*;

public class GameState {
    private Board board;
    private CardManager cardManager;
    private MyStack undoStack;
    private MyStack redoStack;

    public GameState() {
        initialize();
    }

    public void initialize() {
        board = new Board();
        cardManager = new CardManager();
        initializeCard();
        undoStack = new MyStack();
        redoStack = new MyStack();
    }

    public void initializeCard() {

        cardManager.addCard(new Card(1, CardType.CHANCE,"go to jail", EffectType.GO_TO_JAIL,-1));
        cardManager.addCard(new Card(2,CardType.CHANCE,"move 5 tiles forward", EffectType.MOVE,5));
        cardManager.addCard(new Card(3,CardType.CHANCE,"give 20$ to bank", EffectType.LOSE_MONEY,20));
        cardManager.addCard(new Card(4,CardType.CHANCE,"move 10 tiles forward", EffectType.MOVE,10));
        cardManager.addCard(new Card(5, CardType.CHANCE, "give 10$ to bank", EffectType.LOSE_MONEY,10));

        cardManager.addCard(new Card(1,CardType.COMMUNITY,"get 20$ from bank", EffectType.GET_MONEY,20));
        cardManager.addCard(new Card(2,CardType.COMMUNITY,"give 10$ to each player", EffectType.PAY_TO_PLAYERS,10));
        cardManager.addCard(new Card(3,CardType.COMMUNITY,"get 10$ from bank", EffectType.GET_MONEY,10));
        cardManager.addCard(new Card(4,CardType.COMMUNITY,"give 20$ to each player", EffectType.PAY_TO_PLAYERS,20));
        cardManager.addCard(new Card(5, CardType.COMMUNITY,"get 50$ from bank", EffectType.GET_MONEY,50));
    }

    public void executeAction(Action action) {
        action.redo();
        undoStack.push(action);
        redoStack.clear();
    }

    public void undo() {
        if(undoStack.isEmpty()) {
            return;
        }
        Action action = undoStack.pop();
        action.undo();
        redoStack.push(action);
    }

    public void redo() {
        if(redoStack.isEmpty()) {
            return;
        }
        Action action = redoStack.pop();
        action.redo();
        undoStack.push(action);
    }

}
