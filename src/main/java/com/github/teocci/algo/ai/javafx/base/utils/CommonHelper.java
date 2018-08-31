package com.github.teocci.algo.ai.javafx.base.utils;

import com.github.teocci.algo.ai.javafx.base.model.dino.Element;

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
}
