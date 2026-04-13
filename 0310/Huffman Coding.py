import heapq
import html
import time
from collections import Counter
try:
    from graphviz import Digraph  # type: ignore[reportMissingImports]
except ModuleNotFoundError:
    Digraph = None

class Node:
    def __init__(self, char, freq):
        self.char = char
        self.freq = freq
        self.left = None
        self.right = None
        # 為了在 graphviz 中唯一標識每個節點
        self.id = object() 
    
    # 定義比較規則供 Priority Queue 使用
    def __lt__(self, other):
        return self.freq < other.freq

def huffman_encoding(text):
    if not text: return None, None
    
    # 1. 統計頻率並放入堆疊
    freq_map = Counter(text)
    heap = [Node(char, freq) for char, freq in freq_map.items()]
    heapq.heapify(heap)
    
    # 2. 構建赫夫曼樹 (Greedy)
    while len(heap) > 1:
        node1 = heapq.heappop(heap)
        node2 = heapq.heappop(heap)
        merged = Node(None, node1.freq + node2.freq)
        merged.left = node1
        merged.right = node2
        heapq.heappush(heap, merged)
    
    root = heap[0]
    codes = {}
    
    # 3. 生成編碼表 (DFS)
    def generate_codes(node, current_code):
        if not node: return
        if node.char:
            # 處理特殊字元以利顯示
            display_char = node.char
            if node.char == ' ': display_char = "' '"
            elif node.char == '\n': display_char = "\\n"
            codes[display_char] = current_code
        generate_codes(node.left, current_code + "0")
        generate_codes(node.right, current_code + "1")
        
    generate_codes(root, "")
    # 返回編碼表和樹的根節點
    return codes, root 

def visualise_huffman_tree_svg(root, filename='huffman_tree'):
    """
    不依賴外部套件，使用 SVG 輸出赫夫曼樹。
    """
    if not root:
        print("樹為空，無法視覺化。")
        return

    if not filename.lower().endswith('.svg'):
        filename = f"{filename}.svg"

    def tree_height(node):
        if not node:
            return 0
        return 1 + max(tree_height(node.left), tree_height(node.right))

    positions = {}
    next_x = 0

    def assign_positions(node, depth):
        nonlocal next_x
        if not node:
            return None

        left_x = assign_positions(node.left, depth + 1) if node.left else None
        right_x = assign_positions(node.right, depth + 1) if node.right else None

        if left_x is None and right_x is None:
            x = next_x
            next_x += 1
        elif left_x is None:
            x = right_x
        elif right_x is None:
            x = left_x
        else:
            x = (left_x + right_x) / 2

        positions[node] = (x, depth)
        return x

    def display_char(char):
        if char == ' ':
            return '<space>'
        if char == '\n':
            return '\\n'
        if char == '\t':
            return '\\t'
        return char

    assign_positions(root, 0)

    x_step = 120
    y_step = 110
    margin_x = 70
    margin_y = 70
    levels = tree_height(root)
    leaf_slots = max(next_x, 1)

    width = int((leaf_slots - 1) * x_step + 2 * margin_x)
    height = int((levels - 1) * y_step + 2 * margin_y)
    radius = 22

    def px(x_index):
        return margin_x + x_index * x_step

    def py(depth):
        return margin_y + depth * y_step

    svg_lines = [
        f'<svg xmlns="http://www.w3.org/2000/svg" width="{width}" height="{height}" viewBox="0 0 {width} {height}">',
        '  <rect width="100%" height="100%" fill="#F9FAFB" />',
        '  <style>',
        '    .edge { stroke: #6B7280; stroke-width: 2; }',
        '    .edge-label { fill: #374151; font-size: 14px; font-family: "Segoe UI", sans-serif; }',
        '    .node-text { fill: #111827; font-size: 13px; font-family: "Segoe UI", sans-serif; text-anchor: middle; dominant-baseline: middle; }',
        '  </style>',
    ]

    for node, (x_index, depth) in positions.items():
        x1 = px(x_index)
        y1 = py(depth)

        if node.left:
            x2, y2 = px(positions[node.left][0]), py(positions[node.left][1])
            svg_lines.append(f'  <line class="edge" x1="{x1:.1f}" y1="{y1:.1f}" x2="{x2:.1f}" y2="{y2:.1f}" />')
            svg_lines.append(
                f'  <text class="edge-label" x="{(x1 + x2) / 2:.1f}" y="{(y1 + y2) / 2 - 8:.1f}">0</text>'
            )

        if node.right:
            x2, y2 = px(positions[node.right][0]), py(positions[node.right][1])
            svg_lines.append(f'  <line class="edge" x1="{x1:.1f}" y1="{y1:.1f}" x2="{x2:.1f}" y2="{y2:.1f}" />')
            svg_lines.append(
                f'  <text class="edge-label" x="{(x1 + x2) / 2:.1f}" y="{(y1 + y2) / 2 - 8:.1f}">1</text>'
            )

    for node, (x_index, depth) in positions.items():
        x = px(x_index)
        y = py(depth)

        if node.char:
            leaf_label = html.escape(display_char(node.char))
            freq_label = html.escape(f"({node.freq})")
            svg_lines.append(
                f'  <rect x="{x - 36:.1f}" y="{y - 20:.1f}" width="72" height="40" rx="10" fill="#DBEAFE" stroke="#1D4ED8" stroke-width="2" />'
            )
            svg_lines.append(
                f'  <text class="node-text" x="{x:.1f}" y="{y - 7:.1f}"><tspan x="{x:.1f}" dy="0">{leaf_label}</tspan><tspan x="{x:.1f}" dy="15">{freq_label}</tspan></text>'
            )
        else:
            svg_lines.append(
                f'  <circle cx="{x:.1f}" cy="{y:.1f}" r="{radius}" fill="#E5E7EB" stroke="#374151" stroke-width="2" />'
            )
            svg_lines.append(
                f'  <text class="node-text" x="{x:.1f}" y="{y:.1f}">{node.freq}</text>'
            )

    svg_lines.append('</svg>')

    try:
        with open(filename, 'w', encoding='utf-8') as svg_file:
            svg_file.write('\n'.join(svg_lines))
        print(f"成功生成赫夫曼樹圖片：{filename}")
    except OSError as e:
        print("輸出 SVG 圖片時發生錯誤。")
        print(f"錯誤訊息: {e}")

# --- 新增：生成圖片的函數 ---
def visualise_huffman_tree(root, filename='huffman_tree'):
    """
    使用 Graphviz 視覺化赫夫曼樹。
    """
    if Digraph is None:
        print("未安裝 Python 套件 graphviz，改用內建 SVG 輸出。")
        visualise_huffman_tree_svg(root, filename)
        return

    if not root:
        print("樹為空，無法視覺化。")
        return

    dot = Digraph(comment='Huffman Tree')
    # 設定節點樣式
    dot.attr('node', shape='circle', style='filled', color='lightgrey', fontname='Helvetica')
    dot.attr('edge', fontname='Helvetica')

    def add_nodes_edges(node):
        # 準備節點標籤
        if node.char:
            # 葉子節點：顯示字元和頻率
            display_char = node.char
            if node.char == ' ': display_char = "<space>" # 圖形中顯示 <space>
            elif node.char == '\n': display_char = "\\\\n"
            label = f"{display_char}\n({node.freq})"
            # 葉子節點使用不同的樣式
            dot.node(str(id(node.id)), label=label, shape='box', color='lightblue')
        else:
            # 內部節點：僅顯示頻率
            label = f"{node.freq}"
            dot.node(str(id(node.id)), label=label)

        # 遞迴處理左右子節點並添加邊
        if node.left:
            add_nodes_edges(node.left)
            dot.edge(str(id(node.id)), str(id(node.left.id)), label='0')
        if node.right:
            add_nodes_edges(node.right)
            dot.edge(str(id(node.id)), str(id(node.right.id)), label='1')

    # 從根節點開始構建圖形
    add_nodes_edges(root)
    
    # 渲染並儲存圖片 (預設生成 PDF 和 PNG)
    try:
        dot.render(filename, format='png', view=False)
        print(f"成功生成赫夫曼樹圖片：{filename}.png")
    except Exception as e:
        print(f"生成圖片時發生錯誤。請確保已安裝 Graphviz 執行檔。")
        print(f"錯誤訊息: {e}")
        print("改用內建 SVG 輸出。")
        visualise_huffman_tree_svg(root, filename)

# --- 主程式 ---
# 測試範例
text = "huffman coding example"
start_time = time.time()
# 修改：接收根節點
huffman_codes, root = huffman_encoding(text) 
end_time = time.time()

if huffman_codes:
    print("Huffman Codes:", huffman_codes)
    print(f"Time Complexity: O(n log n)")
    print(f"Execution Time: {end_time - start_time:.6f} s")
    
    # --- 呼叫繪圖函數 ---
    visualise_huffman_tree(root, 'huffman_tree_output')
else:
    print("輸入文本為空。")