package com.sd.lib.swipemenu.ext;

import com.sd.lib.swipemenu.SwipeMenu;

/**
 * 无限滑动处理
 */
public abstract class InfiniteSwipeMenuHandler implements SwipeMenu.OnScrollStateChangeCallback
{
    private SwipeMenu mSwipeMenu;

    /**
     * 设置要处理的对象
     *
     * @param swipeMenu
     */
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

    /**
     * 绑定数据
     *
     * @param viewDirection view方向
     * @param dataDirection 数据方向
     */
    public void bindData(Direction viewDirection, Direction dataDirection)
    {
        onBindData(viewDirection, dataDirection, false);
    }

    @Override
    public void onScrollStateChanged(SwipeMenu swipeMenu, SwipeMenu.ScrollState oldState, SwipeMenu.ScrollState newState)
    {
        processIfNeed(swipeMenu);
    }

    private void processIfNeed(SwipeMenu swipeMenu)
    {
        final SwipeMenu.ScrollState scrollState = swipeMenu.getScrollState();
        if (scrollState != SwipeMenu.ScrollState.Idle)
            return;

        final SwipeMenu.State state = swipeMenu.getState();
        if (state == SwipeMenu.State.Close)
            return;

        if (!handleState(state))
            return;

        Direction direction = null;
        Direction directionOther = null;

        switch (state)
        {
            case OpenLeft:
                direction = Direction.Left;
                directionOther = Direction.Right;
                break;
            case OpenTop:
                direction = Direction.Top;
                directionOther = Direction.Bottom;
                break;
            case OpenRight:
                direction = Direction.Right;
                directionOther = Direction.Left;
                break;
            case OpenBottom:
                direction = Direction.Bottom;
                directionOther = Direction.Top;
                break;
            default:
                return;
        }

        onBindData(Direction.Center, direction, true);

        swipeMenu.setState(SwipeMenu.State.Close, false);
        onMoveIndex(direction);

        onBindData(direction, direction, true);
        onBindData(directionOther, directionOther, true);

        onPageChanged(direction);
    }

    /**
     * 是否处理指定状态
     *
     * @param state
     * @return
     */
    protected boolean handleState(SwipeMenu.State state)
    {
        return true;
    }

    /**
     * 绑定数据
     *
     * @param viewDirection view方向
     * @param dataDirection 数据方向
     * @param isInfinite    是否是由无限滑动触发的
     */
    protected abstract void onBindData(Direction viewDirection, Direction dataDirection, boolean isInfinite);

    /**
     * 移动索引
     *
     * @param direction 索引需要移动的方向
     */
    protected abstract void onMoveIndex(Direction direction);

    /**
     * 页面变化回调
     *
     * @param direction
     */
    protected abstract void onPageChanged(Direction direction);

    public enum Direction
    {
        Left,
        Top,
        Right,
        Bottom,
        Center
    }
}
