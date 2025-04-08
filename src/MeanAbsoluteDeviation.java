import java.awt.Color;
import java.awt.image.BufferedImage;

public class MeanAbsoluteDeviation implements ErrorCalculator {
    @Override
    public double calculateError(BufferedImage image, int x, int y, int width, int height) {
        int count = width * height;
        long sumR = 0, sumG = 0, sumB = 0;

        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                Color c = new Color(image.getRGB(i, j));
                sumR += c.getRed();
                sumG += c.getGreen();
                sumB += c.getBlue();
            }
        }

        double meanR = sumR / (double) count;
        double meanG = sumG / (double) count;
        double meanB = sumB / (double) count;

        double mad = 0;
        double madR = 0, madG = 0, madB = 0;
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                Color c = new Color(image.getRGB(i, j));
                madR += Math.abs(c.getRed() - meanR);
                madG += Math.abs(c.getGreen() - meanG);
                madB += Math.abs(c.getBlue() - meanB);
            }
        }
        madR /= count;
        madG /= count;
        madB /= count;
        mad = madR + madG + madB; 
        return mad / (3); // Rata-rata MAD per channel
    }
}
