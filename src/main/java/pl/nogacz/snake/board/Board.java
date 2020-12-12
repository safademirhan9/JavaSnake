package pl.nogacz.snake.board;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import pl.nogacz.snake.application.Design;
import pl.nogacz.snake.application.EndGame;
import pl.nogacz.snake.pawn.Pawn;
import pl.nogacz.snake.pawn.PawnClass;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

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
    private int tailLength = 0;

    private Coordinates snakeHeadCoordinates = new Coordinates(10, 10);

    private PawnClass snakeHeadClass = new PawnClass(Pawn.SNAKE_HEAD);
    private PawnClass snakeBodyClass = new PawnClass(Pawn.SNAKE_BODY);
    private PawnClass foodClass = new PawnClass(Pawn.FOOD);

    private ArrayList<Coordinates> snakeTail = new ArrayList<>();

    private JFrame info=new JFrame("Info");
    private JPanel infoPanel=new JPanel();
    private JLabel infoLabel=new JLabel();
    private JButton infoButton=new JButton("OK");
    boolean cancel=true;

    public Board(Design design) {

        this.design = design;
        addStartEntity();
        mapTask();
    }

    public void getMessage(int a,int b){

        info=new JFrame("Info");
        infoPanel=new JPanel();
        infoLabel=new JLabel();
        infoButton=new JButton("OK");

        String operation= (a==0) ? "Save" : "Load";
        String success= (b==0) ? "successful" : "failed";

        if(b==-1)
            success="canceled";
        
        infoLabel.setText(operation+" "+success+". Press OK to resume the game.");

        infoButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {

                info.setVisible(false);
                isPaused=false;
                mapTask();

            }
        });

        
        info.setSize(300,100);
        infoPanel.add(infoLabel);
        infoPanel.add(infoButton);
        info.add(infoPanel);
        info.setLocationRelativeTo(null);
        
        info.setVisible(true);

    }

    private void addStartEntity() {
        board.put(snakeHeadCoordinates, snakeHeadClass);

        for (int i = 0; i < 22; i++) {
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
        for (Map.Entry<Coordinates, PawnClass> entry : board.entrySet()) {
            design.removePawn(entry.getKey());
        }
    }

    private void displayAllImage() {
        for (Map.Entry<Coordinates, PawnClass> entry : board.entrySet()) {
            design.addPawn(entry.getKey(), entry.getValue());
        }
    }

    private void moveSnake() {
        switch (direction) {
            case 1:
                moveSnakeHead(new Coordinates(snakeHeadCoordinates.getX(), snakeHeadCoordinates.getY() - 1));
                break;
            case 2:
                moveSnakeHead(new Coordinates(snakeHeadCoordinates.getX(), snakeHeadCoordinates.getY() + 1));
                break;
            case 3:
                moveSnakeHead(new Coordinates(snakeHeadCoordinates.getX() - 1, snakeHeadCoordinates.getY()));
                break;
            case 4:
                moveSnakeHead(new Coordinates(snakeHeadCoordinates.getX() + 1, snakeHeadCoordinates.getY()));
                break;
        }
    }

    private void moveSnakeHead(Coordinates coordinates) {
        if (coordinates.isValid()) {
            if (isFieldNotNull(coordinates)) {
                if (getPawn(coordinates).getPawn().isFood()) {
                    board.remove(snakeHeadCoordinates);
                    board.put(snakeHeadCoordinates, snakeBodyClass);
                    board.put(coordinates, snakeHeadClass);
                    snakeTail.add(snakeHeadCoordinates);
                    tailLength++;

                    snakeHeadCoordinates = coordinates;

                    addEat();
                } else {
                    isEndGame = true;

                    new EndGame("End game...\n" + "You have " + tailLength + " points. \n" + "Maybe try again? :)");
                }
            } else {
                board.remove(snakeHeadCoordinates);
                board.put(coordinates, snakeHeadClass);

                snakeHeadCoordinates = coordinates;

                if (tailLength > 0) {
                    moveSnakeBody();
                }
            }
        }
    }

    private void moveSnakeBody() {
        switch (direction) {
            case 1:
                moveSnakeBodyHandler(new Coordinates(snakeHeadCoordinates.getX(), snakeHeadCoordinates.getY() + 1));
                break;
            case 2:
                moveSnakeBodyHandler(new Coordinates(snakeHeadCoordinates.getX(), snakeHeadCoordinates.getY() - 1));
                break;
            case 3:
                moveSnakeBodyHandler(new Coordinates(snakeHeadCoordinates.getX() + 1, snakeHeadCoordinates.getY()));
                break;
            case 4:
                moveSnakeBodyHandler(new Coordinates(snakeHeadCoordinates.getX() - 1, snakeHeadCoordinates.getY()));
                break;
        }
    }

    private void moveSnakeBodyHandler(Coordinates coordinates) {
        if (tailLength == snakeTail.size()) {
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
        } while (isFieldNotNull(foodCoordinates));

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
                if (!isEndGame && !isPaused) {
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

            case Q: System.exit(0); break;           

            case T:

                cancel=true;
                isPaused=true;
                BoardInfo BI=new BoardInfo(board, direction, tailLength, snakeHeadCoordinates, snakeHeadClass, snakeBodyClass, foodClass, snakeTail);
                
                JFrame saveFrame = new JFrame("Saving the game");

                JPanel savePanel = new JPanel();

                JLabel saveLabel = new JLabel("SAVE GAME:");

                JButton saveConfirm = new JButton("Confirm");

                JTextField saveInput = new JTextField("Enter the destination adress here");

                saveInput.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        saveInput.setText("");
                    }

                    @Override
                    public void mousePressed(java.awt.event.MouseEvent e) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent e) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        // TODO Auto-generated method stub
                    }
                });

                saveConfirm.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {

                        String adress = saveInput.getText();

                        if (adress.equals(""))
                            saveInput.setText("Please enter a destination adress");

                        else{
                            
                            cancel=false;

                            if(startSave(adress,BI)){

                                getMessage(0, 0);                              

                            }

                            else
                                getMessage(0, 1);

                                saveFrame.dispose();
                        }
                            

                    }
                });

                saveFrame.setSize(500, 125);
                saveFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                saveFrame.addWindowListener(new WindowListener() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent we) {
                    
                }

				@Override
				public void windowOpened(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowClosed(WindowEvent e) {
                    
                    if(cancel)
                        getMessage(0, -1);
				}

				@Override
				public void windowIconified(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowDeiconified(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowActivated(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowDeactivated(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}

                });

                saveInput.setPreferredSize(new Dimension(400, 25));
                saveFrame.toFront();

                savePanel.setPreferredSize(new Dimension(400, 400)); 

                savePanel.add(saveLabel);
                savePanel.add(saveInput);
                savePanel.add(saveConfirm);
                
                saveFrame.add(savePanel);

                saveFrame.setVisible(true);

                break;
            
            case L:
                
                removeAllImage();
                cancel=true;
                isPaused=true;

                JFrame frame = new JFrame("Loading the game");

                JPanel panel = new JPanel();

                JLabel label = new JLabel("LOAD GAME:");

                JButton confirm = new JButton("Confirm");

                JTextField input = new JTextField("Enter the destination adress here");

                input.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        input.setText("");
                    }

                    @Override
                    public void mousePressed(java.awt.event.MouseEvent e) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent e) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        // TODO Auto-generated method stub
                    }
                });

                confirm.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {

                        String adress = input.getText();

                        if (adress.equals(""))
                            input.setText("Please enter a destination adress");

                        else
                            cancel=false;

                            if(startLoad(adress)){

                                getMessage(1, 0);                              

                            }

                            else
                                getMessage(1, 1);

                                frame.dispose();

                    }
                });

                frame.setSize(500, 125);
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.addWindowListener(new WindowListener() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent we) {
                    
                }

				@Override
				public void windowOpened(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowClosed(WindowEvent e) {
                    if(cancel)
                        getMessage(1, -1);
					
				}

				@Override
				public void windowIconified(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowDeiconified(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowActivated(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowDeactivated(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}

                });

                input.setPreferredSize(new Dimension(400, 25));
                frame.toFront();

                panel.setPreferredSize(new Dimension(400, 400)); 

                panel.add(label);
                panel.add(input);
                panel.add(confirm);
                
                frame.add(panel);

                frame.setVisible(true);

                break;

            default:
                    break;
        }
    }

    private boolean startLoad(String adress){

        FileInputStream fi;
        ObjectInputStream in;

        try {

            fi=new FileInputStream(adress);
            in=new ObjectInputStream(fi);

            BoardInfo BI=(BoardInfo)in.readObject();

            in.close();
            fi.close();

            this.board=BI.getBoard();
            direction=BI.getDirection();
            this.tailLength=BI.getTailLength();
            this.snakeHeadCoordinates=BI.getHeadCoordinates();
            this.snakeHeadClass=BI.getHeadClass();
            this.snakeBodyClass=BI.getBodyClass();
            this.foodClass=BI.getFoodClass();
            this.snakeTail=BI.getSnakeTail();
            
        } catch (Exception e) {
            return false;
        }

        return true;

    }

    private boolean startSave(String adress,BoardInfo BI){

        FileOutputStream fo;
        ObjectOutputStream out;

            try {
                fo = new FileOutputStream(adress);
                out = new ObjectOutputStream(fo);
                out.writeObject(BI);
                
                fo.close();
                out.close();

            } catch (IOException e) {
                return false;
            }

        

        return true;

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
