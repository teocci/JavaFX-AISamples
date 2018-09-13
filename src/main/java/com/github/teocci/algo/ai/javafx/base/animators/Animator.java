package com.github.teocci.algo.ai.javafx.base.animators;

import com.github.teocci.algo.ai.javafx.base.utils.LogHelper;
import javafx.animation.AnimationTimer;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-23
 */
public abstract class Animator<E> extends AnimationTimer
{
    private static final String TAG = LogHelper.makeLogTag(Animator.class);

    protected long lastTime = System.currentTimeMillis();

    protected E controller;

    public Animator(E controller)
    {
        this.controller = controller;
    }
}
