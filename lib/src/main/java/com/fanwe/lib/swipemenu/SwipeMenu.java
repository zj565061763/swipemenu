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
     * 是否处于打开状态
     *
     * @return
     */
    boolean isOpened();

    /**
     * 打开关闭菜单
     *
     * @param open   true-打开，false-关闭
     * @param anim   true-动画效果，false-无动画
     * @param notify true-通知回调，false-不通知
     */
    void open(boolean open, boolean anim, boolean notify);

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
         * @param swipeMenu
         */
        void onViewPositionChanged(SwipeMenu swipeMenu);
    }
}
