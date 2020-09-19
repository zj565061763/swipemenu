package com.sd.lib.swipemenu.pull_condition;

import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.sd.lib.swipemenu.SwipeMenu;

public class NestedScrollPullCondition extends ViewPullCondition
{
    private final ScrollToBoundPullCondition mScrollToBoundPullCondition;

    public NestedScrollPullCondition(View view, int axes)
    {
        super(view);
        if ((axes & ViewCompat.SCROLL_AXIS_HORIZONTAL) != 0)
        {
            mScrollToBoundPullCondition = new ScrollToBoundPullCondition(view, ScrollToBoundPullCondition.Axis.Horizontal);
        } else if ((axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0)
        {
            mScrollToBoundPullCondition = new ScrollToBoundPullCondition(view, ScrollToBoundPullCondition.Axis.Vertical);
        } else
        {
            mScrollToBoundPullCondition = null;
        }
    }

    @Override
    protected boolean canPullImpl(SwipeMenu.Direction pullDirection, MotionEvent event, SwipeMenu swipeMenu)
    {
        if (mScrollToBoundPullCondition == null)
        {
            swipeMenu.removePullCondition(this);
            return true;
        }

        if (!mScrollToBoundPullCondition.canPull(pullDirection, event, swipeMenu))
            return false;

        return true;
    }
}
