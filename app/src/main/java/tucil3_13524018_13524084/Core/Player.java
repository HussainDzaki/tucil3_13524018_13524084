package tucil3_13524018_13524084.Core;

public class Player {
    Integer x;
    Integer y;
    boolean isAlive;

    public Player(){
        this.x = 0;
        this.y = 0;
        this.isAlive = false;
    }

    public Player(Integer x, Integer y){
        this.x = x;
        this.y = y;
        this.isAlive = true;
    }

    public Integer getXCoords(){
        return this.x;
    }

    public Integer getYCoords(){
        return this.y;
    }

    public boolean isAlive(){
        return this.isAlive;
    }

    public void setPlayerStatus(boolean isAlive){
        this.isAlive = isAlive;
    }

}