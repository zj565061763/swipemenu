package com.sd.lib.swipemenu;

import android.view.View;

public interface SwipeMenu
{
    void setDebug(boolean debug);

    /**
     * 设置状态变化回调
     *
     * @param callback
     */
    void setOnStateChangeCallback(OnStateChangeCallback callback);

    /**
     * 位置变化回调
     *
     * @param callback
     */
    void setOnViewPositionChangeCallback(OnViewPositionChangeCallback callback);

    /**
     * 设置滚动状态变化回调
     *
     * @param callback
     */
    void setOnScrollStateChangeCallback(OnScrollStateChangeCallback callback);

    /**
     * 设置拖动限制条件
     *
     * @param pullCondition
     */
    void setPullCondition(PullCondition pullCondition);

    /**
     * 返回内容view
     *
     * @return
     */
    View getContentView();

    /**
     * 返回菜单view
     *
     * @param direction {@link Direction}
     * @return
     */
    View getMenuView(Direction direction);

    /**
     * 返回菜显示方向
     *
     * @return
     */
    Direction getMenuDirection();

    /**
     * 返回当前菜单的状态
     *
     * @return
     */
    State getState();

    /**
     * 返回滚动状态
     *
     * @return
     */
    ScrollState getScrollState();

    /**
     * 返回当前view滚动的百分比[0-1]
     *
     * @return
     */
    float getScrollPercent();

    /**
     * 设置菜单状态
     *
     * @param state {@link State}
     * @param anim  true-动画效果，false-无动画
     * @return
     */
    boolean setState(State state, boolean anim);

    enum Direction
    {
        Left,
        Top,
        Right,
        Bottom;

        public final boolean isHorizontal()
        {
            return this == Left || this == Right;
        }
    }

    enum State
    {
        Close,
        OpenLeft,
        OpenTop,
        OpenRight,
        OpenBottom
    }

    enum ScrollState
    {
        /**
         * 空闲状态
         */
        Idle,
        /**
         * 拖动状态
         */
        Drag,
        /**
         * 惯性滑动状态
         */
        Fling
    }

    interface OnStateChangeCallback
    {
        /**
         * 状态变更回调
         *
         * @param state
         * @param swipeMenu
         */
        void onStateChanged(State state, SwipeMenu swipeMenu);
    }

    interface OnViewPositionChangeCallback
    {
        /**
         * view位置变化回调，侧滑菜单滑动或者拖动
         *
         * @param delta     移动的偏移量
         * @param isDrag    true-拖动，false-惯性滚动
         * @param swipeMenu
         */
        void onViewPositionChanged(int delta, boolean isDrag, SwipeMenu swipeMenu);
    }

    interface OnScrollStateChangeCallback
    {
        /**
         * 滚动状态变化回调
         *
         * @param state
         * @param swipeMenu
         */
        void onScrollStateChanged(ScrollState state, SwipeMenu swipeMenu);
    }

    interface PullCondition
    {
        /**
         * 要开始拖动的时候会回调此方法
         *
         * @param swipeMenu
         * @return true-可以拖动，false-不能拖动
         */
        boolean canPull(SwipeMenu swipeMenu);
    }
}
