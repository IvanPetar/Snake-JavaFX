package hr.algebra.snakejavafx.model;

public enum SnakeMoveTag {
    SNAKE_MOVES("SnakeMoves"),
    MOVE("Move"),
    DIRECTION("Direction"),
    POSITION_X("PositionX"),
    POSITION_Y("PositionY");

    private final String tagName;

    SnakeMoveTag(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }
}