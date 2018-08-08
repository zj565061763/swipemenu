package com.fanwe.lib.swipemenu.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.fanwe.lib.swipemenu.SwipeMenu;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class SwipeMenuAdapterView extends FrameLayout
{
    private final Map<SwipeMenu, Integer> mMapSwipeMenu = new WeakHashMap<>();
    private InterceptTouchEventCallback mInterceptTouchEventCallback;

    public SwipeMenuAdapterView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setInterceptTouchEventCallback(new SingleModeTouchListener());
    }

    /**
     * 设置拦截回调对象
     *
     * @param callback
     */
    public void setInterceptTouchEventCallback(InterceptTouchEventCallback callback)
    {
        mInterceptTouchEventCallback = callback;
    }

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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (mInterceptTouchEventCallback != null)
        {
            if (mInterceptTouchEventCallback.onInterceptTouchEvent(ev, this))
                return true;
        }

        return super.onInterceptTouchEvent(ev);
    }

    public interface InterceptTouchEventCallback
    {
        boolean onInterceptTouchEvent(MotionEvent event, SwipeMenuAdapterView adapterView);
    }

    public static final class SingleModeTouchListener implements InterceptTouchEventCallback
    {
        private final int[] mLocation = {0, 0};
        private final Rect mRect = new Rect();

        @Override
        public boolean onInterceptTouchEvent(MotionEvent event, SwipeMenuAdapterView adapterView)
        {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
                for (SwipeMenu item : adapterView.getSwipeMenu())
                {
                    final View view = (View) item;

                    view.getLocationOnScreen(mLocation);
                    mRect.left = mLocation[0];
                    mRect.top = mLocation[1];
                    mRect.right = mLocation[0] + view.getWidth();
                    mRect.bottom = mLocation[1] + view.getHeight();

                    if (mRect.contains((int) event.getRawX(), (int) event.getRawY()))
                    {
                    } else
                    {
                        item.close(true);
                    }
                }
            }
            return false;
        }
    }
}
