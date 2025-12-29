package server.model;

public class Property {
    private int propertyID;
    private String name;
    private String colorGroup;
    private int purchasePrice;
    private int baseRent;
    private int MortgageValue;

    private int houseCount;
    private BuildingType buildingType;
    private boolean hasHotel;
    private boolean isMortgaged;
    private Integer ownerID;

    public Property(int id, String name, String color, int price, int baseRent){
        this.propertyID = id;
        this.name = name;
        this.colorGroup = color;
        this.purchasePrice = price;
        this.MortgageValue = price * 3 / 4;
        this.baseRent = baseRent;
        this.houseCount = 0;
        this.hasHotel = false;
        this.isMortgaged = false;
        this.ownerID = null;
    }

    public int getPropertyID() {
        return propertyID;
    }

    public void setOwnerID(Integer newOwner) {
        this.ownerID = newOwner;
    }

    public Integer getOwnerID() {
        return ownerID;
    }

    public int getHouseCount() {
        return houseCount;
    }

    public void setHouseCount(int newHouseCount) {
        this.houseCount = newHouseCount;
    }

    public void setBuildingType(BuildingType buildingType) {
        this.buildingType = buildingType;
    }

    public void setHasHotel(boolean b) {
        this.hasHotel = b;
    }

    public void setMortgaged(boolean b) {
        this.isMortgaged = b;
    }

    public int getMortgageValue() {
        return MortgageValue;
    }

    public boolean isMortgaged() {
        return isMortgaged;
    }

    public String getColorGroup(){
        return colorGroup;
    }

    public int getPurchasePrice() {
        return purchasePrice;
    }

    public int calculateRent(boolean monopolyActive){

        if (isMortgaged){
            return 0;
        }
        int rent = baseRent;

        if (monopolyActive&& houseCount==0 && !hasHotel){
            return rent*2;
        }

        if (hasHotel){
            return rent*6;
        }
        switch (houseCount){
            case 1:
                return rent*2;
                case 2:
                    return rent*3;
                    case 3:
                        return rent*4;
                        case 4:
                            return rent*5;
            default:
                return rent;
        }
    }
    public boolean hasHotel() {
        return hasHotel;
    }

//    public BuildingType getBuildingType() {
//        return buildingType;
//    }

}
