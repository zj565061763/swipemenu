package com.sd.lib.swipemenu.pull_condition;

import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

import com.sd.lib.swipemenu.SwipeMenu;

public class IgnoreViewPullCondition extends BasePullCondition<View>
{
    private final int[] mLocation = new int[2];

    public IgnoreViewPullCondition(View source)
    {
        super(source);
    }

    @Override
    protected boolean canPullImpl(SwipeMenu swipeMenu, SwipeMenu.Direction pullDirection, MotionEvent event)
    {
        if (isViewUnder(getSource(), (int) event.getRawX(), (int) event.getRawY(), mLocation))
            return false;

        return true;
    }

    private static boolean isViewUnder(View view, int x, int y, int[] outLocation)
    {
        if (view == null)
            return false;

        if (!isAttached(view))
            return false;

        final int[] location = getLocationOnScreen(view, outLocation);
        final int left = location[0];
        final int top = location[1];
        final int right = left + view.getWidth();
        final int bottom = top + view.getHeight();

        return left < right && top < bottom
                && x >= left && x < right && y >= top && y < bottom;
    }

    private static boolean isAttached(View view)
    {
        if (view == null)
            return false;

        if (Build.VERSION.SDK_INT >= 19)
            return view.isAttachedToWindow();
        else
            return view.getWindowToken() != null;
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
