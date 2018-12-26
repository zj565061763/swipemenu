/*
 * Copyright (C) 2017 Sunday (https://github.com/zj565061763)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sd.lib.swipemenu;

import android.view.View;

public interface SwipeMenu
{
    /**
     * 设置调试模式，会有日志输出，日志tag：SwipeMenu
     *
     * @param debug
     */
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
     * 设置内容view
     *
     * @param view
     */
    void setContentView(View view);

    /**
     * 设置某个方向的菜单view
     *
     * @param view
     * @param direction
     */
    void setMenuView(View view, Direction direction);

    /**
     * 设置菜单模式，默认覆盖模式{@link Mode#Overlay}
     *
     * @param mode {@link Mode}
     */
    void setMode(Mode mode);

    /**
     * 返回内容view
     *
     * @return
     */
    View getContentView();

    /**
     * 返回指定方向的菜单view
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
     * 返回当前内容view滚动的百分比[0-1]
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

    enum State
    {
        Close,
        OpenLeft,
        OpenTop,
        OpenRight,
        OpenBottom
    }

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

    enum Mode
    {
        /**
         * 覆盖模式
         */
        Overlay,
        /**
         * 抽屉拉开模式
         */
        Drawer
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
         * @param oldState
         * @param newState
         * @param swipeMenu
         */
        void onStateChanged(State oldState, State newState, SwipeMenu swipeMenu);
    }

    interface OnViewPositionChangeCallback
    {
        /**
         * view位置变化回调，侧滑菜单滑动或者拖动
         *
         * @param left      内容view父容器的left
         * @param top       内容view父容器的top
         * @param isDrag    true-拖动，false-惯性滚动
         * @param swipeMenu
         */
        void onViewPositionChanged(int left, int top, boolean isDrag, SwipeMenu swipeMenu);
    }

    interface OnScrollStateChangeCallback
    {
        /**
         * 滚动状态变化回调
         *
         * @param oldState
         * @param newState
         * @param swipeMenu
         */
        void onScrollStateChanged(ScrollState oldState, ScrollState newState, SwipeMenu swipeMenu);
    }
}
