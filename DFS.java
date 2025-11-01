import java.util.*;

public class DFS {
    private Grid grid;
    private boolean[][] visited;
    private boolean flagCaptured = false;
    private boolean gameOver = false;
    private int steps = 0;
    private boolean slowMode;

    public DFS(Grid grid, boolean slowMode) {
        this.grid = grid;
        this.visited = new boolean[grid.getSize()][grid.getSize()];
        this.slowMode = slowMode;
    }

    public void start() {
        System.out.println("\n--- The Fisherman’s Journey Begins (Depth-First Search) ---");
        long startTime = System.currentTimeMillis();

        dfs(0, 0);

        long endTime = System.currentTimeMillis();

        if (!gameOver)
            System.out.println("\nNo path to victory... the trenches swallowed the agent whole.");

        System.out.println("\nTotal steps: " + steps);
        System.out.println("Elapsed time: " + (endTime - startTime) + "ms");
    }

    private void dfs(int row, int col) {
        if (gameOver) return;
        if (!grid.inBounds(row, col) || visited[row][col]) return;

        int cell = grid.getCell(row, col);
        if (cell == Grid.WALL || cell == Grid.MINE) return;
        if (cell == Grid.EXIT && !flagCaptured) return;

        visited[row][col] = true;

        // Sense danger around this cell
        int nearbyMines = grid.countNearbyMines(row, col);

        simulateMovement(row, col, nearbyMines);

        System.out.println("Step " + (++steps) + ": Agent at (" + row + "," + col + ") | Nearby Mines: " + nearbyMines);
        if (nearbyMines > 0)
            System.out.println("Warning: Mines nearby! Avoiding dangerous direction...");

        if (cell == Grid.FLAG && !flagCaptured) {
            flagCaptured = true;
            System.out.println("Flag captured at (" + row + "," + col + ")!");
        }

        if (cell == Grid.EXIT && flagCaptured) {
            System.out.println("Exit reached safely!");
            gameOver = true;
            return;
        }

        int[][] moves = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        List<int[]> safeMoves = new ArrayList<>();

        for (int[] mv : moves) {
            int nr = row + mv[0];
            int nc = col + mv[1];
            if (grid.inBounds(nr, nc) && !visited[nr][nc]) {
                int next = grid.getCell(nr, nc);
                if (next == Grid.WALL || next == Grid.MINE) continue;
                if (next == Grid.EXIT && !flagCaptured) continue;
                int danger = grid.countNearbyMines(nr, nc);
                safeMoves.add(new int[]{nr, nc, danger});
            }
        }

        // Sort moves by safety
        safeMoves.sort(Comparator.comparingInt(a -> a[2]));

        // Explore deeper
        for (int[] move : safeMoves) {
            dfs(move[0], move[1]);
            if (gameOver) return;
        }

        grid.setCell(row, col, Grid.EMPTY);
        grid.printGrid();
        grid.waitForNextStep(slowMode);
    }

    private void simulateMovement(int destRow, int destCol, int dangerLevel) {

        for (int r = 0; r < grid.getSize(); r++) {
            for (int c = 0; c < grid.getSize(); c++) {
                if (grid.getCell(r, c) == Grid.AGENT) {
                    grid.setCell(r, c, Grid.EMPTY);
                }
            }
        }

        if (dangerLevel > 0) {
            grid.setCell(destRow, destCol, 8);
        } else {
            grid.setCell(destRow, destCol, Grid.AGENT);
        }

        grid.printGrid();
        grid.waitForNextStep(slowMode);

        grid.setCell(destRow, destCol, Grid.AGENT);
    }
}
