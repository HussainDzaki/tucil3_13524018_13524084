package tucil3_13524018_13524084.Controller;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import tucil3_13524018_13524084.Animation.PlayerMoveAnimation;
import tucil3_13524018_13524084.Core.Board;
import tucil3_13524018_13524084.Core.Direction;
import tucil3_13524018_13524084.Core.Player;
import tucil3_13524018_13524084.Core.TileType;
import tucil3_13524018_13524084.GUI.BoardGUI;
import tucil3_13524018_13524084.GameEventException.GameOverException;

public class GameController {
    private BoardGUI boardGUI;
    private Canvas canvas;

    private ScheduledThreadPoolExecutor inputThreadExecutor;
    private ThreadPoolExecutor animationThreadExecutor;

    private boolean playing = true;
    private boolean reset = false;

    private final long INPUT_BUFFER_MILLIS = 100;
    private Queue<Pair<KeyCode, Long>> inputBuffer;

    private PlayerMoveAnimation move;

    public GameController(BoardGUI boardGUI, Canvas canvas) {
        this.inputBuffer = new ArrayDeque<>();
        this.boardGUI = boardGUI;
        this.canvas = canvas;

        animationThreadExecutor = new ThreadPoolExecutor(3, 5, 2, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        inputThreadExecutor = new ScheduledThreadPoolExecutor(1);
        inputThreadExecutor.scheduleAtFixedRate(() -> {
            while (!inputBuffer.isEmpty()
                    && System.currentTimeMillis() - inputBuffer.peek().getValue() > INPUT_BUFFER_MILLIS) {
                inputBuffer.remove();
            }

            if (move != null && move.isAnimating() || inputBuffer.isEmpty()) {
                return;
            }
            if (!playing) {
                if (reset) {
                    reset = false;
                    boardGUI.resetBoard();
                }
                return;
            }
            KeyCode code = inputBuffer.remove().getKey();
            if (code == KeyCode.R) {
                boardGUI.resetBoard();
            } else {
                int srcX = boardGUI.getBoard().getPlayer().getXCoords();
                int srcY = boardGUI.getBoard().getPlayer().getYCoords();
                try {
                    if (code == KeyCode.W || code == KeyCode.UP) {
                        boardGUI.getBoard().movePlayer(Direction.UP);
                    } else if (code == KeyCode.A || code == KeyCode.LEFT) {
                        boardGUI.getBoard().movePlayer(Direction.LEFT);
                    } else if (code == KeyCode.S || code == KeyCode.DOWN) {
                        boardGUI.getBoard().movePlayer(Direction.DOWN);
                    } else if (code == KeyCode.D || code == KeyCode.RIGHT) {
                        boardGUI.getBoard().movePlayer(Direction.RIGHT);
                    }
                } catch (GameOverException e) {
                    System.out.println(e);
                }
                int dstX = boardGUI.getBoard().getPlayer().getXCoords();
                int dstY = boardGUI.getBoard().getPlayer().getYCoords();
                move = new PlayerMoveAnimation(boardGUI.getPlayerGUI(), srcX, srcY, dstX, dstY, 20);
                move.setThreadExecutor(animationThreadExecutor);
                move.animateForward();
            }
        }, 10, 10, TimeUnit.MILLISECONDS);

        Platform.runLater(() -> {
            this.canvas.getScene().setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.W
                        || event.getCode() == KeyCode.A
                        || event.getCode() == KeyCode.S
                        || event.getCode() == KeyCode.D
                        || event.getCode().isArrowKey()
                        || event.getCode() == KeyCode.R) {
                    System.out.println(event.getCode());
                    inputBuffer.add(new Pair<KeyCode, Long>(event.getCode(), System.currentTimeMillis()));
                    if (event.getCode().isArrowKey())
                        event.consume();
                }
            });
            this.canvas.getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (e) -> {
                inputThreadExecutor.shutdown();
                animationThreadExecutor.shutdown();
            });
        });
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        if (playing == false) {
            reset = true;
        }
        this.playing = playing;
    }

    public String getMessage() {
        Board board = boardGUI.getBoard();

        switch (board.getGameStatus()) {
            case GAME_OVER:
                Player player = board.getPlayer();
                switch (board.getTileAt(player.getXCoords(), player.getYCoords()).getType()) {
                    case LAVA:
                        return "GAME OVER! You died in the lava. Press R to restart.";
                    case COIN_COLLECTED:
                    case COIN_NUMBER:
                        return "GAME OVER! You got a coin in the wrong order. Press R to restart.";
                    default:
                        return "GAME OVER! You went out of bound. Press R to restart.";
                }
            case WON:
                return "You WIN! You've reach the goal. Press R to restart.";
            case PLAYING:
            default:
                return "Play with WASD or ARROW KEYS. Press R to restart.";
        }
    }

    public void resetBoard() {
        boardGUI.getBoard().resetBoard();
    }
}
