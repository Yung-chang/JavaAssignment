public class Assignment_2 {
    public static void main(String[] args) {
        double[] alphaValues = {0.1, 0.8}; // 分別測試低學習率與高學習率

        for (double alpha : alphaValues) {
            System.out.println("--- Testing Alpha: " + alpha + " ---");
            simulate(alpha);
            System.out.println();
        }
    }

    public static void simulate(double alpha) {
        double qValue = 0.5; // 初始 Q 值
        double reward = 1.0; // 假設環境變動後的新獎勵
        
        // 模擬 5 步更新：Q(t+1) = (1-alpha)Q(t) + alpha * r(t)
        for (int t = 1; t <= 5; t++) {
            qValue = (1 - alpha) * qValue + alpha * reward;
            System.out.printf("Step %d: Updated Q-Value = %.4f\n", t, qValue);
        }

        // 根據指標公式計算 (簡化邏輯)
        double adaptTime = 1.0 / alpha;
        double gratitudeScore = alpha * 100; // alpha 越高，對新獎勵反應越快
        double stubbornScore = (1.0 - alpha) * 100; // alpha 越低，越堅持舊經驗

        System.out.println("Adapt Time: " + String.format("%.2f", adaptTime));
        System.out.println("Gratitude Score: " + gratitudeScore);
        System.out.println("Stubborn Score: " + stubbornScore);
    }
}