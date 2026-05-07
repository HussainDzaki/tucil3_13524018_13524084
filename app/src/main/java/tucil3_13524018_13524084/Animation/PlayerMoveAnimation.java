package tucil3_13524018_13524084.Animation;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import tucil3_13524018_13524084.Core.Player;
import tucil3_13524018_13524084.GUI.PlayerGUI;

public class PlayerMoveAnimation implements AnimationStep {
    protected volatile boolean animating;

    protected double speedMultiplier;

    protected PlayerGUI player;
    protected final int fromX;
    protected final int fromY;
    protected final int toX;
    protected final int toY;
    protected final double tilePerSecond;

    private ThreadPoolExecutor threadExecutor;

    public PlayerMoveAnimation(PlayerGUI player, int fromX, int fromY, int toX, int toY, double tilePerSecond) {
        animating = false;
        this.player = player;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.tilePerSecond = tilePerSecond;
        speedMultiplier = 1;
    }

    @Override
    public boolean isAnimating() {
        return animating;
    }

    @Override
    public void stopAnimation() {
        animating = false;
    }

    @Override
    public void animateForward() {
        animating = false;
        if (threadExecutor != null) {
            threadExecutor.submit(() -> movePlayer(fromX, fromY, toX, toY));
        } else {
            skipForward();
        }
    }

    @Override
    public void animateBackward() {
        animating = false;
        if (threadExecutor != null) {
            threadExecutor.submit(() -> movePlayer(toX, toY, fromX, fromY));
        } else {
            skipBackward();
        }
    }

    protected void movePlayer(int fromX, int fromY, int toX, int toY) {
        animating = true;
        setPlayerPosition(fromX, fromY);
        double currentX = fromX;
        double currentY = fromY;
        while (animating && ((toX - fromX) * (toX - currentX) > 0 || (toY - fromY) * (toY - currentY) > 0)) {
            try {
                Thread.sleep(Math.max(1, (long) Math.ceil(10 / speedMultiplier)));
                currentX += tilePerSecond / 100 * Double.compare(toX, currentX);
                currentY += tilePerSecond / 100 * Double.compare(toY, currentY);
                setPlayerPosition(currentX, currentY);
            } catch (InterruptedException e) {
                Thread.interrupted();
                setPlayerPosition(toX, toY);
                animating = false;
            }
        }
        if (animating) {
            setPlayerPosition(toX, toY);
            animating = false;
        }
    }

    protected void setPlayerPosition(double x, double y) {
        Platform.runLater(() -> {
            player.setPosition(x, y);
        });
    }

    @Override
    public void skipForward() {
        animating = false;
        setPlayerPosition(toX, toY);
    }

    @Override
    public void skipBackward() {
        animating = false;
        setPlayerPosition(fromX, fromY);
    }

    @Override
    public void setSpeedMultiplier(double multiplier) {
        this.speedMultiplier = multiplier;
    }

    @Override
    public void setThreadExecutor(ThreadPoolExecutor threadExecutor) {
        this.threadExecutor = threadExecutor;
    }
}
