package com.sd.lib.swipemenu.pull_condition;

import android.view.MotionEvent;
import android.view.View;

import com.sd.lib.swipemenu.SwipeMenu;

/**
 * 限制触摸点不在指定的View边界内，{@link SwipeMenu}才可以拖动
 */
public class IgnoreViewPullCondition extends ViewPullCondition
{
    private final int[] mLocation = new int[2];

    public IgnoreViewPullCondition(View view)
    {
        super(view);
    }

    @Override
    protected boolean canPullImpl(SwipeMenu.Direction pullDirection, MotionEvent event, SwipeMenu swipeMenu)
    {
        if (isViewUnder(getView(), (int) event.getRawX(), (int) event.getRawY(), mLocation))
            return false;

        return true;
    }

    private static boolean isViewUnder(View view, int x, int y, int[] outLocation)
    {
        if (view == null)
            return false;

        final int[] location = getLocationOnScreen(view, outLocation);
        final int left = location[0];
        final int top = location[1];
        final int right = left + view.getWidth();
        final int bottom = top + view.getHeight();

        return left < right && top < bottom
                && x >= left && x < right && y >= top && y < bottom;
    }

    private static int[] getLocationOnScreen(View view, int[] outLocation)
    {
        if (outLocation == null || outLocation.length != 2)
            outLocation = new int[]{0, 0};

        if (view != null)
            view.getLocationOnScreen(outLocation);

        return outLocation;
    }
}
