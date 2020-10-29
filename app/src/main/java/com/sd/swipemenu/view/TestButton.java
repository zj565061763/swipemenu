package com.sd.swipemenu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatButton;

public class TestButton extends AppCompatButton
{
    public TestButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        final boolean result = super.onTouchEvent(event);
        Log.i("TouchEvent", "TestButton onTouchEvent:" + event.getAction() + " result:" + result);
        return result;
    }
}
