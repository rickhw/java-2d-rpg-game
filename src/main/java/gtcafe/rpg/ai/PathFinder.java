package gtcafe.rpg.ai;
import gtcafe.rpg.core.GameContext;

import java.util.ArrayList;

import gtcafe.rpg.entity.Entity;

/** 
 * - https://en.wikipedia.org/wiki/A*_search_algorithm
 * - [Step by Step Explanation of A* Pathfinding Algorithm in Java](https://www.youtube.com/watch?v=2JNEme00ZFA)
 */
public class PathFinder {
    GameContext context;
    Node[][] node;
    ArrayList<Node> openList = new ArrayList<>();

    public ArrayList<Node> pathList = new ArrayList<>();
    Node startNode, goalNode, currentNode;
    boolean goalReached = false;
    int step = 0;

    public PathFinder(GameContext context) {
        this.context = context;
        instantiateNodes();
    }

    public void instantiateNodes() {
        node = new Node[context.getMaxWorldCol()][context.getMaxWorldRow()];
        int col = 0;
        int row = 0;

        while(col < context.getMaxWorldCol() && row < context.getMaxWorldRow()) {
            node[col][row] = new Node(col, row);

            col++;
            if (col == context.getMaxWorldCol()) {
                col = 0;
                row++;
            }
        }
    }

    public void resetNodes() {
        int col = 0;
        int row = 0;

        while(col < context.getMaxWorldCol() && row < context.getMaxWorldRow()) {
            // Reset open, checked and solid state
            node[col][row].open = false;
            node[col][row].checked = false;
            node[col][row].solid = false;

            col++;
            if (col == context.getMaxWorldCol()) {
                col = 0;
                row++;
            }
        }

        // Reset other settings
        openList.clear();
        pathList.clear();
        goalReached = false;
        step = 0;
    }

    public void setNodes(int startCol, int startRow, int goalCol, int goalRow, Entity entity) {
        resetNodes();

        // Set start and goal node
        startNode = node[startCol][startRow];
        currentNode = startNode;
        goalNode = node[goalCol][goalRow];
        openList.add(currentNode);

        int col = 0;
        int row = 0;

        while(col < context.getMaxWorldCol() && row < context.getMaxWorldRow()) {
            // SET SOLIDE NODE
            int tileNum = context.getTileManager().mapTileNum[context.getCurrentMap().index][col][row];
            if (context.getTileManager().tiles[tileNum].collision == true) {
                node[col][row].solid = true;
            }

            // SET COST
            getCost(node[col][row]);

            col++;
            if(col == context.getMaxWorldCol()) {
                col = 0;
                row++;
            }
        }

        // CHECK INTERACTIVE TILES
        for (int i=0; i<context.getInteractiveTile()[1].length; i++) {
            if(context.getInteractiveTile()[context.getCurrentMap().index][i] != null && context.getInteractiveTile()[context.getCurrentMap().index][i].destructible == true) {
                int itCol = context.getInteractiveTile()[context.getCurrentMap().index][i].worldX / context.getTileSize();
                int itRow = context.getInteractiveTile()[context.getCurrentMap().index][i].worldY / context.getTileSize();
                node[itCol][itRow].solid = true;
            }
        }

        // CHECK NPC
        for (int i=0; i<context.getNpc()[1].length; i++) {
            if(context.getNpc()[context.getCurrentMap().index][i] != null && context.getNpc()[context.getCurrentMap().index][i] != entity) {
                int itCol = context.getNpc()[context.getCurrentMap().index][i].worldX / context.getTileSize();
                int itRow = context.getNpc()[context.getCurrentMap().index][i].worldY / context.getTileSize();
                node[itCol][itRow].solid = true;
            }
        }

        // CHECK MONSTER
        for (int i=0; i<context.getMonster()[1].length; i++) {
            if(context.getMonster()[context.getCurrentMap().index][i] != null && context.getMonster()[context.getCurrentMap().index][i] != entity) {
                int itCol = context.getMonster()[context.getCurrentMap().index][i].worldX / context.getTileSize();
                int itRow = context.getMonster()[context.getCurrentMap().index][i].worldY / context.getTileSize();
                node[itCol][itRow].solid = true;
            }
        }

        // CHECK PLAYER
        if (context.getPlayer() != entity) {
            int itCol = context.getPlayer().worldX / context.getTileSize();
            int itRow = context.getPlayer().worldY / context.getTileSize();
            node[itCol][itRow].solid = true;
        }
    }

    public void getCost(Node node) {
        // G cost
        int xDistance = Math.abs(node.col - startNode.col);
        int yDistance = Math.abs(node.row - startNode.row);
        node.gCost = xDistance + yDistance;

        // H cost
        xDistance = Math.abs(node.col - goalNode.col);
        yDistance = Math.abs(node.row - goalNode.row);
        node.hCost= xDistance + yDistance;

        // F cost
        node.fCost = node.gCost + node.hCost;
    }

    public boolean search() {

        while(goalReached == false && step < 500) { // limited steps

            int col = currentNode.col;
            int row = currentNode.row;

            // Check the current node
            currentNode.checked = true;
            openList.remove(currentNode);

            // Open the Up node
            if (row - 1 >= 0) {
                openNode(node[col][row-1]);
            }
            // Open the left node
            if (col - 1 >= 0) {
                openNode(node[col-1][row]);
            }
            // Open the down node
            if (row + 1 < context.getMaxWorldRow()) {
                openNode(node[col][row+1]);
            }
            // Open the right node
            if (col + 1 < context.getMaxWorldCol()) {
                openNode(node[col+1][row]);
            }

            // Find the best node
            int bestNodeIndex = 0;
            int bestNodefCost = 999;

            for(int i=0; i<openList.size(); i++) {
                // Check if this node's F cost is better
                if(openList.get(i).fCost < bestNodefCost) {
                    bestNodeIndex = i;
                    bestNodefCost = openList.get(i).fCost;
                }
                // If F cost is equal, check the G cost
                else if (openList.get(i).fCost == bestNodefCost) {
                    if (openList.get(i).gCost < openList.get(bestNodeIndex).gCost) {
                        bestNodeIndex = i;
                    }
                }
            }

            // If there is no node in the openList, end the loop
            if (openList.size() == 0) {
                break;
            }

            // After the loop, openList[bestNodeIndex] is the next step (= currentNode)
            currentNode = openList.get(bestNodeIndex);

            if (currentNode == goalNode) {
                goalReached = true;
                trackThePath();
            }

            step++;
        }

        return goalReached;
    }

    public void openNode(Node node) {

        if (node.open == false && node.checked == false && node.solid == false) {
            node.open = true;
            node.parent = currentNode;
            openList.add(node);
        }
    }

    public void trackThePath() {

        Node current = goalNode;

        while (current != startNode) {
            // Always adding to the first slot, 
            // so the lasst added node is in the [0]
            pathList.add(0, current); // with this pathList, NPC / Monster can track the player
            current = current.parent;
        }
    }
}
