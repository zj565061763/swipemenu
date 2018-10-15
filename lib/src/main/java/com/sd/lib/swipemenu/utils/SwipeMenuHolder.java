package com.sd.lib.swipemenu.utils;

import android.view.View;

import com.sd.lib.swipemenu.SwipeMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class SwipeMenuHolder implements SwipeMenu.OnStateChangeCallback
{
    private final Map<SwipeMenu, Object> mMapSwipeMenu = new WeakHashMap<>();
    private final Map<Object, SwipeMenuInfo> mMapInfo = new HashMap<>();

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

    @Override
    public void onStateChanged(SwipeMenu.State oldState, SwipeMenu.State newState, SwipeMenu swipeMenu)
    {
        final Object tag = mMapSwipeMenu.get(swipeMenu);
        final SwipeMenuInfo info = mMapInfo.get(tag);
        if (info != null)
            info.mState = newState;
    }

    /**
     * 移除某个tag对应的菜单状态
     *
     * @param tag
     */
    public void remove(Object tag)
    {
        mMapInfo.remove(tag);
    }

    /**
     * 得到所有菜单
     *
     * @return
     */
    public Set<SwipeMenu> getAllSwipeMenu()
    {
        return mMapSwipeMenu.keySet();
    }

    /**
     * 除了指定的菜单外，设置所有菜单的状态
     *
     * @param state
     * @param anim
     * @param except
     */
    public void setAllSwipeMenuStateExcept(SwipeMenu.State state, boolean anim, SwipeMenu except)
    {
        for (SwipeMenu item : getAllSwipeMenu())
        {
            if (item != except)
                item.setState(state, anim);
        }
    }

    private static class SwipeMenuInfo
    {
        public SwipeMenu.State mState;
    }
}
