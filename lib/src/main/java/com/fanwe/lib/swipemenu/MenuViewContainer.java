package com.fanwe.lib.swipemenu;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

final class MenuViewContainer extends LinearLayout
{
    private SwipeMenu.Gravity mMenuGravity;
    private boolean mLockEvent;
    private View mMenuView;

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
            switch (gravity)
            {
                case Right:
                    setGravity(Gravity.RIGHT);
                    break;
                case Left:
                    setGravity(Gravity.LEFT);
                    break;
            }
        }
    }

    public SwipeMenu.Gravity getMenuGravity()
    {
        return mMenuGravity;
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

        if (mMenuView == null)
            return 0;

        switch (mMenuGravity)
        {
            case Right:
                return -mMenuView.getMeasuredWidth();
            case Left:
                return mMenuView.getMeasuredWidth();
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

        mMenuView = child;
    }
}
