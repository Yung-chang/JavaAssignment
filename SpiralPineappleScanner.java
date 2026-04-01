import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SpiralPineappleScanner {

    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("0001.jpg"));
        int w = img.getWidth();
        int h = img.getHeight();

        Graphics2D g = img.createGraphics();
        g.setColor(Color.RED);
        int totalPoints = 0;

        // 設定網格步進，在每個網格點嘗試啟動螺旋驗證
        int step = 35; 
        for (int y = step; y < h - step; y += step) {
            for (int x = step; x < w - step; x += step) {
                
                // 執行局部螺旋掃描 (Spiral Search)
                // 尋找半徑 20 像素內的最佳特徵點
                Point bestPoint = performSpiralSearch(img, x, y, 17);
                
                if (bestPoint != null) {
                    g.fillOval(bestPoint.x - 15, bestPoint.y - 15, 25, 25);
                    totalPoints++;
                }
            }
        }

        g.dispose();
        ImageIO.write(img, "png", new File("spiral_result.png"));
        System.out.println("螺旋掃描完成！總偵測點數: " + totalPoints);
    }

    /**
     * 螺旋搜索演算法
     * 從 (startX, startY) 開始，以螺旋路徑向外尋找特徵值最強的中心
     */
    private static Point performSpiralSearch(BufferedImage img, int startX, int startY, int maxRadius) {
        int x = startX, y = startY;
        int dx = 0, dy = -1;
        int maxSteps = maxRadius * 2;
        
        double maxFeature = 0;
        Point best = null;

        // 螺旋路徑邏輯
        for (int step = 1; step < maxSteps * maxSteps; step++) {
            if (x >= 0 && x < img.getWidth() && y >= 0 && y < img.getHeight()) {
                double feature = getFeature(img.getRGB(x, y));
                if (feature > maxFeature && feature > 0.829) { // 門檻值
                    maxFeature = feature;
                    best = new Point(x, y);
                }
            }

            // 轉彎邏輯 (形成螺旋)
            if (x == y || (x < 0 && x == -y) || (x > 0 && x == 1 - y)) {
                int temp = dx;
                dx = -dy;
                dy = temp;
            }
            x += dx;
            y += dy;
            
            if (Math.abs(x - startX) > maxRadius || Math.abs(y - startY) > maxRadius) break;
        }
        return best;
    }

    private static double getFeature(int rgb) {
        float[] hsv = new float[3];
        Color.RGBtoHSB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, (rgb & 0xFF), hsv);
        // 鳳梨特徵：飽和度與亮度的權重
        return hsv[1] * (1.0 - hsv[2]);
    }
}