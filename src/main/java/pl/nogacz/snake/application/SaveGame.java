package pl.nogacz.snake.application;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class SaveGame {

    private String message;

    public SaveGame(String message) {
        this.message = message;

        printDialog();
    }

    public void printDialog() {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("JavaSnake");
        alert.setContentText(message);

        ButtonType newGameButton = new ButtonType("New game");
        ButtonType exitButton = new ButtonType("Exit");

        alert.getButtonTypes().setAll(newGameButton, exitButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == newGameButton){
            //newGame();
            System.out.println("TEST");
        } else {
            System.exit(0);
        }
    }

}