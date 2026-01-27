package server.logic;
import server.action.*;
import server.core.GameState;
import server.datastructure.MyHashTable;
import server.datastructure.MyLinkedList;
import server.model.*;

public class TransactionManager {

    private final GameState gameState;

    public TransactionManager(GameState gameState) {
        this.gameState = gameState;
    }

    public boolean build(int actionId, Player player, Property property, BuildingType buildingType, int buildingCost) {
        if (player.isBankrupt()) {
            return false;
        }

        if (property.getOwnerID() == null ||
                property.getOwnerID() != player.getPlayerID()) {
            return false;
        }

        if (property.isMortgaged()) {
            return false;
        }

        if (!gameState.hasMonopoly(player.getPlayerID(), property.getColorGroup())) {
            return false;
        }

        if (player.getBalance() < buildingCost) {
            return false;
        }

        if (!isBuildingRuleValid(property, buildingType)) {
            return false;
        }

        BuildAction action = new BuildAction(
                actionId,
                player,
                property,
                buildingType,
                buildingCost
        );

        gameState.executeAction(action);
        return true;
    }

    private boolean isBuildingRuleValid(Property property, BuildingType buildingType) {

        int houseCount = property.getHouseCount();

        if (buildingType == BuildingType.HOUSE) {
            return !property.isMortgaged()
                    && !property.hasHotel()
                    && houseCount < 4;
        }

        if (buildingType == BuildingType.HOTEL) {
            return houseCount == 4 && !property.hasHotel();
        }

        return false;
    }
    public boolean buyProperty(
            int actionId,
            Player buyer,
            Property property
    ) {

        if (buyer.isBankrupt()) {
            return false;
        }

        if (property.getOwnerID() != null) {
            return false;
        }

        if (property.isMortgaged()) {
            return false;
        }

        int price = property.getPurchasePrice();

        if (buyer.getBalance() < price) {
            return false;
        }

        int prevBalance = buyer.getBalance();
        int newBalance = prevBalance - price;

        BuyAction action = new BuyAction(
                actionId,
                buyer,
                property,
                prevBalance,
                newBalance
        );

        gameState.executeAction(action);
        return true;
    }
    public boolean applyCardEffect(int actionId, int playerId, CardType cardType) {

        MyHashTable<Player> players = gameState.getPlayers();
        Player player = players.get(playerId);

        if (player == null || player.isBankrupt()) {
            return false;
        }

        Card card = (cardType == CardType.CHANCE)
                ? gameState.getCardManager().drawChanceCard()
                : gameState.getCardManager().drawCommunityCard();

        EffectType effect = card.getEffectType();

        int prevBalance = player.getBalance();
        int prevPosition = player.getCurrentPosition();

        int newBalance = prevBalance;
        int newPosition = prevPosition;

        boolean wasInJail = player.getStatus() == PlayerStatus.IN_JAIL;
        boolean goToJail = false;

        MyHashTable<Integer> prevBalances = new MyHashTable<>(players.size());
        MyHashTable<Integer> newBalances = new MyHashTable<>(players.size());

        switch (effect) {

            case MOVE:
                newPosition = prevPosition + card.getEffectValue();
                break;

            case GET_MONEY:
                newBalance = prevBalance + card.getEffectValue();
                break;

            case LOSE_MONEY:
                newBalance = prevBalance - card.getEffectValue();
                break;

            case PAY_TO_PLAYERS:
                int amount = card.getEffectValue();
                int receiverCount = 0;

                for (int id = 1; id <= players.size(); id++) {
                    Player p = players.get(id);
                    if (p != null && p.getPlayerID() != playerId) {
                        receiverCount++;
                    }
                }

                for (int id = 1; id <= players.size(); id++) {
                    Player p = players.get(id);
                    if (p == null) continue;

                    prevBalances.insert(id, p.getBalance());

                    if (id == playerId) {
                        int updated = p.getBalance() - amount * receiverCount;
                        newBalances.insert(id, updated);
                        newBalance = updated;
                    } else {
                        newBalances.insert(id, p.getBalance() + amount);
                    }
                }
                break;

            case GO_TO_JAIL:
                newPosition = 10;
                goToJail = true;
                break;

            default:
                return false;
        }

        CardEffectAction action = new CardEffectAction(
                actionId,
                player,
                card,
                prevBalance,
                newBalance,
                prevPosition,
                newPosition,
                wasInJail,
                goToJail,
                players,
                prevBalances,
                newBalances
        );
        gameState.executeAction(action);
        return true;
    }
    public boolean applyMortgage(int actionId, int playerId, Property property, boolean isMortgaging) {

        MyHashTable<Player> players = gameState.getPlayers();
        Player player = players.get(playerId);

        if (player == null || player.isBankrupt()) {
            System.out.println("first");
            return false;
        }

        if (isMortgaging && property.isMortgaged()) {
            System.out.println("second");
            return false;
        }
        if (!isMortgaging && !property.isMortgaged()) {
            System.out.println("third");
            return false;
        }
        System.out.println("prop id: "+property.getOwnerID());
        System.out.println("player id: "+playerId);
        if (property.getOwnerID() == null || property.getOwnerID() != playerId) {
            System.out.println("fourth");
            return false;
        }

        MortgageAction action = new MortgageAction(actionId, player, property, isMortgaging);
        gameState.executeAction(action);
        return true;
    }
    public boolean applyMove(int actionId, Player player, int steps) {

        if (player == null || player.isBankrupt()) {
            return false;
        }

        int prevPosition = player.getCurrentPosition();
        int prevBalance = player.getBalance();

        Tile startTile = gameState.getBoard().getAllTiles().getTileAtPosition(prevPosition);
        MyLinkedList.MoveResult result = gameState.getBoard().getAllTiles().move(startTile, steps);

        Tile landedTile = result.getLandedTile();
        int newPosition = gameState.getBoard().getAllTiles().getPositionOfTile(landedTile);
        int newBalance = prevBalance;

        if (result.hasPassedGO()) {
            newBalance += 200;
        }

        MoveAction action = new MoveAction(actionId, player, prevPosition, newPosition, prevBalance, newBalance);
        gameState.executeAction(action);

        return true;
    }
    public boolean applyPayRent(int actionId, int payerId, int propertyId) {

        MyHashTable<Player> players = gameState.getPlayers();
        Player payer = players.get(payerId);

        if (payer == null || payer.isBankrupt()) {
            return false;
        }

        Property property = gameState.getProperties().get(propertyId);
        if (property == null || property.getOwnerID() == null) {
            return false;
        }

        int ownerId = property.getOwnerID();
        if (ownerId == payerId) {
            return false;
        }

        Player receiver = players.get(ownerId);
        if (receiver == null || receiver.isBankrupt()) {
            return false;
        }

        int rent = property.calculateRent(
                gameState.hasMonopoly(receiver.getPlayerID(), property.getColorGroup())
        );

        int payerPrevBalance = payer.getBalance();
        int receiverPrevBalance = receiver.getBalance();

        if (payerPrevBalance < rent) {
            payer.setBalance(0);
            payer.declareBankruptcy();
            return true;
        }

        int payerNewBalance = payerPrevBalance - rent;
        int receiverNewBalance = receiverPrevBalance + rent;

        PayRentAction action = new PayRentAction(
                actionId,
                payer,
                receiver,
                payerPrevBalance,
                payerNewBalance,
                receiverPrevBalance,
                receiverNewBalance,
                rent
        );

        gameState.executeAction(action);

        return true;
    }
    public boolean applyPayTax(int actionId, Player player, Tax tax) {

        if (player == null || player.isBankrupt()) {
            return false;
        }

        int amount = tax.getAmount();
        int previousBalance = player.getBalance();

        if (previousBalance < amount) {
            player.setBalance(0);
            player.declareBankruptcy();
            return true;
        }

        PayTaxAction action = new PayTaxAction(actionId, player, amount);
        gameState.executeAction(action);

        return true;
    }

    public boolean applyTrade(int actionId, int playerAId, int playerBId, int propertyAId, Integer propertyBId, int amount) {
        MyHashTable<Player> players = gameState.getPlayers();
        MyHashTable<Property> properties = gameState.getProperties();

        Player playerA = players.get(playerAId);
        Player playerB = playerBId >= 0 ? players.get(playerBId) : null;

        if (playerA == null || playerA.isBankrupt()){
            System.out.println("first");
            return false;
        }
        if (playerB != null && playerB.isBankrupt()){
            System.out.println("second");
            return false;
        }

        Property propertyA = properties.get(propertyAId);
        Property propertyB = propertyBId != null ? properties.get(propertyBId) : null;

        if (propertyA == null){
            System.out.println("third");
            return false;
        }
        if (propertyBId != null && propertyB == null){
            System.out.println("fourth");
            return false;
        }

        System.out.println("prop A id:"+propertyAId+" B id:"+propertyBId);
        if (!playerA.ownsProperty(propertyAId)){
            System.out.println("fifth");
            return false;
        }
        if (propertyB != null && !playerB.ownsProperty(propertyBId)){
            System.out.println("sixth");
            return false;
        }

        if (propertyB == null && playerB != null && playerB.getBalance() - amount < 0){
            System.out.println("seventh");
            return false;
        }

        TradeAction action = new TradeAction(actionId, playerA, playerB, propertyA, propertyB, amount);
        gameState.executeAction(action);

        return true;
    }

}
