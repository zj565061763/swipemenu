package com.fanwe.lib.swipemenu;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

final class MenuViewContainer extends ViewGroup
{
    private SwipeMenu.Gravity mMenuGravity;
    private boolean mLockEvent;
    private View mMenuView;

    public MenuViewContainer(Context context)
    {
        super(context);
    }

    /**
     * 设置菜单view
     *
     * @param view
     */
    public void setMenuView(View view)
    {
        if (mMenuView != view)
        {
            mMenuView = null;
            removeAllViews();

            if (view != null)
            {
                mMenuView = view;
                Utils.removeViewFromParent(view);
                addView(view);
            }
        }
    }

    /**
     * 返回菜单view
     *
     * @return
     */
    public View getMenuView()
    {
        return mMenuView;
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

    /**
     * 返回菜单位置
     *
     * @return
     */
    public SwipeMenu.Gravity getMenuGravity()
    {
        return mMenuGravity;
    }

    /**
     * 设置是否锁定触摸事件，锁定后菜单view接收不到触摸事件
     *
     * @param lockEvent
     */
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
        if (mMenuView != child)
            throw new RuntimeException("you can not add menu view this way");
    }

    @Override
    public void onViewRemoved(View child)
    {
        super.onViewRemoved(child);
        if (mMenuView != null)
            throw new RuntimeException("you can not remove menu view this way");
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams()
    {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = 0;
        int height = 0;

        if (mMenuView != null && mMenuView.getVisibility() != GONE)
        {
            measureChild(mMenuView, widthMeasureSpec, heightMeasureSpec);
            width = mMenuView.getMeasuredWidth();
            height = mMenuView.getMeasuredHeight();
        }

        width = Utils.getMeasureSize(width, widthMeasureSpec);
        height = Utils.getMeasureSize(height, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        if (mMenuView != null && mMenuView.getVisibility() != GONE)
        {
            final int left = mMenuGravity == SwipeMenu.Gravity.Left ? 0 : (getMeasuredWidth() - mMenuView.getMeasuredWidth());
            mMenuView.layout(left, 0, left + mMenuView.getMeasuredWidth(), mMenuView.getMeasuredHeight());
        }
    }
}
