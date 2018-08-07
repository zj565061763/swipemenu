package com.fanwe.lib.swipemenu;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.fanwe.lib.gesture.FTouchHelper;

abstract class BaseSwipeMenu extends ViewGroup implements SwipeMenu
{
    private final MenuViewContainer mMenuViewContainer;
    private View mContentView;

    private boolean mIsOpened;

    private final int mMinFlingVelocity;

    private OnStateChangeCallback mOnStateChangeCallback;
    private OnViewPositionChangeCallback mOnViewPositionChangeCallback;

    public BaseSwipeMenu(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mMenuViewContainer = new MenuViewContainer(context);
        mMenuViewContainer.setMenuGravity(Gravity.Right);
        addView(mMenuViewContainer);

        mMinFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    @Override
    public final void setOnStateChangeCallback(OnStateChangeCallback callback)
    {
        mOnStateChangeCallback = callback;
    }

    @Override
    public final void setOnViewPositionChangeCallback(OnViewPositionChangeCallback callback)
    {
        mOnViewPositionChangeCallback = callback;
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
    public final void setMenuView(View view)
    {
        mMenuViewContainer.setMenuView(view);
    }

    @Override
    public final void setMenuGravity(Gravity gravity)
    {
        mMenuViewContainer.setMenuGravity(gravity);
    }

    @Override
    public final View getContentView()
    {
        return mContentView;
    }

    @Override
    public View getMenuView()
    {
        return mMenuViewContainer.getMenuView();
    }

    @Override
    public boolean isOpened()
    {
        return mIsOpened;
    }

    @Override
    public final void open(boolean open, boolean notify)
    {
        openInternal(open, false, notify);
    }

    @Override
    public final void openWithAnim(boolean open, boolean notify)
    {
        openInternal(open, true, notify);
    }

    private void openInternal(boolean open, boolean anim, boolean notify)
    {
        if (mContentView == null)
            return;

        if (mIsOpened == open)
            return;

        mIsOpened = open;

        if (anim)
            smoothScroll(mContentView.getLeft(), mMenuViewContainer.getLeftForContentView(open));
        else
            requestLayout();

        if (notify)
        {
            if (mOnStateChangeCallback != null)
                mOnStateChangeCallback.onStateChanged(mIsOpened, this);
        }
    }

    /**
     * 返回View可以滚动的最大距离
     *
     * @return
     */
    protected final int getMaxScrollDistance()
    {
        return mMenuViewContainer.getMaxScrollDistance();
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

    @Override
    public void onViewRemoved(View child)
    {
        super.onViewRemoved(child);
        if (child == mMenuViewContainer)
            throw new RuntimeException("you can not remove:" + child);
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
                return mIsOpened;
            case Left:
                return !mIsOpened;
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
                return !mIsOpened;
            case Left:
                return mIsOpened;
            default:
                throw new AssertionError();
        }
    }

    private int getLeftContentViewMin()
    {
        return mMenuViewContainer.getLeftContentViewMin();
    }

    private int getLeftContentViewMax()
    {
        return mMenuViewContainer.getLeftContentViewMax();
    }

    /**
     * 移动View
     *
     * @param delta
     */
    protected final void moveViews(int delta)
    {
        if (mContentView == null)
            return;
        if (delta == 0)
            return;

        final int left = mContentView.getLeft();
        final int leftMin = getLeftContentViewMin();
        final int leftMax = getLeftContentViewMax();

        delta = FTouchHelper.getLegalDelta(left, leftMin, leftMax, delta);
        if (delta == 0)
            return;

        ViewCompat.offsetLeftAndRight(mContentView, delta);

        final boolean totalOpened = mContentView.getLeft() == mMenuViewContainer.getLeftForContentView(true);
        mMenuViewContainer.setLockEvent(!totalOpened);

        if (mOnViewPositionChangeCallback != null)
            mOnViewPositionChangeCallback.onViewPositionChanged(this);
    }

    /**
     * 拖动结束后需要执行的逻辑
     *
     * @param velocityX
     */
    protected final void dealDragFinish(int velocityX)
    {
        if (mContentView == null)
            return;

        final int leftstart = mContentView.getLeft();
        int leftEnd = 0;

        final int leftMin = getLeftContentViewMin();
        final int leftMax = getLeftContentViewMax();

        if (Math.abs(velocityX) > mMinFlingVelocity)
        {
            leftEnd = velocityX > 0 ? leftMax : leftMin;
        } else
        {
            final int leftMiddle = (leftMin + leftMax) / 2;
            leftEnd = leftstart >= leftMiddle ? leftMax : leftMin;
        }

        smoothScroll(leftstart, leftEnd);
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
        if (mContentView == null)
            return;

        if (isViewIdle())
        {
            final int left = mContentView.getLeft();
            if (left == mMenuViewContainer.getLeftForContentView(true))
            {
                openInternal(true, false, true);
            } else if (left == mMenuViewContainer.getLeftForContentView(false))
            {
                openInternal(false, false, true);
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

        if (mContentView != null && mContentView.getVisibility() != GONE)
        {
            mContentView.measure(getChildMeasureSpec(widthMeasureSpec, 0, mContentView.getLayoutParams().width),
                    getChildMeasureSpec(heightMeasureSpec, 0, mContentView.getLayoutParams().height));

            mMenuViewContainer.measure(MeasureSpec.makeMeasureSpec(mContentView.getMeasuredWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mContentView.getMeasuredHeight(), MeasureSpec.EXACTLY));

            width = mContentView.getMeasuredWidth();
            height = mContentView.getMeasuredHeight();
        }

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

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        if (mContentView == null || mContentView.getVisibility() == GONE)
            return;

        // ContentView
        int left = 0;
        int top = 0;
        if (isViewIdle())
        {
            left = mMenuViewContainer.getLeftForContentView(mIsOpened);
            top = 0;
        } else
        {
            left = mContentView.getLeft();
            top = mContentView.getTop();
        }

        mContentView.layout(left, top,
                left + mContentView.getMeasuredWidth(), top + mContentView.getMeasuredHeight());

        // MenuView
        if (mMenuViewContainer.getVisibility() != GONE)
        {
            mMenuViewContainer.layout(0, 0,
                    mMenuViewContainer.getMeasuredWidth(), mMenuViewContainer.getMeasuredHeight());
        }

        if (ViewCompat.getZ(mContentView) <= ViewCompat.getZ(mMenuViewContainer))
            ViewCompat.setZ(mContentView, ViewCompat.getZ(mMenuViewContainer) + 1);
    }
}
