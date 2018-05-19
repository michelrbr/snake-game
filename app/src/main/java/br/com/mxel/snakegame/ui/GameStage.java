package br.com.mxel.snakegame.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

import br.com.mxel.snakegame.model.Controls;
import br.com.mxel.snakegame.model.Snake;

public class GameStage extends SurfaceView implements Runnable {

    private final long MILLIS_PER_SECOND = 1000;
    private final int NUM_BLOCKS_WIDE = 40;
    private final long FPS = 7;

    private Thread _thread = null;
    private final SurfaceHolder _surfaceHolder;
    private final Paint _paint;
    private Canvas _canvas;
    private volatile boolean _isPlaying;
    private int _screenX;
    private int _screenY;
    private int _snakeBlockSize;
    private int _controlButtonSize;
    private int _numBlocksHigh;
    private long _nextFrameTime;

    private Snake _snake;
    private Controls _controls;
    private Rect _food;

    public GameStage(Context context, Point size) {
        super(context);

        _screenX = size.x;
        _screenY = size.y;

        _surfaceHolder = getHolder();
        _paint = new Paint();

        _snakeBlockSize = _screenX / NUM_BLOCKS_WIDE;

        _numBlocksHigh = _screenY / _snakeBlockSize;

        _controlButtonSize = _snakeBlockSize * 3;

        int controlsY = _screenY - (_controlButtonSize * 3) - _snakeBlockSize;

        _controls = new Controls(_snakeBlockSize, controlsY, _controlButtonSize);

        _food = new Rect();

        startGame();
    }

    @Override
    public void run() {
        while (_isPlaying) {

            if(updateRequired()) {
                update();
                draw();
            }

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

    private void startGame() {

        _snake = new Snake(
                NUM_BLOCKS_WIDE / 2,
                _numBlocksHigh / 2,
                Snake.Direction.RIGHT);

        spawnFood();

        _nextFrameTime = System.currentTimeMillis();
    }

    private void spawnFood() {

        Random random = new Random();
        int rx = random.nextInt(NUM_BLOCKS_WIDE - 1) + 1;
        int ry = random.nextInt(_numBlocksHigh - 1) + 1;

        int x = rx * _snakeBlockSize;
        int y = ry * _snakeBlockSize;

        _food.set(
                x,
                y,
                x + _snakeBlockSize,
                y + _snakeBlockSize);
    }

    public boolean updateRequired() {

        if(_nextFrameTime <= System.currentTimeMillis()){

            _nextFrameTime = System.currentTimeMillis() + MILLIS_PER_SECOND / FPS;
            return true;
        }

        return false;
    }

    public void update() {

        _snake.moveSnake();

        if (detectDeath()) {

            startGame();
        }
    }

    private boolean detectDeath(){

        boolean dead = false;

        // Hit the screen edge
        if (_snake.getHeadX() == -1) dead = true;
        if (_snake.getHeadX() >= NUM_BLOCKS_WIDE) dead = true;
        if (_snake.getHeadY() == -1) dead = true;
        if (_snake.getHeadY() == _numBlocksHigh) dead = true;

        return dead;
    }

    private void draw() {

        if (_surfaceHolder.getSurface().isValid()) {

            _canvas = _surfaceHolder.lockCanvas();

            // Set background color
            _canvas.drawColor(Color.argb(255, 26, 128, 182));

            // Set controls color
            _paint.setColor(Color.argb(100, 255, 255, 255));

            // Draw controls
            for (Rect control : _controls.getButtons()) {
                _canvas.drawRect(
                        control.left,
                        control.top,
                        control.right,
                        control.bottom,
                        _paint);
            }

            // Set snake color
            _paint.setColor(Color.argb(255, 255, 255, 255));

            // Draw the snake
            for (int i = 0; i < _snake.getSnakeLength() + 1; i++) {
                _canvas.drawRect(_snake.bodyXs[i] * _snakeBlockSize,
                        (_snake.bodyYs[i] * _snakeBlockSize),
                        (_snake.bodyXs[i] * _snakeBlockSize) + _snakeBlockSize,
                        (_snake.bodyYs[i] * _snakeBlockSize) + _snakeBlockSize,
                        _paint);
            }

            // Set food color
            _paint.setColor(Color.argb(255, 100, 255, 100));
            _canvas.drawRect(
                    _food.left,
                    _food.top,
                    _food.right,
                    _food.bottom,
                    _paint);

            _surfaceHolder.unlockCanvasAndPost(_canvas);
        }
    }
}
