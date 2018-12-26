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
package com.sd.lib.swipemenu.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.sd.lib.swipemenu.SwipeMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class SwipeMenuOverlay extends FrameLayout implements SwipeMenu.OnStateChangeCallback
{
    private final Map<SwipeMenu, Object> mMapSwipeMenu = new WeakHashMap<>();
    private final Map<Object, SwipeMenuInfo> mMapInfo = new HashMap<>();

    public SwipeMenuOverlay(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
     * 绑定某个菜单的状态
     *
     * @param swipeMenu
     * @param tag       唯一标识，如果在有复用机制的列表中使用，tag可以传入每一项对应的实体
     */
    public void bind(SwipeMenu swipeMenu, Object tag)
    {
        if (swipeMenu == null || tag == null)
            throw new NullPointerException();

        if (tag instanceof View)
            throw new IllegalArgumentException("tag must not be instance of view");

        mMapSwipeMenu.put(swipeMenu, tag);
        swipeMenu.setOnStateChangeCallback(this);

        SwipeMenuInfo info = mMapInfo.get(tag);
        if (info == null)
        {
            info = new SwipeMenuInfo();
            info.mState = SwipeMenu.State.Close;
            mMapInfo.put(tag, info);
        }

        swipeMenu.setState(info.mState, false);
    }

    /**
     * 移除某个tag对应的菜单状态
     *
     * @param tag
     */
    public final void remove(Object tag)
    {
        mMapInfo.remove(tag);
    }

    /**
     * 遍历菜单
     *
     * @param callback
     */
    public final void foreach(ForeachCallback callback)
    {
        if (callback == null)
            return;

        for (SwipeMenu item : mMapSwipeMenu.keySet())
        {
            if (callback.onNext(item))
                break;
        }
    }

    @Override
    public void onStateChanged(SwipeMenu.State oldState, SwipeMenu.State newState, SwipeMenu swipeMenu)
    {
        final Object tag = mMapSwipeMenu.get(swipeMenu);
        final SwipeMenuInfo info = mMapInfo.get(tag);
        if (info != null)
            info.mState = newState;
    }

    private static class SwipeMenuInfo
    {
        public SwipeMenu.State mState;
    }

    public interface ForeachCallback
    {
        boolean onNext(SwipeMenu swipeMenu);
    }
}
