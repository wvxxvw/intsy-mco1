import java.util.*;

public class BFS {
    private Grid grid;
    private boolean[][] visited;
    private boolean flagCaptured = false;
    private boolean gameOver = false;
    private int steps = 0;
    private boolean slowMode;

    // BFS Constructor
    public BFS(Grid grid, boolean slowMode) {
        this.grid = grid;
        this.visited = new boolean[grid.getSize()][grid.getSize()];
        this.slowMode = slowMode;
    }

    public void start() {
        System.out.println("\n--- The England’s Game Begins (Breadth-First Search) ---");
        long startTime = System.currentTimeMillis();

        int startRow = 0, startCol = 0;
        Vector2 start = new Vector2(startRow, startCol);
        Vector2 flag = findCoords(Grid.FLAG);
        Vector2 exit = findCoords(Grid.EXIT);

        // Find path from agent using BFS to flag
        ArrayList<Vector2> pathToFlag = bfsPath(start, flag, "Flag");
        if (pathToFlag == null) {
            System.out.println("No path to flag found... The field is unforgiving.");
            return;
        }

        // Move along that path, one tile at a time
        moveAlongPath(pathToFlag);
        // Flag capture conditional
        flagCaptured = true;
        System.out.println("Flag captured at " + flag.getVector() + "!");

        // Reset visited for second phase
        visited = new boolean[grid.getSize()][grid.getSize()];

        // Now BFS from flag to exit
        ArrayList<Vector2> pathToExit = bfsPath(flag, exit, "Exit");
        if (pathToExit == null) {
            System.out.println("No path to exit found... The trench has claimed another soul.");
            return;
        }

        // Move along path again
        moveAlongPath(pathToExit);

        gameOver = true;
        System.out.println("Agent reached the exit safely!");

        long endTime = System.currentTimeMillis();
        System.out.println("\nTotal steps: " + steps);
        System.out.println("Elapsed time: " + (endTime - startTime) + "ms");
    }

    // BFS algorithm that returns the full path as a list of Vector2 positions
    private ArrayList<Vector2> bfsPath(Vector2 start, Vector2 target, String phaseName) {
        Queue<Vector2> queue = new LinkedList<>();
        Map<Vector2, Vector2> parent = new HashMap<>();
        visited[start.getVector2_x()][start.getVector2_y()] = true;
        queue.add(start);

        int[][] moves = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}; // standard 4-directional BFS

        System.out.println("\n=== BFS Exploration Phase: " + phaseName + " ===");
        System.out.println("Queue Log (tiles checked in BFS order):");

        while (!queue.isEmpty()) {
            Vector2 current = queue.poll();
            int r = current.getVector2_x();
            int c = current.getVector2_y();

            System.out.print(current.getVector() + " "); // prints out tiles as they’re dequeued
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            int cell = grid.getCell(r, c);
            if (cell == Grid.MINE || cell == Grid.WALL)
                continue;

            if (current.compare(target)) {
                System.out.println("\nReached " + phaseName + " at " + target.getVector() + "!");
                // Reconstruct path backwards
                ArrayList<Vector2> path = new ArrayList<>();
                while (current != null) {
                    path.add(current);
                    current = parent.get(current);
                }
                Collections.reverse(path);
                return path;
            }
            // Checks tile validity
            for (int[] mv : moves) {
                int newR = r + mv[0];
                int newC = c + mv[1];
                if (grid.inBounds(newR, newC) && !visited[newR][newC]) {
                    int cellType = grid.getCell(newR, newC);

                    if (cellType == Grid.MINE || cellType == Grid.WALL)
                        continue;
                    if (cellType == Grid.EXIT && !flagCaptured)
                        continue;

                    Vector2 neighbor = new Vector2(newR, newC);
                    queue.add(neighbor);
                    visited[newR][newC] = true;
                    parent.put(neighbor, current);
                }
            }
        }
        System.out.println("\nNo path found during BFS " + phaseName + " phase...");
        return null; // no path found
    }

    // Makes the agent walk along a computed path step by step
    private void moveAlongPath(ArrayList<Vector2> path) {
        System.out.println("\nWalking path (" + path.size() + " tiles):");
        for (int i = 1; i < path.size(); i++) { // skip starting tile
            Vector2 stepCell = path.get(i);
            System.out.println("->> Moving to " + stepCell.getVector());
            simulateMovement(stepCell.getVector2_x(), stepCell.getVector2_y());
        }
    }

    // Finds coordinates of a specific cell type (flag or exit)
    private Vector2 findCoords(int target) {
        for (int i = 0; i < grid.getSize(); i++) {
            for (int j = 0; j < grid.getSize(); j++) {
                if (grid.getCell(i, j) == target)
                    return new Vector2(i, j);
            }
        }
        return null;
    }

    // Moves agent one tile at a time (no diagonal movement)
    private void simulateMovement(int destRow, int destCol) {
        int currentRow = -1, currentCol = -1;

        // Finds current agent position
        for (int r = 0; r < grid.getSize(); r++) {
            for (int c = 0; c < grid.getSize(); c++) {
                if (grid.getCell(r, c) == Grid.AGENT) {
                    currentRow = r;
                    currentCol = c;
                    break;
                }
            }
        }

        if (currentRow == -1) currentRow = 0;
        if (currentCol == -1) currentCol = 0;

        // Move one tile exactly (no diagonals)
        int dRow = Integer.compare(destRow, currentRow);
        int dCol = Integer.compare(destCol, currentCol);

        int nextRow = currentRow + dRow;
        int nextCol = currentCol + dCol;

        if (!grid.inBounds(nextRow, nextCol)) return;

        int nextCell = grid.getCell(nextRow, nextCol);
        if (nextCell == Grid.MINE || nextCell == Grid.WALL) return;

        if (nextCell == Grid.EXIT && !flagCaptured) {
            System.out.println("Exit locked. Must find the flag first!");
            return;
        }

        // Actually moves the agent
        grid.setCell(currentRow, currentCol, Grid.EMPTY);
        grid.setCell(nextRow, nextCol, Grid.AGENT);

        int nearbyMines = grid.countNearbyMines(nextRow, nextCol);
        System.out.println("Step " + (++steps) + ": Agent at (" + nextRow + "," + nextCol + ") | Nearby Mines: " + nearbyMines);
        if (nearbyMines > 0)
            System.out.println("Nearby mines detected. Stay cautious...");

        grid.printGrid();
        grid.waitForNextStep(slowMode);
    }
}
