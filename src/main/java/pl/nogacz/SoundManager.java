package pl.nogacz.snake.board;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class SoundManager{
    Clip audioClip;
    boolean playCompleted;
    String path;
    public SoundManager(String path){
        this.path = path;
        File audioFile = new File(path);

        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            AudioFormat format = audioStream.getFormat();

            DataLine.Info info = new DataLine.Info(Clip.class, format);

            audioClip = (Clip) AudioSystem.getLine(info);

            audioClip.open(audioStream);

        } catch (Exception ex) {
            System.out.println("Check the error.");
            ex.printStackTrace();
        }
    }
    public void play() {
        audioClip.start();
    }
    public void loop(){
        audioClip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void stop(){
        audioClip.stop();
    }
}