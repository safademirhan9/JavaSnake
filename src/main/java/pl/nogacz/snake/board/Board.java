package pl.nogacz.snake.board;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import pl.nogacz.snake.application.Design;
import pl.nogacz.snake.application.EndGame;
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

    private static int direction = 1; // 1 - UP || 2 - BOTTOM || 3 - LEFT || 4 - RIGHT
    private int tailLength = 0;

    private Coordinates snakeHeadCoordinates = new Coordinates(10, 10);
    
    private Coordinates currentRottenAppleCoordinates[] = new Coordinates[3]; // current coordinates of three different apple that are spawned currently

    long lastSpawnTime;
    long lastDissappearTime;

    long lastSpawnTime2;
    long lastDissappearTime2;

    long lastSpawnTime3;
    long lastDissappearTime3;

    int randSpawnRottenAppleTime;
    int randDissappearRottenAppleTime;
    
    int randSpawnRottenAppleTime2;
    int randDissappearRottenAppleTime2;

    int randSpawnRottenAppleTime3;
    int randDissappearRottenAppleTime3;
    
    
    //boolean check for the first dissappearing process for each three apple objects
    boolean disappearRotten = true;
    boolean disappearRotten2 = true;
    boolean disappearRotten3 = true;

    //a constant variable for limiting the rotten apple amount appearing at the same time
    private static final int objectLimit = 6;


    private PawnClass snakeHeadClass = new PawnClass(Pawn.SNAKE_HEAD);
    private PawnClass snakeBodyClass = new PawnClass(Pawn.SNAKE_BODY);
    private PawnClass foodClass = new PawnClass(Pawn.FOOD);
    
    private PawnClass rottenAppleClass = new PawnClass(Pawn.ROTTEN_APPLE);
    private PawnClass rottenAppleClass2 = new PawnClass(Pawn.ROTTEN_APPLE2);
    private PawnClass rottenAppleClass3 = new PawnClass(Pawn.ROTTEN_APPLE3);

    private ArrayList<Coordinates> snakeTail = new ArrayList<>();
    private ArrayList<Coordinates> rottenApples = new ArrayList<>();
    private ArrayList<Coordinates> rottenApples2 = new ArrayList<>();
    private ArrayList<Coordinates> rottenApples3 = new ArrayList<>();

    public Board(Design design) {
        this.design = design;
        
        //initial spawn times for three different rotten apple objects when the game starts
        randSpawnRottenAppleTime = 5;
        randSpawnRottenAppleTime2 = 10;
        randSpawnRottenAppleTime3 = 7;

        addStartEntity();
        mapTask();
    }

    private void addStartEntity() {
        board.put(snakeHeadCoordinates, snakeHeadClass);
        lastSpawnTime = System.currentTimeMillis();
        lastDissappearTime = System.currentTimeMillis();

        lastSpawnTime2 = System.currentTimeMillis();
        lastDissappearTime2 = System.currentTimeMillis();

        lastSpawnTime3 = System.currentTimeMillis();
        lastDissappearTime3 = System.currentTimeMillis();

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
            case 1: moveSnakeHead(new Coordinates(snakeHeadCoordinates.getX(), snakeHeadCoordinates.getY() - 1)); break;
            case 2: moveSnakeHead(new Coordinates(snakeHeadCoordinates.getX(), snakeHeadCoordinates.getY() + 1)); break;
            case 3: moveSnakeHead(new Coordinates(snakeHeadCoordinates.getX() - 1, snakeHeadCoordinates.getY())); break;
            case 4: moveSnakeHead(new Coordinates(snakeHeadCoordinates.getX() + 1, snakeHeadCoordinates.getY())); break;
        }
    }

    private void moveSnakeHead(Coordinates coordinates) {
        if(coordinates.isValid()) {
            if(isFieldNotNull(coordinates)) {
                if(getPawn(coordinates).getPawn().isFood()) { //snake eats healthy apple.
                    board.remove(snakeHeadCoordinates);
                    board.put(snakeHeadCoordinates, snakeBodyClass);
                    board.put(coordinates, snakeHeadClass);
                    snakeTail.add(snakeHeadCoordinates);
                    tailLength++;

                    snakeHeadCoordinates = coordinates;

                    addEat();                    
                } 
                
                else if(getPawn(coordinates).getPawn().isRottenApple() || getPawn(coordinates).getPawn().isRottenApple2() || getPawn(coordinates).getPawn().isRottenApple3()){ // if snake hits rotten apple game ends.
                    new EndGame("End game...\n" +
                    "You have " + tailLength + " points. \n" +
                    "Maybe try again? :)");
                }
                else {
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

    private void spawnRottenApple(){
        if(Math.abs(lastSpawnTime - System.currentTimeMillis())/1000 == randSpawnRottenAppleTime){
            addRottenApples();
            rottenApples.add(currentRottenAppleCoordinates[0]);
            lastSpawnTime = System.currentTimeMillis();
            randSpawnRottenAppleTime = random.nextInt(9)+1;
            //System.out.println("random spawn time:" + randSpawnRottenAppleTime);
        }

        if(Math.abs(lastSpawnTime2 - System.currentTimeMillis())/1000 == randSpawnRottenAppleTime2){
            addRottenApples2();
            rottenApples2.add(currentRottenAppleCoordinates[1]);
            lastSpawnTime2 = System.currentTimeMillis();
            randSpawnRottenAppleTime2 = random.nextInt(20)+1;
            //System.out.println("random spawn time2:" + randSpawnRottenAppleTime2);
        }

        if(Math.abs(lastSpawnTime3 - System.currentTimeMillis())/1000 == randSpawnRottenAppleTime3){
            addRottenApples3();
            rottenApples3.add(currentRottenAppleCoordinates[2]);
            lastSpawnTime3 = System.currentTimeMillis();
            randSpawnRottenAppleTime3 = random.nextInt(15)+1;
           //System.out.println("random spawn time3:" + randSpawnRottenAppleTime3);
        }
    }

    private void disappearRottenApple(){
        if(rottenApples.size() == 1 && disappearRotten){ //for initial dissappear process apple1.
            randDissappearRottenAppleTime = random.nextInt(9) + 1;
            //System.out.println("random dissappear time:" + randDissappearRottenAppleTime);
            lastDissappearTime = System.currentTimeMillis();
            disappearRotten=false;

        }

        if(rottenApples2.size() == 1 && disappearRotten2){ //for initial dissappear process apple2.
            randDissappearRottenAppleTime2 = random.nextInt(9) + 1;
            //System.out.println("random dissappear time2:" + randDissappearRottenAppleTime2);
            lastDissappearTime2 = System.currentTimeMillis();
            disappearRotten2=false;
        }

        if(rottenApples3.size() == 1 && disappearRotten3){ //for initial dissappear process apple3.
            randDissappearRottenAppleTime3 = random.nextInt(10) + 1;
            //System.out.println("random dissappear time3:" + randDissappearRottenAppleTime3);
            lastDissappearTime3 = System.currentTimeMillis();
            disappearRotten3=false;
        }


        if(Math.abs(lastDissappearTime - System.currentTimeMillis())/1000 == randDissappearRottenAppleTime){//apple1
            if(!rottenApples.isEmpty()){
                board.remove(rottenApples.get(0));
                design.removePawn(rottenApples.get(0));
                
                rottenApples.remove(0);
                randDissappearRottenAppleTime = random.nextInt(9) + 1;
                //System.out.println("random dissappear time:" + randDissappearRottenAppleTime);
                lastDissappearTime = System.currentTimeMillis();
                disappearRotten = true;
            
            }
        }

        if(Math.abs(lastDissappearTime2 - System.currentTimeMillis())/1000 == randDissappearRottenAppleTime2){//apple2
            if(!rottenApples2.isEmpty()){
                board.remove(rottenApples2.get(0));
                design.removePawn(rottenApples2.get(0));
                
                rottenApples2.remove(0);
                randDissappearRottenAppleTime2 = random.nextInt(9)+1;
                //System.out.println("random dissappear time TWO:" + randDissappearRottenAppleTime2);
                lastDissappearTime2 = System.currentTimeMillis();
                disappearRotten2 = true;
            
            }
        }

        if(Math.abs(lastDissappearTime3 - System.currentTimeMillis())/1000 == randDissappearRottenAppleTime3){//apple3
            if(!rottenApples3.isEmpty()){
                board.remove(rottenApples3.get(0));
                design.removePawn(rottenApples3.get(0));
                
                rottenApples3.remove(0);
                randDissappearRottenAppleTime3 = random.nextInt(10) + 1;
                //System.out.println("random dissappear time THREE:" + randDissappearRottenAppleTime3);
                lastDissappearTime3 = System.currentTimeMillis();
                disappearRotten3 = true;       
            }
        }
    }

    private void moveSnakeBody() {
        switch(direction) {
            case 1: moveSnakeBodyHandler(new Coordinates(snakeHeadCoordinates.getX(), snakeHeadCoordinates.getY() + 1)); break;
            case 2: moveSnakeBodyHandler(new Coordinates(snakeHeadCoordinates.getX(), snakeHeadCoordinates.getY() - 1)); break;
            case 3: moveSnakeBodyHandler(new Coordinates(snakeHeadCoordinates.getX() + 1, snakeHeadCoordinates.getY())); break;
            case 4: moveSnakeBodyHandler(new Coordinates(snakeHeadCoordinates.getX() - 1, snakeHeadCoordinates.getY())); break;
        }
    }

    private void moveSnakeBodyHandler(Coordinates coordinates) {
        if(tailLength == snakeTail.size()) {
            Coordinates endTail = snakeTail.get(0);
            board.remove(endTail);
            snakeTail.remove(endTail);
        }

        board.put(coordinates, snakeBodyClass);
        snakeTail.add(coordinates);
    }

    private void addEat() { 
        Coordinates foodCoordinates;

        do {
            foodCoordinates = new Coordinates(random.nextInt(21), random.nextInt(21));
        } while(isFieldNotNull(foodCoordinates));

        board.put(foodCoordinates, foodClass);
    }

    private void addRottenApples(){ 
        Coordinates rottenAppleCoordinates;

        do{
            rottenAppleCoordinates = new Coordinates(random.nextInt(21),random.nextInt(21));
            currentRottenAppleCoordinates[0] = rottenAppleCoordinates;
        }while(isFieldNotNull(rottenAppleCoordinates));

        board.put(rottenAppleCoordinates, rottenAppleClass);
    }

    private void addRottenApples2(){ 
        Coordinates rottenAppleCoordinates2;

        do{
            rottenAppleCoordinates2 = new Coordinates(random.nextInt(21),random.nextInt(21));
            currentRottenAppleCoordinates[1] = rottenAppleCoordinates2;
        }while(isFieldNotNull(rottenAppleCoordinates2));

        board.put(rottenAppleCoordinates2, rottenAppleClass2);
    }

    private void addRottenApples3(){ 
        Coordinates rottenAppleCoordinates3;

        do{
            rottenAppleCoordinates3 = new Coordinates(random.nextInt(21),random.nextInt(21));
           currentRottenAppleCoordinates[2] = rottenAppleCoordinates3;
        }while(isFieldNotNull(rottenAppleCoordinates3));

        board.put(rottenAppleCoordinates3, rottenAppleClass3);
    }

    private void clearRottenApples(){ //when rotten apples(blocks) appear at the same time more than the value of objectLimit , board will be cleared when the (objectLimit + 1)th rotten apple spawns.
        int sum = rottenApples.size() + rottenApples2.size() + rottenApples3.size();
        if(sum > objectLimit){
            for(int i = 0; i < rottenApples.size(); i++){
                board.remove(rottenApples.get(i));
                design.removePawn(rottenApples.get(i));    
            }
            lastDissappearTime = System.currentTimeMillis();
            disappearRotten = true;
            rottenApples.clear();

            for(int i = 0; i < rottenApples2.size(); i++){
                board.remove(rottenApples2.get(i));
                design.removePawn(rottenApples2.get(i));    
            }
            lastDissappearTime2 = System.currentTimeMillis();
            disappearRotten2 = true;
            rottenApples2.clear();

            for(int i = 0; i < rottenApples3.size(); i++){
                board.remove(rottenApples3.get(i));
                design.removePawn(rottenApples3.get(i));    
            }
            lastDissappearTime3 = System.currentTimeMillis();
            disappearRotten3 = true;
            rottenApples3.clear();
        }
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
                if(!isEndGame) {
                    spawnRottenApple();
                    disappearRottenApple();
                    clearRottenApples();                   
                    checkMap();
                    mapTask();
                   
                }
            }
        });

        new Thread(task).start();
    }

    public void readKeyboard(KeyEvent event) {
        switch(event.getCode()) {
            case W: changeDirection(1); break;
            case S: changeDirection(2); break;
            case A: changeDirection(3); break;
            case D: changeDirection(4); break;

            case UP: changeDirection(1); break;
            case DOWN: changeDirection(2); break;
            case LEFT: changeDirection(3); break;
            case RIGHT: changeDirection(4); break;
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
