package pl.nogacz.snake.board;

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
import java.awt.Dimension;
import javax.swing.ImageIcon;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.swing.border.EmptyBorder;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.nogacz.snake.application.Resources;
import pl.nogacz.snake.application.EndGame;

/**
 * @author Dawid Nogacz on 19.05.2019
 */
public class Board {
    private HashMap<Coordinates, PawnClass> board = new HashMap<>();
    private Design design;
    private Random random = new Random();

    private boolean isEndGame = false;
    private boolean isNewGame = false;
    //added
    private volatile boolean   paused = false;

    private static int direction = 1; // 1 - UP || 2 - BOTTOM || 3 - LEFT || 4 - RIGHT
    private int tailLength = 0;

    private Coordinates snakeHeadCoordinates = new Coordinates(10, 10);

    private PawnClass snakeHeadClass = new PawnClass(Pawn.SNAKE_HEAD);
    private PawnClass snakeBodyClass = new PawnClass(Pawn.SNAKE_BODY);
    private PawnClass foodClass = new PawnClass(Pawn.FOOD);

    private ArrayList<Coordinates> snakeTail = new ArrayList<>();

    public Board(Design design) {
        this.design = design;

        addStartEntity();
        mapTask();
    }

    private void newGame(){
        isNewGame = true;
        isEndGame = false;
        setDefault();
        addStartEntity();
        mapTask();
     }

    private void setDefault(){
        design = new Design();
        snakeHeadCoordinates = new Coordinates(10, 10);
        isEndGame = false;
        paused = false;
        direction = 1;
        tailLength = 0;
        snakeTail = new ArrayList<Coordinates>();
        board = new HashMap<Coordinates, PawnClass>();
        
    }

    private void addStartEntity() {
       /*if(isNewGame){
            board.remove(snakeHeadCoordinates);
            //board.put(snakeHeadCoordinates, snakeHeadClass);
        }
       else*/
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
            case 1: moveSnakeHead(new Coordinates(snakeHeadCoordinates.getX(), snakeHeadCoordinates.getY() - 1)); break;
            case 2: moveSnakeHead(new Coordinates(snakeHeadCoordinates.getX(), snakeHeadCoordinates.getY() + 1)); break;
            case 3: moveSnakeHead(new Coordinates(snakeHeadCoordinates.getX() - 1, snakeHeadCoordinates.getY())); break;
            case 4: moveSnakeHead(new Coordinates(snakeHeadCoordinates.getX() + 1, snakeHeadCoordinates.getY())); break;
        }
    }

    private void moveSnakeHead(Coordinates coordinates) {
        if(coordinates.isValid()) {
            if(isFieldNotNull(coordinates)) {
                if(getPawn(coordinates).getPawn().isFood()) {
                    board.remove(snakeHeadCoordinates);
                    board.put(snakeHeadCoordinates, snakeBodyClass);
                    board.put(coordinates, snakeHeadClass);
                    snakeTail.add(snakeHeadCoordinates);
                    tailLength++;

                    snakeHeadCoordinates = coordinates;

                    addEat();
                } else {
                    isEndGame = true;
                    //when game ends new manu is popped
                   PopupMenu();
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
                if(!isEndGame && !paused) {
                    checkMap();
                    mapTask();
                }
                if(paused){
                   System.out.println("paused");
                    while(paused){
                    try{
                    Thread.sleep(100);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
    
                    }
                    System.out.println("NOT paused");
                    
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
            case ESCAPE : PopupMenu(); break;

        }
    }

    private void PopupMenu(){
        paused = true;
        // Create and set up a frame window
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("MENU");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
         
        // Set the panel to add buttons
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0,1));
        //JLabel backImage =new JLabel(new ImageIcon("C:/Users/baydi/eclipse-workspace/deneme/src/resources/background.jpg"));
        //backImage.setOpaque(false);
        //backImage.setSize(600, 600);
      
        // Set the BoxLayout to be X_AXIS: from left to right
        BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
         
        // Set the Boxayout to be Y_AXIS from top to down
        //BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
 
        panel.setLayout(boxlayout);
         
        // Set border for the panel
        //panel.setBorder(new EmptyBorder(new Insets(50, 50, 50, 50)));
        panel.setBorder(new EmptyBorder(new Insets(50, 100, 50, 100)));   
       
        
        // Define new buttons and text fields
        JLabel tx1 = new JLabel("Menu");
        JLabel point = new JLabel("Your Score is " + tailLength);
        JButton jb1 = new JButton("RESUME");
        JButton jb2 = new JButton("NEW GAME");
        JButton jb3 = new JButton("CHANGE SETTINGS");
        JButton jb4 = new JButton("EXIT");

        tx1.setFont(new Font("Serif", Font.PLAIN, 30));
        point.setFont(new Font("Serif", Font.PLAIN, 20));

        /*
        tx1.setIcon(new ImageIcon("C:/Users/baydi/481 projeler/JavaSnake/build/resources/main/SNAKE_HEAD_UP.jpg"));
        System.out.println(Resources.getPath("background.jpg"));
        //jb1.setIcon(new ImageIcon("C:\Users\baydi\481 projeler\JavaSnake\src\main\resources\BRICK.png"));
        jb2.setIcon(new ImageIcon("C:/Users/baydi/eclipse-workspace/deneme/src/resources/SNAKE_BODY.png"));
        jb3.setIcon(new ImageIcon("C:/Users/baydi/eclipse-workspace/deneme/src/resources/SNAKE_BODY.png"));
        jb4.setIcon(new ImageIcon("C:/Users/baydi/eclipse-workspace/deneme/src/resources/SNAKE_BODY.png"));*/
        
        jb1.setPreferredSize(new Dimension(300, 30));
        jb2.setPreferredSize(new Dimension(300, 30));
        jb3.setPreferredSize(new Dimension(300, 30));
        jb4.setPreferredSize(new Dimension(300, 30));
        
        jb1.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e){  
                       paused = false;
        	           frame.setVisible(false);  
        	}  
        	}); 
        
        jb2.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e){  
                        paused = false;
                        frame.setVisible(false);
                        newGame();    
        	}  
        	}); 
        
        jb3.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e){  
        	}  
        	}); 
        
        jb4.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e){  
 	           System.exit(0);;  
        	}  
        });
        
        /*jb1.setIcon(new ImageIcon("C:/Users/baydi/eclipse-workspace/deneme/src/resources/SNAKE_BODY.png"));
        jb1.setIconTextGap(3);
        jb1.setHorizontalAlignment(SwingConstants.LEFT);*/
         tx1.setAlignmentX(Component.CENTER_ALIGNMENT);
         point.setAlignmentX(Component.CENTER_ALIGNMENT);
         jb1.setAlignmentX(Component.CENTER_ALIGNMENT);
         jb2.setAlignmentX(Component.CENTER_ALIGNMENT);
         jb3.setAlignmentX(Component.CENTER_ALIGNMENT);
         jb4.setAlignmentX(Component.CENTER_ALIGNMENT);

     
        // Add buttons to the frame (and spaces between buttons)
        panel.add(tx1);
        panel.add(Box.createRigidArea(new Dimension(0, 60))); 
        panel.add(point);
        panel.add(Box.createRigidArea(new Dimension(0, 60)));     
        if(!isEndGame){
        panel.add(jb1);     
        panel.add(Box.createRigidArea(new Dimension(0, 60)));
        }     
        panel.add(jb2);
        panel.add(Box.createRigidArea(new Dimension(0, 60)));
        panel.add(jb3);
        panel.add(Box.createRigidArea(new Dimension(0, 60)));
        panel.add(jb4);
        
        //frame.add(backImage);
        // Set size for the frame
        frame.setSize(600, 600);
        //frame.setBackground(bgColor);
        
         panel.setVisible(true);
        // Set the window to be visible as the default to be false
        frame.add(panel);
        frame.validate();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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

    //remove file:/from path
    public String pathComponent(String filename) {
        int i = filename.indexOf("C");
        return (i > -1) ? filename.substring(i) : filename;
    }

    public static int getDirection() {
        return direction;
    }

}
