package tucil3_13524018_13524084.Solver;

import java.util.LinkedList;
import java.util.Queue;

import tucil3_13524018_13524084.Core.Tile;
import tucil3_13524018_13524084.Core.Direction;
import tucil3_13524018_13524084.Core.Board;

public class Node {
    private final int x, y;
    private final Queue<Integer> remainingCoins;
    private long gCost; 
    private long hCost; 
    private final Node parent;
    private final Direction lastMove;

    public Node(int x, int y, Queue<Integer> remainingCoins,  long gCost, long hCost, Node parent, Direction lastMove) {
        this.x = x;
        this.y = y;
        this.remainingCoins = remainingCoins;
        this.gCost = gCost;
        this.hCost = hCost;
        this.parent = parent;
        this.lastMove = lastMove;
    }

    public Node move(Direction dir, Board board) {
        int curX = this.x;
        int curY = this.y;
        long moveCost = 0;
        Queue<Integer> newQueue = new LinkedList<>(this.remainingCoins);

        while (true) {
            int nextX = curX + dir.getDx();
            int nextY = curY + dir.getDy();

            if (board.isOutOfBound(nextX, nextY)) {
                return null;
            }
            if (board.getTileAt(nextX, nextY).isObstacle()) {
                break;
            }

            curX = nextX;
            curY = nextY;
            Tile target = board.getTileAt(curX, curY);
            moveCost += target.getTileCost();

            if (target.isLava()) return null; 

            if (target.isCoin()) {
                int val = target.getCoinSequence();
                
                if (!newQueue.isEmpty() && val == newQueue.peek()) {
                    newQueue.poll();
                } else if (!newQueue.isEmpty() && val > newQueue.peek()) {
                    return null; 
                }
            }
            
            if (target.isGoal() && newQueue.isEmpty()) break; 
        }

        if (curX == this.x && curY == this.y) return null;

        return new Node(curX, curY, newQueue, this.gCost + moveCost, 0, this, dir);
    }
    
    public String getStateId() {
        return x + "," + y + "," + remainingCoins.size();
    }

    public long getGCost() {
        return gCost;
    }
    public long getHCost() {
        return hCost;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Direction getLastMove() {
        return lastMove;
    }
    public Node getParent() {
        return parent;
    }
    public Queue<Integer> getRemainingCoins() {
        return remainingCoins;
    }

    public void setHCost(long hCost) {
        this.hCost = hCost;
    }
    public void setGCost(long gCost) {
        this.gCost = gCost;
    }
}
