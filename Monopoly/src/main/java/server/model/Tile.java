package server.model;

public class Tile {
    private final int id;
    private final String name;
    private final TileType type;
    private Object data;
    private Tile nextTile;

    public Tile (int id, String name, TileType type){
        this.id = id;
        this.name = name;
        this.type = type;
        this.data = null;
        this.nextTile = null;
    }
    public Tile (int id, String name, TileType type, Object data){
        this.id = id;
        this.name = name;
        this.type = type;
        this.data = data;
        this.nextTile = null;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public TileType getType() {
        return type;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
    public Tile getNextTile() {
        return nextTile;
    }
    public void setNextTile(Tile nextTile) {
        this.nextTile = nextTile;
    }
    @Override
    public String toString() {
        return String.format("Tile[id=%d, name='%s', type=%s]", id, name, type);

    }
}
