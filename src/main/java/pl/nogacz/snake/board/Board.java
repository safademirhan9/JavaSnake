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
import pl.nogacz.snake.application.Resources;
import pl.nogacz.snake.Snake;
import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import javax.imageio.*;
import javafx.scene.image.Image;



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
        //board = new HashMap<Coordinates, PawnClass>();
        
    }

    private void addStartEntity() {
       if(isNewGame){
            board.remove(snakeHeadCoordinates);
            snakeHeadCoordinates = new Coordinates(10, 10);
        }
       
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
                   Menu();
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
            case ESCAPE : Menu(); break;

        }
    }

    private void Menu(){
        System.out.println();
        paused = true;
        JFrame frame = new JFrame("MENU");       
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        /*img = ImageIO.read(getClass().getResourceAsStream(IMG_PATH));*/
         
        // Set the panel to add buttons
        JPanel panel = new JPanel();/* {  
            public void paintComponent(Graphics g) {  
                try{
                 Image img = new Image(pathComponent("background.jpg"));  
                    g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);  
                    /*final Canvas canvas = new Canvas(W * NUM_IMGS, H);
                    final GraphicsContext gc = canvas.getGraphicsContext2D();
                    gc.setFill(Color.GOLD);
                    gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                }catch(Exception e){

                }
            }  
        }; */

        panel.setLayout(new GridLayout(0,1));
           
        // Set the BoxLayout to be X_AXIS: from left to right
        BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.Y_AXIS);

        panel.setLayout(boxlayout);
         
        // Set border for the panel
        panel.setBorder(new EmptyBorder(new Insets(50, 100, 50, 100)));   
       
        // Define new buttons and text fields
        JLabel tx1 = new JLabel("Menu");
        JLabel point = new JLabel("Your Score is " + tailLength);
        JButton resumeButton = new JButton("RESUME");
        JButton newGameButton = new JButton("NEW GAME");        
        JButton settingsButton = new JButton("CHANGE SETTINGS");
        JButton exitButton = new JButton("EXIT");

        tx1.setFont(new Font("Serif", Font.PLAIN, 30));
        point.setFont(new Font("Serif", Font.PLAIN, 20));
        
        resumeButton.setPreferredSize(new Dimension(300, 30));
        newGameButton.setPreferredSize(new Dimension(300, 30));
        settingsButton.setPreferredSize(new Dimension(300, 30));
        exitButton.setPreferredSize(new Dimension(300, 30));
        
        resumeButton.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e){  
                       paused = false;
        	           frame.setVisible(false);  
        	}  
        	}); 
        
        newGameButton.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e){  
                        paused = false;
                        frame.setVisible(false);
                        restartApplication();
                        /*EndGame end = new EndGame("end");
                        end.newGame();
                        /*Snake s = new Snake();
                        s.newGame(); */ 
        	}  
            }); 
        
        settingsButton.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e){  
        	}  
        	}); 
        
        exitButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e){  
 	           System.exit(0);;  
        	}  
        });

        tx1.setIcon(new ImageIcon(pathComponent("FOOD.png")));
        tx1.setIconTextGap(3);
        tx1.setHorizontalAlignment(SwingConstants.LEFT);
        tx1.setAlignmentX(Component.CENTER_ALIGNMENT);

        point.setIcon(new ImageIcon(pathComponent("SNAKE_HEAD_UP.png")));
        point.setAlignmentX(Component.CENTER_ALIGNMENT);

        resumeButton.setIcon(new ImageIcon(pathComponent("SNAKE_BODY.png")));
        resumeButton.setIconTextGap(3);
        resumeButton.setHorizontalAlignment(SwingConstants.LEFT);
        resumeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        newGameButton.setIcon(new ImageIcon(pathComponent("SNAKE_BODY.png")));
        newGameButton.setIconTextGap(3);
        newGameButton.setHorizontalAlignment(SwingConstants.LEFT);
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        settingsButton.setIcon(new ImageIcon(pathComponent("SNAKE_BODY.png")));
        settingsButton.setIconTextGap(3);
        settingsButton.setHorizontalAlignment(SwingConstants.LEFT);
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        exitButton.setIcon(new ImageIcon(pathComponent("SNAKE_BODY.png")));
        exitButton.setIconTextGap(3);
        exitButton.setHorizontalAlignment(SwingConstants.LEFT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
               
        // Add buttons to the frame (and spaces between buttons)
        panel.add(tx1);
        panel.add(Box.createRigidArea(new Dimension(0, 60))); 
        panel.add(point);
        panel.add(Box.createRigidArea(new Dimension(0, 60)));     
        if(!isEndGame){
        panel.add(resumeButton);     
        panel.add(Box.createRigidArea(new Dimension(0, 60)));
        }     
        panel.add(newGameButton);
        panel.add(Box.createRigidArea(new Dimension(0, 60)));
        panel.add(settingsButton);
        panel.add(Box.createRigidArea(new Dimension(0, 60)));
        panel.add(exitButton);
        
        // Set size for the frame
        frame.setSize(600, 700);
        
         panel.setVisible(true);
        // Set the window to be visible as the default to be false
        frame.add(panel);
        frame.validate();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
     }

     public void restartApplication()
    {       System.out.println("deneme-----------------------------------------------");
        try {
            final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            System.out.println("javaBin    "+javaBin);
            final File currentJar = new File(Snake.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            /* is it a jar file? */
            if(!currentJar.getName().endsWith(".jar"))
                return;

            /* Build command: java -jar application.jar */
            final ArrayList<String> command = new ArrayList<String>();
            command.add(javaBin);
            command.add("-jar");
            command.add(currentJar.getPath());

            final ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
            System.exit(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
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

    //remove file:/from path
    public String pathComponent(String filename) {
        String path = Resources.getPath(filename);
        return path.substring(path.indexOf("/")+1);
    }

    public static int getDirection() {
        return direction;
    }
  
}
