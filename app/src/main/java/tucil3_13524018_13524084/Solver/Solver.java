package tucil3_13524018_13524084.Solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import tucil3_13524018_13524084.Core.Board;
import tucil3_13524018_13524084.Core.Tile;
import tucil3_13524018_13524084.Core.Direction;

public abstract class Solver {
    protected Board board;

    public Solver(Board board){
        this.board = board;
    }

    public abstract List<Direction> solve();

    public List<Direction> reconstructPath(Node node) {
        List<Direction> path = new ArrayList<>();
        Node curr = node;
        while (curr != null && curr.getLastMove() != null) {
            path.add(curr.getLastMove());
            curr = curr.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    public long calculateHeuristic(int curX, int curY, Queue<Integer> currentCoins, Board board) {
        int targetX, targetY;

        if (!currentCoins.isEmpty()) {
            // jika target adalah koordinat koin berikutnya
            int nextCoin = currentCoins.peek();
            Tile coinTile = board.findCoinTile(nextCoin);
            if (coinTile == null) {
                throw new IllegalStateException(
                        "Error: Koin " + nextCoin + " terdaftar di Queue tapi tidak ditemukan di Board Map!");
            }
            targetX = coinTile.getXCoords();
            targetY = coinTile.getYCoords();
        } else {
            // jika target adalah Goal
            targetX = board.getGoalTile().getXCoords();
            targetY = board.getGoalTile().getYCoords();
        }

        // Manhattan Distance
        return Math.abs(curX - targetX) + Math.abs(curY - targetY);
    }
}
