public class MathModels {
    public static void main(String[] args) {
        System.out.println("=== Matrix Multiplication Result ===");
        double[][] A = {{1, 2}, {3, 4}};
        double[][] B = {{5, 6}, {7, 8}};
        multiplyMatrices(A, B);

        System.out.println("\n=== Central Difference Derivative Approximation ===");
        calculateDerivative(Math.PI / 4, 0.01); // 在 45 度處逼近 sin(x) 的導數
    }

    public static void multiplyMatrices(double[][] A, double[][] B) {
        double[][] C = new double[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
                System.out.print(C[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void calculateDerivative(double x, double h) {
        // f(x) = sin(x), 預期導數為 cos(x)
        // Formula: f'(x) ≈ (f(x+h) - f(x-h)) / 2h
        double approximation = (Math.sin(x + h) - Math.sin(x - h)) / (2 * h);
        double exact = Math.cos(x);

        System.out.printf("Approximate Slope: %.6f\n", approximation);
        System.out.printf("Exact Cosine Value: %.6f\n", exact);
        System.out.printf("Error: %.6e\n", Math.abs(approximation - exact));
    }
}