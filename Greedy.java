import java.util.*;

public class Greedy {
    private Grid grid;
    private Grid visited;
    private boolean flagCaptured = false;
    private boolean gameOver = false;
    private int steps = 0;
    private boolean slowMode;

    public static final Vector2 up = new Vector2(0, 1);
    public static final Vector2 down = new Vector2(0, -1);
    public static final Vector2 left = new Vector2(-1, 0);
    public static final Vector2 right = new Vector2(1, 0);

    public static final ArrayList<Vector2> sequence = new ArrayList<Vector2>(Arrays.asList(up, left, down, right));

    public Greedy(Grid grid, boolean slowMode) {
        this.grid = grid;
        this.visited = new Grid();
        this.slowMode = slowMode;
    }

    // Manhattan Distance heuristic
    private int heuristic(int r, int c, int targetR, int targetC) {
        return Math.abs(r - targetR) + Math.abs(c - targetC);
    }
    private int heuristic(Vector2 current, Vector2 target) {
        return Math.abs(current.getVector2_x() - target.getVector2_x()) + Math.abs(current.getVector2_y() - target.getVector2_y());
    }
    public Vector2 findCoords(Grid grid, int target){
        
        for(int i = 0; i < grid.getSize(); i++){
            for(int j = 0; j < grid.getSize(); j++){
                if(grid.getCell(i, j) == target)
                    return new Vector2(i, j);
            }
        }
        return null;
    }
    public void start(int xRow, int yCol) {
        System.out.println("\n--- The Raider’s Instinct (Greedy Best-First Search) ---");
        long startTime = System.currentTimeMillis();

        Vector2 flag = findCoords(grid, Grid.FLAG);
        Vector2 exit = findCoords(grid, Grid.EXIT);
        Vector2 startingCell = new Vector2(xRow, yCol);
        Vector2 SecondPhase = new Vector2();
        SecondPhase = performGreedy(startingCell, flag);

        System.out.println("Got flag!");
        System.out.println("Going to exit!");
        performGreedy(SecondPhase, exit);

        long endTime = System.currentTimeMillis();
        System.out.println("\nTotal steps: " + steps);
        System.out.println("Elapsed time: " + (endTime - startTime) + "ms");
    }

    private void enqueue(ArrayList<Vector2> queue, Vector2 cell){
        queue.add(cell);
    }

    private void dequeue(ArrayList<Vector2> queue, Vector2 cell){
        
    }
    private void detectNeighbors(ArrayList<Vector2> queue, Vector2 cell, Vector2 target){
        
        for(Vector2 dir : sequence){
            Vector2 neighbor = cell.add(dir);
            if(!neighbor.check_if_vec_inside_bounds( new Vector2(0,0), new Vector2(grid.getSize()-1, grid.getSize()-1)))
                continue;
            if((!check_if_existing(queue, neighbor) ||
                grid.getCell(neighbor) == Grid.EMPTY ||
                neighbor == target)
                &&
                grid.getCell(neighbor) != Grid.WALL &&
                grid.getCell(neighbor) != Grid.MINE &&
                visited.getCell(neighbor) != Grid.VISITED)
                queue.add(neighbor);
        }
    }
    private boolean check_if_existing(ArrayList<Vector2> queue, Vector2 value){
        for(Vector2 temp : queue){
            if(temp.compare(value))
                return true;
        }
        return false;
    }
    private Vector2 priorityQueue(ArrayList<Vector2> queue, Vector2 target){
        if(queue.size() == 0)
            return null;
        int lowest = heuristic(queue.get(0), target);
        Vector2 finalLowest = queue.get(0);
        for(Vector2 current : queue){
            if(current == target){
                return current;
            }
            if(heuristic(current, target) <= lowest){
                finalLowest = current;
                lowest = heuristic(current, target);
            }

        }
        queue.remove(finalLowest);
        return finalLowest;

    }
    private void showQueue(ArrayList<Vector2> queue){
        for(Vector2 temp : queue){
            System.out.println(temp.getVector());
        }
    }
    public Vector2 performGreedy(Vector2 starting, Vector2 target){
        Vector2 currentCell = starting;
        Vector2 previousCell = new Vector2();
        System.out.println("Moving " + starting.getVector() + " to " + target.getVector());
        ArrayList<Vector2> currentQueue = new ArrayList<>();

        while(!currentCell.compare(target)){

            grid.setCell(previousCell, Grid.EMPTY);
            if(grid.getCell(currentCell) != Grid.FLAG && grid.getCell(currentCell) != Grid.EXIT)
                grid.setCell(currentCell, Grid.AGENT);
            visited.setCell(currentCell, Grid.VISITED);
            grid.printGrid();
            detectNeighbors(currentQueue, currentCell, target);
            previousCell = currentCell;
            currentCell = priorityQueue(currentQueue, target);


            if(currentCell != null){
                if(currentCell.compare(target)){
                    grid.setCell(currentCell, Grid.EMPTY);
                    grid.setCell(previousCell, Grid.EMPTY);
                    return currentCell;
                }
            }
            try {
                Thread.sleep(1000); // 1000 milliseconds = 1 second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }
        return null;
    }
}
