import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;
import javax.imageio.ImageIO;
public class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Masukkan alamat absolut gambar input: ");
            String inputPath = scanner.nextLine(); // Alamat Absolut Gambar Input
            System.out.print("Masukkan alamat absolut gambar output: ");
            String outputPath = scanner.nextLine(); // Alamat Absolut Gambar Hasil Kompresi
            System.out.println("Pilih metode perhitungan error: ");
            System.out.println("1. Variance");
            System.out.println("2. Mean Absolute Deviation (MAD)");
            System.out.println("3. Max Pixel Difference (MPD)");
            System.out.println("4. Entropy");
            System.out.println("5. SSIM (Structural Similarity Index)");
            int method; // Metode perhitungan error
            double threshold; // Threshold
            int minBlockSize; // Ukuran minimum blok
            double target; // Target rasio kompresi (0.0 - 1.0)
            while (true) {
                System.out.print("Masukkan pilihan (1-5): ");
                method = scanner.nextInt(); // Metode perhitungan error
                if (method >= 1 && method <= 5) {
                    break; // Valid method selected
                } else {
                    System.out.println("Pilihan tidak valid. Silakan coba lagi.");
                }
            }
            BufferedImage image = ImageIO.read(new File(inputPath));
            if (image == null) {
                System.out.println("Gambar tidak ditemukan atau format tidak didukung.");
                scanner.close();
                return; // Gambar tidak ditemukan
            }
            ErrorCalculator errorCalculator; // Perhitungan error
            double low; // Batas bawah error
            double high; // Batas atas error
            switch (method) {
                case 1 -> { 
                    errorCalculator = new VarianceRGB();
                    low = 0.0;
                    high = 65025.0;}
                case 2 -> { 
                    errorCalculator = new MeanAbsoluteDeviation(); 
                    low = 0.0; 
                    high = 255.0;}
                case 3 -> { 
                    errorCalculator = new MaxPixelDifference(); 
                    low = 0.0; 
                    high = 255.0;}
                case 4 -> { 
                    errorCalculator = new EntropyCalculator(); 
                    low = 0.0; 
                    high = 8.0;}
                case 5 -> { 
                    errorCalculator = new SSIMCalculator(); 
                    low = 0.0; 
                    high = 1.0;}
                default -> {
                    System.out.println("Invalid method selected.");
                    scanner.close();
                    return; // Invalid method
                }
            }
            while (true){
                System.out.print("Masukkan threshold: ");
                threshold = scanner.nextDouble(); // Threshold
                if (method == 5) { // SSIM
                    threshold = 1.0 - threshold; // Invert threshold for SSIM
                }
                if (threshold >= low && threshold <= high) {
                    break; // Valid threshold selected
                } else {
                    System.out.println("Threshold tidak valid. Silakan coba lagi.");
                }
            }
            while (true) { 
                System.out.print("Masukkan ukuran minimum blok: ");
                minBlockSize = scanner.nextInt(); // Ukuran minimum blok
                if (minBlockSize > 0 && minBlockSize <= image.getWidth() && minBlockSize <= image.getHeight()) {
                    break; // Valid block size selected
                } else {
                    System.out.println("Ukuran minimum blok tidak valid. Silakan coba lagi.");
                }
            }
            while (true) { 
                System.out.print("Target persentase kompresi (0,0 - 1,0) atau 0 untuk default: ");
                target = scanner.nextDouble(); // Target rasio kompresi (0.0 - 1.0)
                if (target >= 0.0 && target <= 1.0) {
                    break; // Valid target selected
                } else {
                    System.out.println("Target tidak valid. Silakan coba lagi.");
                }
            }
            
            scanner.close();
            if (target > 0) {
                threshold = TargetThreshold.findOptimalThreshold(image, errorCalculator, minBlockSize, target, low, high); // Mencari threshold optimal
                if (threshold == -1) {
                    System.out.println("Target tidak bisa dicapai.");
                    return; // Target not achievable
                }
            } else {}
            long start = System.currentTimeMillis();
            QuadTree qt = new QuadTree(image, errorCalculator, threshold, minBlockSize);
            System.out.println("Processing...");
            
            BufferedImage compressed = qt.reconstruct(image.getWidth(), image.getHeight());
            ImageIO.write(compressed, "png", new File(outputPath));
            long end = System.currentTimeMillis();
            
            System.out.println("Waktu eksekusi: " + (end - start) + " ms");
            System.out.printf("Ukuran Gambar Sebelum: %.2f KB%n", (TargetThreshold.getImageSizeInBytes(image, "png") / 1000.0));
            System.out.printf("Ukuran Gambar Setelah: %.2f KB%n", (TargetThreshold.getImageSizeInBytes(compressed, "png") / 1000.0));
            System.out.printf("Persentase Kompresi: %.1f%%\n", (1.0 - ((double) TargetThreshold.getImageSizeInBytes(compressed, "png") / TargetThreshold.getImageSizeInBytes(image, "png"))) * 100.0);
            System.out.println("Kedalaman Pohon: " + qt.getMaxDepth());
            System.out.println("Banyak Simpul: " + qt.getNodeCount());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
