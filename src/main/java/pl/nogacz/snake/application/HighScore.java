package pl.nogacz.snake.application;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

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
        return scores;
    }
}
