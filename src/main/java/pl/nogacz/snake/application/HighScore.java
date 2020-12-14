package pl.nogacz.snake.application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

import javafx.scene.control.Alert;

public class HighScore {
    private static final File HIGH_SCORE_FILE = new File(Paths.get(".", "high.scores").toUri()); //text file, name and score per line, tab seperated
    private static final int HIGH_SCORE_COUNT = 10;

    private HighScore() { }

    private static ArrayList<Object[]> readScores() { // returns an ArrayList of [String, Integer]
        if (!HIGH_SCORE_FILE.exists()) {
            try {
                HIGH_SCORE_FILE.createNewFile();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        ArrayList<Object[]> scores = new ArrayList<>(HIGH_SCORE_COUNT);
        try {
            Scanner reader = new Scanner(HIGH_SCORE_FILE);
            while(reader.hasNextLine()) {
                String data = reader.nextLine();
                scores.add(new Object[] {data.split("\t")[0], Integer.valueOf(data.split("\t")[1])});
            }
            reader.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (scores.size() > HIGH_SCORE_COUNT)
            scores = new ArrayList<>(scores.subList(0, HIGH_SCORE_COUNT));
        return truncateScores(scores);
    }

    private static ArrayList<Object[]> truncateScores(ArrayList<Object[]> scores) {
        if (scores.size() > HIGH_SCORE_COUNT)
            scores = new ArrayList<>(scores.subList(0, HIGH_SCORE_COUNT));
        return scores;
    }

    public static boolean isHighScore(int score) {
        return readScores().stream().anyMatch(i -> (int)i[1] < score); //if any i[1](score) is smaller, return true
    }

    public static void writeScore(String name, int score) {
        ArrayList<Object[]> scores = readScores();
        scores.add(new Object[] {name, score});
        scores.sort((o1, o2) -> (int)o2[1] - (int)o1[1]);
        truncateScores(scores);
        try {
            FileWriter writer = new FileWriter(HIGH_SCORE_FILE, false);
            for (Object[] o : scores) {
                writer.write(o[0] + "\t" + o[1] + "\n");
            }
            writer.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void showScores() {
        ArrayList<Object[]> scores = readScores();
        int numberOfDigits = (int)Math.floor(Math.log10((int)scores.get(0)[1])) + 1;

        //create a single string of scores: [<score1>] <name1>\n[<score2>] <name2>\n... etc.
        String stringOfScores = scores.stream().map(x -> String.format("[%0"+numberOfDigits+"d] %s", x[1], x[0])).collect(Collectors.joining("\n"));

        Alert highscores = new Alert(Alert.AlertType.NONE);
        highscores.setTitle("Highscores");
        highscores.setContentText(stringOfScores);
        highscores.showAndWait();
    }
}
