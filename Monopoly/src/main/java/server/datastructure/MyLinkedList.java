package server.datastructure;

import server.model.Tile;
import server.model.TileType;

public class MyLinkedList {

    public static class Node{
        private Tile tile;

        public Node (Tile tile){
            this.tile = tile;
        }
        public Tile getTile() {
            return tile;
        }
        public Node getNext() {
            Tile nextTile= tile.getNextTile();
            return nextTile != null ? new Node(nextTile) : null;
        }
    }
    public static class MoveResult{
        private final Tile landedTile;
        private final boolean passedGO;

        public MoveResult(Tile landedTile, boolean passedGO) {
            this.landedTile = landedTile;
            this.passedGO = passedGO;
        }
        public Tile getLandedTile() {
            return landedTile;
        }
        public boolean hasPassedGO() {
            return passedGO;
        }
    }
    private Tile head;
    private int size;

    public MyLinkedList(){
        this.head = null;
        this.size = 0;
    }
    public void add(Tile tile){
        if(head == null){
            head = tile;
            tile.setNextTile(head);
        }
        else{
            Tile current = head;
            while(current.getNextTile() != head){
                current = current.getNextTile();
            }
            current.setNextTile(tile);
            tile.setNextTile(head);
        }
        size++;
    }
    //    public Node getHeadNode(){
//        return head != null ? new Node(head) : null;
//    }
    public Tile getHeadTile(){
        return head;
    }

    public MoveResult move (Tile startTile, int steps){
        if(startTile == null || steps < 0){
            throw new IllegalArgumentException("Invalid move parameters");
        }
        Tile current = startTile;
        boolean passedGO = false;

        for(int i=0 ; i<steps ; i++){
            current = current.getNextTile();

            if(current == head){
                passedGO = true;
            }
        }
        return new MoveResult(current, passedGO);

    }
    public int size(){
        return size;
    }
    public Tile findTileById(int tileId){
        if(head == null){
            return null;
        }
        Tile current = head;
        do {
            if (current.getId() == tileId) {
                return current;
            }
            current = current.getNextTile();
        }
        while(current != head);
        return null;
    }
    public Tile getTileAtPosition(int position){
        if(head == null || position < 0 ){
            return null;
        }
        Tile current = head;
        for(int i=0 ; i<position ; i++){
            current = current.getNextTile();
        }
        return current;
    }
}