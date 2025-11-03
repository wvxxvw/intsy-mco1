import java.util.*;

public class Greedy {
    private Grid grid;
    private boolean[][] visited;
    private boolean flagCaptured = false;
    private boolean gameOver = false;
    private int steps = 0;
    private boolean slowMode;

    public Greedy(Grid grid, boolean slowMode) {
        this.grid = grid;
        this.visited = new boolean[grid.getSize()][grid.getSize()];
        this.slowMode = slowMode;
    }

    // Manhattan Distance heuristic
    private int heuristic(int r, int c, int targetR, int targetC) {
        return Math.abs(r - targetR) + Math.abs(c - targetC);
    }

    public void start() {
        System.out.println("\n--- The Raider’s Instinct (Greedy Best-First Search) ---");
        long startTime = System.currentTimeMillis();

        int currentRow = 0, currentCol = 0;
        visited[currentRow][currentCol] = true;

        while (!gameOver) {
            grid.printGrid();
            int nearbyMines = grid.countNearbyMines(currentRow, currentCol);
            System.out.println("Step " + steps + ": Agent at (" + currentRow + "," + currentCol + ") | Nearby Mines: " + nearbyMines);
            if (nearbyMines > 0) System.out.println("Mines detected nearby. Tread carefully...");

            int cell = grid.getCell(currentRow, currentCol);

            // Reactions
            if (cell == Grid.MINE) {
                System.out.println("BOOM! The Raider stepped on a mine. The trench devours another soul...");
                gameOver = true;
                break;
            }

            if (cell == Grid.FLAG && !flagCaptured) {
                flagCaptured = true;
                System.out.println("The Flag is captured! The way to the Exit is now clear...");
                visited = new boolean[grid.getSize()][grid.getSize()];
                visited[currentRow][currentCol] = true;
            }

            if (cell == Grid.EXIT && flagCaptured) {
                System.out.println("Agent reached the Exit safely! Victory through instinct!");
                gameOver = true;
                break;
            }

            // Generate neighboring moves
            int[][] moves = {{1,0}, {-1,0}, {0,1}, {0,-1}};
            List<int[]> validMoves = new ArrayList<>();

            for (int[] mv : moves) {
                int nr = currentRow + mv[0];
                int nc = currentCol + mv[1];

                if (!grid.inBounds(nr, nc) || visited[nr][nc]) continue;

                int nextCell = grid.getCell(nr, nc);
                if (nextCell == Grid.WALL || nextCell == Grid.MINE) continue;
                if (nextCell == Grid.EXIT && !flagCaptured) continue;

                int targetR = flagCaptured ? grid.getERow() : grid.getFRow();
                int targetC = flagCaptured ? grid.getECol() : grid.getFCol();
                int h = heuristic(nr, nc, targetR, targetC);

                validMoves.add(new int[]{nr, nc, h});
            }

            // No valid moves — stuck
            if (validMoves.isEmpty()) {
                System.out.println("Dead end... The Raider backtracks instinctively...");
                int[] back = findSafeBacktrack(currentRow, currentCol);
                if (back == null) {
                    System.out.println("No way out... The Raider perishes in the dark trench.");
                    break;
                }
                grid.setCell(currentRow, currentCol, Grid.EMPTY);
                grid.setCell(back[0], back[1], Grid.AGENT);
                currentRow = back[0];
                currentCol = back[1];
                steps++;
                grid.waitForNextStep(slowMode);
                continue;
            }

            // Choose best move (lowest heuristic)
            validMoves.sort(Comparator.comparingInt(a -> a[2]));
            int[] best = validMoves.get(0);
            int nextRow = best[0];
            int nextCol = best[1];

            // Move one step
            grid.setCell(currentRow, currentCol, Grid.EMPTY);
            grid.setCell(nextRow, nextCol, Grid.AGENT);
            visited[nextRow][nextCol] = true;

            currentRow = nextRow;
            currentCol = nextCol;
            steps++;

            grid.waitForNextStep(slowMode);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\nTotal steps: " + steps);
        System.out.println("Elapsed time: " + (endTime - startTime) + "ms");
    }

    // Finds a safe previously visited tile to backtrack to
    private int[] findSafeBacktrack(int row, int col) {
        int[][] moves = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] mv : moves) {
            int nr = row + mv[0];
            int nc = col + mv[1];
            if (grid.inBounds(nr, nc) && visited[nr][nc]) {
                return new int[]{nr, nc};
            }
        }
        return null;
    }
}
