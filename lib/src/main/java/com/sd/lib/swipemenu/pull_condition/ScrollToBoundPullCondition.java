package com.sd.lib.swipemenu.pull_condition;

import android.view.MotionEvent;
import android.view.View;

import com.sd.lib.swipemenu.SwipeMenu;

public class ScrollToBoundPullCondition extends BasePullCondition<View>
{
    public ScrollToBoundPullCondition(View source)
    {
        super(source);
    }

    @Override
    protected boolean canPullImpl(SwipeMenu swipeMenu, SwipeMenu.Direction pullDirection, MotionEvent event)
    {
        if (pullDirection == SwipeMenu.Direction.Left)
        {
            if (getSource().canScrollHorizontally(1))
                return false;
        } else if (pullDirection == SwipeMenu.Direction.Right)
        {
            if (getSource().canScrollHorizontally(-1))
                return false;
        } else if (pullDirection == SwipeMenu.Direction.Top)
        {
            if (getSource().canScrollVertically(1))
                return false;
        } else if (pullDirection == SwipeMenu.Direction.Bottom)
        {
            if (getSource().canScrollVertically(-1))
                return false;
        }

        return true;
    }
}
