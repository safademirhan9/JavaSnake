package pl.nogacz.snake.application;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javafx.scene.input.MouseEvent;

public class SaveGame {

    private String message;

    public SaveGame(String message) {
        this.message = message;

        printSaveOption();
    }

    public void printSaveOption() {

        JFrame frame = new JFrame("Saving the game");

        JPanel panel = new JPanel();

        JLabel label = new JLabel("SAVE GAME:");

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
            public void actionPerformed(ActionEvent e) {
                
                String adress=input.getText();

                if(adress.equals(""))
                    input.setText("Please enter a destination adress");

            }            
        });

        
        frame.setSize(500,125);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);

        input.setPreferredSize(new Dimension(400,25));

        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true);

        panel.setPreferredSize(new Dimension(400,400));;

        panel.add(label);
        panel.add(input);
        panel.add(confirm);

        frame.add(panel); 
        
        frame.setVisible(true);

        panel.setVisible(true);                

    }

}