package com.fanwe.lib.swipemenu.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.fanwe.lib.swipemenu.SwipeMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class SwipeMenuAdapterView extends FrameLayout
{
    public SwipeMenuAdapterView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    private final Map<SwipeMenu, Integer> mMapSwipeMenu = new WeakHashMap<>();

    private final int[] mLocation = {0, 0};
    private final Rect mRect = new Rect();

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

    private List<SwipeMenu> getOpenedSwipeMenu()
    {
        final List<SwipeMenu> list = new ArrayList<>();
        for (Map.Entry<SwipeMenu, Integer> item : mMapSwipeMenu.entrySet())
        {
            final SwipeMenu swipeMenu = item.getKey();
            if (swipeMenu.isOpened())
                list.add(swipeMenu);
        }
        return list;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
        {
            final List<SwipeMenu> list = getOpenedSwipeMenu();
            for (SwipeMenu item : list)
            {
                final View view = (View) item;

                view.getLocationOnScreen(mLocation);
                mRect.left = mLocation[0];
                mRect.top = mLocation[1];
                mRect.right = mLocation[0] + view.getWidth();
                mRect.bottom = mLocation[1] + view.getHeight();

                if (mRect.contains((int) ev.getRawX(), (int) ev.getRawY()))
                {
                } else
                {
                    item.close(true);
                }
            }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
