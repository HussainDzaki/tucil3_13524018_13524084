package tucil3_13524018_13524084.Core;

public class Tile {
    private Integer x;
    private Integer y;

    private TileType type;
    private Integer cost;
    private Integer coinSequence;


    public Tile(Integer x, Integer y, TileType type, Integer cost) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.cost = cost;
    }

    public void setCoinSequence(Integer value){
        if (value < 1 && value > 9) {
            throw new IllegalArgumentException("Input angka " + value + " diluar batas 1-9");
        }
        if (type != TileType.COIN_NUMBER) {
            throw new RuntimeException("Gagal set angka! Tipe tile saat ini adalah " + this.type.name());
        }

        this.coinSequence = value;
    }

    public Integer getXCoords() {
        return this.x;
    }

    public Integer getYCoords() {
        return this.y;
    }

    public TileType getType(){
        return this.type;
    }

    public Integer getTileCost(){
        return this.cost;
    }


    public void setCoords(Integer x, Integer y) {
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

    public void setTileCost(Integer cost){
        this.cost = cost;
    }

    public Integer getCoinSequence(){
        return this.coinSequence;
    }
}
