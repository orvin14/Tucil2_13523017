import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

public class TargetThreshold {
    public static double findOptimalThreshold(
        BufferedImage image,
        ErrorCalculator calculator,
        int minBlockSize,
        double targetCompressionRatio,
        double low,
        double high
    ) {
        if (image == null || calculator == null || targetCompressionRatio <= 0.0 || targetCompressionRatio >= 1.0) {
            return -1;
        }

        double bestThreshold = low;
        double bestError = Double.MAX_VALUE;

        long originalSize = getImageSizeInBytes(image, "png"); // or "jpg" for JPEG
        double epsilon = 0.01;

        while (high - low > epsilon) {
            double mid = (low + high) / 2.0;
            QuadTree qt = new QuadTree(image, calculator, mid, minBlockSize);
            BufferedImage compressedImage = qt.reconstruct(image.getWidth(), image.getHeight());

            long compressedSize = getImageSizeInBytes(compressedImage, "png");
            double compressionRatio = 1.0 - ((double) compressedSize / originalSize);
            double error = Math.abs(compressionRatio - targetCompressionRatio);

            if (error < bestError) {
                bestError = error;
                bestThreshold = mid;
            }

            if (compressionRatio < targetCompressionRatio) {
                low = mid;
            } else {
                high = mid;
            }
        }

        return bestThreshold;
    }

    public static long getImageSizeInBytes(BufferedImage image, String format) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, format, baos);
            return baos.size();
        } catch (Exception e) {
            e.printStackTrace();
            return Long.MAX_VALUE; // Gagal evaluasi
        }
    }
}
