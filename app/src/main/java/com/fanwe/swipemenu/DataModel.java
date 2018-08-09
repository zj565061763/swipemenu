package com.fanwe.swipemenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/19.
 */
public class DataModel
{
    public static final int TYPE_ZERO = 0;
    public static final int TYPE_ONE = 1;

    public String name;
    public int type;

    public static List<DataModel> get(int count)
    {
        final List<DataModel> list = new ArrayList<>();
        for (int i = 0; i < count; i++)
        {
            final DataModel model = new DataModel();
            model.name = String.valueOf(i);
            model.type = (i % 2 == 0) ? TYPE_ZERO : TYPE_ONE;
            list.add(model);
        }
        return list;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
