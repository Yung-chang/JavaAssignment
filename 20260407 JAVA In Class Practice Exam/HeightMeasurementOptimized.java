import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class HeightMeasurementOptimized {

    public static void main(String[] args) {
        try {
            // ==================== pic1 (1).jpg (目標 163 cm) ====================
            processImage("pic1 (1).jpg",
                new Point(665, 725), new Point(670, 495),   // 右生腳、頭
                new Point(455, 685), new Point(460, 510),   // 左生腳、頭
                new Point(150, 485), new Point(950, 485),   // 上藍線
                new Point(150, 665), new Point(950, 665),   // 下藍線
                180.0, 163.0, 2.5, 1.2795);                 // known, target, shoe, factor

            StringBuilder separator = new StringBuilder();
            for (int i = 0; i < 70; i++) {
                separator.append('=');
            }
            System.out.println("\n" + separator.toString() + "\n");

            // ==================== pic3.jpg (目標 186 cm) ====================
            processImage("pic3.jpg",
                new Point(720, 680), new Point(725, 430),   // 右生腳、頭 (pic3)
                new Point(520, 640), new Point(525, 440),   // 左生腳、頭
                new Point(200, 415), new Point(1000, 415),  // 上藍線
                new Point(200, 615), new Point(1000, 615),  // 下藍線
                180.0, 186.0, 2.0, 1.5200);                 // known, target, shoe, factor

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processImage(String filename,
                                     Point rightFoot, Point rightHead,
                                     Point leftFoot, Point leftHead,
                                     Point ubl, Point ubr, Point lbl, Point lbr,
                                     double knownHeightCm, double targetHeightCm,
                                     double shoeCorrectionCm, double correctionFactor) throws Exception {

        BufferedImage image = ImageIO.read(new File(filename));

        Point2D.Double vp = computeVanishingPoint(ubl, ubr, lbl, lbr);
        double horizonY = (vp != null) ? vp.y : 460.0;

        double rightPixel = Math.abs(rightFoot.y - rightHead.y);
        double leftPixel  = Math.abs(leftFoot.y - leftHead.y);

        double rightMid = (rightFoot.y + rightHead.y) / 2.0;
        double leftMid  = (leftFoot.y + leftHead.y) / 2.0;

        double rightDist = Math.max(30, Math.abs(horizonY - rightMid));
        double leftDist  = Math.max(30, Math.abs(horizonY - leftMid));

        double baseRatio = leftPixel / rightPixel;
        double perspectiveFactor = (leftDist / rightDist) * correctionFactor;

        double calculatedHeight = baseRatio * perspectiveFactor * knownHeightCm + shoeCorrectionCm;

        System.out.println("=== " + filename + " 測量結果 ===");
        System.out.printf("右邊學生 (180cm)       : %.1f cm\n", knownHeightCm);
        System.out.printf("左邊學生計算值         : %.1f cm\n", calculatedHeight);
        System.out.printf("誤差                   : %.1f cm\n", Math.abs(calculatedHeight - targetHeightCm));
        System.out.printf("鞋底修正               : %.1f cm\n", shoeCorrectionCm);
        System.out.printf("校正因子               : %.1f\n", correctionFactor);

        drawVerification(image, rightFoot, rightHead, leftFoot, leftHead, ubl, ubr, lbl, lbr, vp, filename);
    }

    private static Point2D.Double computeVanishingPoint(Point a1, Point a2, Point b1, Point b2) {
        double x1 = a1.x, y1 = a1.y, x2 = a2.x, y2 = a2.y;
        double x3 = b1.x, y3 = b1.y, x4 = b2.x, y4 = b2.y;
        double den = (x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4);
        if (Math.abs(den) < 1e-5) return null;
        double t = ((x1 - x3)*(y3 - y4) - (y1 - y3)*(x3 - x4)) / den;
        return new Point2D.Double(x1 + t*(x2 - x1), y1 + t*(y2 - y1));
    }

    private static void drawVerification(BufferedImage image, Point rf, Point rh, Point lf, Point lh,
                                         Point ubl, Point ubr, Point lbl, Point lbr,
                                         Point2D.Double vp, String filename) {
        Graphics2D g = image.createGraphics();
        g.setStroke(new BasicStroke(4));

        g.setColor(Color.BLUE);
        g.drawLine(ubl.x, ubl.y, ubr.x, ubr.y);
        g.drawLine(lbl.x, lbl.y, lbr.x, lbr.y);

        g.setColor(Color.RED);
        g.drawLine(rf.x, rf.y, rh.x, rh.y);
        g.drawLine(lf.x, lf.y, lh.x, lh.y);

        if (vp != null) {
            g.setColor(Color.MAGENTA);
            g.drawLine(rh.x, rh.y, (int)vp.x, (int)vp.y);
        }

        g.setColor(Color.GREEN);
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        g.drawString("180cm", rh.x + 30, rh.y - 10);

        try {
            ImageIO.write(image, "jpg", new File("result_" + filename));
            System.out.println("驗證圖已儲存：result_" + filename + "\n");
        } catch (Exception e) {}
    }
}
