package br.com.mxel.snakegame.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.view.SurfaceView;

public class GameStage extends SurfaceView implements Runnable {


    private Thread _thread;
    private boolean _isPlaying;

    public GameStage(Context context, Point size) {
        super(context);
    }

    @Override
    public void run() {

        while (_isPlaying) {
            draw();
        }
    }

    public void pause() {

        _isPlaying = false;
        try {
            _thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {

        _isPlaying = true;
        _thread = new Thread(this);
        _thread.start();
    }

    private void draw() {

        if (getHolder().getSurface().isValid()) {

            Canvas canvas = getHolder().lockCanvas();

            canvas.drawColor(Color.argb(255, 210, 182, 134));

            getHolder().unlockCanvasAndPost(canvas);
        }
    }
}
