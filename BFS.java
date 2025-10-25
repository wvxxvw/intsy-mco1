import java.util.*;

public class BFS {
    private Grid grid;
    private boolean[][] visited;
    private boolean flagCaptured = false;
    private boolean gameOver = false;
    private int steps = 0;
    private boolean slowMode;

    public BFS(Grid grid, boolean slowMode) {
        this.grid = grid;
        this.visited = new boolean[grid.getSize()][grid.getSize()];
        this.slowMode = slowMode;
    }

    public void start() {
        System.out.println("\n--- The England’s Game Begins (Breadth-First Search) ---");
        long startTime = System.currentTimeMillis();

        int startRow = 0, startCol = 0;
        Queue<int[]> queue = new LinkedList<>();
        visited[startRow][startCol] = true;
        queue.add(new int[]{startRow, startCol});

        while (!queue.isEmpty() && !gameOver) {
            int[] current = queue.poll();
            int row = current[0];
            int col = current[1];

            int cell = grid.getCell(row, col);

            // Skip unpassable tiles
            if (cell == Grid.MINE || cell == Grid.WALL) {
                continue;
            }

            // Move agent visually (step-by-step)
            simulateMovement(row, col);

            int nearbyMines = grid.countNearbyMines(row, col);
            System.out.println("Step " + (++steps) + ": Agent at (" + row + "," + col + ") | Nearby Mines: " + nearbyMines);
            if (nearbyMines > 0) System.out.println("Nearby mines detected. Stay cautious...");

            // Check special tiles
            if (cell == Grid.FLAG && !flagCaptured) {
                flagCaptured = true;
                System.out.println("Flag captured at (" + row + "," + col + ")!");
                // Reset BFS from this new starting point
                visited = new boolean[grid.getSize()][grid.getSize()];
                queue.clear();
                queue.add(new int[]{row, col});
                visited[row][col] = true;
                continue;
            }

            if (cell == Grid.EXIT && flagCaptured) {
                System.out.println("Agent reached the exit safely!");
                gameOver = true;
                break;
            }

            // Explore BFS neighbors (down, up, right, left)
            int[][] moves = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            for (int[] mv : moves) {
                int newRow = row + mv[0];
                int newCol = col + mv[1];
                if (grid.inBounds(newRow, newCol) && !visited[newRow][newCol]) {
                    int nextCell = grid.getCell(newRow, newCol);

                    // Avoid impassable tiles
                    if (nextCell == Grid.MINE || nextCell == Grid.WALL) continue;

                    // Exit locked before flag
                    if (nextCell == Grid.EXIT && !flagCaptured) continue;

                    visited[newRow][newCol] = true;
                    queue.add(new int[]{newRow, newCol});
                }
            }
        }

        long endTime = System.currentTimeMillis();

        if (!gameOver)
            System.out.println("\nNo viable path left... The trench has claimed another soul.");

        System.out.println("\nTotal steps: " + steps);
        System.out.println("Elapsed time: " + (endTime - startTime) + "ms");
    }

    // Moves agent one tile at a time toward target coordinates safely
    private void simulateMovement(int destRow, int destCol) {
        int currentRow = -1, currentCol = -1;

        // Find agent’s current position
        for (int r = 0; r < grid.getSize(); r++) {
            for (int c = 0; c < grid.getSize(); c++) {
                if (grid.getCell(r, c) == Grid.AGENT) {
                    currentRow = r;
                    currentCol = c;
                }
            }
        }

        if (currentRow == -1) currentRow = 0;
        if (currentCol == -1) currentCol = 0;

        // Step-by-step walking
        while (currentRow != destRow || currentCol != destCol) {
            int dRow = Integer.compare(destRow, currentRow);
            int dCol = Integer.compare(destCol, currentCol);

            int nextRow = currentRow + dRow;
            int nextCol = currentCol + dCol;

            // Check if next step is safe
            if (!grid.inBounds(nextRow, nextCol)) break;

            int nextCell = grid.getCell(nextRow, nextCol);

            // Avoid mines and walls during actual movement
            if (nextCell == Grid.MINE || nextCell == Grid.WALL) {
                System.out.println("Blocked at (" + nextRow + "," + nextCol + "). Changing path...");
                return;
            }

            // Exit early if locked
            if (nextCell == Grid.EXIT && !flagCaptured) {
                System.out.println("Exit locked. Must find the flag first!");
                return;
            }

            // Move
            grid.setCell(currentRow, currentCol, Grid.EMPTY);
            grid.setCell(nextRow, nextCol, Grid.AGENT);
            currentRow = nextRow;
            currentCol = nextCol;

            grid.printGrid();
            grid.waitForNextStep(slowMode);
        }
    }
}
 