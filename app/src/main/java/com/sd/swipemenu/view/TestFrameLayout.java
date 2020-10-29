package com.sd.swipemenu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TestFrameLayout extends FrameLayout
{
    public TestFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        final boolean result = super.onInterceptTouchEvent(event);
        Log.i("TouchEvent", "TestFrameLayout onInterceptTouchEvent:" + event.getAction() + " result:" + result);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        final boolean result = super.onTouchEvent(event);
        Log.i("TouchEvent", "TestFrameLayout onTouchEvent:" + event.getAction() + " result:" + result);
        return result;
    }
}
