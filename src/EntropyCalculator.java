import java.awt.Color;
import java.awt.image.BufferedImage;

public class EntropyCalculator implements ErrorCalculator {
    @Override
    public double calculateError(BufferedImage image, int x, int y, int width, int height) {
        int[] histogram = new int[256];
        int count = width * height * 3;

        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                Color c = new Color(image.getRGB(i, j));
                histogram[c.getRed()]++;
                histogram[c.getGreen()]++;
                histogram[c.getBlue()]++;
            }
        }

        double entropy = 0.0;
        for (int freq : histogram) {
            if (freq > 0) {
                double p = freq / (double) count;
                entropy -= p * Math.log(p) / Math.log(2);
            }
        }

        return entropy;
    }
}

