package server.core;

import server.action.Action;
import server.datastructure.MyHashTable;
import server.datastructure.MyStack;
import server.model.*;

public class GameState {
    private Board board;
    private CardManager cardManager;
    private MyStack undoStack;
    private MyStack redoStack;
    private MyHashTable<Property> properties;
    private MyHashTable<Player> players;

    public GameState() {
        initialize();
    }

    public void initialize() {
        board = new Board();
        cardManager = new CardManager();
        initializeCard();
        undoStack = new MyStack();
        redoStack = new MyStack();
        properties = new MyHashTable<>(26);
        players = new MyHashTable<>(4);
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

    public void initializeProperty() {
        properties.insert(1 ,new Property(1,"mediterranean", "brown", 60,10));
        properties.insert(2,new Property(2,"baltic", "brown", 70,15));
        properties.insert(3, new Property(3,"mehran", "purple", 200,50));
        properties.insert(4, new Property(4,"mehran", "purple", 200,50));
        properties.insert(5, new Property(5,"vermont", "blue", 100,20));
        properties.insert(6, new Property(6,"connecticut", "blue", 120,25));
        properties.insert(7, new Property(7,"st.charles", "pink", 140,30));
        properties.insert(8, new Property(8,"states", "pink", 140,30));
        properties.insert(9, new Property(9,"virginia", "pink", 160,35));
        properties.insert(10, new Property(10,"afarin", "purple", 200,50));
        properties.insert(11, new Property(11,"st.james", "orange", 180,40));
        properties.insert(12, new Property(12,"tennessee", "orange", 180,40));
        properties.insert(13, new Property(13,"NYC", "orange", 200,45));
        properties.insert(14, new Property(14,"kentucky", "red", 220,50));
        properties.insert(15, new Property(15,"indiana", "red", 220,50));
        properties.insert(16, new Property(16,"illinois", "red", 240,55));
        properties.insert(17, new Property(17,"nejadasghar", "purple", 200,50));
        properties.insert(18, new Property(18,"atlantic", "yellow", 260,60));
        properties.insert(19, new Property(19,"ventnor", "yellow", 260,60));
        properties.insert(20, new Property(20,"marvin", "yellow", 280,65));
        properties.insert(21, new Property(21,"pacific", "green", 300,70));
        properties.insert(22, new Property(22,"carolina", "green", 300,70));
        properties.insert(23, new Property(23,"pennsylvania", "green", 320,75));
        properties.insert(24, new Property(24,"ab doogh khiarr", "purple", 200,50));
        properties.insert(25, new Property(25,"park place", "navy blue", 350,80));
        properties.insert(26, new Property(26,"boardwalk", "navy blue", 400,85));

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
