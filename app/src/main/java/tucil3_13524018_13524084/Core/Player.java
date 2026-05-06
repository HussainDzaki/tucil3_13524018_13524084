package tucil3_13524018_13524084.Core;

public class Player {
    int x;
    int y;
    boolean isAlive;
    long totalCost;

    public Player() {
        this.x = 0;
        this.y = 0;
        this.isAlive = false;
    }

    public Player(Player other) {
        this.x = other.x;
        this.y = other.y;
        this.isAlive = other.isAlive;
        this.totalCost = other.totalCost;
    }

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.isAlive = true;
        this.totalCost = 0;
    }

    public int getXCoords() {
        return this.x;
    }

    public int getYCoords() {
        return this.y;
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public void setPlayerStatus(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public void setPlayerPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public long getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(long totalCost) {
        this.totalCost = totalCost;
    }

}