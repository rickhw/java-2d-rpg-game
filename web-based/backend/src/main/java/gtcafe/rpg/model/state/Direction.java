package gtcafe.rpg.model.state;

public enum Direction {
    UP, DOWN, LEFT, RIGHT, ANY;

    public Direction opposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            case ANY -> ANY;
        };
    }
}
