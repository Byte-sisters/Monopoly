package server.action;

import server.model.Player;
import server.model.Property;
import server.model.BuildingType;

public class BuildAction extends Action {
    private final Property property;
    private final BuildingType buildingType;
    private final int buildingCost;
    private final int previousBalance;
    private final int newBalance;
    private final int previousBuildingCount;
    private final int newBuildingCount;

    public BuildAction(int actionId, Player player, Property property, BuildingType buildingType, int buildingCost) {
        super(actionId, ActionType.BUILD, player.getPlayerID(), player);

        this.property = property;
        this.buildingType = buildingType;
        this.buildingCost = buildingCost;
        this.previousBalance = player.getBalance();
        this.newBalance = previousBalance - buildingCost;
        this.previousBuildingCount = property.getHouseCount();
        this.newBuildingCount = previousBuildingCount + 1;
    }

    @Override
    public void redo() {
        Player player = (Player) affectedEntities;
        player.setBalance(newBalance);
        property.setHouseCount(newBuildingCount);
        property.setBuildingType(buildingType);

        if (buildingType == BuildingType.HOTEL) {
            property.setHasHotel(true);
            property.setHouseCount(0);
        } else {
            property.setHouseCount(newBuildingCount);
        }
    }

    @Override
    public void undo() {
        Player player = (Player) affectedEntities;
        player.setBalance(previousBalance);
        property.setHouseCount(previousBuildingCount);

        if (previousBuildingCount == 0) {
            property.setBuildingType(null);
        } else if (previousBuildingCount < 5) {
            property.setBuildingType(BuildingType.HOUSE);
            property.setHouseCount(previousBuildingCount);
            property.setHasHotel(false);
        } else {
            property.setBuildingType(BuildingType.HOTEL);
            property.setHasHotel(true);
            property.setHouseCount(4);
        }
    }
}