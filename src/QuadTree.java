import java.awt.Color;
import java.awt.image.BufferedImage;

public class QuadTree {
    private QuadTreeNode root;
    private ErrorCalculator calculator;
    private double threshold;
    private int minBlockSize;
    private int nodeCount = 0;
    private int maxDepth = 0;

    public QuadTree(BufferedImage image, ErrorCalculator calculator, double threshold, int minBlockSize) {
        this.calculator = calculator;
        this.threshold = threshold;
        this.minBlockSize = minBlockSize;
        this.root = build(image, 0, 0, image.getWidth(), image.getHeight(), 0);
    }

    private QuadTreeNode build(BufferedImage image, int x, int y, int width, int height, int depth) {
        nodeCount++;
        maxDepth = Math.max(maxDepth, depth);

        QuadTreeNode node = new QuadTreeNode(x, y, width, height);
        if (width <= minBlockSize || height <= minBlockSize || calculator.calculateError(image, x, y, width, height) <= threshold) {
            node.setAsLeaf(averageColor(image, x, y, width, height));
            return node;
        }

        int w2 = width / 2;
        int h2 = height / 2;

        node.children[0] = build(image, x, y, w2, h2, depth + 1);
        node.children[1] = build(image, x + w2, y, width - w2, h2, depth + 1);
        node.children[2] = build(image, x, y + h2, w2, height - h2, depth + 1);
        node.children[3] = build(image, x + w2, y + h2, width - w2, height - h2, depth + 1);

        return node;
    }

    private Color averageColor(BufferedImage image, int x, int y, int width, int height) {
        long sumR = 0, sumG = 0, sumB = 0;
        int count = width * height;
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                Color c = new Color(image.getRGB(i, j));
                sumR += c.getRed();
                sumG += c.getGreen();
                sumB += c.getBlue();
            }
        }
        return new Color((int)(sumR / count), (int)(sumG / count), (int)(sumB / count));
    }

    public BufferedImage reconstruct(int width, int height) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        fillImage(root, result);
        return result;
    }

    private void fillImage(QuadTreeNode node, BufferedImage image) {
        if (node.isLeaf) {
            for (int i = node.x; i < node.x + node.width; i++) {
                for (int j = node.y; j < node.y + node.height; j++) {
                    image.setRGB(i, j, node.averageColor.getRGB());
                }
            }
        } else {
            for (QuadTreeNode child : node.children) {
                fillImage(child, image);
            }
        }
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}