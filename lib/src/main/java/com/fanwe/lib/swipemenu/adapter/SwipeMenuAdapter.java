package com.fanwe.lib.swipemenu.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.fanwe.lib.swipemenu.SwipeMenu;

public interface SwipeMenuAdapter
{
    /**
     * 创建菜单view
     *
     * @param position
     * @param parent
     * @return
     */
    View onCreateMenuView(int position, ViewGroup parent);

    /**
     * 绑定数据
     *
     * @param position
     * @param contentView
     * @param menuView
     * @param swipeMenu
     */
    void onBindData(int position, View contentView, View menuView, SwipeMenu swipeMenu);

    /**
     * 返回每一项的唯一标识，建议返回每一项对应的实体对象
     *
     * @param position
     * @return
     */
    Object getTag(int position);
}
