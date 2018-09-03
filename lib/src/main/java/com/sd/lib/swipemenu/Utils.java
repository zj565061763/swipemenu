package com.sd.lib.swipemenu;

import android.view.View;
import android.view.ViewGroup;

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

        if (view.getParent() == null)
            return;
        try
        {
            ((ViewGroup) view.getParent()).removeView(view);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
