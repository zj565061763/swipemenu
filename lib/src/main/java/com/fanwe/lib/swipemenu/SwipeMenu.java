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
     * 返回菜单位置
     *
     * @return {@link Gravity}
     */
    Gravity getMenuGravity();

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
     * 打开菜单
     *
     * @param anim true-动画效果，false-无动画
     */
    void open(boolean anim);

    /**
     * 关闭菜单
     *
     * @param anim true-动画效果，false-无动画
     */
    void close(boolean anim);

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
         * @param isDrag
         * @param swipeMenu
         */
        void onViewPositionChanged(boolean isDrag, SwipeMenu swipeMenu);
    }
}
