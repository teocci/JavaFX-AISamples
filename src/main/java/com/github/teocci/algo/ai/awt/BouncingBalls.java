package com.github.teocci.algo.ai.awt;

import com.github.teocci.algo.ai.stdlib.StdDraw;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-23
 */
public class BouncingBalls
{
    public static void main(String[] args)
    {
        int N = Integer.parseInt(args[0]);
        Ball[] balls = new Ball[N];
        for (int i = 0; i < N; i++)
            balls[i] = new Ball();
        // main simulation loop 03:16
        while(true)
        {
            StdDraw.clear();
            for (int i = 0; i< N; i++)
            {
                balls[i].move();
                balls[i].draw();
            }
            StdDraw.show(50);
        }
    }
}
