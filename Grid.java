import java.util.Random;

public class Grid {
    private final int SIZE = 10;
    private final int[][] grid = new int[SIZE][SIZE];
    private int[][] backup;

    public static final int EMPTY = 0;
    public static final int MINE = 1;
    public static final int WALL = 2;
    public static final int FLAG = 3;
    public static final int EXIT = 4;
    public static final int AGENT = 9;

    private int FRow, FCol, ERow, ECol;

    public Grid() {
        generateGrid();
        backupGrid();
    }

    public void waitForNextStep(boolean slowMode) {
        if (slowMode) {
            System.out.print("Press C to continue... ");
            try {
                while (true) {
                    int key = System.in.read();
                    if (key == 'C' || key == 'c') break;
                }
            } catch (Exception e) {
                System.out.println("Error waiting for input.");
            }
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void generateGrid() {
        Random rand = new Random();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int randomize = rand.nextInt(100);
                if (randomize < 10) grid[row][col] = MINE;
                else if (randomize < 20) grid[row][col] = WALL;
                else grid[row][col] = EMPTY;
            }
        }

        grid[0][0] = AGENT;

        do {
            FRow = rand.nextInt(SIZE);
            FCol = rand.nextInt(SIZE);
        } while ((FRow == 0 && FCol == 0) || (FRow <= 1 && FCol <= 1) || grid[FRow][FCol] != EMPTY);

        do {
            ERow = rand.nextInt(SIZE);
            ECol = rand.nextInt(SIZE);
        } while ((ERow == 0 && ECol == 0) || (ERow == FRow && ECol == FCol) || grid[ERow][ECol] != EMPTY);

        grid[FRow][FCol] = FLAG;
        grid[ERow][ECol] = EXIT;
    }

    private void backupGrid() {
        backup = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++)
            System.arraycopy(grid[r], 0, backup[r], 0, SIZE);
    }

    public void restoreGrid() {
        for (int r = 0; r < SIZE; r++)
            System.arraycopy(backup[r], 0, grid[r], 0, SIZE);
    }

    public int getSize() { return SIZE; }
    public int getCell(int row, int col) { return grid[row][col]; }
    public void setCell(int row, int col, int val) { grid[row][col] = val; }
    public void clearCell(int row, int col) { grid[row][col] = EMPTY; }

    public boolean inBounds(int row, int col) {
        return row >= 0 && col >= 0 && row < SIZE && col < SIZE;
    }

    public boolean isPassable(int row, int col, boolean flagCaptured) {
        if (!inBounds(row, col)) return false;
        int cell = grid[row][col];
        if (cell == WALL) return false;
        if (cell == EXIT && !flagCaptured) return false;
        return true;
    }

    public int countNearbyMines(int row, int col) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = row + dr, nc = col + dc;
                if (inBounds(nr, nc) && grid[nr][nc] == MINE) count++;
            }
        }
        return count;
    }

    public void printGrid() {
        final String RESET = "\u001B[0m";
        final String RED = "\u001B[31m";
        final String GREEN = "\u001B[32m";
        final String YELLOW = "\u001B[33m";
        final String BLUE = "\u001B[34m";
        final String GRAY = "\u001B[90m";

        System.out.println("\nLegend: " + BLUE + "9=Agent " + GREEN + "3=Flag " +
                YELLOW + "4=Exit " + RED + "1=Mine " +
                GRAY + "2=Wall " + RESET + "0=Empty\n");

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                switch (grid[row][col]) {
                    case MINE -> System.out.print(RED + "1 " + RESET);
                    case WALL -> System.out.print(GRAY + "2 " + RESET);
                    case FLAG -> System.out.print(GREEN + "3 " + RESET);
                    case EXIT -> System.out.print(YELLOW + "4 " + RESET);
                    case AGENT -> System.out.print(BLUE + "9 " + RESET);
                    default -> System.out.print("0 ");
                }
            }
            System.out.println();
        }
    }

    public int getFRow() { return FRow; }
    public int getFCol() { return FCol; }
    public int getERow() { return ERow; }
    public int getECol() { return ECol; }
}
