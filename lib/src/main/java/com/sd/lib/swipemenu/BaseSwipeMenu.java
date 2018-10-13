package com.sd.lib.swipemenu;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.sd.lib.gesture.FTouchHelper;

abstract class BaseSwipeMenu extends ViewGroup implements SwipeMenu
{
    private final ContentContainer mContentContainer;
    private final MenuContainer mMenuContainer;

    private State mState = State.Close;
    private Direction mOpenDirection;

    private boolean mIsOpened;
    private ScrollState mScrollState = ScrollState.Idle;

    private final int mMinFlingVelocity;

    private OnStateChangeCallback mOnStateChangeCallback;
    private OnViewPositionChangeCallback mOnViewPositionChangeCallback;
    private OnScrollStateChangeCallback mOnScrollStateChangeCallback;
    private PullCondition mPullCondition;

    public BaseSwipeMenu(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mContentContainer = new ContentContainer(context);
        addView(mContentContainer);

        mMenuContainer = new MenuContainer(context);
        mMenuContainer.setMenuGravity(Gravity.Right);
        addView(mMenuContainer);

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
    public final void setOnScrollStateChangeCallback(OnScrollStateChangeCallback callback)
    {
        mOnScrollStateChangeCallback = callback;
    }

    @Override
    public final void setPullCondition(PullCondition pullCondition)
    {
        mPullCondition = pullCondition;
    }

    @Override
    public final void setContentView(View view)
    {
        mContentContainer.setContentView(view);
    }

    @Override
    public final void setMenuView(View view)
    {
        mMenuContainer.setContentView(view);
    }

    @Override
    public final void setMenuGravity(Gravity gravity)
    {
        mMenuContainer.setMenuGravity(gravity);
    }

    @Override
    public final View getContentView()
    {
        return mContentContainer.getContentView();
    }

    @Override
    public final View getMenuView()
    {
        return mMenuContainer.getContentView();
    }

    public final Gravity getMenuGravity()
    {
        return mMenuContainer.getMenuGravity();
    }

    @Override
    public final State getState()
    {
        return mState;
    }

    @Override
    public final ScrollState getScrollState()
    {
        return mScrollState;
    }

    protected final void setScrollState(ScrollState state)
    {
        if (mScrollState != state)
        {
            mScrollState = state;
            if (mOnScrollStateChangeCallback != null)
                mOnScrollStateChangeCallback.onScrollStateChanged(state, this);
        }
    }

    @Override
    public float getScrollPercent()
    {
        final float total = getMaxScrollDistance();
        if (total <= 0)
            return 0;

        final int current = Math.abs(getContentLeftCurrent() - getContentLeft(false));
        return current / total;
    }

    @Override
    public final boolean isOpened()
    {
        return mIsOpened;
    }

    @Override
    public boolean setOpened(boolean opened, boolean anim)
    {
        if (getContentView() == null)
            return false;

        boolean changed = false;
        if (mIsOpened != opened)
        {
            mIsOpened = opened;
            updateLockEvent();
            updateViewByState(anim);

            if (mOnStateChangeCallback != null)
                mOnStateChangeCallback.onStateChanged(opened, this);

            changed = true;
        }

        if (!anim && !changed)
            updateViewByState(false);

        return changed;
    }

    /**
     * 根据状态更新view的位置
     *
     * @param anim
     */
    private void updateViewByState(boolean anim)
    {
        final int leftCurrent = getContentLeftCurrent();
        final int leftState = getContentLeft(mIsOpened);

        if (leftCurrent != leftState)
        {
            abortAnimation();
            if (anim)
            {
                if (onSmoothScroll(mContentContainer.getLeft(), getContentLeft(mIsOpened)))
                {
                    setScrollState(ScrollState.Fling);
                    invalidate();
                }
            } else
            {
                requestLayout();
            }
        }
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        if (getChildCount() > 4)
            throw new RuntimeException("SwipeMenu can only has 2 children at most");

        final View content = getChildAt(2);
        final View menu = getChildAt(3);

        setContentView(content);
        setMenuView(menu);
    }

    @Override
    public void onViewRemoved(View child)
    {
        super.onViewRemoved(child);
        if (child == mMenuContainer || child == mContentContainer)
            throw new RuntimeException("you can not remove:" + child);
    }

    /**
     * 设置拖动方向
     *
     * @param direction
     */
    protected final void setOpenDirection(Direction direction)
    {
        if (direction == null)
        {
            mOpenDirection = null;
        } else
        {
            if (mOpenDirection == null)
                mOpenDirection = direction;
        }
    }

    /**
     * 返回拖动方向
     *
     * @return
     */
    protected final Direction getOpenDirection()
    {
        return mOpenDirection;
    }

    private void updateLockEvent()
    {
        if (mIsOpened)
        {
            final boolean totalOpened = getContentLeftCurrent() == getContentLeft(true);
            mMenuContainer.setLockEvent(!totalOpened);
        } else
        {
            mMenuContainer.setLockEvent(true);
        }
    }

    protected final boolean checkPullCondition()
    {
        return mPullCondition == null ? true : mPullCondition.canPull(this);
    }

    /**
     * 是否可以从左向右拖动
     *
     * @return
     */
    protected final boolean canPullLeftToRight()
    {
        if (!checkPullCondition())
            return false;

        switch (getMenuGravity())
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
        if (!checkPullCondition())
            return false;

        switch (getMenuGravity())
        {
            case Right:
                return !mIsOpened;
            case Left:
                return mIsOpened;
            default:
                throw new AssertionError();
        }
    }

    private int getContentLeftCurrent()
    {
        return mContentContainer.getLeft();
    }

    private int getContentLeftMin()
    {
        switch (getMenuGravity())
        {
            case Right:
                return getContentLeft(true);
            case Left:
                return getContentLeft(false);
            default:
                throw new AssertionError();
        }
    }

    private int getContentLeftMax()
    {
        switch (getMenuGravity())
        {
            case Right:
                return getContentLeft(false);
            case Left:
                return getContentLeft(true);
            default:
                throw new AssertionError();
        }
    }

    private int getContentLeft(boolean opened)
    {
        if (!opened)
            return 0;

        final View menuView = getMenuView();
        if (menuView == null)
            return 0;

        switch (getMenuGravity())
        {
            case Right:
                return -menuView.getMeasuredWidth();
            case Left:
                return menuView.getMeasuredWidth();
            default:
                throw new AssertionError();
        }
    }

    /**
     * 移动View
     *
     * @param delta
     * @param isDrag
     */
    protected final void moveViews(int delta, boolean isDrag)
    {
        if (getContentView() == null)
            return;

        if (delta == 0)
            return;

        final int leftCurrent = getContentLeftCurrent();
        final int leftMin = getContentLeftMin();
        final int leftMax = getContentLeftMax();

        delta = FTouchHelper.getLegalDelta(leftCurrent, leftMin, leftMax, delta);
        if (delta == 0)
            return;

        ViewCompat.offsetLeftAndRight(mContentContainer, delta);
        updateLockEvent();

        if (mOnViewPositionChangeCallback != null)
            mOnViewPositionChangeCallback.onViewPositionChanged(isDrag, this);
    }

    /**
     * 拖动结束后需要执行的逻辑
     *
     * @param velocityX
     */
    protected final void dealDragFinish(int velocityX)
    {
        final int leftCurrent = getContentLeftCurrent();
        final int leftMin = getContentLeftMin();
        final int leftMax = getContentLeftMax();

        int leftEnd = 0;
        if (Math.abs(velocityX) > mMinFlingVelocity)
        {
            leftEnd = velocityX > 0 ? leftMax : leftMin;
        } else
        {
            final int leftMiddle = (leftMin + leftMax) / 2;
            leftEnd = leftCurrent >= leftMiddle ? leftMax : leftMin;
        }

        final boolean opened = leftEnd == getContentLeft(true) ? true : false;

        if (!setOpened(opened, true))
            updateViewByState(true);

        if (mScrollState == ScrollState.Drag)
            setScrollState(ScrollState.Idle);
    }

    /**
     * view惯性滚动结束需要执行的逻辑
     */
    protected final void dealScrollFinish()
    {
        if (isViewIdle())
        {
            updateViewByState(false);
            setScrollState(ScrollState.Idle);
        }
    }

    /**
     * 返回View可以滚动的最大距离
     *
     * @return
     */
    protected final int getMaxScrollDistance()
    {
        final View view = getMenuView();
        return view == null ? 0 : view.getMeasuredWidth();
    }

    /**
     * 停止滑动动画
     */
    protected abstract void abortAnimation();

    /**
     * 滑动
     *
     * @param start
     * @param end
     * @return
     */
    protected abstract boolean onSmoothScroll(int start, int end);

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        mContentContainer.measure(widthMeasureSpec, heightMeasureSpec);

        int width = mContentContainer.getMeasuredWidth();
        int height = mContentContainer.getMeasuredHeight();

        mMenuContainer.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));

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
        // content
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;

        if (isViewIdle())
        {
            left = getContentLeft(mIsOpened);
            top = 0;

        } else
        {
            left = mContentContainer.getLeft();
            top = mContentContainer.getTop();
        }
        right = left + mContentContainer.getMeasuredWidth();
        bottom = top + mContentContainer.getMeasuredHeight();

        mContentContainer.layout(left, top, right, bottom);

        // menu
        mMenuContainer.layout(0, 0,
                mMenuContainer.getMeasuredWidth(), mMenuContainer.getMeasuredHeight());

        if (ViewCompat.getZ(mContentContainer) <= ViewCompat.getZ(mMenuContainer))
            ViewCompat.setZ(mContentContainer, ViewCompat.getZ(mMenuContainer) + 1);

        updateLockEvent();
    }
}
