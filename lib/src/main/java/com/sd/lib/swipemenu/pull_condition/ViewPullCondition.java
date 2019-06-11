package com.sd.lib.swipemenu.pull_condition;

import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

import com.sd.lib.swipemenu.SwipeMenu;

import java.lang.ref.WeakReference;

public abstract class ViewPullCondition implements SwipeMenu.PullCondition
{
    private final WeakReference<View> mView;

    public ViewPullCondition(View view)
    {
        if (view == null)
            throw new IllegalArgumentException("view is null for pull condition");

        mView = new WeakReference<>(view);
    }

    public final View getView()
    {
        return mView == null ? null : mView.get();
    }

    @Override
    public final boolean canPull(SwipeMenu swipeMenu, SwipeMenu.Direction pullDirection, MotionEvent event)
    {
        final View view = getView();
        if (view == null)
        {
            swipeMenu.removePullCondition(this);
            return true;
        }

        if (view.getVisibility() != View.VISIBLE)
            return true;

        if (!isAttached(view))
            return true;

        return canPullImpl(swipeMenu, pullDirection, event);
    }

    protected abstract boolean canPullImpl(SwipeMenu swipeMenu, SwipeMenu.Direction pullDirection, MotionEvent event);

    private static boolean isAttached(View view)
    {
        if (view == null)
            return false;

        if (Build.VERSION.SDK_INT >= 19)
            return view.isAttachedToWindow();
        else
            return view.getWindowToken() != null;
    }
}
