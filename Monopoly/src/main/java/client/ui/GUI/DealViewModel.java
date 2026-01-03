package client.ui.GUI;

public class DealViewModel {

    public int proposerId;

    public String[] offeredProperties;
    public int offeredPropertiesCount;

    public int offeredMoney;

    public String[] requestedProperties;
    public int requestedPropertiesCount;

    public int requestedMoney;

    public boolean isIncoming;

    public DealViewModel(int maxProperties) {
        offeredProperties = new String[maxProperties];
        requestedProperties = new String[maxProperties];
        offeredPropertiesCount = 0;
        requestedPropertiesCount = 0;
    }
}
