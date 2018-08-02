package com.fanwe.lib.swipemenu;

import android.view.View;

public interface SwipeMenu
{
    /**
     * 设置状态变化回调
     *
     * @param callback
     */
    void setOnStateChangedCallback(OnStateChangedCallback callback);

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
     * 返回当前的状态{@link State}
     *
     * @return
     */
    State getState();

    /**
     * 打开菜单
     */
    void open();

    /**
     * 关闭菜单
     */
    void close();

    enum State
    {
        /**
         * 菜单处于关闭状态
         */
        Closed,
        /**
         * 菜单处于打开状态
         */
        Opened
    }

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

    interface OnStateChangedCallback
    {
        /**
         * 状态变更回调
         *
         * @param state
         * @param swipeMenu
         */
        void onStateChanged(State state, SwipeMenu swipeMenu);
    }
}
