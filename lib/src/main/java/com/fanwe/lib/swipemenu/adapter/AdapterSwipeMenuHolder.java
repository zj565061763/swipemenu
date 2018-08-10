package com.fanwe.lib.swipemenu.adapter;

import android.view.View;

import com.fanwe.lib.swipemenu.SwipeMenu;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class AdapterSwipeMenuHolder implements SwipeMenu.OnStateChangeCallback
{
    private final SwipeMenuAdapter mAdapter;
    private final Map<SwipeMenu, Object> mMapSwipeMenu = new WeakHashMap<>();
    private final Map<Object, SwipeMenuInfo> mMapInfo = new HashMap<>();

    public AdapterSwipeMenuHolder(SwipeMenuAdapter adapter)
    {
        if (adapter == null)
            throw new NullPointerException();
        mAdapter = adapter;
    }

    /**
     * 绑定某个位置和某个菜单的状态
     *
     * @param swipeMenu
     * @param position
     */
    public void bind(SwipeMenu swipeMenu, int position)
    {
        if (swipeMenu == null)
            throw new NullPointerException();

        if (position < 0)
            throw new IllegalArgumentException("position out of range ( position >= 0 )");

        final Object tag = mAdapter.getTag(position);
        if (tag instanceof View)
            throw new IllegalArgumentException("tag must not be instance of view");

        mMapSwipeMenu.put(swipeMenu, tag);
        swipeMenu.setOnStateChangeCallback(this);

        SwipeMenuInfo info = mMapInfo.get(tag);
        if (info == null)
        {
            swipeMenu.setOpened(false, false);
            info = new SwipeMenuInfo(swipeMenu);
            mMapInfo.put(tag, info);
        } else
        {
            swipeMenu.setOpened(info.mIsOpened, false);
        }
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

    @Override
    public void onStateChanged(boolean isOpened, SwipeMenu swipeMenu)
    {
        final Object tag = mMapSwipeMenu.get(swipeMenu);
        final SwipeMenuInfo info = mMapInfo.get(tag);
        if (info != null)
            info.mIsOpened = isOpened;
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
