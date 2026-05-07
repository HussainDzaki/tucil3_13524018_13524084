package tucil3_13524018_13524084.Controller;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.stage.WindowEvent;
import tucil3_13524018_13524084.Animation.AnimationStep;
import tucil3_13524018_13524084.GUI.BoardGUI;

public class AlgorithmController {
    private BoardGUI boardGUI;
    private Canvas canvas;

    private ScheduledThreadPoolExecutor animationThreadExecutor;
    private ThreadPoolExecutor stepThreadExecutor;

    private List<AnimationStep> animation;
    private AnimationStep currentAnimationStep;
    private volatile boolean playingForward = false;
    private volatile boolean playingBackward = false;
    private volatile int animationProgress = 0;
    private double speedMultiplier = 1;

    public AlgorithmController(BoardGUI boardGUI, Canvas canvas) {
        this.boardGUI = boardGUI;
        this.canvas = canvas;

        animationThreadExecutor = new ScheduledThreadPoolExecutor(3);
        stepThreadExecutor = new ThreadPoolExecutor(3, 5, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        animationThreadExecutor.scheduleWithFixedDelay(() -> {
            if (playingForward) {
                if (currentAnimationStep == null || !currentAnimationStep.isAnimating()) {
                    if (0 <= animationProgress && animationProgress < animation.size()) {
                        currentAnimationStep = animation.get(animationProgress);
                        currentAnimationStep.animateForward();
                        animationProgress++;
                    } else {
                        playingForward = false;
                    }
                }
            } else if (playingBackward) {
                if (currentAnimationStep == null || !currentAnimationStep.isAnimating()) {
                    if (0 < animationProgress && animationProgress <= animation.size()) {
                        animationProgress--;
                        currentAnimationStep = animation.get(animationProgress);
                        currentAnimationStep.animateBackward();
                    } else {
                        playingBackward = false;
                    }
                }
            }
        }, 1, 1, TimeUnit.MILLISECONDS);

        Platform.runLater(() -> {
            canvas.getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
                animationThreadExecutor.shutdown();
                stepThreadExecutor.shutdown();
            });
        });

        setSpeedMultiplier(1);
    }

    public void setAnimation(List<AnimationStep> animation) {
        animationProgress = 0;
        this.animation = animation;
        for (AnimationStep animationStep : animation) {
            animationStep.setThreadExecutor(stepThreadExecutor);
            animationStep.setSpeedMultiplier(speedMultiplier);
        }
    }

    public int getAnimationProgress() {
        if (animation == null)
            return 0;
        return animationProgress;
    }

    public int getMaxProgress() {
        if (animation == null)
            return 0;
        return animation.size();
    }

    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
        if (animation != null) {
            for (AnimationStep animationStep : animation) {
                animationStep.setSpeedMultiplier(speedMultiplier);
            }
        }
    }

    public void clearAnimation() {
        this.animation = null;
    }

    public void playForward() {
        if (animation == null) {
            throw new IllegalStateException("Animation is not set.");
        }
        playingForward = true;
        playingBackward = false;
    }

    public void playBackward() {
        if (animation == null) {
            throw new IllegalStateException("Animation is not set.");
        }
        playingForward = false;
        playingBackward = true;
    }

    public void pause() {
        if (animation == null) {
            throw new IllegalStateException("Animation is not set.");
        }
        playingForward = false;
        playingBackward = false;
    }

    public void nextStep() {
        if (animation == null) {
            throw new IllegalStateException("Animation is not set.");
        }
        if (currentAnimationStep != null && currentAnimationStep.isAnimating()) {
            // currentAnimationStep.skipForward();
        } else if (0 <= animationProgress && animationProgress < animation.size()) {
            currentAnimationStep = animation.get(animationProgress);
            currentAnimationStep.animateForward();
            animationProgress++;
        } else if (animationProgress == animation.size()) {
            animation.get(animationProgress - 1).skipForward();
        }
        playingBackward = false;
        playingForward = false;
    }

    public void previousStep() {
        if (animation == null) {
            throw new IllegalStateException("Animation is not set.");
        }
        if (currentAnimationStep != null && currentAnimationStep.isAnimating()) {
            // currentAnimationStep.skipBackward();
        } else if (0 < animationProgress && animationProgress <= animation.size()) {
            animationProgress--;
            currentAnimationStep = animation.get(animationProgress);
            currentAnimationStep.animateBackward();
        } else if (animationProgress == 0) {
            animation.get(animationProgress).skipBackward();
        }
        playingBackward = false;
        playingForward = false;
    }
}
