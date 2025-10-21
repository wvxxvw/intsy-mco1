import java.util.*;

public class DFS {
    private Grid grid;
    private boolean[][] visited;
    private boolean flagCaptured = false;
    private boolean gameOver = false;
    private int steps = 0;
    private int prevRow = 0, prevCol = 0;

    public DFS(Grid grid) {
        this.grid = grid;
        this.visited = new boolean[grid.getSize()][grid.getSize()];
    }

    public void start() {
        System.out.println("\n--- Starting Depth-First Search: The Fisherman ---");

        dfs(0, 0);

        if (!gameOver) {
            System.out.println("No path found or exit unreachable.");
        }

        System.out.println("\nTotal steps: " + steps);
    }

    private void dfs(int row, int col) {
        // Stop recursion if game is already finished
        if (gameOver) return;

        // Checks the grid boundary and if that cell was visited or not
        if (!grid.inBounds(row, col) || visited[row][col]) return;

        // It checks if the agent can go to that cell or not (wall or cannot exit if no flag)
        if (!grid.isPassable(row, col, flagCaptured)) return;

        visited[row][col] = true;

        // saves what the original cell was before going to it
        int origCell = grid.getCell(row, col);

        // Move agent visually
        grid.setCell(prevRow, prevCol, Grid.EMPTY);
        grid.setCell(row, col, Grid.AGENT);
        prevRow = row; prevCol = col;

        steps++;
        int nearbyMines = grid.countNearbyMines(row, col);
        System.out.println("\nStep " + steps + ": Agent moved to (" + row + "," + col + ") | Nearby Mines: " + nearbyMines);
        grid.printGrid();

        // Check conditions for winning or losing
        if (origCell == Grid.MINE) {
            System.out.println("Agent stepped on a mine at (" + row + "," + col + "). Game Over!");
            gameOver = true;
            return;
        }

        if (origCell == Grid.FLAG) {
            flagCaptured = true;
            System.out.println("Flag captured at (" + row + "," + col + ")!");
        }

        if (origCell == Grid.EXIT && flagCaptured) {
            System.out.println("Agent reached the exit successfully!");
            gameOver = true;
            return;
        }

        // Explore in 4 directions (up, down, left, right)
        int[][] moves = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        for (int[] mv : moves) {
            dfs(row + mv[0], col + mv[1]);
            if (gameOver) return; // Stop after mission success/failure
        }
    }
}
