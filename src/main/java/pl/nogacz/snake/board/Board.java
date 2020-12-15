package pl.nogacz.snake.board;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import pl.nogacz.snake.application.Design;
import pl.nogacz.snake.application.EndGame;
import pl.nogacz.snake.application.PauseGame;
import pl.nogacz.snake.application.keybindMenu;
import pl.nogacz.snake.application.skinMenu;
import pl.nogacz.snake.pawn.Pawn;
import pl.nogacz.snake.pawn.PawnClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Dawid Nogacz on 19.05.2019
 */
public class Board {
    private HashMap<Coordinates, PawnClass> board = new HashMap<>();
    private Design design;
    private Random random = new Random();

    private boolean isEndGame = false;
    private boolean isPaused = false;

    private static int direction = 1; // 1 - UP || 2 - BOTTOM || 3 - LEFT || 4 - RIGHT
    private static int skinTone = skinMenu.getSkinTone();
    private int tailLength = 0;
    private Coordinates snakeHeadCoordinates = new Coordinates(10, 10);
    private PawnClass snakeHeadClass = new PawnClass(Pawn.SNAKE_HEAD);
    private PawnClass snakeBodyClass = new PawnClass(Pawn.SNAKE_BODY);
    private PawnClass snakeBodyClass2 = new PawnClass(Pawn.SNAKE_BODY2);
    private PawnClass snakeBodyClass3 = new PawnClass(Pawn.SNAKE_BODY3);
    private PawnClass foodClass = new PawnClass(Pawn.FOOD);

    private ArrayList<Coordinates> snakeTail = new ArrayList<>();

    public static PawnClass setBodySkin() {
        switch(skinTone) {
            case 1: return new PawnClass(Pawn.SNAKE_BODY);
            case 2: return new PawnClass(Pawn.SNAKE_BODY2);
            case 3: return new PawnClass(Pawn.SNAKE_BODY3);
            default: return new PawnClass(Pawn.SNAKE_BODY2);
        }
    } 

    public Board(Design design) {
        this.design = design;

        addStartEntity();
        mapTask();
    }

    private void addStartEntity() {
        board.put(snakeHeadCoordinates, snakeHeadClass);

        for(int i = 0; i < 22; i++) {
            board.put(new Coordinates(0, i), new PawnClass(Pawn.BRICK));
            board.put(new Coordinates(21, i), new PawnClass(Pawn.BRICK));
            board.put(new Coordinates(i, 0), new PawnClass(Pawn.BRICK));
            board.put(new Coordinates(i, 21), new PawnClass(Pawn.BRICK));
        }
        addEat();
        displayAllImage();
    }

    private void checkMap() {
        removeAllImage();
        moveSnake();
        displayAllImage();
    }

    private void removeAllImage() {
        for(Map.Entry<Coordinates, PawnClass> entry : board.entrySet()) {
            design.removePawn(entry.getKey());
        }
    }

    private void displayAllImage() {
        for(Map.Entry<Coordinates, PawnClass> entry : board.entrySet()) {
            design.addPawn(entry.getKey(), entry.getValue());
        }
    }

    private void moveSnake() {
        switch(direction) {
            case 1: moveSnakeHead(new Coordinates(snakeHeadCoordinates.getX(), snakeHeadCoordinates.getY() - 1), skinMenu.getSkinTone()); break;
            case 2: moveSnakeHead(new Coordinates(snakeHeadCoordinates.getX(), snakeHeadCoordinates.getY() + 1), skinMenu.getSkinTone()); break;
            case 3: moveSnakeHead(new Coordinates(snakeHeadCoordinates.getX() - 1, snakeHeadCoordinates.getY()), skinMenu.getSkinTone()); break;
            case 4: moveSnakeHead(new Coordinates(snakeHeadCoordinates.getX() + 1, snakeHeadCoordinates.getY()), skinMenu.getSkinTone()); break;
        }
    }

    private void moveSnakeHead(Coordinates coordinates, int skinTone) {
        if(coordinates.isValid()) {
            if(isFieldNotNull(coordinates)) {
                if(getPawn(coordinates).getPawn().isFood()) {
                    board.remove(snakeHeadCoordinates);
                    switch(skinTone) {
                        case 1: board.put(snakeHeadCoordinates, snakeBodyClass); break;
                        case 2: board.put(snakeHeadCoordinates, snakeBodyClass2); break;
                        case 3: board.put(snakeHeadCoordinates, snakeBodyClass3); break;
                        default: break;

                    }
                    board.put(coordinates, snakeHeadClass);
                    snakeTail.add(snakeHeadCoordinates);
                    tailLength++;

                    snakeHeadCoordinates = coordinates;

                    addEat();
                } else {
                    isEndGame = true;

                    new EndGame("End game...\n" +
                            "You have " + tailLength + " points. \n" +
                            "Maybe try again? :)");
                }
            } else {
                board.remove(snakeHeadCoordinates);
                board.put(coordinates, snakeHeadClass);

                snakeHeadCoordinates = coordinates;

                if(tailLength > 0) {
                    moveSnakeBody();
                }
            }
        }
    }

    private void moveSnakeBody() {
        switch(direction) {
            case 1: moveSnakeBodyHandler(new Coordinates(snakeHeadCoordinates.getX(), snakeHeadCoordinates.getY() + 1), skinMenu.getSkinTone()); break;
            case 2: moveSnakeBodyHandler(new Coordinates(snakeHeadCoordinates.getX(), snakeHeadCoordinates.getY() - 1), skinMenu.getSkinTone()); break;
            case 3: moveSnakeBodyHandler(new Coordinates(snakeHeadCoordinates.getX() + 1, snakeHeadCoordinates.getY()), skinMenu.getSkinTone()); break;
            case 4: moveSnakeBodyHandler(new Coordinates(snakeHeadCoordinates.getX() - 1, snakeHeadCoordinates.getY()), skinMenu.getSkinTone()); break;
        }
    }

    private void moveSnakeBodyHandler(Coordinates coordinates, int skinTone) {
        if(tailLength == snakeTail.size()) {
            Coordinates endTail = snakeTail.get(0);
            board.remove(endTail);
            snakeTail.remove(endTail);
        }
        switch(skinTone) {
            case 1: board.put(coordinates, snakeBodyClass); break;
            case 2: board.put(coordinates, snakeBodyClass2); break;
            case 3: board.put(coordinates, snakeBodyClass3); break;
            default: break;

        }
        snakeTail.add(coordinates);
    }

    private void addEat() {
        Coordinates foodCoordinates;

        do {
            foodCoordinates = new Coordinates(random.nextInt(21), random.nextInt(21));
        } while(isFieldNotNull(foodCoordinates));

        board.put(foodCoordinates, foodClass);
    }

    private void mapTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(140);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                return null;
            }
        };

        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                if(!isEndGame && !isPaused) {
                    checkMap();
                    mapTask();
                }
            }
        });
        new Thread(task).start();
    }
    public synchronized void resumeGame() {
        isPaused = false;
        notifyAll();
        checkMap();
        mapTask();
    }

    public void readKeyboard(KeyEvent event) {
        if(!keybindMenu.getKeymap()) {
            switch(event.getCode()) {
                case W: changeDirection(1); break;
                case S: changeDirection(2); break;
                case A: changeDirection(3); break;
                case D: changeDirection(4); break;
                case ESCAPE: 
                if(isPaused) {
                    resumeGame();
                }
                else {
                    isPaused = true;
                    new PauseGame("Game Paused!\nPress Esc after pressing Continue for game to continue");
                }   
                break;
                default: break;
            }
        }
        else {
            switch(event.getCode()) {
                case UP: changeDirection(1); break;
                case DOWN: changeDirection(2); break;
                case LEFT: changeDirection(3); break;
                case RIGHT: changeDirection(4); break;
                case ESCAPE: 
                if(isPaused) {
                    resumeGame();
                }
                else {
                    isPaused = true;
                    new PauseGame("Game Paused!\nPress Esc after pressing Continue for game to continue");
                }
                break;
                default: break;
            }
        }
    }

    private void changeDirection(int newDirection) {
        if(newDirection == 1 && direction != 2) {
            direction = 1;
        } else if(newDirection == 2 && direction != 1) {
            direction = 2;
        } else if(newDirection == 3 && direction != 4) {
            direction = 3;
        } else if(newDirection == 4 && direction != 3) {
            direction = 4;
        }
    }

    private boolean isFieldNotNull(Coordinates coordinates) {
        return getPawn(coordinates) != null;
    }

    private PawnClass getPawn(Coordinates coordinates) {
        return board.get(coordinates);
    }

    public static int getDirection() {
        return direction;
    }
}
