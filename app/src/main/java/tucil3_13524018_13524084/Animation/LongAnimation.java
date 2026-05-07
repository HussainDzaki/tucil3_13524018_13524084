package tucil3_13524018_13524084.Animation;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

import javafx.application.Platform;

public class LongAnimation implements AnimationStep {
    private AtomicLong currentValue;
    private final long initialValue;
    private final long finalValue;

    public LongAnimation(AtomicLong referencedValue, long initialValue, long finalValue) {
        this.currentValue = referencedValue;
        this.initialValue = initialValue;
        this.finalValue = finalValue;
    }

    @Override
    public boolean isAnimating() {
        return false;
    }

    @Override
    public void stopAnimation() {
        return;
    }

    @Override
    public void animateForward() {
        Platform.runLater(() -> {
            currentValue.set(finalValue);
        });
    }

    @Override
    public void animateBackward() {
        Platform.runLater(() -> {
            currentValue.set(initialValue);
        });
    }

    @Override
    public void skipForward() {
        Platform.runLater(() -> {
            currentValue.set(finalValue);
        });
    }

    @Override
    public void skipBackward() {
        Platform.runLater(() -> {
            currentValue.set(initialValue);
        });
    }

    @Override
    public void setSpeedMultiplier(double multiplier) {
        // No need
    }

    @Override
    public void setThreadExecutor(ThreadPoolExecutor threadExecutor) {
        // No need
    }
}
