package com.fanwe.lib.swipemenu.adapter;

import com.fanwe.lib.swipemenu.SwipeMenu;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class SwipeMenuHolder
{
    private final Map<SwipeMenu, Integer> mMapSwipeMenu = new WeakHashMap<>();

    /**
     * 添加要管理的菜单
     *
     * @param swipeMenu
     * @param position
     */
    public void put(SwipeMenu swipeMenu, int position)
    {
        if (swipeMenu == null)
            throw new NullPointerException();
        if (position < 0)
            throw new IllegalArgumentException();

        mMapSwipeMenu.put(swipeMenu, position);
    }

    /**
     * 得到所有菜单
     *
     * @return
     */
    public Set<SwipeMenu> getSwipeMenu()
    {
        return mMapSwipeMenu.keySet();
    }

    /**
     * 关闭所有菜单
     *
     * @param anim
     */
    public void closeAllSwipeMenu(boolean anim)
    {
        for (SwipeMenu item : getSwipeMenu())
        {
            item.close(anim);
        }
    }
}
