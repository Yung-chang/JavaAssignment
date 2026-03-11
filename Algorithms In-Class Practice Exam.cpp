#include <iostream>
#include <vector>
#include <algorithm>
#include <chrono>
#include <iomanip>
#include <random>

using namespace std;
using namespace std::chrono;

// Helper function: Prints the current state of the array
void printStep(const vector<int>& arr, string msg) {
    cout << setw(20) << left << msg << ": [ ";
    for (int x : arr) cout << x << " ";
    cout << "]" << endl;
}

// --- 1. Bubble Sort: O(n^2) ---
// Repeatedly swaps adjacent elements if they are in the wrong order.
void bubbleSort(vector<int> arr, bool showSteps) {
    int n = arr.size();
    for (int i = 0; i < n - 1; i++) {
        for (int j = 0; j < n - i - 1; j++) {
            if (arr[j] > arr[j + 1]) swap(arr[j], arr[j + 1]);
        }
        if (showSteps) printStep(arr, "Round " + to_string(i + 1));
    }
}

// --- 2. Insertion Sort: O(n^2) ---
// Builds the final sorted array one item at a time by "inserting" it into the sorted part.
void insertionSort(vector<int> arr, bool showSteps) {
    int n = arr.size();
    for (int i = 1; i < n; i++) {
        int key = arr[i], j = i - 1;
        while (j >= 0 && arr[j] > key) {
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = key;
        if (showSteps) printStep(arr, "Insert " + to_string(key));
    }
}

// --- 3. Merge Sort: O(n log n) ---
// A Divide and Conquer algorithm that splits the array and merges sorted halves.
void merge(vector<int>& arr, int l, int m, int r, bool showSteps) {
    vector<int> temp;
    int i = l, j = m + 1;
    while (i <= m && j <= r) {
        if (arr[i] <= arr[j]) temp.push_back(arr[i++]);
        else temp.push_back(arr[j++]);
    }
    while (i <= m) temp.push_back(arr[i++]);
    while (j <= r) temp.push_back(arr[j++]);
    for (int k = 0; k < temp.size(); k++) arr[l + k] = temp[k];
    if (showSteps) printStep(arr, "Merge [" + to_string(l) + "-" + to_string(r) + "]");
}

void mergeSort(vector<int>& arr, int l, int r, bool showSteps) {
    if (l < r) {
        int m = l + (r - l) / 2;
        mergeSort(arr, l, m, showSteps);
        mergeSort(arr, m + 1, r, showSteps);
        merge(arr, l, m, r, showSteps);
    }
}

// --- 4. Quick Sort: O(n log n) ---
// Picks a 'pivot' and partitions the array around it.
void quickSort(vector<int>& arr, int low, int high, bool showSteps) {
    if (low < high) {
        int pivot = arr[high], i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) swap(arr[++i], arr[j]);
        }
        swap(arr[i + 1], arr[high]);
        int pi = i + 1;
        if (showSteps) printStep(arr, "Pivot " + to_string(pivot) + " fixed");
        quickSort(arr, low, pi - 1, showSteps);
        quickSort(arr, pi + 1, high, showSteps);
    }
}

// --- Performance Benchmarking Function ---
void runBenchmark(int n) {
    vector<int> base(n);
    mt19937 rng(42); // Fixed seed for reproducibility
    for (int &x : base) x = rng() % 100000;

    auto measure = [&](string name, void (*sortFunc)(vector<int>&)) {
        vector<int> data = base;
        auto start = high_resolution_clock::now();
        sortFunc(data);
        auto stop = high_resolution_clock::now();
        cout << left << setw(15) << name << ": " 
             << duration_cast<microseconds>(stop - start).count() / 1000.0 << " ms" << endl;
    };

    cout << "\n>>> Benchmark Results for n = " << n << ":" << endl;
    if (n <= 10000) {
        measure("Bubble Sort", [](vector<int>& v) { bubbleSort(v, false); });
        measure("Insertion Sort", [](vector<int>& v) { insertionSort(v, false); });
    } else {
        cout << "(n is too large, skipping O(n^2) algorithms to save time)" << endl;
    }
    measure("Merge Sort", [](vector<int>& v) { mergeSort(v, 0, v.size() - 1, false); });
    measure("Quick Sort", [](vector<int>& v) { quickSort(v, 0, v.size() - 1, false); });
}

int main() {
    // A. Step-by-Step Demonstration (Small Data)
    vector<int> demo = {5, 2, 4, 1, 3};
    cout << "=== Sorting Step Demonstration (n=5) ===" << endl;
    printStep(demo, "Original");
    
    cout << "\n[Bubble Sort]" << endl; bubbleSort(demo, true);
    cout << "\n[Insertion Sort]" << endl; insertionSort(demo, true);
    
    vector<int> mDemo = demo;
    cout << "\n[Merge Sort]" << endl; mergeSort(mDemo, 0, 4, true);
    
    vector<int> qDemo = demo;
    cout << "\n[Quick Sort]" << endl; quickSort(qDemo, 0, 4, true);

    // B. Time Complexity Testing (Large Data)
    cout << "\n\n=== Time Complexity Empirical Observation ===" << endl;
    runBenchmark(1000);   // Small scale
    runBenchmark(10000);  // Observe exponential growth of O(n^2)
    runBenchmark(100000); // Observe stability of O(n log n)

    return 0;
}