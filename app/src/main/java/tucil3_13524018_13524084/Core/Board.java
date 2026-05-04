package tucil3_13524018_13524084.Core;


import java.util.List;
import java.util.Queue;
import java.util.ArrayList;
import java.util.LinkedList;

import tucil3_13524018_13524084.GameEventException.*;

public class Board {
    private Integer columnSize;
    private Integer rowSize;
    private Player player;
    private GameStatus gameStatus;
    private Queue<Integer> coinOrder = new LinkedList<>();

    private List<Tile> tiles;

    public Board() {
        this.columnSize = 0;
        this.rowSize = 0;
        this.tiles = new ArrayList<Tile>();
    }

    public Board(Integer rowSize, Integer colSize, List<Tile> tiles, Player player, Queue<Integer> coinOrder) {
        this.rowSize = rowSize;
        this.columnSize = colSize;
        this.tiles = tiles;
        this.player = player;
        this.gameStatus = GameStatus.PLAYING;
        this.coinOrder = coinOrder;
    }

    public void printBoard() {
        System.out.println();
        for (int i = 0; i < tiles.size(); i++) {
            Tile t = tiles.get(i);
            if (t.getXCoords() == player.x && t.getYCoords() == player.y) {
                System.out.print('Z');
            }else if (t.getType().equals(TileType.COIN_NUMBER)) {
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

    public void movePlayer(Direction playerDirection) throws GameOverException {
        while (true) {
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

            if (targetTile.isLava()) {
                player.setPlayerStatus(false);
                gameStatus = GameStatus.GAME_OVER;
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
                }else if (currentCoinSequence > coinOrder.peek()) {
                    this.gameStatus = GameStatus.GAME_OVER;
                    throw new GameOverException("Urutan salah! Kamu menginjak " + currentCoinSequence + " padahal seharusnya " + coinOrder.peek());
                }
            }


        }
    }

    public boolean isOutOfBound(Integer x, Integer y) {
        return (x < 0 || y < 0 || x > rowSize || y > columnSize);
    }

    public Tile getTileAt(Integer x, Integer y) {
        for (Tile tile : tiles) {
            if (tile.getXCoords() == x && tile.getYCoords() == y) {
                return tile;
            }
        }
        throw new IllegalStateException("Tidak ada tile pada Koordinat: (" + x + "," + y + ")");
    }
}
