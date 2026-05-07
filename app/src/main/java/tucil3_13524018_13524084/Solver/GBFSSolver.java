package tucil3_13524018_13524084.Solver;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import tucil3_13524018_13524084.Core.Direction;
import tucil3_13524018_13524084.Core.Board;

public class GBFSSolver extends Solver {

    public GBFSSolver(Board board) {
        super(board);
    }

    @Override
    public List<Direction> solve() {
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingLong(Node::getHCost));

        Set<String> visited = new HashSet<>();
        int startX = board.getPlayer().getXCoords();
        int startY = board.getPlayer().getYCoords();
        Queue<Integer> initialCoins = new LinkedList<>(board.getCoinOrder());

        long hInitial = calculateHeuristic(startX, startY, initialCoins, board);
        Node StartNode = new Node(startX, startY, initialCoins, 0, hInitial, null, null);
        pq.add(StartNode);
        while (!pq.isEmpty()) {
            Node current = pq.poll();

            if (board.getTileAt(current.getX(), current.getY()).isGoal() && current.getRemainingCoins().isEmpty()) {
                return reconstructPath(current);
            }

            String stateId = current.getX() + "," + current.getY() + "," + current.getRemainingCoins().size();
            visited.add(stateId);
            for (Direction dir : Direction.values()) {
                Node nextNode = current.move(dir, board);
                if (nextNode != null && !visited.contains(nextNode.getStateId())) {
                    long h = calculateHeuristic(nextNode.getX(), nextNode.getY(), nextNode.getRemainingCoins(), board);
                    nextNode.setHCost(h);
                    pq.add(nextNode);
                }
            }
        }
        return null;
    }
}
