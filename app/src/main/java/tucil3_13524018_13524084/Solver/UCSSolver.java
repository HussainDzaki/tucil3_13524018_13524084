package tucil3_13524018_13524084.Solver;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import tucil3_13524018_13524084.Core.Board;
import tucil3_13524018_13524084.Core.Direction;

public class UCSSolver extends Solver {
    public UCSSolver(Board board){
        super(board);
    }

    public List<Direction> solve() {
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingLong(Node::getGCost));
        Map<String, Long> visited = new HashMap<>();
        int startX = board.getPlayer().getXCoords();
        int startY = board.getPlayer().getYCoords();
        Queue<Integer> initialCoins = new LinkedList<>(board.getCoinOrder());
        Node StartNode = new Node(startX, startY, initialCoins, 0,0, null, null);
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
                    pq.add(nextNode);
                }
            }
            
        }
        return null;
    }
}
