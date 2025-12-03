package gtcafe.rpg.ai;

public class Node {
    Node parent;
    public int col;
    public int row;
    int gCost;  // 從起點 到 目前位置 已經走的實際距離
    int hCost;  // 預估從 目前位置 到 終點 大概的距離
    int fCost;  // F=H+H, 尋找最低的 F 成本，則是最佳解法
    boolean solid;
    boolean open;
    boolean checked;

    public Node(int col, int row) {
        this.col = col;
        this.row = row;
    }
}
