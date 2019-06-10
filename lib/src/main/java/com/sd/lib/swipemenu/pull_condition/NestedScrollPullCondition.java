package com.sd.lib.swipemenu.pull_condition;

import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;

import com.sd.lib.swipemenu.SwipeMenu;

public class NestedScrollPullCondition extends BasePullCondition<View>
{
    private int mAxes;
    private final ScrollToBoundPullCondition mScrollToBoundPullCondition;

    public NestedScrollPullCondition(View source, int axes)
    {
        super(source);
        if ((axes & ViewCompat.SCROLL_AXIS_HORIZONTAL) != 0)
        {
            mAxes = ViewCompat.SCROLL_AXIS_HORIZONTAL;
            mScrollToBoundPullCondition = new ScrollToBoundPullCondition(source, ScrollToBoundPullCondition.Axis.Horizontal);
        } else if ((axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0)
        {
            mAxes = ViewCompat.SCROLL_AXIS_VERTICAL;
            mScrollToBoundPullCondition = new ScrollToBoundPullCondition(source, ScrollToBoundPullCondition.Axis.Vertical);
        } else
        {
            mAxes = ViewCompat.SCROLL_AXIS_NONE;
            mScrollToBoundPullCondition = null;
        }
    }

    @Override
    protected boolean canPullImpl(SwipeMenu swipeMenu, SwipeMenu.Direction pullDirection, MotionEvent event)
    {
        if (mAxes == ViewCompat.SCROLL_AXIS_NONE)
        {
            swipeMenu.removePullCondition(this);
            return true;
        }

        if (mAxes == ViewCompat.SCROLL_AXIS_HORIZONTAL)
        {
            if (!mScrollToBoundPullCondition.canPull(swipeMenu, pullDirection, event))
                return false;
        }

        if (mAxes == ViewCompat.SCROLL_AXIS_VERTICAL)
        {
            if (!mScrollToBoundPullCondition.canPull(swipeMenu, pullDirection, event))
                return false;
        }

        return true;
    }
}
