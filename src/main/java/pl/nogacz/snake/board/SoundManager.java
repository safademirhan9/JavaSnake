//source: https://stackoverflow.com/questions/35109539/adding-sound-to-game-help-needed
package pl.nogacz.snake.board;
import javax.sound.sampled.*;
import java.io.File;

public class SoundManager{
    Clip audioClip;
    String path;
    public SoundManager(String path) throws Exception{
        this.path = path;
        File audioFile = new File(path);
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);
        } catch (Exception ex) {
            throw new Exception();
        }
    }
    public void play(){
        audioClip.start();
    }
    public void loop(){
        audioClip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void stop(){
        audioClip.stop();
    }
}
