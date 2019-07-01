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

    /**
     * 绑定数据
     */
    public void bindData()
    {
        onBindData(Direction.Center, Direction.Center);
        onBindData(Direction.Left, Direction.Left);
        onBindData(Direction.Top, Direction.Top);
        onBindData(Direction.Right, Direction.Right);
        onBindData(Direction.Bottom, Direction.Bottom);
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
                onBindData(Direction.Center, Direction.Left);
                break;
            case OpenTop:
                onBindData(Direction.Center, Direction.Top);
                break;
            case OpenRight:
                onBindData(Direction.Center, Direction.Right);
                break;
            case OpenBottom:
                onBindData(Direction.Center, Direction.Bottom);
                break;
            default:
                return;
        }

        swipeMenu.setState(SwipeMenu.State.Close, false);
    }

    protected abstract void onBindData(Direction viewDirection, Direction dataDirection);

    public enum Direction
    {
        Left,
        Top,
        Right,
        Bottom,
        Center
    }
}
