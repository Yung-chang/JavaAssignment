import java.util.Arrays;
import java.util.Random;

public class AlgorithmExperiment {

    public static void main(String[] args) {
        // 設定要測試的資料規模 n (可根據需求增加，例如 100000)
        int[] sizes = {1000, 5000, 10000, 20000, 50000}; 
        
        System.out.println("============================================================================");
        System.out.println("               演算法效能實測結果 (平均執行時間單位: ns)");
        System.out.println("============================================================================");
        // 使用 %-12s 確保每個標題佔據固定寬度且靠左對齊
        System.out.printf("%-10s | %-15s | %-15s | %-15s | %-15s%n", 
                          "n (Size)", "Linear Scan", "Insertion Sort", "Bubble Sort", "Merge Sort");
        System.out.println("----------------------------------------------------------------------------");

        for (int n : sizes) {
            long totalLinear = 0, totalInsertion = 0, totalBubble = 0, totalMerge = 0;

            // 重複實驗 10 次取平均 (遵循圖 2 的實驗規範)
            for (int run = 0; run < 10; run++) {
                int[] data = generateRandomArray(n);

                totalLinear += measureTime(() -> linearScan(data));

                int[] dataIns = data.clone();
                totalInsertion += measureTime(() -> insertionSort(dataIns));

                int[] dataBub = data.clone();
                totalBubble += measureTime(() -> bubbleSort(dataBub));

                int[] dataMrg = data.clone();
                totalMerge += measureTime(() -> mergeSort(dataMrg, dataMrg.length));
            }

            // 輸出格式化後的對齊結果
            System.out.printf("%-10d | %-15d | %-15d | %-15d | %-15d%n", 
                              n, totalLinear/10, totalInsertion/10, totalBubble/10, totalMerge/10);
        }
        System.out.println("============================================================================");
    }

    // --- 演算法區 ---
    public static void linearScan(int[] a) {
        long sum = 0;
        for (int x : a) sum += x;
    }

    public static void insertionSort(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= 0 && a[j] > key) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    public static void bubbleSort(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            for (int j = 0; j < a.length - i - 1; j++) {
                if (a[j] > a[j + 1]) {
                    int temp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = temp;
                }
            }
        }
    }

    public static void mergeSort(int[] a, int n) {
        if (n < 2) return;
        int mid = n / 2;
        int[] l = Arrays.copyOfRange(a, 0, mid);
        int[] r = Arrays.copyOfRange(a, mid, n);
        mergeSort(l, mid);
        mergeSort(r, n - mid);
        merge(a, l, r, mid, n - mid);
    }

    private static void merge(int[] a, int[] l, int[] r, int left, int right) {
        int i = 0, j = 0, k = 0;
        while (i < left && j < right) {
            if (l[i] <= r[j]) a[k++] = l[i++];
            else a[k++] = r[j++];
        }
        while (i < left) a[k++] = l[i++];
        while (j < right) a[k++] = r[j++];
    }

    // --- 工具區 ---
    public static int[] generateRandomArray(int n) {
        Random rd = new Random();
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = rd.nextInt(10000);
        return a;
    }

    public static long measureTime(Runnable task) {
        long start = System.nanoTime();
        task.run();
        return System.nanoTime() - start;
    }
}