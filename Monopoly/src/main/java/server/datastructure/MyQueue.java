package server.datastructure;

import server.model.Card;

public class MyQueue {

    public static class Node{
        private Card card;
        private Node next;
        public Node(Card card) {
            this.card = card;
            next = null;
        }
        public Card getCard() {
            return card;
        }
        public Node getNext() {
            return next;
        }
    }

    private Node front;
    private Node rear;
    private int size;

    public MyQueue() {
        front = null;
        rear = null;
        size = 0;
    }

    public Card dequeue() {
        if (front == null) {
            throw new RuntimeException("Queue is empty");
        }
        Card card = front.getCard();
        front = front.getNext();
        if (front == null) {
            rear = null;
        }
        size--;
        return card;
    }

    public void enqueue(Card card) {
        Node newNode = new Node(card);
        if(rear == null){
            front = newNode;
            rear = newNode;
        }else{
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }

    public Card peek(){
        if (front == null) {
            throw new RuntimeException("Queue is empty");
        }
        return front.getCard();
    }

    public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }
}
