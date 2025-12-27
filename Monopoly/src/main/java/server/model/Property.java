package server.model;

public class Property {
    private int propertyID;
    private String name;
    private String colorGroup;
    private int purchasePrice;
    private int baseRent;

    private int houseCount;
    private boolean hasHotel;
    private boolean isMortgaged;
    private Integer ownerID;

    public Property(int id, String name, String color, int price, int baseRent){
        this.propertyID = id;
        this.name = name;
        this.colorGroup = color;
        this.purchasePrice = price;
        this.baseRent = baseRent;
        this.houseCount = 0;
        this.hasHotel = false;
        this.isMortgaged = false;
        this.ownerID = null;
    }

    public int getPropertyID() {
        return propertyID;
    }
}
