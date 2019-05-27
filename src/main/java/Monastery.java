import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * 参考：https://train.usaco.org/usacoanal2?a=LdbgoDfypjW&S=castle
 */
public class Monastery {

    private static class WeightedQuickUnionUF {
        private int[] parent;
        private int[] size;
        private int count;

        WeightedQuickUnionUF(int n) {
            count = n;
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int p) {
            while (p != parent[p])
                p = parent[p];
            return p;
        }

        void union(int p, int q) {
            int rootP = find(p);
            int rootQ = find(q);
            if (rootP == rootQ) return;

            if (size[rootP] < size[rootQ]) {
                parent[rootP] = rootQ;
                size[rootQ] += size[rootP];
            } else {
                parent[rootQ] = rootP;
                size[rootP] += size[rootQ];
            }
            count--;
        }

        void union(int r, int c, Direction dir) {
            int p = r * COLS + c;
            int q = dir.convert(r, c);
            if (q < 0 || q >= ROWS * COLS) return;
            this.union(p, q);
        }

        void simulateUnion(int r, int c, Direction dir) {
            int p = r * COLS + c;
            int q = dir.convert(r, c);
            if (q < 0 || q >= ROWS * COLS) return;

            int rootP = find(p);
            int rootQ = find(q);
            if (rootP == rootQ) return;

            if (size[rootP] + size[rootQ] > MAX) {
                MAX = size[rootP] + size[rootQ];
                R = r + 1;
                C = c + 1;
                DIR = dir;
            }
        }
    }

    private static int COLS, ROWS, MAX, R, C;

    private static Direction DIR = Direction.NORTH;

    private enum Direction {
        WEST(1), NORTH(2), EAST(4), SOUTH(8);

        private static Direction[] TO_REMOVE = {NORTH, EAST};

        private final int code;

        Direction(int code) {
            this.code = code;
        }

        int convert(int r, int c) {
            if (this == WEST && c > 0) return r * COLS + c - 1;
            else if (this == NORTH && r > 1) return (r - 1) * COLS + c;
            else if (this == EAST && c < COLS - 1) return r * COLS + c + 1;
            else if (this == SOUTH && r < ROWS - 1) return (r + 1) * COLS + c;
            else return -1;
        }
    }

    /**
     * 返回修道院内部结构关键信息以及最佳修缮策略
     *
     * @param rooms 房间布局图，值为上下左右四个方向是否有墙的映射值之和
     * @return 修道院内部结构关键信息，累计包含6个元素，具体参见题目说明
     */
    public int[] resolve(int[][] rooms) {
        ROWS = rooms.length;
        COLS = rooms[0].length;
        WeightedQuickUnionUF uf = new WeightedQuickUnionUF(ROWS * COLS);
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int val = rooms[i][j];
                for (Direction dir : Direction.values()) {
                    if ((val & dir.code) == 0) {
                        uf.union(i, j, dir);
                    }
                }
            }
        }

        MAX = R = C = 0;
        for (int j = 0; j < COLS; j++) {
            for (int i = ROWS - 1; i >= 0; i--) {
                int val = rooms[i][j];
                for (Direction direction : Direction.TO_REMOVE) {
                    if ((val & direction.code) == direction.code) {
                        uf.simulateUnion(i, j, direction);
                    }
                }
            }
        }
        return new int[]{
            uf.count,
            IntStream.of(uf.size).max().orElse(1),
            MAX,
            R, C, DIR.code
        };
    }

    public static void main(String[] args) {
        final long now = System.currentTimeMillis();
        System.out.println(Arrays.toString(new Monastery().resolve(
            new int[][]{
                {3, 2, 6, 3, 6},
                {1, 8, 4, 1, 4},
                {13, 7, 13, 9, 4},
                {3, 0, 2, 6, 5},
                {9, 8, 8, 12, 13}
            }
        )));
        System.out.printf("Time Spent: %.2fS%n", (System.currentTimeMillis() - now) / 1000.0);
    }

}
