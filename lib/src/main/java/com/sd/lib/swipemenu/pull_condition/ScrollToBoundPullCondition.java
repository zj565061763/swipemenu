package com.sd.lib.swipemenu.pull_condition;

import android.view.MotionEvent;
import android.view.View;

import com.sd.lib.swipemenu.SwipeMenu;

/**
 * 限制View滚动到边界后，{@link SwipeMenu}才可以拖动
 */
public class ScrollToBoundPullCondition extends BasePullCondition<View>
{
    private final Axis mAxis;

    public ScrollToBoundPullCondition(View source)
    {
        this(source, Axis.All);
    }

    public ScrollToBoundPullCondition(View source, Axis axis)
    {
        super(source);
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
            if (getSource().canScrollHorizontally(1))
                return false;
        } else if (pullDirection == SwipeMenu.Direction.Right)
        {
            if (getSource().canScrollHorizontally(-1))
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
            if (getSource().canScrollVertically(1))
                return false;
        } else if (pullDirection == SwipeMenu.Direction.Bottom)
        {
            if (getSource().canScrollVertically(-1))
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
