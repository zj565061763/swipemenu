package com.sd.lib.swipemenu.pull_condition;

import android.view.MotionEvent;
import android.view.View;

import com.sd.lib.swipemenu.SwipeMenu;

/**
 * 限制View滚动到边界后，{@link SwipeMenu}才可以拖动
 */
public class ScrollToBoundPullCondition extends ViewPullCondition
{
    private final Axis mAxis;

    public ScrollToBoundPullCondition(View view)
    {
        this(view, Axis.All);
    }

    public ScrollToBoundPullCondition(View view, Axis axis)
    {
        super(view);
        if (axis == null)
            throw new IllegalArgumentException("axis is null");

        mAxis = axis;
    }

    @Override
    protected boolean canPullImpl(SwipeMenu swipeMenu, SwipeMenu.Direction pullDirection, MotionEvent event)
    {
        switch (mAxis)
        {
            case All:
                if (pullDirection.isHorizontal() && !canPullHorizontal(pullDirection))
                    return false;

                if (pullDirection.isVertical() && !canPullVertical(pullDirection))
                    return false;

                break;
            case Horizontal:
                if (pullDirection.isHorizontal() && !canPullHorizontal(pullDirection))
                    return false;

                break;
            case Vertical:
                if (pullDirection.isVertical() && !canPullVertical(pullDirection))
                    return false;

                break;
        }

        return true;
    }

    private boolean canPullHorizontal(SwipeMenu.Direction pullDirection)
    {
        if (pullDirection == SwipeMenu.Direction.Left)
        {
            if (getView().canScrollHorizontally(1))
                return false;
        } else if (pullDirection == SwipeMenu.Direction.Right)
        {
            if (getView().canScrollHorizontally(-1))
                return false;
        } else
        {
            throw new RuntimeException();
        }
        return true;
    }

    private boolean canPullVertical(SwipeMenu.Direction pullDirection)
    {
        if (pullDirection == SwipeMenu.Direction.Top)
        {
            if (getView().canScrollVertically(1))
                return false;
        } else if (pullDirection == SwipeMenu.Direction.Bottom)
        {
            if (getView().canScrollVertically(-1))
                return false;
        } else
        {
            throw new RuntimeException();
        }
        return true;
    }

    public enum Axis
    {
        All,
        Horizontal,
        Vertical,
    }
}
