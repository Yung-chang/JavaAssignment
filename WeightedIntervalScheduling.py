import heapq
import time
import bisect
from collections import Counter

# ==========================================
# 1. 赫夫曼編碼 (Huffman Coding) 實作
# ==========================================
class HuffmanNode:
    def __init__(self, char, freq):
        self.char = char
        self.freq = freq
        self.left = None
        self.right = None

    def __lt__(self, other):
        return self.freq < other.freq

def huffman_encoding(text):
    print("\n--- Huffman Coding Result ---")
    if not text: return None
    
    # 統計頻率並放入最小堆疊 (Priority Queue)
    freq_map = Counter(text)
    heap = [HuffmanNode(char, freq) for char, freq in freq_map.items()]
    heapq.heapify(heap)
    
    # 構建赫夫曼樹 (Greedy Strategy)
    while len(heap) > 1:
        node1 = heapq.heappop(heap)
        node2 = heapq.heappop(heap)
        merged = HuffmanNode(None, node1.freq + node2.freq)
        merged.left = node1
        merged.right = node2
        heapq.heappush(heap, merged)
    
    # 生成編碼表 (DFS)
    codes = {}
    def generate_codes(node, current_code):
        if not node: return
        if node.char:
            codes[node.char] = current_code
        generate_codes(node.left, current_code + "0")
        generate_codes(node.right, current_code + "1")
        
    generate_codes(heap[0], "")
    
    # 印出結果 (與截圖風格一致)
    for char in sorted(codes.keys()):
        display_char = f"'{char}'" if char != ' ' else "'space'"
        print(f"Character: {display_char:8} | Huffman Code: {codes[char]}")
    
    print(f"Theoretical Complexity: O(n log n)")
    return codes

# ==========================================
# 2. 加權區間排程 (Weighted Interval Scheduling) 實作
# ==========================================
class Interval:
    def __init__(self, id, start, finish, weight):
        self.id = id
        self.start = start
        self.finish = finish
        self.weight = weight

def weighted_interval_scheduling(intervals):
    print("\n--- Weighted Interval Scheduling Result ---")
    # 1. 依結束時間排序 (Sorting)
    intervals.sort(key=lambda x: x.finish)
    n = len(intervals)
    finish_times = [i.finish for i in intervals]
    
    # 2. 計算 p(j) (Binary Search 優化)
    p = [0] * n
    for j in range(n):
        # 尋找與目前任務不重疊的最晚任務
        idx = bisect.bisect_right(finish_times, intervals[j].start) - 1
        p[j] = idx + 1 # 為了與學術定義一致，索引從 1 開始標記

    # 3. 動態規劃填表 (DP Table)
    M = [0] * (n + 1)
    for j in range(1, n + 1):
        # 轉移方程式: max(包含當前任務, 不包含當前任務)
        include_val = intervals[j-1].weight + (M[p[j-1]] if p[j-1] > 0 else 0)
        M[j] = max(include_val, M[j-1])
    
    # 印出細節
    print(f"{'Task ID':<10} | {'Start':<6} | {'Finish':<6} | {'Weight':<6} | {'p(j)':<4}")
    for i, inter in enumerate(intervals):
        print(f"Task {inter.id:<5} | {inter.start:<6} | {inter.finish:<6} | {inter.weight:<6} | p({i+1})={p[i]}")
    
    print(f"\nMaximum Total Weight: {M[n]}")
    print(f"Theoretical Complexity: O(n log n)")
    return M[n]

# ==========================================
# 主程式執行
# ==========================================
if __name__ == "__main__":
    # 測試資料 (可依需求修改)
    sample_text = "huffman coding example"
    sample_intervals = [
        Interval(1, 1, 4, 30),
        Interval(2, 2, 6, 50),
        Interval(3, 5, 7, 40),
        Interval(4, 6, 8, 70)
    ]
    
    # 執行與計時
    start = time.time()
    huffman_encoding(sample_text)
    weighted_interval_scheduling(sample_intervals)
    end = time.time()
    
    print(f"\nTotal Execution Time: {end - start:.6f} seconds")