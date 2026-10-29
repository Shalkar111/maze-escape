import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] broken = new int[w.cells() - 1];
        int count = 0;

        for (int k = 0; k < w.count() && count < broken.length; k++) {
            if (dsu.union(w.a()[k], w.b()[k])) {
                broken[count++] = k;
            }
        }
        return Arrays.copyOf(broken, count);
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        Integer[] order = new Integer[w.count()];
        for (int k = 0; k < order.length; k++) {
            order[k] = k;
        }
        Arrays.sort(order, (x, y) -> Integer.compare(w.cost()[x], w.cost()[y]));

        DisjointSet dsu = new DisjointSet(w.cells());
        int[] broken = new int[w.cells() - 1];
        int count = 0;
        for (int k : order) {
            if (dsu.union(w.a()[k], w.b()[k])) {
                broken[count++] = k;
                if (count == broken.length) {
                    break;
                }
            }
        }
        return Arrays.copyOf(broken, count);
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells();
        int[] degree = new int[n];
        for (int k = 0; k < w.count(); k++) {
            degree[w.a()[k]]++;
            degree[w.b()[k]]++;
        }

        int[][] edges = new int[n][];
        for (int cell = 0; cell < n; cell++) {
            edges[cell] = new int[degree[cell]];
        }
        int[] filled = new int[n];
        for (int k = 0; k < w.count(); k++) {
            edges[w.a()[k]][filled[w.a()[k]]++] = k;
            edges[w.b()[k]][filled[w.b()[k]]++] = k;
        }

        boolean[] inTree = new boolean[n];
        PriorityQueue<Integer> queue = new PriorityQueue<>(
                (x, y) -> Integer.compare(w.cost()[x], w.cost()[y]));
        inTree[0] = true;
        for (int edge : edges[0]) {
            queue.add(edge);
        }

        long total = 0;
        int added = 1;
        while (added < n && !queue.isEmpty()) {
            int edge = queue.remove();
            int a = w.a()[edge];
            int b = w.b()[edge];
            if (inTree[a] && inTree[b]) {
                continue;
            }
            int next = inTree[a] ? b : a;
            inTree[next] = true;
            total += w.cost()[edge];
            added++;
            for (int nextEdge : edges[next]) {
                queue.add(nextEdge);
            }
        }
        return total;
    }
}
