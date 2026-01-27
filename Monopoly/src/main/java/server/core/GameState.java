package server.core;

import server.action.Action;
import server.datastructure.MyHashTable;
import server.datastructure.MyHeap;
import server.datastructure.MyStack;
import server.model.*;

public class GameState {
    private Board board;
    private CardManager cardManager;
    private MyStack undoStack;
    private MyStack redoStack;
    private MyHashTable<Property> properties;
    private MyHashTable<Player> players;
    private MyHeap<Player> wealthHeap;
    private MyHeap<Player> rentHeap;
    private int actionIdCounter;
    private int pendingTradePlayerA = -1;
    private int pendingTradePlayerB = -1;
    private int pendingTradePropertyA = -1;
    private Integer pendingTradePropertyB = null;
    private int pendingTradeAmount = 0;
    private Integer pendingBuyPlayerId = -1;
    private Integer pendingBuyPropertyId = -1;
    private int pendingTradeFrom = -1;
    private int pendingTradeTo = -1;
    private boolean[] canBuildHouse;
    private boolean[] canBuildHotel;
    private boolean[] mortgaged;

    public GameState() {
        initialize();
    }

    public void initialize() {
        board = new Board();
        cardManager = new CardManager();
        undoStack = new MyStack();
        redoStack = new MyStack();
        properties = new MyHashTable<>(26);
        players = new MyHashTable<>(4);
        wealthHeap = new MyHeap<>(4, (a, b) -> a.getBalance() - b.getBalance());
        rentHeap = new MyHeap<>(4, (a, b) -> a.getRentIncome() - b.getRentIncome());
        this.actionIdCounter = 0;
        initializeCard();
        initializeProperty();
        canBuildHouse = new boolean[properties.size()];
        canBuildHotel = new boolean[properties.size()];
        mortgaged = new boolean[properties.size()];
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
        properties.insert(4, new Property(4,"oriental avenue", "brown", 100,50));
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
        properties.insert(24, new Property(24,"ab doogh khiarr", "navy blue", 200,50));
        properties.insert(25, new Property(25,"park place", "navy blue", 350,80));
        properties.insert(26, new Property(26,"boardwalk", "navy blue", 400,85));

    }

    public void addPlayer(Player player) {
        players.insert(player.getPlayerID(), player);
        wealthHeap.insert(player);
        rentHeap.insert(player);
    }

    public Player getRichestPlayer() {
        return wealthHeap.peek();
    }

    public Player getTopRentEarner() {
        return rentHeap.peek();
    }

    public Player getRichestPlayerSafe() {
        Player p = wealthHeap.peek();
        if (p == null || p.isBankrupt()) return null;
        return p;
    }

    public Player getTopRentEarnerSafe() {
        Player p = rentHeap.peek();
        if (p == null || p.isBankrupt()) return null;
        return p;
    }

    public synchronized int nextActionId() {
        return ++actionIdCounter;
    }

    public void executeAction(Action action) {
        action.redo();
        undoStack.push(action);
        redoStack.clear();
        updateHeaps();
    }

    public void undo() {
        if(undoStack.isEmpty()) {
            return;
        }
        Action action = undoStack.pop();
        action.undo();
        redoStack.push(action);
        updateHeaps();
    }

    public void redo() {
        if(redoStack.isEmpty()) {
            return;
        }
        Action action = redoStack.pop();
        action.redo();
        undoStack.push(action);
        updateHeaps();
    }

    public boolean hasMonopoly(int playerID , String colorGroup){

        for(int i =0 ; i<board.getTotalTiles(); i++){
            Tile tile= board.getAllTiles().getTileAtPosition(i);
            if (tile.getType()== TileType.PROPERTY){
                int index = (int) tile.getData();
                Property p = properties.get(index);

                if (p.getColorGroup().equals(colorGroup)){
                    if (p.getOwnerID()== null || p.getOwnerID() != playerID){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void updateHeaps() {
        wealthHeap.clear();
        rentHeap.clear();

        for (int i = 1; i <= players.size(); i++) {
            Player p = players.get(i);
            if (p != null && !p.isBankrupt()) {
                wealthHeap.insert(p);
                rentHeap.insert(p);
            }
        }
    }

    public MyHashTable<Player> getPlayers() {
        return players;
    }

    public CardManager getCardManager() {
        return cardManager;
    }

    public Board getBoard() {
        return board;
    }

    public MyHashTable<Property> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("PLAYERS\n");
        for (int i = 1; i <= players.size(); i++) {
            Player p = players.get(i);
            if (p != null) {
                sb.append(p.getPlayerID()).append(",")
                        .append(p.getName()).append(",")
                        .append(p.getBalance()).append(",")
                        .append(p.getCurrentPosition()).append(",")
                        .append(p.getStatus()).append(",")
                        .append(p.getPropertiesCount()).append(",")
                        .append(p.hasRolledThisTurn())
                        .append("\n");
            }
        }

        sb.append("PENDING_BUY\n");
        if (pendingBuyPlayerId == null)
            sb.append("NONE\n");
        else
            sb.append(pendingBuyPlayerId)
                    .append(",")
                    .append(pendingBuyPropertyId)
                    .append("\n");
        sb.append("PENDING_TRADE\n");
        if (pendingTradeFrom == -1)
            sb.append("NONE\n");
        else
            sb.append(pendingTradeFrom)
                    .append(",")
                    .append(pendingTradeTo)
                    .append("\n");

        sb.append("BUILD\n");
        sb.append("HOUSE:");
        for (int i = 0; i < properties.size(); i++)
            if (canBuildHouse[i]) sb.append(i).append(",");
        sb.append("\n");

        sb.append("HOTEL:");
        for (int i = 0; i < properties.size(); i++)
            if (canBuildHotel[i]) sb.append(i).append(",");
        sb.append("\n");

        sb.append("MORTGAGE\n");
        for (int i = 0; i < properties.size(); i++) {
            Property p = properties.get(i);
            if (p != null && p.getOwnerID() != null) {
                sb.append(i)
                        .append(":")
                        .append(p.getOwnerID())
                        .append(":")
                        .append(p.isMortgaged() ? "M" : "U")
                        .append(",");
            }
        }
        sb.append("\n");

        return sb.toString();
    }


    public void setPendingBuy(int playerId, int propertyId) {
        pendingBuyPlayerId = playerId;
        pendingBuyPropertyId = propertyId;
    }

    public boolean canBuyProperty(int playerId) {
        return pendingBuyPlayerId != null &&
                pendingBuyPlayerId == playerId;
    }

    public int getPendingBuyPropertyId() {
        return pendingBuyPropertyId;
    }

    public void clearPendingBuy() {
        pendingBuyPlayerId = null;
        pendingBuyPropertyId = null;
    }

    public void setPendingTrade(int from, int to) {
        pendingTradeFrom = from;
        pendingTradeTo = to;
    }

    public boolean hasPendingTradeFor(int playerId) {
        return pendingTradeTo == playerId;
    }

    public void clearBuildFlags() {
        for (int i = 0; i < properties.size(); i++) {
            canBuildHouse[i] = false;
            canBuildHotel[i] = false;
        }
    }

    public void setCanBuildHouse(int propertyId) {
        canBuildHouse[propertyId] = true;
    }

    public void setCanBuildHotel(int propertyId) {
        canBuildHotel[propertyId] = true;
    }

    public boolean canBuildAnyHouse() {
        for (int i = 0; i < properties.size(); i++)
            if (canBuildHouse[i]) return true;
        return false;
    }

    public boolean canBuildAnyHotel() {
        for (int i = 0; i < properties.size(); i++)
            if (canBuildHotel[i]) return true;
        return false;
    }

    // ---------- MORTGAGE ----------
    public void mortgage(int propertyId) {
        mortgaged[propertyId] = true;
    }

    public void unmortgage(int propertyId) {
        mortgaged[propertyId] = false;
    }

    public boolean canUnmortgageAny() {
        for (int i = 0; i < properties.size(); i++)
            if (mortgaged[i]) return true;
        return false;
    }

    public boolean canMortgageAny(int playerId) {
        for (int i = 0; i < properties.size(); i++) {
            Property prop = properties.get(i);
            if (prop != null && prop.getOwnerID() != null
                    && prop.getOwnerID() == playerId
                    && !prop.isMortgaged()) {
                return true;
            }
        }
        return false;
    }

    public boolean canUnmortgageAny(int playerId) {
        for (int i = 0; i < properties.size(); i++) {
            Property prop = properties.get(i);
            if (prop != null && prop.getOwnerID() != null
                    && prop.getOwnerID() == playerId
                    && prop.isMortgaged()) {
                return true;
            }
        }
        return false;
    }
    public Property getPendingBuildProperty(int playerId) {
        for (int i = 0; i < properties.size(); i++) {
            Property p = properties.get(i);
            if (p.getOwnerID() == playerId && !p.isMortgaged() &&
                    hasMonopoly(playerId, p.getColorGroup())) {
                return p;
            }
        }
        return null;
    }

    public void setPendingTrade(int playerA, int playerB, int propertyA, Integer propertyB, int amount) {
        this.pendingTradePlayerA = playerA;
        this.pendingTradePlayerB = playerB;
        this.pendingTradePropertyA = propertyA;
        this.pendingTradePropertyB = propertyB;
        this.pendingTradeAmount = amount;
    }

    public int getPendingTradePlayerA() { return pendingTradePlayerA; }
    public int getPendingTradePlayerB() { return pendingTradePlayerB; }
    public int getPendingTradePropertyA() { return pendingTradePropertyA; }
    public Integer getPendingTradePropertyB() { return pendingTradePropertyB; }
    public int getPendingTradeAmount() { return pendingTradeAmount; }

    public void clearPendingTrade() {
        pendingTradePlayerA = pendingTradePlayerB = pendingTradePropertyA = -1;
        pendingTradePropertyB = null;
        pendingTradeAmount = 0;
    }
}
