package gtcafe.rpg;

public enum Direction {
    UP("up"),
    DOWN("down"),
    RIGHT("right"),
    LEFT("left"),
    ANY("any")
    ;

    String name;
    private Direction(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
