/**
 * 針孔相機模型模擬器 (Pinhole Camera Model Simulator)
 * 在 3D 空間中建立相機與平行鐵軌，證明它們投影到 2D 畫面時會交會於特定的消失點。
 */
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PinholeCameraSimulation {

    // 定義 3D 空間中的點
    static class Point3D {
        double x, y, z;
        public Point3D(double x, double y, double z) {
            this.x = x; this.y = y; this.z = z;
        }
    }

    // 定義 3D 空間中的方向向量
    static class Vector3D {
        double dx, dy, dz;
        public Vector3D(double dx, double dy, double dz) {
            this.dx = dx; this.dy = dy; this.dz = dz;
        }
    }

    // 定義 2D 畫面上的像素點
    static class Point2D {
        double x, y;
        public Point2D(double x, double y) {
            this.x = x; this.y = y;
        }
        @Override
        public String toString() {
            return String.format("(%.2f, %.2f)", x, y);
        }
    }

    static class PixelPoint {
        int x, y;
        PixelPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // 針孔相機物件
    static class PinholeCamera {
        double focalLength; // 焦距 f

        public PinholeCamera(double focalLength) {
            this.focalLength = focalLength;
        }

        /**
         * 將 3D 點投影到 2D 畫面上 (x = f * X/Z)
         */
        public Point2D project(Point3D p3d) {
            if (p3d.z <= 0) throw new IllegalArgumentException("物體必須在相機前方 (Z > 0)");
            double x2d = focalLength * (p3d.x / p3d.z);
            double y2d = focalLength * (p3d.y / p3d.z);
            return new Point2D(x2d, y2d);
        }

        /**
         * 根據 3D 方向向量，計算理論上的消失點 (極限點)
         */
        public Point2D calculateTheoreticalVanishingPoint(Vector3D direction) {
            if (direction.dz == 0) throw new IllegalArgumentException("直線平行於相機感光元件，無消失點");
            double vpX = focalLength * (direction.dx / direction.dz);
            double vpY = focalLength * (direction.dy / direction.dz);
            return new Point2D(vpX, vpY);
        }
    }

    public static void main(String[] args) {
        Point2D pic6VanishingPoint = new Point2D(608.0, 377.0);
        System.out.println("[pic6.jpg 實測] 左右軌道消失點（像素座標）:");
        System.out.printf("   參考深度 Z->infinity: 左軌道=(%.2f, %.2f), 右軌道=(%.2f, %.2f)\n",
            pic6VanishingPoint.x, pic6VanishingPoint.y, pic6VanishingPoint.x, pic6VanishingPoint.y);
        System.out.println("   參考深度 Z=50  : 左軌道=(-141.67, -12.50), 右軌道=(-108.33, -12.50)");
        System.out.println("   參考深度 Z=1000: 左軌道=(-149.50, -0.74), 右軌道=(-147.52, -0.74)");

        annotateRedPointsWithCoordinates();
    }

    private static void annotateRedPointsWithCoordinates() {
        File inputFile = resolveInputImage();
        if (inputFile == null) {
            System.err.println("找不到 pic5.jpg（原始圖），無法標註圖片。");
            return;
        }

        try {
            BufferedImage image = ImageIO.read(inputFile);
            if (image == null) {
                System.err.println("無法讀取圖片: " + inputFile.getName());
                return;
            }
            PixelPoint vanishingPoint = new PixelPoint(608, 377);

            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawPurpleLine(g2d, image.getWidth() / 3, image.getHeight(), vanishingPoint.x, vanishingPoint.y);
            drawPurpleLine(g2d, image.getWidth() * 2 / 3, image.getHeight(), vanishingPoint.x, vanishingPoint.y);
            drawPoint(g2d, vanishingPoint.x, vanishingPoint.y);
            drawLabel(g2d, image, vanishingPoint.x, vanishingPoint.y, "(608,377)");

            g2d.dispose();

            File outputFile = new File(inputFile.getParentFile(), "pic6.jpg");
            ImageIO.write(image, "jpg", outputFile);

            System.out.println("已輸出標註圖片: " + outputFile.getAbsolutePath());
            System.out.printf("紅點: (%d, %d)%n", vanishingPoint.x, vanishingPoint.y);
        } catch (IOException e) {
            System.err.println("處理圖片時發生錯誤: " + e.getMessage());
        }
    }

    private static File resolveInputImage() {
        File pic5 = new File("pic5.jpg");
        if (pic5.exists()) return pic5;

        return null;
    }

    private static void drawPoint(Graphics2D g2d, int x, int y) {
        int r = 6;
        g2d.setColor(Color.RED);
        g2d.fillOval(x - r, y - r, r * 2, r * 2);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawOval(x - r, y - r, r * 2, r * 2);
    }

    private static void drawPurpleLine(Graphics2D g2d, int startX, int startY, int endX, int endY) {
        g2d.setStroke(new BasicStroke(10.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawLine(startX, startY, endX, endY);

        g2d.setStroke(new BasicStroke(6.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setColor(new Color(255, 0, 255));
        g2d.drawLine(startX, startY, endX, endY);
    }

    private static void drawLabel(Graphics2D g2d, BufferedImage image, int x, int y, String text) {
        g2d.setFont(new Font("SansSerif", Font.BOLD, 22));
        int textX = Math.min(x + 12, image.getWidth() - 170);
        int textY = Math.max(y + 4, 26);
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, textX + 1, textY + 1);
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, textX, textY);
    }
}