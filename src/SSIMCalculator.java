import java.awt.Color;
import java.awt.image.BufferedImage;

public class SSIMCalculator implements ErrorCalculator {

    private static final double K1 = 0.01;
    private static final double K2 = 0.03;
    private static final int L = 255;
    private static final double C1 = Math.pow(K1 * L, 2);
    private static final double C2 = Math.pow(K2 * L, 2);

    private static final double wR = 0.299;
    private static final double wG = 0.587;
    private static final double wB = 0.114;

    @Override
    public double calculateError(BufferedImage image, int x, int y, int width, int height) {
        Color avg = averageColor(image, x, y, width, height);

        double ssimR = computeSSIMForChannel(image, x, y, width, height, avg.getRed(), 'r');
        double ssimG = computeSSIMForChannel(image, x, y, width, height, avg.getGreen(), 'g');
        double ssimB = computeSSIMForChannel(image, x, y, width, height, avg.getBlue(), 'b');

        double ssim = wR * ssimR + wG * ssimG + wB * ssimB;
        return 1.0 - ssim; 
    }

    private double computeSSIMForChannel(BufferedImage img, int x, int y, int w, int h, int avg, char channel) {
        double meanOrig = 0, meanAvg = avg;
        double varOrig = 0, covar = 0;
        int n = w * h;

        int[] values = new int[n];
        int idx = 0;

        for (int i = x; i < x + w; i++) {
            for (int j = y; j < y + h; j++) {
                Color c = new Color(img.getRGB(i, j));
                int val = switch (channel) {
                    case 'r' -> c.getRed();
                    case 'g' -> c.getGreen();
                    case 'b' -> c.getBlue();
                    default -> 0;
                };
                values[idx++] = val;
                meanOrig += val;
            }
        }

        meanOrig /= n;

        for (int i = 0; i < n; i++) {
            varOrig += Math.pow(values[i] - meanOrig, 2);
            covar += (values[i] - meanOrig) * (meanAvg - avg);
        }

        varOrig /= n;
        covar /= n;

        double numerator = (2 * meanOrig * meanAvg + C1) * (2 * covar + C2);
        double denominator = (meanOrig * meanOrig + meanAvg * meanAvg + C1) * (varOrig + C2);

        return denominator != 0 ? numerator / denominator : 1.0;
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
}

