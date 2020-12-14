package pl.nogacz.snake.application;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import pl.nogacz.snake.Snake;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Dawid Nogacz on 19.05.2019
 */
public class EndGame {
    private String message;
    private int score;

    public EndGame(int score) {
        this.message = "End game...\nYou have " + score + " points. \n";
        this.score = score;

        printDialog();
    }

    public void printDialog() {
        if (HighScore.isHighScore(this.score)) {
            highScoreDialog();
        } else {
            endGameDialog();
        }
    }

    public void endGameDialog() {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("JavaChess");
        alert.setContentText(message + "Maybe try again? :)");

        ButtonType newGameButton = new ButtonType("New game");
        ButtonType exitButton = new ButtonType("Exit");

        alert.getButtonTypes().setAll(newGameButton, exitButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == newGameButton){
            newGame();
        } else {
            System.exit(0);
        }
    }

    public void highScoreDialog() {
        TextInputDialog input = new TextInputDialog();
        input.setTitle("JavaChess");
        input.setHeaderText(message + "High Score! \nCongratulations!");
        input.setContentText("Enter your name: ");

        Optional<String> result = input.showAndWait();

        if (result.isPresent()) {
            HighScore.writeScore(result.get(), score);
        }

        endGameDialog();
    }

    public void newGame() {
        restartApplication();
    }

    private void restartApplication()
    {
        try {
            final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            final File currentJar = new File(Snake.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            /* is it a jar file? */
            if(!currentJar.getName().endsWith(".jar"))
                return;

            /* Build command: java -jar application.jar */
            final ArrayList<String> command = new ArrayList<>();
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
}
