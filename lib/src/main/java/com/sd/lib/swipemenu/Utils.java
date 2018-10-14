package com.sd.lib.swipemenu;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

class Utils
{
    public static int getMeasureSize(int size, int measureSpec)
    {
        int result = 0;

        final int modeSpec = View.MeasureSpec.getMode(measureSpec);
        final int sizeSpec = View.MeasureSpec.getSize(measureSpec);

        switch (modeSpec)
        {
            case View.MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case View.MeasureSpec.EXACTLY:
                result = sizeSpec;
                break;
            case View.MeasureSpec.AT_MOST:
                result = Math.min(size, sizeSpec);
                break;
        }
        return result;
    }

    public static void removeViewFromParent(View view)
    {
        if (view == null)
            return;

        final ViewParent parent = view.getParent();
        if (parent == null)
            return;

        try
        {
            ((ViewGroup) parent).removeView(view);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
