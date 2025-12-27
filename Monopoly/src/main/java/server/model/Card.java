package server.model;

public class Card {
    private int cardID;
    private CardType cardType;
    private String description;
    private EffectType effectType;
    private int effectValue;

    public Card(int id, CardType type,String desc, EffectType effectType, int value) {
        this.cardID = id;
        this.cardType = type;
        this.description = desc;
        this.effectType = effectType;
        this.effectValue = value;
    }

    public CardType getCardType() {
        return cardType;
    }
}
