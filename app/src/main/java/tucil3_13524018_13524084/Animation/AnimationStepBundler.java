package tucil3_13524018_13524084.Animation;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

public class AnimationStepBundler implements AnimationStep {
    private ArrayList<AnimationStep> animationSteps;
    private double speedMultiplier = 1;
    private ThreadPoolExecutor threadExecutor = null;

    public AnimationStepBundler() {
        animationSteps = new ArrayList<>();
    }

    @Override
    public boolean isAnimating() {
        return false;
    }

    @Override
    public void stopAnimation() {
        for (AnimationStep animationStep : animationSteps) {
            animationStep.stopAnimation();
        }
    }

    @Override
    public void animateForward() {
        for (AnimationStep animationStep : animationSteps) {
            animationStep.animateForward();
        }
    }

    @Override
    public void animateBackward() {
        for (int i = animationSteps.size() - 1; i >= 0; i--) {
            animationSteps.get(i).animateBackward();
        }
    }

    @Override
    public void skipForward() {
        for (AnimationStep animationStep : animationSteps) {
            animationStep.skipForward();
        }
    }

    @Override
    public void skipBackward() {
        for (int i = animationSteps.size() - 1; i >= 0; i--) {
            animationSteps.get(i).skipBackward();
        }
    }

    @Override
    public void setSpeedMultiplier(double multiplier) {
        speedMultiplier = multiplier;
        for (AnimationStep animationStep : animationSteps) {
            animationStep.setSpeedMultiplier(multiplier);
        }
    }

    @Override
    public void setThreadExecutor(ThreadPoolExecutor threadExecutor) {
        this.threadExecutor = threadExecutor;
        for (AnimationStep animationStep : animationSteps) {
            animationStep.setThreadExecutor(threadExecutor);
        }
    }

    public void addAnimationStep(AnimationStep step) {
        step.setSpeedMultiplier(speedMultiplier);
        step.setThreadExecutor(threadExecutor);
        animationSteps.add(step);
    }
}
