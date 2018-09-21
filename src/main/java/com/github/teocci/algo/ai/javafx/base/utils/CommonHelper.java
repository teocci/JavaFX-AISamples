package com.github.teocci.algo.ai.javafx.base.utils;

import com.github.teocci.algo.ai.javafx.base.model.dino.Element;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-31
 */
public class CommonHelper
{
    public static void execMove(List<? extends Element> elems, double speed, int playerXpos)
    {
        for (int i = 0; i < elems.size(); i++) {
            elems.get(i).move(speed);
            if (elems.get(i).getPosX() < -playerXpos) {
                elems.remove(i);
                i--;
            }
        }
    }

    public static <T> T[] add2BArray(T[] elements, T element)
    {
        T[] newArray = Arrays.copyOf(elements, elements.length + 1);
        newArray[0] = element;
        System.arraycopy(elements, 0, newArray, 1, elements.length);

        return newArray;
    }

    public static <T> T[] add2Array(T[] elements, T element)
    {
        T[] newArray = Arrays.copyOf(elements, elements.length + 1);
        newArray[elements.length] = element;

        return newArray;
    }

    public static <T> T[] toArray(final List<T> obj)
    {
        if (obj == null || obj.isEmpty()) {
            return null;
        }
        final T t = obj.get(0);
        final T[] res = (T[]) Array.newInstance(t.getClass(), obj.size());
        for (int i = 0; i < obj.size(); i++) {
            res[i] = obj.get(i);
        }
        return res;
    }
}
