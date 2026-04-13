import java.util.*;

// --- 基礎資料結構：樹節點 ---
class Node {
    int data;
    Node left, right;
    public Node(int item) {
        data = item;
        left = right = null;
    }
}

public class CombinedAlgorithmLab {

    // ========== Assignment 1: 堆疊 (Stack) 視覺化實作 ==========
    static class MyStack {
        private int maxSize;
        private int[] stackArray;
        private int top;

        public MyStack(int s) {
            maxSize = s;
            stackArray = new int[maxSize];
            top = -1;
        }

        public void push(int j) {
            if (top < maxSize - 1) {
                stackArray[++top] = j;
                System.out.println("Push: " + j);
            }
        }

        public int pop() {
            if (top >= 0) {
                int val = stackArray[top--];
                System.out.println("Pop: " + val);
                return val;
            }
            return -1;
        }

        public void displayStack() {
            System.out.println("\n[Stack 視覺化狀態]");
            if (top == -1) System.out.println("[ 空 ]");
            for (int i = top; i >= 0; i--) {
                System.out.println("|  " + stackArray[i] + "  |" + (i == top ? " <- Top" : ""));
            }
            System.out.println("-------");
        }
    }

    // ========== Assignment 2 & 3: 二元樹與走訪 (含 2D 顯示) ==========
    public void displayTree(Node root, int level) {
        if (root == null) return;
        displayTree(root.right, level + 1);
        if (level != 0) {
            for (int i = 0; i < level - 1; i++) System.out.print("|\t");
            System.out.println("|------- " + root.data);
        } else {
            System.out.println(root.data + " (Root)");
        }
        displayTree(root.left, level + 1);
    }

    public void printTraversals(Node root) {
        System.out.print("前序 (Preorder): "); preorder(root); System.out.println();
        System.out.print("中序 (Inorder): "); inorder(root); System.out.println();
        System.out.print("後序 (Postorder): "); postorder(root); System.out.println();
    }

    private void preorder(Node n) { if (n != null) { System.out.print(n.data + " "); preorder(n.left); preorder(n.right); } }
    private void inorder(Node n) { if (n != null) { inorder(n.left); System.out.print(n.data + " "); inorder(n.right); } }
    private void postorder(Node n) { if (n != null) { postorder(n.left); postorder(n.right); System.out.print(n.data + " "); } }

    // ========== Assignment 4: 分治法 (Merge Sort & Binary Search) ==========
    public void mergeSort(int[] arr, int l, int r) {
        if (l < r) {
            int m = l + (r - l) / 2;
            System.out.println("  -> 拆分範圍: [" + l + " 到 " + r + "]");
            mergeSort(arr, l, m);
            mergeSort(arr, m + 1, r);
            merge(arr, l, m, r);
        }
    }

    private void merge(int[] arr, int l, int m, int r) {
        int n1 = m - l + 1, n2 = r - m;
        int[] L = new int[n1], R = new int[n2];
        for (int i = 0; i < n1; ++i) L[i] = arr[l + i];
        for (int j = 0; j < n2; ++j) R[j] = arr[m + 1 + j];

        int i = 0, j = 0, k = l;
        while (i < n1 && j < n2) arr[k++] = (L[i] <= R[j]) ? L[i++] : R[j++];
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
        System.out.print("  <- 合併完成: "); displayArray(arr, l, r);
    }

    public int binarySearch(int[] arr, int l, int r, int x) {
        if (r >= l) {
            int mid = l + (r - l) / 2;
            System.out.println("  搜尋範圍: [" + l + "," + r + "], 中間值 arr[" + mid + "]=" + arr[mid]);
            if (arr[mid] == x) return mid;
            return (arr[mid] > x) ? binarySearch(arr, l, mid - 1, x) : binarySearch(arr, mid + 1, r, x);
        }
        return -1;
    }

    private void displayArray(int[] arr, int l, int r) {
        System.out.print("[ ");
        for (int i = l; i <= r; i++) System.out.print(arr[i] + " ");
        System.out.println("]");
    }

    // ========== Assignment 5: 圖形走訪 (BFS & DFS) ==========
    static class Graph {
        private Map<Integer, List<Integer>> adj = new HashMap<>();
        public void addEdge(int u, int v) { adj.computeIfAbsent(u, k -> new ArrayList<>()).add(v); }

        public void BFS(int start) {
            System.out.println("\n[BFS 執行追蹤]");
            Set<Integer> visited = new LinkedHashSet<>();
            Queue<Integer> queue = new LinkedList<>();
            visited.add(start); queue.add(start);

            while (!queue.isEmpty()) {
                int u = queue.poll();
                System.out.println("造訪節點 " + u + " | 佇列狀態: " + queue);
                for (int v : adj.getOrDefault(u, new ArrayList<>())) {
                    if (!visited.contains(v)) { visited.add(v); queue.add(v); }
                }
            }
            System.out.println("最終路徑: " + visited);
        }
    }

    // ========== 綜合測試主程式 ==========
    public static void main(String[] args) {
        CombinedAlgorithmLab lab = new CombinedAlgorithmLab();

        System.out.println("=== Assignment 1: Stack 展示 ===");
        MyStack stack = new MyStack(5);
        stack.push(10); stack.push(20); stack.push(30);
        stack.displayStack();
        stack.pop();
        stack.displayStack();

        System.out.println("\n=== Assignment 2 & 3: Binary Tree 展示 ===");
        Node root = new Node(10);
        root.left = new Node(5); root.right = new Node(15);
        root.left.left = new Node(2); root.left.right = new Node(7);
        lab.displayTree(root, 0);
        lab.printTraversals(root);

        System.out.println("\n=== Assignment 4: Merge Sort 展示 ===");
        int[] data = {38, 27, 43, 3, 9, 82, 10};
        System.out.print("原始陣列: "); lab.displayArray(data, 0, data.length-1);
        lab.mergeSort(data, 0, data.length - 1);

        System.out.println("\n=== Assignment 4: Binary Search 展示 (找 9) ===");
        int result = lab.binarySearch(data, 0, data.length - 1, 9);
        System.out.println("結果: " + (result != -1 ? "索引為 " + result : "未找到"));

        System.out.println("\n=== Assignment 5: Graph BFS 展示 ===");
        Graph g = new Graph();
        g.addEdge(0, 1); g.addEdge(0, 2); g.addEdge(1, 3); g.addEdge(2, 4);
        g.BFS(0);
    }
}