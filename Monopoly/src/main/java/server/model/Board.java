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
        tiles.add(new Tile (2,"mediterranean avenue", TileType.PROPERTY,new Property(1,"mediterranean", "brown", 60,10)));
        tiles.add(new Tile (3,"community chest", TileType.CARD));
        tiles.add(new Tile (4,"baltic avenue", TileType.PROPERTY, new Property(1,"baltic", "brown", 70,15)));
        tiles.add(new Tile (5,"income tax", TileType.TAX, new Tax(200)));
        tiles.add(new Tile (6,"mehran avenue", TileType.PROPERTY, new Property(2,"mehran", "purple", 200,50)));
        tiles.add(new Tile (7,"oriental avenue", TileType.PROPERTY, new Property(3,"oriental", "blue", 100,20)));
        tiles.add(new Tile (8,"chance", TileType.CARD));
        tiles.add(new Tile (9,"vermont avenue", TileType.PROPERTY, new Property(3,"vermont", "blue", 100,20)));
        tiles.add(new Tile (10,"connecticut avenue", TileType.PROPERTY, new Property(3,"connecticut", "blue", 120,25)));
        tiles.add(new Tile (11,"just visiting jail", TileType.JAIL, new Jail())); // this is not complete
        tiles.add(new Tile (12,"st.charles place", TileType.PROPERTY, new Property(4,"st.charles", "pink", 140,30)));
        tiles.add(new Tile (13,"income tax", TileType.TAX, new Tax(175)));
        tiles.add(new Tile (14,"states avenue", TileType.PROPERTY, new Property(4,"states", "pink", 140,30)));
        tiles.add(new Tile (15,"virginia avenue", TileType.PROPERTY, new Property(4,"virginia", "pink", 160,35)));
        tiles.add(new Tile (16,"afarin avenue", TileType.PROPERTY, new Property(2,"afarin", "purple", 200,50)));
        tiles.add(new Tile (17,"st.james place", TileType.PROPERTY,new Property(5,"st.james", "orange", 180,40)));
        tiles.add(new Tile (18,"community chest", TileType.CARD));
        tiles.add(new Tile (19,"tennessee avenue", TileType.PROPERTY,new Property(5,"tennessee", "orange", 180,40)));
        tiles.add(new Tile (20,"NYC avenue", TileType.PROPERTY,new Property(5,"NYC", "orange", 200,45)));
        tiles.add(new Tile (21,"north avenue", TileType.CARD)); //this is not complete
        tiles.add(new Tile (22,"kentucky avenue", TileType.PROPERTY,new Property(6,"kentucky", "red", 220,50)));
        tiles.add(new Tile (23,"chance", TileType.CARD));
        tiles.add(new Tile (24,"indiana avenue", TileType.PROPERTY,new Property(6,"indiana", "red", 220,50)));
        tiles.add(new Tile (25,"illinois avenue", TileType.PROPERTY,new Property(6,"illinois", "red", 240,55)));
        tiles.add(new Tile (26,"nejadasghar avenue", TileType.PROPERTY, new Property(2,"nejadasghar", "purple", 200,50)));
        tiles.add(new Tile (27,"atlantic avenue", TileType.PROPERTY,new Property(7,"atlantic", "yellow", 260,60)));
        tiles.add(new Tile (28,"ventnor avenue", TileType.PROPERTY,new Property(7,"ventnor", "yellow", 260,60)));
        tiles.add(new Tile (29,"income tax", TileType.TAX, new Tax(125)));
        tiles.add(new Tile (30,"marvin gardens", TileType.PROPERTY,new Property(7,"marvin", "yellow", 280,65)));
        tiles.add(new Tile (31,"go to jail", TileType.JAIL, new Jail())); // this is not complete
        tiles.add(new Tile (32,"pacific avenue", TileType.PROPERTY,new Property(8,"pacific", "green", 300,70)));
        tiles.add(new Tile (33,"north carolina avenue", TileType.PROPERTY,new Property(8,"carolina", "green", 300,70)));
        tiles.add(new Tile (34,"community chest", TileType.CARD));
        tiles.add(new Tile (35,"pennsylvania avenue", TileType.PROPERTY,new Property(8,"pennsylvania", "green", 320,75)));
        tiles.add(new Tile (36,"ab doogh khiar avenue", TileType.PROPERTY, new Property(2,"ab doogh khiarr", "purple", 200,50)));
        tiles.add(new Tile (37,"chance", TileType.CARD));
        tiles.add(new Tile (38," park place", TileType.PROPERTY, new Property(9,"park place", "navy blue", 350,80)));
        tiles.add(new Tile (39,"income tax", TileType.TAX, new Tax(100)));
        tiles.add(new Tile (40,"boardwalk", TileType.PROPERTY, new Property(9,"boardwalk", "navy blue", 400,85)));
        totalTiles = tiles.size();
    }


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