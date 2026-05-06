package tucil3_13524018_13524084.Animation;

import java.util.concurrent.ThreadPoolExecutor;

public interface AnimationStep {
    public boolean isAnimating();
    public void stopAnimation();
    public void animateForward();
    public void animateBackward();
    public void skipForward();
    public void skipBackward();
    public void setSpeedMultiplier(double multiplier);
    public void setThreadExecutor(ThreadPoolExecutor threadExecutor);
}