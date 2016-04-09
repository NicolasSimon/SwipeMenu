package view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import ns.swipemenu.R;

public class SwipeMenu extends LinearLayout implements View.OnTouchListener {
    private static final int                ORIENTATION_TOP_TO_BOTTOM = 0;
    private static final int                ORIENTATION_BOTTOM_TO_TOP = 1;
    private static final int                ORIENTATION_LEFT_TO_RIGHT = 2;
    private static final int                ORIENTATION_RIGHT_TO_LEFT = 3;

    private final int                       DEFAULT_MIN_SIZE = 100;
    private final int                       DEFAULT_MAX_SIZE = 300;
    private int                             startX;
    private int                             startY;
    private int                             heightAtStart;
    private int                             widthAtStart;
    private ViewGroup.LayoutParams          mParams;

    private SwipeMenu                  mInstance;
    private DropDownInterface               mCallback;

    private VelocityTracker                 mTracker;
    private MyAnimation                     mAnimation;

    private int                             mSmall;
    private int                             mBig;

    private int                             mOrientation;

    public SwipeMenu(Context context) {
        super(context);
        mBig = DEFAULT_MAX_SIZE;
        mSmall = DEFAULT_MIN_SIZE;
        init();
    }

    public SwipeMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        getCustomAttributes(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SwipeMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getCustomAttributes(context, attrs);
        init();
    }

    private void getCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DropDownLayout, 0, 0);
        try {
            mBig = (int)ta.getDimension(R.styleable.DropDownLayout_maxHeight, DEFAULT_MAX_SIZE);
            mSmall = (int)ta.getDimension(R.styleable.DropDownLayout_minHeight, DEFAULT_MIN_SIZE);
            mOrientation = ta.getInt(R.styleable.DropDownLayout_orientation, ORIENTATION_TOP_TO_BOTTOM);
        } finally {
            ta.recycle();
        }
    }

    public void setCallback(DropDownInterface callback) {
        mCallback = callback;
    }

    private void init() {
        mInstance = this;
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                removeOnGlobalLayoutListener(mInstance, this);
                mParams = getLayoutParams();
                animateQuickly();
            }
        });
        this.setOnTouchListener(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener){
        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    public void animateQuickly() {
        final int start;
        if (mOrientation < ORIENTATION_LEFT_TO_RIGHT) {
            start = mParams.height;
        } else {
            start = mParams.width;

        }
        final int twentypercent = (int)(((float)mBig - (float)mSmall) * 20.0f / 100.0f);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation a = getAnimation(start, twentypercent);
                a.setDuration(200);
                a.setInterpolator(new FastOutSlowInInterpolator());
                mInstance.startAnimation(a);
            }
        }, 800);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation a = getAnimation(start, twentypercent);
                a.setDuration(200);
                a.setInterpolator(new ReverseInterpolator(new FastOutSlowInInterpolator()));
                mInstance.startAnimation(a);
            }
        }, 1000);
    }

    private Animation getAnimation(final int start, final int delta) {
        return (new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (mOrientation < ORIENTATION_LEFT_TO_RIGHT) {

                    mParams.height = (int)(start + (delta * interpolatedTime));
                } else {
                    mParams.width = (int)(start + (delta * interpolatedTime));
                }
                mInstance.setLayoutParams(mParams);
                mInstance.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return (true);
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mParams == null) {
            return (false);
        }
        int pointerId = event.getPointerId(event.getActionIndex());
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if(mTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                    mTracker = VelocityTracker.obtain();
                } else {
                    mTracker.clear();
                }
                mTracker.addMovement(event);
                startX = X;
                startY = Y;
                heightAtStart = mParams.height;
                widthAtStart = mParams.width;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startX = 0;
                startY = 0;
                heightAtStart = 0;
                widthAtStart = 0;

                // When you want to determine the velocity, call
                // computeCurrentVelocity(). Then call getXVelocity()
                // and getYVelocity() to retrieve the velocity for each pointer ID.
                mTracker.computeCurrentVelocity(1000);
                float velocity;
                int middle = (mSmall + mBig) / 2;
                int calculatedFinalPosition;
                int duration;
                switch (mOrientation) {
                    case ORIENTATION_BOTTOM_TO_TOP:
                        velocity = VelocityTrackerCompat.getYVelocity(mTracker, pointerId);
                        calculatedFinalPosition = (mParams.height > middle) ? mBig : mSmall;
                        duration = (int)(Math.abs(calculatedFinalPosition - mParams.height) / getResources().getDisplayMetrics().density);
                        //1dp/ms -> 1000dp/s
                        if (velocity < -1000) {
                            //velocity is pixels/second
                            float newSpeed = (Math.abs(velocity) / 1000.0f);
                            duration = (int)(Math.abs(calculatedFinalPosition - mParams.height) / newSpeed);
                            calculatedFinalPosition = mBig;
                        } else if (velocity > 1000) {
                            float newSpeed = (velocity / 1000.0f);
                            duration = (int)(Math.abs(calculatedFinalPosition - mParams.height) / newSpeed);
                            calculatedFinalPosition = mSmall;
                        }
                        break;
                    case ORIENTATION_LEFT_TO_RIGHT:
                        velocity = VelocityTrackerCompat.getXVelocity(mTracker, pointerId);
                        calculatedFinalPosition = (mParams.width > middle) ? mBig : mSmall;
                        duration = (int)(Math.abs(calculatedFinalPosition - mParams.width) / getResources().getDisplayMetrics().density);
                        //1dp/ms -> 1000dp/s
                        if (velocity < -1000) {
                            //velocity is pixels/second
                            float newSpeed = (Math.abs(velocity) / 1000.0f);
                            duration = (int)(Math.abs(calculatedFinalPosition - mParams.width) / newSpeed);
                            calculatedFinalPosition = mSmall;
                        } else if (velocity > 1000) {
                            float newSpeed = (velocity / 1000.0f);
                            duration = (int)(Math.abs(calculatedFinalPosition - mParams.width) / newSpeed);
                            calculatedFinalPosition = mBig;
                        }
                        break;
                    case ORIENTATION_RIGHT_TO_LEFT:
                        velocity = VelocityTrackerCompat.getXVelocity(mTracker, pointerId);
                        calculatedFinalPosition = (mParams.width > middle) ? mBig : mSmall;
                        duration = (int)(Math.abs(calculatedFinalPosition - mParams.width) / getResources().getDisplayMetrics().density);
                        //1dp/ms -> 1000dp/s
                        if (velocity < -1000) {
                            //velocity is pixels/second
                            float newSpeed = (Math.abs(velocity) / 1000.0f);
                            duration = (int)(Math.abs(calculatedFinalPosition - mParams.width) / newSpeed);
                            calculatedFinalPosition = mBig;
                        } else if (velocity > 1000) {
                            float newSpeed = (velocity / 1000.0f);
                            duration = (int)(Math.abs(calculatedFinalPosition - mParams.width) / newSpeed);
                            calculatedFinalPosition = mSmall;
                        }
                        break;
                    default:        //TOP_TO_BOTTOM
                        velocity = VelocityTrackerCompat.getYVelocity(mTracker, pointerId);
                        calculatedFinalPosition = (mParams.height > middle) ? mBig : mSmall;
                        duration = (int)(Math.abs(calculatedFinalPosition - mParams.height) / getResources().getDisplayMetrics().density);
                        //1dp/ms -> 1000dp/s
                        if (velocity < -1000) {
                            //velocity is pixels/second
                            float newSpeed = (Math.abs(velocity) / 1000.0f);
                            duration = (int)(Math.abs(calculatedFinalPosition - mParams.height) / newSpeed);
                            calculatedFinalPosition = mSmall;
                        } else if (velocity > 1000) {
                            float newSpeed = (velocity / 1000.0f);
                            duration = (int)(Math.abs(calculatedFinalPosition - mParams.height) / newSpeed);
                            calculatedFinalPosition = mBig;
                        }
                        break;
                }

                duration = Math.max(duration, 200);

                if (mAnimation == null) {
                    mAnimation = new MyAnimation(new MyAnimation.MyAnimationUpdate() {
                        @Override
                        public void onUpdate(float percentage) {
                            if (mCallback != null) {
                                mCallback.onUpdate(percentage);
                            }
                        }
                    }, mOrientation >= ORIENTATION_LEFT_TO_RIGHT);
                    mAnimation.setBounds(mSmall, mBig);
                }

                mAnimation.setViewToAnimate(mInstance, mParams);
                switch (mOrientation) {
                    case ORIENTATION_TOP_TO_BOTTOM:
                    case ORIENTATION_BOTTOM_TO_TOP:
                        mAnimation.setPositions(mParams.height, calculatedFinalPosition);
                        break;
                    case ORIENTATION_LEFT_TO_RIGHT:
                    default:
                        mAnimation.setPositions(mParams.width, calculatedFinalPosition);
                        break;
                }
                mAnimation.setDuration(duration);
                mInstance.startAnimation(mAnimation);
                break;
            case MotionEvent.ACTION_MOVE:
                mTracker.addMovement(event);
                int yDelta = Y - startY;
                int xDelta = X - startX;
                switch (mOrientation) {
                    case ORIENTATION_BOTTOM_TO_TOP:
                        yDelta = startY - Y;
                        if ((heightAtStart + yDelta < mSmall) || (heightAtStart + yDelta > mBig)) {
                            break;
                        }
                        mParams.height = heightAtStart + yDelta;
                        if (mCallback != null) {
                            mCallback.onUpdate(((float)mParams.height - (float)mSmall) / (float)mBig);
                        }
                        mInstance.setLayoutParams(mParams);
                        break;
                    case ORIENTATION_LEFT_TO_RIGHT:
                        if ((widthAtStart + xDelta < mSmall) || (widthAtStart + xDelta > mBig)) {
                            break;
                        }
                        mParams.width = widthAtStart + xDelta;
                        if (mCallback != null) {
                            mCallback.onUpdate(((float)mParams.width - (float)mSmall) / (float)mBig);
                        }
                        mInstance.setLayoutParams(mParams);
                        break;
                    case ORIENTATION_RIGHT_TO_LEFT:
                        xDelta = startX - X;
                        if ((widthAtStart + xDelta < mSmall) || (widthAtStart + xDelta > mBig)) {
                            break;
                        }
                        mParams.width = widthAtStart + xDelta;
                        if (mCallback != null) {
                            mCallback.onUpdate(((float)mParams.width - (float)mSmall) / (float)mBig);
                        }
                        mInstance.setLayoutParams(mParams);
                        break;
                    default:        //TOP_TO_BOTTOM
                        if ((heightAtStart + yDelta < mSmall) || (heightAtStart + yDelta > mBig)) {
                            break;
                        }
                        mParams.height = heightAtStart + yDelta;
                        if (mCallback != null) {
                            mCallback.onUpdate(((float)mParams.height - (float)mSmall) / (float)mBig);
                        }
                        mInstance.setLayoutParams(mParams);
                        break;
                }
                break;
        }
        mInstance.getParent().requestLayout();
        return (true);
    }

    public interface DropDownInterface {
        void onUpdate(float percentage);
    }

    public class ReverseInterpolator implements Interpolator {
        private Interpolator interpolator;

        public ReverseInterpolator(Interpolator interpolator){
            this.interpolator = interpolator;
        }

        public ReverseInterpolator(){
            this (new LinearInterpolator());
        }

        @Override
        public float getInterpolation(float input) {
            return (1 - interpolator.getInterpolation(input));
        }
    }
}
