package com.fanwe.swipemenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/19.
 */
public class DataModel
{
    public String name;

    public static List<DataModel> get(int count)
    {
        final List<DataModel> list = new ArrayList<>();
        for (int i = 0; i < count; i++)
        {
            final DataModel model = new DataModel();
            model.name = String.valueOf(i);
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
