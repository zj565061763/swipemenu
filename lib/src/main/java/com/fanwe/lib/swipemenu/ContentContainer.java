package com.fanwe.lib.swipemenu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

final class ContentContainer extends LockContainer
{
    public ContentContainer(Context context)
    {
        super(context);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams()
    {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        final View contentView = getContentView();
        if (contentView != null && contentView.getVisibility() != GONE)
        {
            contentView.layout(0, 0, contentView.getMeasuredWidth(), contentView.getMeasuredHeight());
        }
    }
}
