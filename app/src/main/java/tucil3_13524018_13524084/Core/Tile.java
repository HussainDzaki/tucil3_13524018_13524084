package tucil3_13524018_13524084.Core;

public class Tile {
    private int x;
    private int y;

    private TileType type;
    private int cost;
    private int coinSequence;


    public Tile(int x, int y, TileType type, int cost) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.cost = cost;
    }

    public void setCoinSequence(int value){
        if (value < 1 && value > 9) {
            throw new IllegalArgumentException("Input angka " + value + " diluar batas 1-9");
        }
        if (type != TileType.COIN_NUMBER) {
            throw new RuntimeException("Gagal set angka! Tipe tile saat ini adalah " + this.type.name());
        }

        this.coinSequence = value;
    }

    public int getXCoords() {
        return this.x;
    }

    public int getYCoords() {
        return this.y;
    }

    public TileType getType(){
        return this.type;
    }

    public int getTileCost(){
        return this.cost;
    }


    public void setCoords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setType(TileType newType) {
        this.type = newType;
    }


    public boolean isLava() {
        return type == TileType.LAVA;
    }

    public boolean isObstacle() {
        return type == TileType.OBSTACLE;
    }

    public boolean isCoin(){
        return type == TileType.COIN_NUMBER;
    }

    public boolean isGoal(){
        return type == TileType.GOAL;
    }

    public void setTileCost(int cost){
        this.cost = cost;
    }

    public int getCoinSequence(){
        return this.coinSequence;
    }
}
