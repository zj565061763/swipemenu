package com.sd.swipemenu.utils;

public abstract class FLoopList<T>
{
    private int mIndex = 0;

    /**
     * 返回索引位置
     *
     * @return
     */
    public int getIndex()
    {
        return mIndex;
    }

    /**
     * 设置索引位置
     *
     * @param index
     * @return
     */
    public int setIndex(int index)
    {
        final int size = size();
        if (size <= 0)
        {
            index = -1;
        } else
        {
            if (index >= size)
                index = size - 1;

            if (index < 0)
                index = 0;
        }

        mIndex = index;
        return mIndex;
    }

    /**
     * 移动索引到后面的位置
     *
     * @param count 移动几个位置
     * @return
     */
    public int moveNext(int count)
    {
        final int index = movedIndex(true, count);
        return setIndex(index);
    }

    /**
     * 移动索引到前面的位置
     *
     * @param count 移动几个位置
     * @return
     */
    public int movePrevious(int count)
    {
        final int index = movedIndex(false, count);
        return setIndex(index);
    }

    /**
     * 返回当前索引所指向的对象
     *
     * @return
     */
    public T current()
    {
        final int index = setIndex(mIndex);
        return index < 0 ? null : get(index);
    }

    /**
     * 返回索引后面第几个位置的对象
     *
     * @param count 索引后面第几个位置
     * @return
     */
    public T next(int count)
    {
        final int index = movedIndex(true, count);
        return index < 0 ? null : get(index);
    }

    /**
     * 返回索引前面第几个位置的对象
     *
     * @return 索引前面第几个位置
     */
    public T previous(int count)
    {
        final int index = movedIndex(false, count);
        return index < 0 ? null : get(index);
    }

    private int movedIndex(boolean next, int count)
    {
        if (count <= 0)
            throw new IllegalArgumentException("count is out of range (count > 0)");

        final int size = size();
        if (size <= 0)
            return -1;

        int tempIndex = next ? mIndex + count : mIndex - count;
        int index = 0;
        if (next)
        {
            index = tempIndex < size ? tempIndex : tempIndex % size;
        } else
        {
            if (tempIndex >= 0)
            {
                index = tempIndex;
            } else
            {
                tempIndex = tempIndex % size;
                index = size + tempIndex;
            }
        }
        return index;
    }

    protected abstract int size();

    protected abstract T get(int index);
}
