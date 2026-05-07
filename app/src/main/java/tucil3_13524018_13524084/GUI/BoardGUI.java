package tucil3_13524018_13524084.GUI;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import tucil3_13524018_13524084.Core.Board;
import tucil3_13524018_13524084.Core.Tile;

public class BoardGUI implements Drawable {
    private Board board;
    private List<TileGUI> tileGUIs;
    private PlayerGUI playerGUI;
    private double canvasWidth;
    private double canvasHeight;

    public BoardGUI(Board board, double canvasWidth, double canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        setBoard(board);
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
        tileGUIs = new ArrayList<>();
        for (Tile tile : board.getTiles()) {
            tileGUIs.add(new TileGUI(tile,
                    canvasWidth / board.getColumnSize(),
                    canvasHeight / board.getRowSize()));
        }
        playerGUI = new PlayerGUI(board.getPlayer(),
                canvasWidth / board.getColumnSize(),
                canvasHeight / board.getRowSize());
    }

    public PlayerGUI getPlayerGUI() {
        return playerGUI;
    }

    public void draw(GraphicsContext gc) {
        for (TileGUI tileGUI : tileGUIs) {
            tileGUI.draw(gc);
        }
        playerGUI.draw(gc);
    }

    public void resetBoard() {
        board.resetBoard();
        playerGUI.setPosition(board.getPlayer().getXCoords(), board.getPlayer().getYCoords());
    }
}
