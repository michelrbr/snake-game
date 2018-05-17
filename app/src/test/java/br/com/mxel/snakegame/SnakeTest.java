package br.com.mxel.snakegame;

import org.junit.Before;
import org.junit.Test;

import br.com.mxel.snakegame.model.SnakeModel;

import static org.junit.Assert.assertEquals;

public class SnakeTest {

    SnakeModel snake;

    @Before
    public void setup() {
        snake = new SnakeModel(10, 10, SnakeModel.Direction.RIGHT);
    }

    @Test
    public void changeDirectionTest() {

        // should keep previews direction because left is opposite from right
        snake.setCurrentDirection(SnakeModel.Direction.LEFT);
        assertEquals(snake.getCurrentDirection(), SnakeModel.Direction.RIGHT);

        // should change direction
        snake.setCurrentDirection(SnakeModel.Direction.DOWN);
        assertEquals(snake.getCurrentDirection(), SnakeModel.Direction.DOWN);

        // shouldn't change direction
        snake.setCurrentDirection(SnakeModel.Direction.UP);
        assertEquals(snake.getCurrentDirection(), SnakeModel.Direction.DOWN);

        // should change direction
        snake.setCurrentDirection(SnakeModel.Direction.LEFT);
        assertEquals(snake.getCurrentDirection(), SnakeModel.Direction.LEFT);

        // shouldn't change direction
        snake.setCurrentDirection(SnakeModel.Direction.RIGHT);
        assertEquals(snake.getCurrentDirection(), SnakeModel.Direction.LEFT);

        // should change direction
        snake.setCurrentDirection(SnakeModel.Direction.UP);
        assertEquals(snake.getCurrentDirection(), SnakeModel.Direction.UP);

        // shouldn't change direction
        snake.setCurrentDirection(SnakeModel.Direction.DOWN);
        assertEquals(snake.getCurrentDirection(), SnakeModel.Direction.UP);
    }

    @Test
    public void directionTest() {

        snake.increaseSize();

        snake.moveSnake();
        assertEquals(snake.getHeadX(), 11);
        assertEquals(snake.getHeadY(), 10);

        snake.setCurrentDirection(SnakeModel.Direction.DOWN);
        snake.moveSnake();
        assertEquals(snake.getHeadX(), 11);
        assertEquals(snake.getHeadY(), 11);

        snake.setCurrentDirection(SnakeModel.Direction.LEFT);
        snake.moveSnake();
        assertEquals(snake.getHeadX(), 10);
        assertEquals(snake.getHeadY(), 11);

        snake.setCurrentDirection(SnakeModel.Direction.UP);
        snake.moveSnake();
        assertEquals(snake.getHeadX(), 10);
        assertEquals(snake.getHeadY(), 10);
    }
}
