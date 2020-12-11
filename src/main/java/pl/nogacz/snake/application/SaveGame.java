package pl.nogacz.snake.application;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;


import pl.nogacz.snake.board.BoardInfo;

public class SaveGame {

    private JFrame frame;
    private JTextField input;
    private JButton confirm;
    private BoardInfo saveBoard;

    public SaveGame(BoardInfo saveBoard) {
        this.saveBoard = saveBoard;

        printSaveOption();
    }

    public void printSaveOption() {

        frame = new JFrame("Saving the game");

        JPanel panel = new JPanel();

        JLabel label = new JLabel("SAVE GAME:");

        confirm = new JButton("Confirm");

        input = new JTextField("Enter the destination adress here");

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
            public void actionPerformed(ActionEvent e) {

                String adress = input.getText();

                if (adress.equals(""))
                    input.setText("Please enter a destination adress");

                else
                    startSave();

            }
        });

        frame.setSize(500, 125);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent we) {
                String ObjButtons[] = { "Yes", "No" };
                int PromptResult = JOptionPane.showOptionDialog(null,
                        "Are you sure you want to exit? Your game will not be saved", "JavaSnake",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
                if (PromptResult == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }

            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowIconified(java.awt.event.WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowDeiconified(java.awt.event.WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowActivated(java.awt.event.WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowDeactivated(java.awt.event.WindowEvent e) {
                // TODO Auto-generated method stub

            }

        });

        input.setPreferredSize(new Dimension(400, 25));
        frame.toFront();

        panel.setPreferredSize(new Dimension(400, 400));
        ;

        panel.add(label);
        panel.add(input);
        panel.add(confirm);

        frame.add(panel);

        frame.setVisible(true);
    }

    public void startSave() {

        FileOutputStream fo;
        ObjectOutputStream out;       

        try {
            fo = new FileOutputStream(input.getText());
            out = new ObjectOutputStream(fo);
            out.writeObject(saveBoard);
            
            fo.close();
            out.close();

        } catch (IOException e) {
            input.setText("Please enter a valid destination.");
            e.printStackTrace();
        }

        frame.dispose();
        System.exit(0);

    }
}