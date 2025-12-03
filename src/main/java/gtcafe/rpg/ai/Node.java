package gtcafe.rpg.ai;

public class Node {
    Node parent;
    public int col;
    public int row;
    int gCost;
    int hCost;  // 從目前位置到終點的成本
    int fCost;
    boolean solid;
    boolean open;
    boolean checked;

    public Node(int col, int row) {
        this.col = col;
        this.row = row;
    }
}
