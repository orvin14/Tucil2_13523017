import java.awt.Color;

public class QuadTreeNode {
    int x, y, width, height;
    Color averageColor;
    boolean isLeaf;
    QuadTreeNode[] children;

    public QuadTreeNode(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isLeaf = false;
        this.children = new QuadTreeNode[4];
    }

    public void setAsLeaf(Color avgColor) {
        this.isLeaf = true;
        this.averageColor = avgColor;
        this.children = null;
    }
} 