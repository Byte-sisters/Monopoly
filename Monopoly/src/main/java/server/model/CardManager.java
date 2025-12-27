package server.model;

import server.datastructure.MyQueue;

public class CardManager {
    private final MyQueue chanceQueue;
    private final MyQueue communityQueue;

    public CardManager() {
        chanceQueue = new MyQueue();
        communityQueue = new MyQueue();
    }

    public void addCard(Card card) {
        if(card.getCardType()==CardType.CHANCE){
            chanceQueue.enqueue(card);
        }else{
            communityQueue.enqueue(card);
        }
    }

    public Card drawChanceCard() {
        Card card = chanceQueue.dequeue();
        chanceQueue.enqueue(card);
        return card;
    }

    public Card drawCommunityCard() {
        Card card = communityQueue.dequeue();
        communityQueue.enqueue(card);
        return card;
    }

    public Card peekChanceCard() {
        return chanceQueue.peek();
    }

    public Card peekCommunityCard() {
        return communityQueue.peek();
    }
}
