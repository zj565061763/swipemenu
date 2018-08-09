package com.fanwe.lib.swipemenu.adapter;

import android.util.Log;
import android.view.View;

import com.fanwe.lib.swipemenu.SwipeMenu;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class SwipeMenuHolder
{
    private final Map<SwipeMenu, Object> mMapSwipeMenu = new WeakHashMap<>();
    private final Map<Object, SwipeMenuInfo> mMapInfo = new HashMap<>();

    /**
     * 添加要管理的菜单
     *
     * @param swipeMenu
     * @param tag
     */
    public void put(SwipeMenu swipeMenu, Object tag)
    {
        if (swipeMenu == null || tag == null)
            throw new NullPointerException();
        if (tag instanceof View)
            throw new IllegalArgumentException("tag must not be instance of view");

        mMapSwipeMenu.put(swipeMenu, tag);

        SwipeMenuInfo info = mMapInfo.get(tag);
        if (info == null)
        {
            swipeMenu.close(false);
            info = new SwipeMenuInfo(swipeMenu);
            mMapInfo.put(tag, info);
        } else
        {
            if (info.mIsOpened)
                swipeMenu.open(false);
            else
                swipeMenu.close(false);
        }

        swipeMenu.setOnStateChangeCallback(mOnStateChangeCallback);
    }

    private final SwipeMenu.OnStateChangeCallback mOnStateChangeCallback = new SwipeMenu.OnStateChangeCallback()
    {
        @Override
        public void onStateChanged(boolean isOpened, SwipeMenu swipeMenu)
        {
            final Object tag = mMapSwipeMenu.get(swipeMenu);
            final SwipeMenuInfo info = mMapInfo.get(tag);
            if (info != null)
                info.mIsOpened = isOpened;

            Log.i(SwipeMenuHolder.class.getSimpleName(), isOpened + " " + tag);
        }
    };

    public void remove(Object tag)
    {
        mMapInfo.remove(tag);
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

    private static class SwipeMenuInfo
    {
        public final WeakReference<SwipeMenu> mSwipeMenu;
        public boolean mIsOpened;

        public SwipeMenuInfo(SwipeMenu swipeMenu)
        {
            mSwipeMenu = new WeakReference<>(swipeMenu);
            mIsOpened = swipeMenu.isOpened();
        }
    }
}
