package pl.nogacz.snake.pawn;

/**
 * @author Dawid Nogacz on 19.05.2019
 */
public enum Pawn {
    FOOD,
    BRICK,
    SNAKE_HEAD,
    SNAKE_BODY,
    ROTTEN_APPLE,
    ROTTEN_APPLE2,
    ROTTEN_APPLE3;

    public boolean isFood() {
        return this == FOOD;
    }

    public boolean isRottenApple(){
        return this == ROTTEN_APPLE;
    }

    public boolean isRottenApple2(){
        return this == ROTTEN_APPLE2;
    }

    public boolean isRottenApple3(){
        return this == ROTTEN_APPLE3;
    }

    public boolean isHead() {
        return this == SNAKE_HEAD;
    }
}
