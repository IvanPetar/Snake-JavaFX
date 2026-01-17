package hr.algebra.snakejavafx.model;

import java.io.Serializable;
import java.util.Objects;

public class Position implements Serializable {
    private final int col;
    private final int row;

    public Position(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public int col() {
        return col;
    }

    public int row() {
        return row;
    }

    public Position move(Direction direction) {
        return new Position(
                col + direction.dx(),
                row + direction.dy()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position p)) return false;
        return col == p.col && row == p.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(col, row);
    }
}
