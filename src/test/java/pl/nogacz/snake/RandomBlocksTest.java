package pl.nogacz.snake;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import pl.nogacz.snake.board.Board;
import pl.nogacz.snake.board.Coordinates;
import pl.nogacz.snake.application.Design;


public class RandomBlocksTest {
    
    @Test
    void testSpawnRottenApple1(){

        Design design = mock(Design.class);
        Board b = new Board(design);

        b.lastSpawnTimesOfApples[0] = System.currentTimeMillis();
        b.newRandomSpawnTimesOfApples[0] = 0;
        b.spawnRottenApple();
        
        b.lastSpawnTimesOfApples[0] = System.currentTimeMillis();
        b.newRandomSpawnTimesOfApples[0] = 0;
        b.spawnRottenApple();
        
        b.lastSpawnTimesOfApples[0] = System.currentTimeMillis();
        b.newRandomSpawnTimesOfApples[0] = 0;
        b.spawnRottenApple();
        
        assertEquals(3, b.rottenApples.size());
        
    }

    @Test
    void testSpawnRottenApple2(){

        Design design = mock(Design.class);
        Board b = new Board(design);

        b.lastSpawnTimesOfApples[1] = System.currentTimeMillis();
        b.newRandomSpawnTimesOfApples[1] = 0;
        b.spawnRottenApple();
        
        b.lastSpawnTimesOfApples[1] = System.currentTimeMillis();
        b.newRandomSpawnTimesOfApples[1] = 0;
        b.spawnRottenApple();
        
        assertEquals(2, b.rottenApples2.size());
    }

    @Test
    void testSpawnRottenApple3(){

        Design design = mock(Design.class);
        Board b = new Board(design);

        b.lastSpawnTimesOfApples[2] = System.currentTimeMillis();
        b.newRandomSpawnTimesOfApples[2] = 0;
        b.spawnRottenApple();

        assertEquals(1, b.rottenApples3.size());
    }


    @Test
    void testDissappearRottenApple1(){
        
        Design design = mock(Design.class);
        Board b = new Board(design);

        b.lastSpawnTimesOfApples[0] = System.currentTimeMillis();
        b.newRandomSpawnTimesOfApples[0] = 0;
        b.spawnRottenApple();
        
        b.lastSpawnTimesOfApples[0] = System.currentTimeMillis();
        b.newRandomSpawnTimesOfApples[0] = 0;
        b.spawnRottenApple();
        
        b.lastSpawnTimesOfApples[0] = System.currentTimeMillis();
        b.newRandomSpawnTimesOfApples[0] = 0;
        b.spawnRottenApple();

        b.lastDissappearTimesOfApples[0] = System.currentTimeMillis();
        b.newRandomDissappearTimesOfApples[0] = 0;
        b.disappearRottenApple();

        assertEquals(2, b.rottenApples.size());
    }

    @Test
    void testDissappearRottenApple2(){
        
        Design design = mock(Design.class);
        Board b = new Board(design);

        b.lastSpawnTimesOfApples[1] = System.currentTimeMillis();
        b.newRandomSpawnTimesOfApples[1] = 0;
        b.spawnRottenApple();
        
        b.lastSpawnTimesOfApples[1] = System.currentTimeMillis();
        b.newRandomSpawnTimesOfApples[1] = 0;
        b.spawnRottenApple();
        
        b.lastSpawnTimesOfApples[1] = System.currentTimeMillis();
        b.newRandomSpawnTimesOfApples[1] = 0;
        b.spawnRottenApple();

        b.lastDissappearTimesOfApples[1] = System.currentTimeMillis();
        b.newRandomDissappearTimesOfApples[1] = 0;
        b.disappearRottenApple();

        b.lastDissappearTimesOfApples[1] = System.currentTimeMillis();
        b.newRandomDissappearTimesOfApples[1] = 0;
        b.disappearRottenApple();

        assertEquals(1, b.rottenApples2.size());
    }

    @Test
    void testDissappearRottenApple3(){
        
        Design design = mock(Design.class);
        Board b = new Board(design);

        b.lastSpawnTimesOfApples[2] = System.currentTimeMillis();
        b.newRandomSpawnTimesOfApples[2] = 0;
        b.spawnRottenApple();

        b.lastDissappearTimesOfApples[2] = System.currentTimeMillis();
        b.newRandomDissappearTimesOfApples[2] = 0;
        b.disappearRottenApple();

        assertEquals(0, b.rottenApples.size());
    }

    @Test
    void testClearRottenApples(){
        Design design = mock(Design.class);
        Board b = new Board(design);
        
        Coordinates testCoordinates = new Coordinates(10, 10);
        
        b.rottenApples.add(testCoordinates);
        b.rottenApples.add(testCoordinates);
        b.rottenApples.add(testCoordinates);

        b.rottenApples2.add(testCoordinates);
        b.rottenApples2.add(testCoordinates);
        b.rottenApples2.add(testCoordinates);
        
        b.rottenApples3.add(testCoordinates);
 
        b.clearRottenApples();

        assertEquals(b.rottenApples.size(),0);
        assertEquals(b.rottenApples2.size(),0);
        assertEquals(b.rottenApples3.size(),0);
    
    }
}