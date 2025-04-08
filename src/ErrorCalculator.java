
import java.awt.image.BufferedImage;

public interface ErrorCalculator {
    double calculateError(BufferedImage image, int x, int y, int width, int height);
}
