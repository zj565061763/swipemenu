package com.sd.lib.swipemenu.pull_condition;

import com.sd.lib.swipemenu.SwipeMenu;

import java.lang.ref.WeakReference;

public abstract class BasePullCondition<S> implements SwipeMenu.PullCondition
{
    private final WeakReference<S> mSource;

    public BasePullCondition(S source)
    {
        if (source == null)
            throw new IllegalArgumentException("source is null for pull condition");

        mSource = new WeakReference<>(source);
    }

    public final S getSource()
    {
        return mSource == null ? null : mSource.get();
    }
}
