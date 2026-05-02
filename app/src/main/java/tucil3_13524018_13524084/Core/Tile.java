package tucil3_13524018_13524084.Core;
import tucil3_13524018_13524084.Core.*;


public class Tile {
    private Integer x;
    private Integer y;

    private TileType type;
    private Integer numberValue;

    public Tile(Integer x, Integer y, TileType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void setNumberValue(Integer value){
        if (value < 1 && value > 9) {
            throw new IllegalArgumentException("Input angka " + value + " diluar batas 1-9");
        }
        if (type != TileType.NUMBER) {
            throw new RuntimeException("Gagal set angka! Tipe tile saat ini adalah " + this.type.name());
        }       

        this.numberValue = value;
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
}
