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
        System.out.println("\n--- The Fisherman's Journey Begins (DFS Stack Version) ---");
        long startTime = System.currentTimeMillis();

        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{0, 0});

        while (!stack.isEmpty() && !gameOver) {

            int[] current = stack.pop();
            int row = current[0];
            int col = current[1];

            if (!grid.inBounds(row, col) || visited[row][col]) continue;

            int cell = grid.getCell(row, col);
            if (cell == Grid.WALL || cell == Grid.MINE) continue;
            if (cell == Grid.EXIT && !flagCaptured) continue;

            visited[row][col] = true;

            int danger = grid.countNearbyMines(row, col);
            moveAgentStepByStep(row, col, danger);

            System.out.println("Step " + (++steps) + ": Agent at (" + row + "," + col + ") | Nearby Mines: " + danger);

            if (danger > 0)
                System.out.println("Warning: Mines nearby!");

            if (cell == Grid.FLAG && !flagCaptured) {
                flagCaptured = true;
                System.out.println("Flag captured!");
            }

            if (cell == Grid.EXIT && flagCaptured) {
                System.out.println("Exit reached safely!");
                gameOver = true;
                break;
            }

            // Collect neighbors
            List<int[]> neighbors = new ArrayList<>();
            int[][] moves = {{1,0},{-1,0},{0,1},{0,-1}};

            for (int[] m : moves) {
                int nr = row + m[0];
                int nc = col + m[1];

                if (!grid.inBounds(nr, nc)) continue;
                if (visited[nr][nc]) continue;

                int nextCell = grid.getCell(nr, nc);
                if (nextCell == Grid.WALL || nextCell == Grid.MINE) continue;
                if (nextCell == Grid.EXIT && !flagCaptured) continue;

                int d = grid.countNearbyMines(nr, nc);
                neighbors.add(new int[]{nr, nc, d});
            }

            // Sort by danger (lower danger explored first)
            neighbors.sort(Comparator.comparingInt(a -> a[2]));

            for (int i = neighbors.size() - 1; i >= 0; i--) {
                stack.push(new int[]{neighbors.get(i)[0], neighbors.get(i)[1]});
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\nTotal steps: " + steps);
        System.out.println("Elapsed time: " + (endTime - startTime) + "ms");
    }

    private void moveAgentStepByStep(int destRow, int destCol, int dangerLevel) {

        int currentRow = -1, currentCol = -1;

        // Find agent
        for (int r = 0; r < grid.getSize(); r++) {
            for (int c = 0; c < grid.getSize(); c++) {
                if (grid.getCell(r, c) == Grid.AGENT) {
                    currentRow = r;
                    currentCol = c;
                    break;
                }
            }
            if (currentRow != -1) break;
        }

        if (currentRow == -1) currentRow = 0;
        if (currentCol == -1) currentCol = 0;

        // Walk one tile at a time to dest
        while (currentRow != destRow || currentCol != destCol) {

            int nextRow = currentRow;
            int nextCol = currentCol;

            if (currentRow < destRow) nextRow++;
            else if (currentRow > destRow) nextRow--;
            else if (currentCol < destCol) nextCol++;
            else if (currentCol > destCol) nextCol--;

            // Stop if blocked
            int nextCell = grid.getCell(nextRow, nextCol);
            if (nextCell == Grid.WALL) {
                System.out.println("Blocked by wall at (" + nextRow + "," + nextCol + ")");
                return;
            }
            if (nextCell == Grid.MINE) {
                System.out.println("Mine ahead at (" + nextRow + "," + nextCol + "). Path aborted.");
                return;
            }
            if (nextCell == Grid.EXIT && !flagCaptured) {
                System.out.println("Exit locked. Must capture flag first!");
                return;
            }

            grid.setCell(currentRow, currentCol, Grid.EMPTY);
            grid.setCell(nextRow, nextCol, Grid.AGENT);

            grid.printGrid();
            grid.waitForNextStep(slowMode);

            grid.setCell(nextRow, nextCol, Grid.AGENT);

            currentRow = nextRow;
            currentCol = nextCol;
        }
    }
}
