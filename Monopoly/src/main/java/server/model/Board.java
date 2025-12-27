package server.model;

import server.datastructure.MyLinkedList;
import server.datastructure.MyLinkedList.MoveResult;

public class Board {
    private final MyLinkedList tiles;
    private int totalTiles;

    public Board() {
        this.tiles = new MyLinkedList();
        this.totalTiles = 0;
        initializeBoard();
    }

    private void initializeBoard() {
        tiles.add(new Tile (1,"GO", TileType.GO,null));
        tiles.add(new Tile (2,"mediterranean avenue", TileType.PROPERTY,1));
        tiles.add(new Tile (3,"community chest", TileType.CARD, CardType.COMMUNITY));
        tiles.add(new Tile (4,"baltic avenue", TileType.PROPERTY, 2));
        tiles.add(new Tile (5,"income tax", TileType.TAX, 200));
        tiles.add(new Tile (6,"mehran avenue", TileType.PROPERTY, 3));
        tiles.add(new Tile (7,"oriental avenue", TileType.PROPERTY,4));
        tiles.add(new Tile (8,"chance", TileType.CARD,CardType.CHANCE));
        tiles.add(new Tile (9,"vermont avenue", TileType.PROPERTY, 5));
        tiles.add(new Tile (10,"connecticut avenue", TileType.PROPERTY, 6));
        tiles.add(new Tile (11,"just visiting jail", TileType.JAIL, JailStatus.VISITING));
        tiles.add(new Tile (12,"st.charles place", TileType.PROPERTY, 7));
        tiles.add(new Tile (13,"income tax", TileType.TAX, 175));
        tiles.add(new Tile (14,"states avenue", TileType.PROPERTY, 8));
        tiles.add(new Tile (15,"virginia avenue", TileType.PROPERTY,9));
        tiles.add(new Tile (16,"afarin avenue", TileType.PROPERTY, 10));
        tiles.add(new Tile (17,"st.james place", TileType.PROPERTY,11));
        tiles.add(new Tile (18,"community chest", TileType.CARD,CardType.COMMUNITY));
        tiles.add(new Tile (19,"tennessee avenue", TileType.PROPERTY,12));
        tiles.add(new Tile (20,"NYC avenue", TileType.PROPERTY,13));
        tiles.add(new Tile (21,"north avenue", TileType.CARD, CardType.CHANCE)); //this is not complete
        tiles.add(new Tile (22,"kentucky avenue", TileType.PROPERTY,14));
        tiles.add(new Tile (23,"chance", TileType.CARD, CardType.CHANCE));
        tiles.add(new Tile (24,"indiana avenue", TileType.PROPERTY,15));
        tiles.add(new Tile (25,"illinois avenue", TileType.PROPERTY,16));
        tiles.add(new Tile (26,"nejadasghar avenue", TileType.PROPERTY, 17));
        tiles.add(new Tile (27,"atlantic avenue", TileType.PROPERTY,18));
        tiles.add(new Tile (28,"ventnor avenue", TileType.PROPERTY,19));
        tiles.add(new Tile (29,"income tax", TileType.TAX, 125));
        tiles.add(new Tile (30,"marvin gardens", TileType.PROPERTY,20));
        tiles.add(new Tile (31,"go to jail", TileType.JAIL, JailStatus.GO_TO_JAIL)); // this is not complete
        tiles.add(new Tile (32,"pacific avenue", TileType.PROPERTY,21));
        tiles.add(new Tile (33,"north carolina avenue", TileType.PROPERTY,22));
        tiles.add(new Tile (34,"community chest", TileType.CARD, CardType.COMMUNITY));
        tiles.add(new Tile (35,"pennsylvania avenue", TileType.PROPERTY,23));
        tiles.add(new Tile (36,"ab doogh khiar avenue", TileType.PROPERTY, 24));
        tiles.add(new Tile (37,"chance", TileType.CARD, CardType.CHANCE));
        tiles.add(new Tile (38," park place", TileType.PROPERTY, 25));
        tiles.add(new Tile (39,"income tax", TileType.TAX, 100));
        tiles.add(new Tile (40,"boardwalk", TileType.PROPERTY, 26));
        totalTiles = tiles.size();
    }

    //maybe it should return MoveResult????
    public void movePlayer(int currentPosition, int steps) {
        Tile startNode = tiles.getTileAtPosition(currentPosition);
        MoveResult result = tiles.move(startNode, steps);
        if(result.hasPassedGO()){
            // give money to player
        }
    }


//    public Tile getTileAtPosition(int position) {
//        MyLinkedList.Node node = getNodeAtPosition(position);
//        return node != null ? node.getTile() : null;
//    }
//
//
//    private MyLinkedList.Node getNodeAtPosition(int position) {
//        if (tiles.getHeadNode() == null || position < 0) {
//            return null;
//        }
//
//        MyLinkedList.Node current = tiles.getHeadNode();
//        for (int i = 0; i < position; i++) {
//            current = current.getNext();
//        }
//        return current;
//    }


    public int getTotalTiles() {
        return totalTiles;
    }


    public Tile getStartTile() {
        return tiles.getHeadTile();
    }


    public int getStartPosition() {
        return 0;
    }
}