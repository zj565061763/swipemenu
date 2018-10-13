package com.sd.lib.swipemenu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

class MenuContainer extends LockContainer
{
    public MenuContainer(Context context)
    {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        final View contentView = getContentView();
        final ViewGroup.LayoutParams params = getLayoutParams();
        if (contentView != null && params != null)
        {
            final ViewGroup.LayoutParams contentViewParams = contentView.getLayoutParams();
            if (params.width != contentViewParams.width || params.height != contentViewParams.height)
            {
                params.width = contentViewParams.width;
                params.height = contentViewParams.height;
                setLayoutParams(params);
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
