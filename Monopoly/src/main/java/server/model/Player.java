package server.model;

import server.action.Action;
import server.datastructure.MyHashTable;
import server.datastructure.MyStack;

public class Player {
    private final int playerID;
    private final String name;
    private int balance;
    private int currentPosition;
    private int rentIncome;
    private PlayerStatus status;
    private int jailTurns;
    private MyHashTable<Property> ownedProperties;
    private MyStack undoStack;
    private MyStack redoStack;

    public Player(int playerID, String name, int initialBalance) {
        this.playerID = playerID;
        this.name = name;
        this.balance = initialBalance;
        this.currentPosition = 0;
        this.status = PlayerStatus.ACTIVE;
        this.jailTurns = 0;

        this.ownedProperties = new MyHashTable<>(20);
        this.undoStack = new MyStack();
        this.redoStack = new MyStack();
    }

    public void addMoney(int amount) {
        balance += amount;
    }

    public void deductMoney(int amount) {
        balance -= amount;
        if (balance < 0) {
            declareBankruptcy();
        }
    }

    public void moveTo(int newPosition) {
        this.currentPosition = newPosition;
    }

    public void sendToJail() {
        this.status = PlayerStatus.IN_JAIL;
        this.jailTurns = 0;
    }

    public void incrementJailTurn() {
        if (status == PlayerStatus.IN_JAIL) {
            jailTurns++;
        }
    }

    public void releaseFromJail() {
        this.status = PlayerStatus.ACTIVE;
        this.jailTurns = 0;
    }

    public void addProperty(Property property) {
        ownedProperties.insert(property.getPropertyID(), property);
    }

    public void removeProperty(int propertyID) {
        ownedProperties.remove(propertyID);
    }

    public boolean ownsProperty(int propertyID) {
        return ownedProperties.contains(propertyID);
    }

    public void recordAction(Action action) {
        undoStack.push(action);
        redoStack.clear();
    }

    public Action undo() {
        if (undoStack.isEmpty()) return null;

        Action action = undoStack.pop();
        action.undo();
        redoStack.push(action);
        return action;
    }

    public Action redo() {
        if (redoStack.isEmpty()) return null;

        Action action = redoStack.pop();
        action.redo();
        undoStack.push(action);
        return action;
    }

    public int getJailTurns() {
        return jailTurns;
    }

    public void declareBankruptcy() {
        status = PlayerStatus.BANKRUPT;
    }

    public int getPlayerID() {
        return playerID;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public boolean isBankrupt() {
        return status == PlayerStatus.BANKRUPT;
    }

    public void setCurrentPosition(int newPosition) {
        this.currentPosition = newPosition;
    }

    public void setBalance(int newBalance) {
        this.balance = newBalance;
    }

    public int getRentIncome() {
        return rentIncome;
    }

}

