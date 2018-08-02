package com.fanwe.lib.swipemenu;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.fanwe.lib.gesture.FTouchHelper;

public abstract class BaseSwipeMenu extends ViewGroup implements SwipeMenu
{
    private final MenuViewContainer mMenuViewContainer;
    private View mContentView;

    private State mState = State.Closed;
    private final int mMinFlingVelocity;
    private OnStateChangedCallback mOnStateChangedCallback;

    public BaseSwipeMenu(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mMenuViewContainer = new MenuViewContainer(context);
        mMenuViewContainer.setMenuGravity(Gravity.Right);
        addView(mMenuViewContainer);

        mMinFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    @Override
    public final void setOnStateChangedCallback(OnStateChangedCallback callback)
    {
        mOnStateChangedCallback = callback;
    }

    @Override
    public final void setMenuGravity(Gravity gravity)
    {
        mMenuViewContainer.setMenuGravity(gravity);
    }

    @Override
    public final void open()
    {
        smoothScroll(getContentView().getLeft(), mMenuViewContainer.getLeftForContentView(State.Opened));
    }

    @Override
    public final void close()
    {
        smoothScroll(getContentView().getLeft(), mMenuViewContainer.getLeftForContentView(State.Closed));
    }

    @Override
    public final void setMenuView(View view)
    {
        mMenuViewContainer.setContentView(view);
    }

    @Override
    public final void setContentView(View view)
    {
        if (mContentView != view)
        {
            removeView(mContentView);

            Utils.removeViewFromParent(view);
            addView(view);

            mContentView = view;
        }
    }

    @Override
    public final State getState()
    {
        return mState;
    }

    protected final int getMaxScrollDistance()
    {
        return mMenuViewContainer.getMaxScrollDistance();
    }

    protected final View getContentView()
    {
        if (mContentView == null)
        {
            final View view = new View(getContext());
            view.setLayoutParams(new LayoutParams(0, 0));
            setContentView(view);
        }
        return mContentView;
    }

    private void setState(State state)
    {
        if (state == null)
            throw new NullPointerException();
        if (mState == state)
            return;

        mState = state;

        if (mOnStateChangedCallback != null)
            mOnStateChangedCallback.onStateChanged(state, this);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        final int count = getChildCount();
        for (int i = 1; i < count; i++)
        {
            final View child = getChildAt(i);
            if (i == 1)
                mContentView = child;
            else if (i == 2)
                setMenuView(child);
            else
                throw new IllegalArgumentException("SwipeMenu can only has 2 children at most");
        }
    }

    /**
     * 是否可以从左向右拖动
     *
     * @return
     */
    protected final boolean canPullLeftToRight()
    {
        switch (mMenuViewContainer.getMenuGravity())
        {
            case Right:
                return mState == State.Opened;
            case Left:
                return mState == State.Closed;
            default:
                throw new AssertionError();
        }
    }

    /**
     * 是否可以从右向左拖动
     *
     * @return
     */
    protected final boolean canPullRightToLeft()
    {
        switch (mMenuViewContainer.getMenuGravity())
        {
            case Right:
                return mState == State.Closed;
            case Left:
                return mState == State.Opened;
            default:
                throw new AssertionError();
        }
    }

    /**
     * 移动View
     *
     * @param delta
     */
    protected final void moveViews(int delta)
    {
        if (delta == 0)
            return;

        final View contentView = getContentView();

        final int left = contentView.getLeft();
        final int minLeft = getLeftContentViewMin();
        final int maxLeft = getLeftContentViewMax();
        delta = FTouchHelper.getLegalDelta(left, minLeft, maxLeft, delta);
        if (delta == 0)
            return;

        ViewCompat.offsetLeftAndRight(contentView, delta);
    }

    /**
     * 拖动结束后需要执行的逻辑
     *
     * @param velocityX
     */
    protected final void dealDragFinish(int velocityX)
    {
        final int leftStart = getContentView().getLeft();
        int leftEnd = 0;

        if (Math.abs(velocityX) > mMinFlingVelocity)
        {
            if (velocityX > 0)
            {
                leftEnd = getLeftContentViewMax();
            } else
            {
                leftEnd = getLeftContentViewMin();
            }
        } else
        {
            final int leftMin = getLeftContentViewMin();
            final int leftMax = getLeftContentViewMax();
            if (leftStart >= ((leftMin + leftMax) / 2))
            {
                leftEnd = getLeftContentViewMax();
            } else
            {
                leftEnd = getLeftContentViewMin();
            }
        }

        smoothScroll(leftStart, leftEnd);
    }

    private boolean smoothScroll(int start, int end)
    {
        final boolean scrolled = onSmoothScroll(start, end);
        if (scrolled)
            invalidate();
        else
            dealViewIdle();
        return scrolled;
    }

    protected abstract boolean onSmoothScroll(int start, int end);

    /**
     * View处于静止未拖动状态时候需要执行的逻辑
     */
    protected final void dealViewIdle()
    {
        if (isViewIdle())
        {
            final int left = getContentView().getLeft();
            if (left == mMenuViewContainer.getLeftForContentView(State.Closed))
            {
                setState(State.Closed);
            } else if (left == mMenuViewContainer.getLeftForContentView(State.Opened))
            {
                setState(State.Opened);
            } else
            {
                requestLayout();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = 0;
        int height = 0;

        final View contentView = getContentView();
        if (contentView.getVisibility() != GONE)
        {
            contentView.measure(getChildMeasureSpec(widthMeasureSpec, 0, contentView.getLayoutParams().width),
                    getChildMeasureSpec(heightMeasureSpec, 0, contentView.getLayoutParams().height));

            mMenuViewContainer.measure(MeasureSpec.makeMeasureSpec(contentView.getMeasuredWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(contentView.getMeasuredHeight(), MeasureSpec.EXACTLY));

            width = contentView.getMeasuredWidth();
            height = contentView.getMeasuredHeight();
        }

        width = Math.max(width, getSuggestedMinimumWidth());
        height = Math.max(height, getSuggestedMinimumHeight());

        width = Utils.getMeasureSize(width, widthMeasureSpec);
        height = Utils.getMeasureSize(height, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    /**
     * view是否处于空闲状态（静止且未被拖动状态）
     *
     * @return
     */
    protected abstract boolean isViewIdle();

    protected final int getLeftContentViewMin()
    {
        return mMenuViewContainer.getLeftContentViewMin();
    }

    protected final int getLeftContentViewMax()
    {
        return mMenuViewContainer.getLeftContentViewMax();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        final View contentView = getContentView();
        if (contentView.getVisibility() == GONE)
            return;

        // ContentView
        int left = 0;
        int top = 0;
        if (isViewIdle())
        {
            left = mMenuViewContainer.getLeftForContentView(mState);
            top = 0;
        } else
        {
            left = contentView.getLeft();
            top = contentView.getTop();
        }

        contentView.layout(left, top,
                left + contentView.getMeasuredWidth(),
                top + contentView.getMeasuredHeight());

        // MenuView
        if (mMenuViewContainer.getVisibility() != GONE)
        {
            mMenuViewContainer.layout(0, 0,
                    mMenuViewContainer.getMeasuredWidth(),
                    mMenuViewContainer.getMeasuredHeight());
        }

        if (ViewCompat.getZ(contentView) <= ViewCompat.getZ(mMenuViewContainer))
            ViewCompat.setZ(contentView, ViewCompat.getZ(mMenuViewContainer) + 1);
    }
}
