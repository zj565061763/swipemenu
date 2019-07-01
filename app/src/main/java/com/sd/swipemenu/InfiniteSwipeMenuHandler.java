package com.sd.swipemenu;

import com.sd.lib.swipemenu.SwipeMenu;

public abstract class InfiniteSwipeMenuHandler implements SwipeMenu.OnScrollStateChangeCallback, SwipeMenu.OnStateChangeCallback
{
    private SwipeMenu mSwipeMenu;

    public void setSwipeMenu(SwipeMenu swipeMenu)
    {
        final SwipeMenu old = mSwipeMenu;
        if (old != swipeMenu)
        {
            if (old != null)
            {
                old.setOnScrollStateChangeCallback(null);
            }

            mSwipeMenu = swipeMenu;

            if (mSwipeMenu != null)
            {
                mSwipeMenu.setOnScrollStateChangeCallback(this);
            }
        }
    }

    @Override
    public void onScrollStateChanged(SwipeMenu swipeMenu, SwipeMenu.ScrollState oldState, SwipeMenu.ScrollState newState)
    {
        bindDataWhenOpenIdle(swipeMenu);
    }

    @Override
    public void onStateChanged(SwipeMenu swipeMenu, SwipeMenu.State oldState, SwipeMenu.State newState)
    {

    }

    public void bindData()
    {
        onBindData(null, null);
        onBindData(SwipeMenu.Direction.Left, SwipeMenu.Direction.Left);
        onBindData(SwipeMenu.Direction.Top, SwipeMenu.Direction.Top);
        onBindData(SwipeMenu.Direction.Right, SwipeMenu.Direction.Right);
        onBindData(SwipeMenu.Direction.Bottom, SwipeMenu.Direction.Bottom);
    }

    private void bindDataWhenOpenIdle(SwipeMenu swipeMenu)
    {
        final SwipeMenu.ScrollState scrollState = swipeMenu.getScrollState();
        if (scrollState != SwipeMenu.ScrollState.Idle)
            return;

        final SwipeMenu.State state = swipeMenu.getState();
        if (state == SwipeMenu.State.Close)
            return;

        switch (state)
        {
            case OpenLeft:
                onBindData(null, SwipeMenu.Direction.Left);
                break;
            case OpenTop:
                onBindData(null, SwipeMenu.Direction.Top);
                break;
            case OpenRight:
                onBindData(null, SwipeMenu.Direction.Right);
                break;
            case OpenBottom:
                onBindData(null, SwipeMenu.Direction.Bottom);
                break;
            default:
                return;
        }

        swipeMenu.setState(SwipeMenu.State.Close, false);
    }

    public abstract void onBindData(SwipeMenu.Direction viewDirection, SwipeMenu.Direction dataDirection);
}
