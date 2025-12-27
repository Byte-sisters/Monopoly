package server.datastructure;
import server.action.Action;

public class MyStack {

    private static class Node {
        private Action action;
        private Node next;

        public Node(Action action) {
            this.action = action;
            this.next = null;
        }

        public Node getNext() {
            return next;
        }

        public Action getAction() {
            return action;
        }
    }

    private Node top;
    private int size;

    public MyStack() {
        top = null;
        size = 0;
    }

    public void push(Action action) {
        Node newNode = new Node(action);
        newNode.next = top;
        top = newNode;
        size++;
    }

    public Action pop() {
        if(top == null){
            throw new RuntimeException("Stack underflow");
        }
        Action action = top.getAction();
        top = top.next;
        size--;
        return action;
    }

    public Action peek() {
        if(top == null){
            throw new RuntimeException("Stack underflow");
        }
        return top.getAction();
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear(){
        top = null;
        size = 0;
    }
}
