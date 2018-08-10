package com.fanwe.lib.swipemenu;

import android.view.View;

public interface SwipeMenu
{
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
     * 设置内容view
     *
     * @param view
     */
    void setContentView(View view);

    /**
     * 设置菜单view
     *
     * @param view
     */
    void setMenuView(View view);

    /**
     * 设置菜单的位置{@link Gravity}，默认靠右边
     *
     * @param gravity
     */
    void setMenuGravity(Gravity gravity);

    /**
     * 返回内容view
     *
     * @return
     */
    View getContentView();

    /**
     * 返回菜单view
     *
     * @return
     */
    View getMenuView();

    /**
     * 返回菜单位置
     *
     * @return {@link Gravity}
     */
    Gravity getMenuGravity();

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
     * 是否处于打开状态
     *
     * @return
     */
    boolean isOpened();

    /**
     * 打开或者关闭菜单
     *
     * @param opened true-打开，false-关闭
     * @param anim   true-动画效果，false-无动画
     * @return
     */
    boolean setOpened(boolean opened, boolean anim);

    enum Gravity
    {
        /**
         * 菜单靠左边
         */
        Left,
        /**
         * 菜单靠右边
         */
        Right
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
         * @param isOpened
         * @param swipeMenu
         */
        void onStateChanged(boolean isOpened, SwipeMenu swipeMenu);
    }

    interface OnViewPositionChangeCallback
    {
        /**
         * view位置变化回调，侧滑菜单滑动或者拖动
         *
         * @param isDrag    true-拖动，false-惯性滚动
         * @param swipeMenu
         */
        void onViewPositionChanged(boolean isDrag, SwipeMenu swipeMenu);
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
        boolean canPull(SwipeMenu swipeMenu);
    }
}
