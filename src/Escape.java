import java.util.Arrays;

/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int[][] open = Graphs.open(w, broken);
        int n = w.cells();
        int exit = n - 1;

        int deadEnds = 0;
        for (int[] neighbours : open) {
            if (neighbours.length == 1) {
                deadEnds++;
            }
        }

        boolean[] seen = new boolean[n];
        int[] parent = new int[n];
        int[] nextDirection = new int[n];
        int[] stack = new int[n];
        Arrays.fill(parent, -1);

        int top = 0;
        int visited = 1;
        stack[0] = 0;
        seen[0] = true;

        while (top >= 0 && !seen[exit]) {
            int cell = stack[top];
            if (nextDirection[cell] == 4) {
                top--;
                continue;
            }

            int direction = nextDirection[cell]++;
            int row = cell / w.cols();
            int col = cell % w.cols();
            int next = -1;
            if (direction == 0 && row > 0) next = cell - w.cols();
            if (direction == 1 && col + 1 < w.cols()) next = cell + 1;
            if (direction == 2 && row + 1 < w.rows()) next = cell + w.cols();
            if (direction == 3 && col > 0) next = cell - 1;

            if (next >= 0 && !seen[next] && contains(open[cell], next)) {
                seen[next] = true;
                parent[next] = cell;
                visited++;
                stack[++top] = next;
            }
        }

        if (!seen[exit]) {
            return new Result(new int[0], visited, deadEnds);
        }

        int length = 1;
        for (int cell = exit; cell != 0; cell = parent[cell]) {
            length++;
        }
        int[] path = new int[length];
        int cell = exit;
        for (int i = length - 1; i >= 0; i--) {
            path[i] = cell;
            cell = parent[cell];
        }
        return new Result(path, visited, deadEnds);
    }

    private static boolean contains(int[] neighbours, int wanted) {
        for (int cell : neighbours) {
            if (cell == wanted) {
                return true;
            }
        }
        return false;
    }
}
