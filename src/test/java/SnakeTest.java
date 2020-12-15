import org.junit.Assert;
import org.junit.jupiter.api.Test;
import pl.nogacz.snake.board.SoundManager;
public class SnakeTest {
    @Test
    public void TestValidFormat(){
        boolean ex=false;
        try {
            SoundManager s= new SoundManager("sounds/Iceland Theme.mp3");
            s.play();
        } catch (Exception e) {
            ex=true;
        }
        Assert.assertTrue(ex);
    }
    @Test
    public void TestFileNotFound(){
        boolean ex=false;
        try {
            SoundManager s=new SoundManager("sounds/a.wav");
            s.play();
        } catch (Exception e) {
            ex=true;
        }
        Assert.assertTrue(ex);
    }

}
