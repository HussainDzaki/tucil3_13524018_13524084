package tucil3_13524018_13524084.Core;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.ArrayDeque;
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

    private int initialPlayerPosX;
    private int initialPlayerPosY;

    private List<Tile> tiles;

    public Board(int rowSize, int colSize, List<Tile> tiles, Player player) {
        this.rowSize = rowSize;
        this.columnSize = colSize;
        this.tiles = tiles;
        this.player = player;
        this.gameStatus = GameStatus.PLAYING;
        this.coinOrder = new ArrayDeque<>();

        this.initialPlayerPosX = player.x;
        this.initialPlayerPosY = player.y;

        List<Integer> coinList = new ArrayList<>();
        for (Tile t : tiles) {
            if (t.isCoin()) {
                coinMap.put(t.getCoinSequence(), t);
                coinList.add(t.getCoinSequence());
                System.out.println(t.getCoinSequence());
            } else if (t.isGoal()) {
                this.goalTile = t;
            }
        }
        coinList.sort((a, b) -> Integer.compare(a, b));
        for (Integer coin : coinList) {
            coinOrder.add(coin);
        }
    }

    public Board(Board other) {
        this.columnSize = other.columnSize;
        this.rowSize = other.rowSize;
        this.player = other.player;
        this.gameStatus = other.gameStatus;
        this.coinOrder = other.coinOrder;
        this.coinMap = other.coinMap;
        this.goalTile = other.goalTile;
        this.tiles = new ArrayList<>(other.tiles);
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
        while (gameStatus == GameStatus.PLAYING) {
            int nextX = player.x + playerDirection.dx;
            int nextY = player.y + playerDirection.dy;
            if (isOutOfBound(nextX, nextY)) {
                if (player.x == goalTile.getXCoords() && player.y == goalTile.getYCoords()) {
                    gameStatus = GameStatus.WON;
                    System.out.println("WIN : PLAYER BERHASIL KE GOAL");
                } else {
                    gameStatus = GameStatus.GAME_OVER;
                    System.out.println("GAME OVER : PLAYER KELUAR DARI PAPAN");
                }
                break;
            }
            Tile targetTile = getTileAt(nextX, nextY);
            if (targetTile.isObstacle()) {
                if (player.x == goalTile.getXCoords() && player.y == goalTile.getYCoords() && coinOrder.isEmpty()) {
                    gameStatus = GameStatus.WON;
                    System.out.println("WIN : PLAYER BERHASIL KE GOAL");
                }
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
                    targetTile.setType(TileType.COIN_COLLECTED);
                } else if (currentCoinSequence > coinOrder.peek()) {
                    this.gameStatus = GameStatus.GAME_OVER;
                    throw new GameOverException("Urutan salah! Kamu menginjak " + currentCoinSequence
                            + " padahal seharusnya " + coinOrder.peek());
                }
            }
        }
    }

    public boolean isOutOfBound(int x, int y) {
        return (x < 0 || y < 0 || x >= columnSize || y >= rowSize);
    }

    public static boolean isOutOfBound(int x, int y, Board board) {
        return (x < 0 || y < 0 || x >= board.getColumnSize() || y >= board.getRowSize());
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

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void resetBoard() {
        gameStatus = GameStatus.PLAYING;
        player.setTotalCost(0);
        player.setPlayerPos(initialPlayerPosX, initialPlayerPosY);
        coinMap.clear();
        List<Integer> coinList = new ArrayList<>();
        for (Tile tile : tiles) {
            if (tile.getType() == TileType.COIN_COLLECTED) {
                tile.setType(TileType.COIN_NUMBER);
            }
            if (tile.isCoin()) {
                coinMap.put(tile.getCoinSequence(), tile);
                coinList.add(tile.getCoinSequence());
            }
        }
        coinList.sort((a, b) -> Integer.compare(a, b));
        coinOrder.clear();
        for (Integer coin : coinList) {
            coinOrder.add(coin);
        }
    }

    public String getBoardString() {
        String boardString = new String();
        for (int y = 0; y < rowSize; y++) {
            for (int x = 0; x < columnSize; x++) {
                Tile tile = getTileAt(x, y);
                if (x == initialPlayerPosX && y == initialPlayerPosY) {
                    boardString += 'Z';
                } else if (tile.getType() == TileType.COIN_NUMBER || tile.getType() == TileType.COIN_COLLECTED) {
                    boardString += tile.getCoinSequence();
                } else {
                    boardString += tile.getType().getSymbol();
                }
            }
            boardString += "\n";
        }
        return boardString;
    }

    public String getCostString() {
        String costString = new String();
        for (int y = 0; y < rowSize; y++) {
            for (int x = 0; x < columnSize; x++) {
                costString += getTileAt(x, y).getTileCost() + " ";
            }
            costString += "\n";
        }
        return costString;
    }
}
