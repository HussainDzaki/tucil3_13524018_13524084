package tucil3_13524018_13524084.Solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import tucil3_13524018_13524084.Core.Board;
import tucil3_13524018_13524084.Core.Direction;
import tucil3_13524018_13524084.Core.Tile;

public class AStarSolver {
    private Board board;

    public AStarSolver(Board board) {
        this.board = board;
    }

    public List<Direction> solve() {
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingLong(n -> n.getGCost() + n.getHCost()));
        Map<String, Long> visited = new HashMap<>();
        int startX = board.getPlayer().getXCoords();
        int startY = board.getPlayer().getYCoords();
        Queue<Integer> initialCoins = new LinkedList<>(board.getCoinOrder());
        Node StartNode = new Node(startX, startY, initialCoins, 0, 0, null, null);
        pq.add(StartNode);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (board.getTileAt(current.getX(), current.getY()).isGoal() && current.getRemainingCoins().isEmpty()) {
                return reconstructPath(current);
            }

            String stateId = current.getStateId();
            if (visited.containsKey(stateId) && visited.get(stateId) <= current.getGCost()) {
                continue;
            }
            visited.put(stateId, current.getGCost());

            for (Direction dir : Direction.values()) {
                Node nextNode = current.move(dir, board);
                if (nextNode != null) {
                    long newGCost = current.getGCost();
                    long newHCost = calculateHeuristic(nextNode.getX(), nextNode.getY(), nextNode.getRemainingCoins(),
                            board);
                    nextNode.setGCost(newGCost);
                    nextNode.setHCost(newHCost);
                    pq.add(nextNode);
                }
            }

        }
        return null;
    }

    private List<Direction> reconstructPath(Node node) {
        List<Direction> path = new ArrayList<>();
        Node curr = node;
        while (curr != null && curr.getLastMove() != null) {
            path.add(curr.getLastMove());
            curr = curr.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    private long calculateHeuristic(int curX, int curY, Queue<Integer> currentCoins, Board board) {
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
