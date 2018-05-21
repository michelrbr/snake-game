package br.com.mxel.snakegame.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
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
    private volatile boolean _isRunning;
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
    private int _score;

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

        _nextFrameTime = System.currentTimeMillis();

        //startGame();
    }

    @Override
    public void run() {
        while (_isRunning) {

            if (updateRequired()) {

                if(_isPlaying) {
                    update();
                }
                draw();
            }
        }
    }

    public void pause() {
        _isRunning = false;
        try {
            _thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        _isRunning = true;
        _thread = new Thread(this);
        _thread.start();
    }

    private void startGame() {

        _snake = new Snake(
                NUM_BLOCKS_WIDE / 2,
                _numBlocksHigh / 2,
                Snake.Direction.RIGHT);

        spawnFood();
        _score = 0;
        _nextFrameTime = System.currentTimeMillis();

        _isPlaying = true;
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

        if ((_snake.getHeadX() * _snakeBlockSize) == _food.left
                && (_snake.getHeadY() * _snakeBlockSize) == _food.top) {
            eatFood();
        }

        _snake.moveSnake();

        if (detectDeath()) {

            _isPlaying = false;
        }
    }

    private void eatFood() {

        spawnFood();
        _snake.increaseSize();
        _score++;
    }

    private boolean detectDeath(){

        boolean dead = false;

        // Hit the screen edge
        if (_snake.getHeadX() == -1) dead = true;
        if (_snake.getHeadX() >= NUM_BLOCKS_WIDE + 1) dead = true;
        if (_snake.getHeadY() == -1) dead = true;
        if (_snake.getHeadY() == _numBlocksHigh + 1) dead = true;

        // Hit itself
        for (int i = _snake.getSnakeLength(); i > 0; i--) {
            if ((i > 4)
                    && (_snake.getHeadX() == _snake.bodyXs[i])
                    && (_snake.getHeadY() == _snake.bodyYs[i])) {
                dead = true;
            }
        }

        return dead;
    }

    private void draw() {

        if (_surfaceHolder.getSurface().isValid()) {

            _canvas = _surfaceHolder.lockCanvas();

            if(_isPlaying) {
                drawGame(_canvas, _paint);
            } else {
                drawStart(_canvas, _paint);
            }

            _surfaceHolder.unlockCanvasAndPost(_canvas);
        }
    }

    private void drawGame(Canvas canvas, Paint paint) {

        // Set background color
        canvas.drawColor(Color.argb(255, 26, 128, 182));

        // Set controls color
        paint.setColor(Color.argb(100, 255, 255, 255));

        // Draw controls
        for (Rect control : _controls.getButtons()) {
            canvas.drawRect(
                    control.left,
                    control.top,
                    control.right,
                    control.bottom,
                    paint);
        }

        // Set food color
        paint.setColor(Color.argb(255, 100, 255, 100));
        canvas.drawRect(
                _food.left,
                _food.top,
                _food.right,
                _food.bottom,
                paint);

        // Set snake color
        paint.setColor(Color.argb(255, 255, 255, 255));

        // Draw the snake
        for (int i = 0; i < _snake.getSnakeLength() + 1; i++) {
            canvas.drawRect(_snake.bodyXs[i] * _snakeBlockSize,
                    (_snake.bodyYs[i] * _snakeBlockSize),
                    (_snake.bodyXs[i] * _snakeBlockSize) + _snakeBlockSize,
                    (_snake.bodyYs[i] * _snakeBlockSize) + _snakeBlockSize,
                    paint);
        }

        // Scale the HUD text
        paint.setTextSize(70);
        canvas.drawText(String.format("Score: %s", _score), 10, 60, paint);
    }

    private void drawStart(Canvas canvas, Paint paint) {

        // Set background color
        canvas.drawColor(Color.argb(255, 26, 128, 182));

        // Set text color
        paint.setColor(Color.argb(255, 255, 255, 255));

        paint.setTextSize(70);

        int halfScreen = _screenX / 2;

        int halfText;

        if(_score > 0) {

            String msgScore = String.format("Last score: %s", _score);
            float scoreMeasure = paint.measureText(msgScore);

            halfText = Math.round(scoreMeasure / 2);

            canvas.drawText(
                    msgScore,
                    halfScreen - halfText,
                    (_screenY / 2) - 100, paint);
        }

        String msgStart = "Touch the screen to start game";
        float startMeasure = paint.measureText(msgStart);

        halfText = Math.round(startMeasure / 2);

        canvas.drawText(msgStart, halfScreen - halfText, _screenY / 2, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

            if(_isPlaying) {

                int posX = Math.round(motionEvent.getX());
                int posY = Math.round(motionEvent.getY());

                if (_controls.getButton(Controls.Button.LEFT).contains(posX, posY)) {
                    _snake.setCurrentDirection(Snake.Direction.LEFT);
                } else if (_controls.getButton(Controls.Button.UP).contains(posX, posY)) {
                    _snake.setCurrentDirection(Snake.Direction.UP);
                } else if (_controls.getButton(Controls.Button.RIGHT).contains(posX, posY)) {
                    _snake.setCurrentDirection(Snake.Direction.RIGHT);
                } else if (_controls.getButton(Controls.Button.DOWN).contains(posX, posY)) {
                    _snake.setCurrentDirection(Snake.Direction.DOWN);
                }
            } else {
                startGame();
            }
        }

        return super.onTouchEvent(motionEvent);
    }
}
