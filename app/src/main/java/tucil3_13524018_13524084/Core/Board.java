package tucil3_13524018_13524084.Core;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import tucil3_13524018_13524084.GameEventException.*;

public class Board {
    private int columnSize;
    private int rowSize;
    private Player player;
    private GameStatus gameStatus;
    private Queue<Integer> coinOrder = new LinkedList<>();
    private Map<Integer, Tile> coinMap = new HashMap<>();
    private Tile goalTile;

    private List<Tile> tiles;

    public Board() {
        this.columnSize = 0;
        this.rowSize = 0;
        this.tiles = new ArrayList<Tile>();
    }

    public Board(int rowSize, int colSize, List<Tile> tiles, Player player, Queue<Integer> coinOrder) {
        this.rowSize = rowSize;
        this.columnSize = colSize;
        this.tiles = tiles;
        this.player = player;
        this.gameStatus = GameStatus.PLAYING;
        this.coinOrder = coinOrder;

        for (Tile t : tiles) {
            if (t.isCoin()) {
                coinMap.put(t.getCoinSequence(), t);
                System.out.println(t.getCoinSequence());
            } else if (t.isGoal()) {
                this.goalTile = t;
            }
        }
    }

    public Board(Board other){
        this.columnSize = other.columnSize;
        this.rowSize = other.rowSize;
        this.player = other.player;
        this.gameStatus = other.gameStatus;
        this.coinOrder = other.coinOrder;
        this.coinMap = other.coinMap;
        this.goalTile = other.goalTile;
        this.tiles = other.tiles;
    }

    public void printBoard() {
        System.out.println();
        for (int i = 0; i < tiles.size(); i++) {
            Tile t = tiles.get(i);
            if (t.getXCoords() == player.x && t.getYCoords() == player.y) {
                System.out.print('Z');
            } else if (t.getType().equals(TileType.COIN_NUMBER)) {
                System.out.print(t.getCoinSequence());
            }

            else {
                System.out.print(t.getType().getSymbol());
            }
            if ((i + 1) % rowSize == 0) {
                System.out.println();
            }

        }
        System.out.println(String.format("\nPlayer at (%d, %d)", player.x, player.y));
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public Tile getNextTarget() {
        if (!coinOrder.isEmpty()) {
            return coinMap.get(coinOrder.peek());
        }
        return goalTile;
    }

    public void movePlayer(Direction playerDirection) throws GameOverException {
        while (true && gameStatus == GameStatus.PLAYING) {
            int nextX = player.x + playerDirection.dx;
            int nextY = player.y + playerDirection.dy;
            if (isOutOfBound(nextX, nextY)) {
                break;
            }
            Tile targetTile = getTileAt(nextX, nextY);
            if (targetTile.isObstacle()) {
                break;
            }

            player.x = nextX;
            player.y = nextY;
            player.setTotalCost(player.getTotalCost() + targetTile.getTileCost());

            if (targetTile.isLava()) {
                player.setPlayerStatus(false);
                gameStatus = GameStatus.GAME_OVER;
                System.out.println("GAME OVER : PLAYER TERKENA LAVA");
                break;
            }

            if (targetTile.isCoin()) {
                int currentCoinSequence = targetTile.getCoinSequence();
                if (coinOrder.isEmpty()) {
                    break;
                }
                if (coinOrder.peek() == currentCoinSequence) {
                    coinOrder.poll();
                    targetTile.setType(TileType.PATH);
                } else if (currentCoinSequence > coinOrder.peek()) {
                    this.gameStatus = GameStatus.GAME_OVER;
                    throw new GameOverException("Urutan salah! Kamu menginjak " + currentCoinSequence
                            + " padahal seharusnya " + coinOrder.peek());
                }
            }
        }
    }

    public boolean isOutOfBound(int x, int y) {
        return (x < 0 || y < 0 || x >= rowSize || y >= columnSize);
    }

    public static boolean isOutOfBound(int x, int y, Board board) {
        return (x < 0 || y < 0 || x >= board.getRowSize() || y >= board.getColumnSize());
    }

    public int getColumnSize() {
        return columnSize;
    }

    public int getRowSize() {
        return rowSize;
    }

    public Tile getTileAt(int x, int y) {
        for (Tile tile : tiles) {
            if (tile.getXCoords() == x && tile.getYCoords() == y) {
                return tile;
            }
        }
        throw new IllegalStateException("Tidak ada tile pada Koordinat: (" + x + "," + y + ")");
    }

    public Tile findCoinTile(int sequence) {
        return coinMap.get(sequence);
    }
    public Tile getGoalTile() {
        return goalTile;
    }

    public Player getPlayer() {
        return player;
    }

    public Queue<Integer> getCoinOrder() {
        return coinOrder;
    }

}
