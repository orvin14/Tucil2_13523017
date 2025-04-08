import java.awt.Color;
import java.awt.image.BufferedImage;

public class VarianceRGB implements ErrorCalculator {
    public double calculateError(BufferedImage image, int x, int y, int width, int height) {
        long sumR = 0, sumG = 0, sumB = 0;
        int n = width * height;

        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                Color c = new Color(image.getRGB(i, j));
                sumR += c.getRed();
                sumG += c.getGreen();
                sumB += c.getBlue();
            }
        }

        double meanR = sumR / (double)n;
        double meanG = sumG / (double)n;
        double meanB = sumB / (double)n;

        double variance = 0.0;
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                Color c = new Color(image.getRGB(i, j));
                variance += Math.pow(c.getRed() - meanR, 2);
                variance += Math.pow(c.getGreen() - meanG, 2);
                variance += Math.pow(c.getBlue() - meanB, 2);
            }
        }

        return variance / (3 * n);
    }
}