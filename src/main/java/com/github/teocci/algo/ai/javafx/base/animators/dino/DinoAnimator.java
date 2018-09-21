package com.github.teocci.algo.ai.javafx.base.animators.dino;

import com.github.teocci.algo.ai.javafx.base.animators.Animator;
import com.github.teocci.algo.ai.javafx.base.controllers.dino.MainController;
import com.github.teocci.algo.ai.javafx.base.utils.LogHelper;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-23
 */
public class DinoAnimator extends Animator<MainController>
{
    private static final String TAG = LogHelper.makeLogTag(DinoAnimator.class);

    public DinoAnimator(MainController controller)
    {
        super(controller);
    }

    @Override
    public void handle(long now)
    {
        // max speed: 100 hundred times per second
        if (now - lastTime > 10000000) {
            lastTime = now;
            controller.draw();
        }
    }
}
