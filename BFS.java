import java.util.*;

public class BFS {
    private Grid grid;
    private boolean[][] visited;
    private boolean flagCaptured = false;
    private boolean gameOver = false;
    private int steps = 0;
    private int prevRow = 0, prevCol = 0; // track agent position

    public BFS(Grid grid) {
        this.grid = grid;
        this.visited = new boolean[grid.getSize()][grid.getSize()];
    }

    public void start() {
        System.out.println("\n--- Starting Breadth-First Search ---");
        long startTime = System.currentTimeMillis();

        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{0, 0});
        visited[0][0] = true;

        while (!queue.isEmpty() && !gameOver) {
            int[] pos = queue.poll();
            int row = pos[0];
            int col = pos[1];

            // Read original cell BEFORE we overwrite it with AGENT
            int origCell = grid.getCell(row, col);

            // If this is an EXIT and flag not yet captured, do not step onto it — skip moving agent here.
            if (origCell == Grid.EXIT && !flagCaptured) {
                // we don't visually move the agent into a locked exit
                // but still allow BFS to explore from this node (it stays in the frontier)
            } else {
                // Move agent visually: clear previous and set new position
                grid.setCell(prevRow, prevCol, Grid.EMPTY);
                grid.setCell(row, col, Grid.AGENT);
                prevRow = row; prevCol = col;

                steps++;
                int nearbyMines = grid.countNearbyMines(row, col);
                System.out.println("\nStep " + steps + ": Agent moved to (" + row + "," + col + ") | Nearby Mines: " + nearbyMines);
                grid.printGrid();

                // Now check the original cell for events (mine/flag/exit)
                if (origCell == Grid.MINE) {
                    System.out.println("Agent stepped on a mine at (" + row + "," + col + "). Game Over!");
                    gameOver = true;
                    break;
                }

                if (origCell == Grid.FLAG) {
                    flagCaptured = true;
                    System.out.println("Flag captured at (" + row + "," + col + ")!");
                }

                if (origCell == Grid.EXIT && flagCaptured) {
                    System.out.println("Agent reached the exit successfully!");
                    gameOver = true;
                    break;
                }
            }

            // Enqueue neighbors using passability rule that respects exit-locking
            int[][] moves = {{1,0}, {-1,0}, {0,1}, {0,-1}};
            for (int[] mv : moves) {
                int newRow = row + mv[0];
                int newCol = col + mv[1];
                if (grid.inBounds(newRow, newCol) && !visited[newRow][newCol]) {
                    // only enqueue if passable given flagCaptured
                    if (grid.isPassable(newRow, newCol, flagCaptured)) {
                        visited[newRow][newCol] = true;
                        queue.add(new int[]{newRow, newCol});
                    }
                }
            }

            // small delay for readability (optional)
            try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        long endTime = System.currentTimeMillis();
        if (!gameOver) System.out.println("No path found or exit unreachable.");

        System.out.println("\nTotal steps: " + steps);
        System.out.println("Elapsed time: " + (endTime - startTime) + "ms");
    }
}
