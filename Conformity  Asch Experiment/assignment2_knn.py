import numpy as np
import matplotlib.pyplot as plt
from matplotlib.colors import ListedColormap

plt.rcParams['font.sans-serif'] = ['Microsoft JhengHei', 'PingFang HK', 'SimHei']
plt.rcParams['axes.unicode_minus'] = False

np.random.seed(42)

EXIT_NAMES = {0: '出口 A (北)', 1: '出口 B (中)', 2: '出口 C (南)'}
EXIT_CENTERS = np.array([[-5.0, 0.0], [0.0, 0.0], [5.0, 0.0]])
TRUE_EXIT = 1  # 作業設定：唯一正確出口固定為 B


def build_city():
    """建立三區城市 + 偏差區。"""
    n = 25
    north = np.random.normal(loc=[-5, 0], scale=1.4, size=(n, 2))
    center = np.random.normal(loc=[0, 0], scale=1.4, size=(n, 2))
    south = np.random.normal(loc=[5, 0], scale=1.4, size=(n, 2))

    # 偏差區：靠近中區但群體標籤偏向 C
    bias = np.random.normal(loc=[1.6, -3.2], scale=0.28, size=(16, 2))

    X = np.vstack([north, center, south, bias])
    y = np.concatenate([
        np.zeros(n, dtype=int),
        np.ones(n, dtype=int),
        np.full(n, 2, dtype=int),
        np.full(16, 2, dtype=int),
    ])
    return X, y


def knn_predict_many(X_train, y_train, X_query, k):
    """對多個查詢點做 KNN 多數決。"""
    dists = np.sqrt(((X_query[:, None, :] - X_train[None, :, :]) ** 2).sum(axis=2))
    nn_idx = np.argpartition(dists, kth=k - 1, axis=1)[:, :k]
    nn_labels = y_train[nn_idx]

    votes = np.zeros((X_query.shape[0], 3), dtype=int)
    for cls in range(3):
        votes[:, cls] = (nn_labels == cls).sum(axis=1)
    return np.argmax(votes, axis=1)


def knn_predict_one(X_train, y_train, target, k):
    """單點 KNN，回傳預測類別、K 鄰居索引與半徑。"""
    dists = np.linalg.norm(X_train - target, axis=1)
    idx = np.argsort(dists)[:k]
    labels = y_train[idx]

    counts = np.bincount(labels, minlength=3)
    pred = int(np.argmax(counts))
    radius = dists[idx[-1]]
    return pred, idx, radius


def improved_dual_channel_predict_many(X_train, y_train, X_query, k_local=5, k_global=21, alpha=0.65, eps=1e-6):
    """改造 KNN：雙通道投票（局部距離加權 + 全域穩定票）。"""
    dists = np.sqrt(((X_query[:, None, :] - X_train[None, :, :]) ** 2).sum(axis=2))

    local_idx = np.argpartition(dists, kth=k_local - 1, axis=1)[:, :k_local]
    local_labels = y_train[local_idx]
    local_dists = np.take_along_axis(dists, local_idx, axis=1)
    local_weights = 1.0 / (local_dists + eps)

    local_scores = np.zeros((X_query.shape[0], 3), dtype=float)
    for cls in range(3):
        local_scores[:, cls] = (local_weights * (local_labels == cls)).sum(axis=1)
    local_scores /= (local_scores.sum(axis=1, keepdims=True) + eps)

    global_idx = np.argpartition(dists, kth=k_global - 1, axis=1)[:, :k_global]
    global_labels = y_train[global_idx]

    global_scores = np.zeros((X_query.shape[0], 3), dtype=float)
    for cls in range(3):
        global_scores[:, cls] = (global_labels == cls).sum(axis=1)
    global_scores /= (global_scores.sum(axis=1, keepdims=True) + eps)

    fused_scores = alpha * local_scores + (1 - alpha) * global_scores
    return np.argmax(fused_scores, axis=1)


def improved_dual_channel_predict_one(X_train, y_train, target, k_local=5, k_global=21, alpha=0.65, eps=1e-6):
    """單點雙通道投票。"""
    dists = np.linalg.norm(X_train - target, axis=1)

    local_idx = np.argsort(dists)[:k_local]
    local_labels = y_train[local_idx]
    local_weights = 1.0 / (dists[local_idx] + eps)

    local_scores = np.zeros(3, dtype=float)
    for cls in range(3):
        local_scores[cls] = (local_weights * (local_labels == cls)).sum()
    local_scores /= (local_scores.sum() + eps)

    global_idx = np.argsort(dists)[:k_global]
    global_labels = y_train[global_idx]
    global_scores = np.bincount(global_labels, minlength=3).astype(float)
    global_scores /= (global_scores.sum() + eps)

    fused_scores = alpha * local_scores + (1 - alpha) * global_scores
    pred = int(np.argmax(fused_scores))
    radius = dists[local_idx[-1]]
    return pred, local_idx, radius


def nearest_true_exit(target):
    """保留介面，但作業設定固定唯一正確出口為 B。"""
    _ = target
    return TRUE_EXIT


def annotate_exits(ax):
    """在子圖上標記三個出口位置。"""
    labels = ['出口 A', '出口 B', '出口 C']
    for i, label in enumerate(labels):
        x, _ = EXIT_CENTERS[i]
        ax.text(
            x,
            4.1,
            label,
            ha='center',
            va='top',
            fontsize=9,
            color='#1f1f1f',
            bbox=dict(facecolor='white', alpha=0.82, edgecolor='none', boxstyle='round,pad=0.2')
        )


def generate_nearest_neighbor_classifier_distribution(output_path='nearest_neighbor_classifier_distribution.png'):
    X, y = build_city()
    investigators = {
        1: np.array([-2.8, -2.0]),
        3: np.array([0.8, -3.7]),
        5: np.array([3.2, -1.6]),
        'improved': np.array([0.2, -3.1]),
    }
    true_exit = TRUE_EXIT

    x_min, x_max = -8.5, 8.5
    y_min, y_max = -7.0, 4.5
    xx, yy = np.meshgrid(np.linspace(x_min, x_max, 280), np.linspace(y_min, y_max, 220))
    grid = np.c_[xx.ravel(), yy.ravel()]

    bg_cmap = ListedColormap(['#c4e4f8', '#f8e6b5', '#f4bcb2'])
    pt_colors = {0: '#2ca9df', 1: '#e3b437', 2: '#e15a43'}

    fig, axes = plt.subplots(2, 2, figsize=(12, 10), facecolor='#efefef')
    axes = axes.ravel()
    fig.suptitle('Nearest Neighbor classifier', fontsize=22, fontweight='bold', y=1.03)
    fig.text(0.5, 0.93, f'唯一正確出口: {EXIT_NAMES[true_exit]}', ha='center', fontsize=11, color='#333333')

    # 每張圖的框點樣式刻意不同，避免看起來都一樣
    frame_styles = {
        1: {'edge': '#00e5ff', 'line': '--', 'size': 78},
        3: {'edge': '#ff9f1c', 'line': '-.', 'size': 64},
        5: {'edge': '#ff4d6d', 'line': ':', 'size': 54},
    }

    for ax, k in zip(axes[:3], [1, 3, 5]):
        investigator = investigators[k]
        pred = knn_predict_many(X, y, grid, k).reshape(xx.shape)
        ax.contourf(xx, yy, pred, levels=[-0.5, 0.5, 1.5, 2.5], cmap=bg_cmap, alpha=1.0)

        for cls in [0, 1, 2]:
            pts = X[y == cls]
            ax.scatter(pts[:, 0], pts[:, 1], c=pt_colors[cls], s=12, alpha=0.95, edgecolors='none')

        pred_one, nn_idx, radius = knn_predict_one(X, y, investigator, k)
        misled = pred_one != true_exit
        style = frame_styles[k]
        ax.scatter(investigator[0], investigator[1], c='white', s=170, marker='*', edgecolors='#222222', linewidths=0.9, zorder=5)
        ax.scatter(
            X[nn_idx, 0],
            X[nn_idx, 1],
            facecolors='none',
            edgecolors=style['edge'],
            s=style['size'],
            linewidth=1.35,
            zorder=4
        )
        ax.add_artist(
            plt.Circle(
                (investigator[0], investigator[1]),
                radius,
                fill=False,
                linestyle=style['line'],
                linewidth=1.45,
                edgecolor=style['edge'],
                alpha=0.95
            )
        )
        ax.text(
            0.03,
            0.03,
            f'預測: {EXIT_NAMES[pred_one]} / {"被誤導" if misled else "正確"}',
            transform=ax.transAxes,
            fontsize=8.8,
            color='white',
            bbox=dict(facecolor='#d62728' if misled else '#2ca02c', alpha=0.88, edgecolor='none')
        )
        ax.text(
            0.03,
            0.14,
            f'選取點: ({investigator[0]:.1f}, {investigator[1]:.1f})',
            transform=ax.transAxes,
            fontsize=8.4,
            color='#222222',
            bbox=dict(facecolor='white', alpha=0.82, edgecolor='none')
        )

        ax.set_title(f'K = {k}', fontsize=16, y=-0.16)
        ax.set_xlim(x_min, x_max)
        ax.set_ylim(y_min, y_max)
        ax.set_xticks([])
        ax.set_yticks([])
        ax.set_facecolor('white')
        for spine in ax.spines.values():
            spine.set_color('#f7f7f7')
            spine.set_linewidth(1.6)
        annotate_exits(ax)

    # 第四張：改造 KNN
    ax = axes[3]
    investigator = investigators['improved']
    improved_pred = improved_dual_channel_predict_many(
        X, y, grid, k_local=5, k_global=21, alpha=0.65
    ).reshape(xx.shape)
    ax.contourf(xx, yy, improved_pred, levels=[-0.5, 0.5, 1.5, 2.5], cmap=bg_cmap, alpha=1.0)

    for cls in [0, 1, 2]:
        pts = X[y == cls]
        ax.scatter(pts[:, 0], pts[:, 1], c=pt_colors[cls], s=12, alpha=0.95, edgecolors='none')

    pred_imp, imp_idx, imp_radius = improved_dual_channel_predict_one(
        X, y, investigator, k_local=5, k_global=21, alpha=0.65
    )
    misled_imp = pred_imp != true_exit
    ax.scatter(investigator[0], investigator[1], c='white', s=170, marker='*', edgecolors='#222222', linewidths=0.9, zorder=5)
    ax.scatter(X[imp_idx, 0], X[imp_idx, 1], facecolors='none', edgecolors='#6a4cff', s=70, linewidth=1.5, zorder=4)
    ax.add_artist(plt.Circle((investigator[0], investigator[1]), imp_radius, fill=False, linestyle='-',
                             linewidth=1.7, edgecolor='#6a4cff', alpha=0.95))
    ax.text(
        0.03,
        0.03,
        f'預測: {EXIT_NAMES[pred_imp]} / {"被誤導" if misled_imp else "正確"}',
        transform=ax.transAxes,
        fontsize=8.8,
        color='white',
        bbox=dict(facecolor='#d62728' if misled_imp else '#2ca02c', alpha=0.88, edgecolor='none')
    )
    ax.text(
        0.03,
        0.14,
        f'選取點: ({investigator[0]:.1f}, {investigator[1]:.1f})',
        transform=ax.transAxes,
        fontsize=8.4,
        color='#222222',
        bbox=dict(facecolor='white', alpha=0.82, edgecolor='none')
    )

    ax.set_title('改造 KNN\n(雙通道投票)', fontsize=14, y=-0.18, color='#222222')
    ax.set_xlim(x_min, x_max)
    ax.set_ylim(y_min, y_max)
    ax.set_xticks([])
    ax.set_yticks([])
    ax.set_facecolor('white')
    for spine in ax.spines.values():
        spine.set_color('#f7f7f7')
        spine.set_linewidth(1.6)
    annotate_exits(ax)

    plt.tight_layout()
    plt.savefig(output_path, dpi=300, bbox_inches='tight', facecolor='#efefef')
    plt.show()

    # 另外輸出 4 張單圖，讓 K=1/3/5 與改造 KNN 有更明顯區別
    single_panels = [
        {
            'name': 'dist_k1_baseline.png',
            'title': 'Baseline KNN - K = 1',
            'mode': 'base',
            'k': 1,
            'frame': '#00e5ff',
            'investigator': investigators[1],
        },
        {
            'name': 'dist_k3_baseline.png',
            'title': 'Baseline KNN - K = 3',
            'mode': 'base',
            'k': 3,
            'frame': '#ff9f1c',
            'investigator': investigators[3],
        },
        {
            'name': 'dist_k5_baseline.png',
            'title': 'Baseline KNN - K = 5',
            'mode': 'base',
            'k': 5,
            'frame': '#ff4d6d',
            'investigator': investigators[5],
        },
        {
            'name': 'dist_improved_dual_channel.png',
            'title': 'Improved KNN - Dual Channel',
            'mode': 'improved',
            'k': None,
            'frame': '#6a4cff',
            'investigator': investigators['improved'],
        },
    ]

    for panel in single_panels:
        fig_s, ax_s = plt.subplots(1, 1, figsize=(7.2, 5.8), facecolor='#efefef')
        investigator = panel['investigator']

        if panel['mode'] == 'base':
            k = panel['k']
            pred_map = knn_predict_many(X, y, grid, k).reshape(xx.shape)
            pred_one, idx_one, radius_one = knn_predict_one(X, y, investigator, k)
            misled_one = pred_one != true_exit
        else:
            pred_map = improved_dual_channel_predict_many(
                X, y, grid, k_local=5, k_global=21, alpha=0.65
            ).reshape(xx.shape)
            pred_one, idx_one, radius_one = improved_dual_channel_predict_one(
                X, y, investigator, k_local=5, k_global=21, alpha=0.65
            )
            misled_one = pred_one != true_exit

        ax_s.contourf(xx, yy, pred_map, levels=[-0.5, 0.5, 1.5, 2.5], cmap=bg_cmap, alpha=1.0)

        for cls in [0, 1, 2]:
            pts = X[y == cls]
            ax_s.scatter(pts[:, 0], pts[:, 1], c=pt_colors[cls], s=14, alpha=0.95, edgecolors='none')

        ax_s.scatter(investigator[0], investigator[1], c='white', s=180, marker='*', edgecolors='#222222', linewidths=1.0, zorder=6)
        ax_s.scatter(X[idx_one, 0], X[idx_one, 1], facecolors='none', edgecolors=panel['frame'], s=70, linewidth=1.5, zorder=5)
        ax_s.add_artist(
            plt.Circle((investigator[0], investigator[1]), radius_one, fill=False,
                       linestyle='--', linewidth=1.6, edgecolor=panel['frame'], alpha=0.95)
        )

        ax_s.text(
            0.03,
            0.03,
            f'預測: {EXIT_NAMES[pred_one]} / {"被誤導" if misled_one else "正確"}',
            transform=ax_s.transAxes,
            fontsize=10,
            color='white',
            bbox=dict(facecolor='#d62728' if misled_one else '#2ca02c', alpha=0.9, edgecolor='none')
        )
        ax_s.text(
            0.03,
            0.14,
            f'選取點: ({investigator[0]:.1f}, {investigator[1]:.1f})',
            transform=ax_s.transAxes,
            fontsize=10,
            color='#222222',
            bbox=dict(facecolor='white', alpha=0.84, edgecolor='none')
        )

        annotate_exits(ax_s)
        ax_s.set_title(panel['title'], fontsize=18, fontweight='bold', color='#222222')
        ax_s.set_xlim(x_min, x_max)
        ax_s.set_ylim(y_min, y_max)
        ax_s.set_xticks([])
        ax_s.set_yticks([])
        ax_s.set_facecolor('white')
        for spine in ax_s.spines.values():
            spine.set_color(panel['frame'])
            spine.set_linewidth(2.4)

        fig_s.tight_layout()
        fig_s.savefig(panel['name'], dpi=300, bbox_inches='tight', facecolor='#efefef')
        plt.close(fig_s)


if __name__ == '__main__':
    generate_nearest_neighbor_classifier_distribution()
