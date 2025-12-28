package server.datastructure;
import server.model.Player;

public class MyBST {

    public enum SortKey { BALANCE, RENT_INCOME, PROPERTY_COUNT }

    public static class Node {
        Player data;
        Node LC, RC;

        Node(Player data) {
            this.data = data;
            this.LC = null;
            this.RC = null;
        }
    }

    private Node root;
    private final SortKey sortKey;

    public MyBST(SortKey key) {
        root = null;
        this.sortKey = key;
    }


    public void insert(Player player) {
        root = insertRecursive(root, player);
    }

    private Node insertRecursive(Node T, Player player) {
        if (T == null) return new Node(player);

        int cmp = compare(player, T.data);

        if (cmp < 0) {
            T.LC = insertRecursive(T.LC, player);
        } else if (cmp > 0) {
            T.RC = insertRecursive(T.RC, player);
        } else {
            if (player.getPlayerID() < T.data.getPlayerID())
                T.LC = insertRecursive(T.LC, player);
            else if (player.getPlayerID() > T.data.getPlayerID())
                T.RC = insertRecursive(T.RC, player);
        }
        return T;
    }

    public void delete(Player player) {
        root = deleteRecursive(root, player);
    }

    private Node deleteRecursive(Node T, Player player) {
        if (T == null) return null;

        int cmp = compare(player, T.data);

        if (cmp < 0) {
            T.LC = deleteRecursive(T.LC, player);
        } else if (cmp > 0) {
            T.RC = deleteRecursive(T.RC, player);
        } else if (player.getPlayerID() != T.data.getPlayerID()) {
            if (player.getPlayerID() < T.data.getPlayerID())
                T.LC = deleteRecursive(T.LC, player);
            else
                T.RC = deleteRecursive(T.RC, player);
        } else {
            if (T.LC == null) return T.RC;
            if (T.RC == null) return T.LC;

            Node minNode = leftmost(T.RC);
            T.data = minNode.data;
            T.RC = deleteRecursive(T.RC, minNode.data);
        }
        return T;
    }

   // inorder traverse is not complete
    public void inorder() {
        inorderRecursive(root);
    }

    private void inorderRecursive(Node T) {
        if (T != null) {
            inorderRecursive(T.LC);
            System.out.println("Player: " + T.data.getName() +
                    ", Balance: " + T.data.getBalance() +
                    ", RentIncome: " + T.data.getRentIncome() +
                    ", PropertyCount: " + T.data.getOwnedPropertiesCount());
            inorderRecursive(T.RC);
        }
    }


    private int compare(Player a, Player b) {
        int valA = 0, valB = 0;
        switch (sortKey) {
            case BALANCE:
                valA = a.getBalance();
                valB = b.getBalance();
                break;
            case RENT_INCOME:
                valA = a.getRentIncome();
                valB = b.getRentIncome();
                break;
            case PROPERTY_COUNT:
                valA = a.getOwnedPropertiesCount();
                valB = b.getOwnedPropertiesCount();
                break;
        }
        if (valA < valB) return -1;
        if (valA > valB) return 1;
        return 0;
    }

    private Node leftmost(Node T) {
        if (T == null) return null;
        while (T.LC != null) T = T.LC;
        return T;
    }


    public int height() {
        return height(root);
    }

    private int height(Node T) {
        if (T == null) return 0;
        return 1 + Math.max(height(T.LC), height(T.RC));
    }


    public int size() {
        return size(root);
    }

    private int size(Node T) {
        if (T == null) return 0;
        return 1 + size(T.LC) + size(T.RC);
    }


    public boolean empty() {
        return root == null;
    }


    public void makeNull() {
        makeNull(root);
        root = null;
    }

    private void makeNull(Node T) {
        if (T == null) return;
        makeNull(T.LC);
        makeNull(T.RC);
        T.LC = null;
        T.RC = null;
        T.data = null;
    }


    public Node parent(Node node) {
        return parent(root, node);
    }

    private Node parent(Node T, Node node) {
        if (T == null || T == node) return null;
        if (T.LC == node || T.RC == node) return T;

        Node temp = parent(T.LC, node);
        if (temp == null) temp = parent(T.RC, node);
        return temp;
    }

    public Node getRoot() {
        return root;
    }
}
