package pl.nogacz.snake.pawn;

/**
 * @author Dawid Nogacz on 19.05.2019
 */
public enum Pawn {
    FRESHFOOD,
    ROTTENFOOD,
    BRICK,
    SNAKE_HEAD,
    SNAKE_BODY;

    public boolean isFreshFood() {
        return this == FRESHFOOD;
    }
    public boolean isRottenFood() {
        return this == ROTTENFOOD;
    }

    public boolean isHead() {
        return this == SNAKE_HEAD;
    }
}
