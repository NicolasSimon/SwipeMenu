package view;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

public class MyAnimation extends Animation {
    private int                                     finalPosition;
    private int                                     initialPosition;
    private int                                     mBig;
    private int                                     mSmall;
    private ViewGroup.LayoutParams                  mParams;

    private View                                    mRevealLayout;

    private MyAnimationUpdate                       mCallback;
    private boolean                                 mLandscape;

    public MyAnimation(MyAnimationUpdate callback, boolean landscape) {
        mCallback = callback;
        mLandscape = landscape;
        this.setInterpolator(new MyAnimationInterpolator());
    }

    public void setViewToAnimate(View v, ViewGroup.LayoutParams params) {
        mRevealLayout = v;
        mParams = params;
    }

    public void setBounds(int small, int big) {
        mSmall = small;
        mBig = big;
    }

    public void setPositions(int start, int end) {
        initialPosition = start;
        finalPosition = end;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        if (!mLandscape) {
            if (finalPosition == mBig) {
                mParams.height = (interpolatedTime == 1) ? finalPosition : initialPosition + (int) (Math.abs(finalPosition - initialPosition) * interpolatedTime);
            } else {
                mParams.height = initialPosition - (int) (Math.abs(finalPosition - initialPosition) * interpolatedTime);
            }
            mRevealLayout.setLayoutParams(mParams);
            if (mCallback != null) {
                mCallback.onUpdate(((float) mParams.height - (float) mSmall) / (float) mBig);
            }
        } else {
            if (finalPosition == mBig) {
                mParams.width = (interpolatedTime == 1) ? finalPosition : initialPosition + (int)(Math.abs(finalPosition - initialPosition) * interpolatedTime);
            } else {
                mParams.width = initialPosition - (int)(Math.abs(finalPosition - initialPosition) * interpolatedTime);
            }
            mRevealLayout.setLayoutParams(mParams);
            if (mCallback != null) {
                mCallback.onUpdate(((float)mParams.width - (float)mSmall) / (float)mBig);
            }
        }
        mRevealLayout.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return (true);
    }

    private class MyAnimationInterpolator implements Interpolator {
        public static final float                       LINEAR_TIME = 0.46667f;
        public static final float                       FIRST_BOUND_TIME = 0.26666f;

        public MyAnimationInterpolator() {}

        public float getInterpolation(float t) {
            if (t < LINEAR_TIME) {
                return (linear(t));
            } else if (t < (LINEAR_TIME + FIRST_BOUND_TIME)) {
                return (firstBounce(t));
            } else {
                return (secondBounce(t));
            }
        }

        private float linear(float t) {
            return (4.592f * t * t);
        }

        private float firstBounce(float t) {
            return (2.5f * t * t - 3.0f * t + 1.85556f);
        }

        private float secondBounce(float t) {
            return (0.625f * t * t - 1.08f * t + 1.458f);
        }
    }

    public interface MyAnimationUpdate {
        void onUpdate(float percentage);
    }
}
