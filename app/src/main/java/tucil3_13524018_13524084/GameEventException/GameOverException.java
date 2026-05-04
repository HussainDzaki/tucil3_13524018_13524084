package tucil3_13524018_13524084.GameEventException;

public class GameOverException extends GameEventException {
    public GameOverException(String reason) {
        super("GAME OVER: " + reason);
    }
}