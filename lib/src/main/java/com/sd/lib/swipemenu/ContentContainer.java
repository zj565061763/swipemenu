package com.sd.lib.swipemenu;

import android.content.Context;
import android.view.ViewGroup;

/**
 * 保存内容View的容器
 */
final class ContentContainer extends BaseContainer
{
    public ContentContainer(Context context)
    {
        super(context);
    }

    @Override
    public final void setLayoutParams(ViewGroup.LayoutParams params)
    {
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        super.setLayoutParams(params);
    }
}
