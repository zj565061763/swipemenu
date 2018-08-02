package com.fanwe.lib.swipemenu;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

final class MenuViewContainer extends ViewGroup
{
    private SwipeMenu.Gravity mMenuGravity;
    private boolean mLockEvent;
    private View mContentView;

    public MenuViewContainer(Context context)
    {
        super(context);
    }

    public void setContentView(View view)
    {
        if (view == null)
        {
            removeAllViews();
            return;
        }

        if (view.getParent() == this)
            return;

        Utils.removeViewFromParent(view);
        removeAllViews();
        addView(view);
    }

    public View getContentView()
    {
        return mContentView;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams()
    {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * 设置菜单位置
     *
     * @param gravity
     */
    public void setMenuGravity(SwipeMenu.Gravity gravity)
    {
        if (gravity == null)
            throw new NullPointerException();

        if (mMenuGravity != gravity)
        {
            mMenuGravity = gravity;
            requestLayout();
        }
    }

    public SwipeMenu.Gravity getMenuGravity()
    {
        return mMenuGravity;
    }

    public int getMaxScrollDistance()
    {
        return mContentView == null ? 0 : mContentView.getMeasuredWidth();
    }

    public int getLeftContentViewMin()
    {
        switch (mMenuGravity)
        {
            case Right:
                return getLeftForContentView(SwipeMenu.State.Opened);
            case Left:
                return getLeftForContentView(SwipeMenu.State.Closed);
            default:
                throw new AssertionError();
        }
    }

    public int getLeftContentViewMax()
    {
        switch (mMenuGravity)
        {
            case Right:
                return getLeftForContentView(SwipeMenu.State.Closed);
            case Left:
                return getLeftForContentView(SwipeMenu.State.Opened);
            default:
                throw new AssertionError();
        }
    }

    public int getLeftForContentView(SwipeMenu.State state)
    {
        if (state == SwipeMenu.State.Closed)
            return 0;

        if (mContentView == null)
            return 0;

        switch (mMenuGravity)
        {
            case Right:
                return -mContentView.getMeasuredWidth();
            case Left:
                return mContentView.getMeasuredWidth();
            default:
                throw new AssertionError();
        }
    }

    public void setLockEvent(boolean lockEvent)
    {
        mLockEvent = lockEvent;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (mLockEvent)
            return true;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mLockEvent)
            return true;
        return super.onTouchEvent(event);
    }

    @Override
    public void onViewAdded(View child)
    {
        super.onViewAdded(child);
        if (getChildCount() > 1)
            throw new RuntimeException("MenuViewContainer can only has one child at most");

        mContentView = child;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = 0;
        int height = 0;

        if (mContentView != null && mContentView.getVisibility() != GONE)
        {
            measureChild(mContentView, widthMeasureSpec, heightMeasureSpec);
            width = mContentView.getMeasuredWidth();
            height = mContentView.getMeasuredHeight();
        }

        width = Utils.getMeasureSize(width, widthMeasureSpec);
        height = Utils.getMeasureSize(height, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        if (mContentView != null && mContentView.getVisibility() != GONE)
        {
            final int left = mMenuGravity == SwipeMenu.Gravity.Left ? 0 : (getMeasuredWidth() - mContentView.getMeasuredWidth());
            mContentView.layout(left, 0, left + mContentView.getMeasuredWidth(), mContentView.getMeasuredHeight());
        }
    }
}
