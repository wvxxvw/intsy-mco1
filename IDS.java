import java.util.*;
class Vector2{
    private int x;
    private int y;
    public Vector2 tempVector;

    public static final Vector2 up = new Vector2(0, 1);
    public static final Vector2 down = new Vector2(0, -1);
    public static final Vector2 left = new Vector2(-1, 0);
    public static final Vector2 right = new Vector2(1, 0);

    public static final ArrayList<Vector2> sequence = new ArrayList<Vector2>(Arrays.asList(up, left, down, right));
    //Prev (right, down, left, up)

    public Vector2(int x, int y){
        this.x = x;
        this.y = y;
    }
    public Vector2(){
        this.x = 0;
        this.y = 0;
    }
    public int getVector2_x(){
        return this.x;
    }
    public int getVector2_y(){
        return this.y;
    }
    public String getVector(){
        return "(" + this.x +","+ this.y + ")";
    }
    public Vector2 add(Vector2 adjustVector){
        return new Vector2(this.x + adjustVector.x, this.y + adjustVector.y);
    }
    public boolean compare(Vector2 vec2){
        return this.x == vec2.x && this.y == vec2.y;
    }
    public boolean check_if_vec_inside_bounds(Vector2 l_bound, Vector2 u_bound){
        if(this.x <= u_bound.x && this.y <= u_bound.y &&
                this.x >= l_bound.x && this.y >= l_bound.y){
            return true;
        }
        else
            return false;
    }
}
public class IDS{
    private Grid grid;
    private Grid visited;
    //private boolean[][] visited;
    private boolean flagCaptured = false;
    private boolean gameOver = false;
    private int steps = 0;
    private boolean slowMode;

    //private int currentDepth = 0;
    //private int depthLimit = 0;



    private int prevRow, prevCol;

    public IDS(Grid grid, boolean slowMode) {
        this.grid = grid;
        this.visited = new Grid();
        this.slowMode = slowMode;
    }

    public void start(int xRow, int yCol){
        Vector2 flag;
        Vector2 exit;
        System.out.println("\nStarting Iterative Depth-First Search: \n");
        prevRow = xRow;
        prevCol = yCol;

        flag = ids(xRow, yCol, Grid.FLAG);
        System.out.println("Found flag! at " + flag.getVector());
        this.visited = new Grid();
        exit = ids(flag.getVector2_x(), flag.getVector2_y(), Grid.EXIT);
        System.out.println("Found exit! at " + exit.getVector());
        if(gameOver){
            System.out.println("No path found / Exit cannot be reached");
        }

        //System.out.println("\nTotal Steps: " + steps);
    }
    private void push(ArrayList<Vector2> stack, Vector2 element){
        stack.add(element);
    }
    private Vector2 pop(ArrayList<Vector2> stack){
        return stack.removeLast();
    }
    private void detect_neighbors(Vector2 current_vector, int target, ArrayList<Vector2> stack){
        for(Vector2 dir : Vector2.sequence){
            Vector2 adjustedCell = current_vector.add(dir);
            if(!adjustedCell.check_if_vec_inside_bounds( new Vector2(0,0), new Vector2(grid.getSize()-1, grid.getSize()-1)))
                continue;
            if((grid.getCell(adjustedCell) == Grid.EMPTY || grid.getCell(adjustedCell) == target)&& !check_if_existing(stack, adjustedCell) && visited.getCell(adjustedCell) != Grid.VISITED){
                push(stack, adjustedCell);
                System.out.println("Pushed " + adjustedCell.getVector());

                showStack(stack);
            }
        }
    }
    private boolean check_if_existing(ArrayList<Vector2> stack, Vector2 value){
        for(Vector2 temp : stack){
            if(temp.compare(value))
                return true;
        }
        return false;
    }
    private void showStack(ArrayList<Vector2> stack){
        System.out.println("Bottom");
        for(Vector2 currentVector : stack){
            System.out.println(currentVector.getVector());
        }
        System.out.println("Top");
    }
    private void clearAgents(Grid grid){
        for(int i = 0; i < grid.getSize(); i++){
            for(int j = 0; j < grid.getSize(); j++){
                Vector2 currentVector = new Vector2(i, j);
                if(grid.getCell(currentVector) == Grid.AGENT){
                    grid.setCell(currentVector, Grid.EMPTY);
                }
            }
        }
    }
    private Vector2 ids(int xRow, int yCol, int target){
        ArrayList<Vector2> stack = new ArrayList<Vector2>();
        System.out.println("Starting IDS");
        int simulation_count = 0;
        Vector2 currentCell = new Vector2(xRow, yCol);
        Vector2 previousCell = new Vector2(0, 0);



        int currentDepth = 0;
        int depthLimit = 0;
        while(grid.getCell(currentCell) != target){

            if(currentDepth == depthLimit){
                System.out.println("Reset");
                clearAgents(grid);
                currentDepth = 0;
                depthLimit++;
                currentCell = new Vector2(xRow, yCol);
                System.out.println("New: " + currentCell.getVector());
                stack.clear();
                visited.clearAllCell();
                previousCell = new Vector2(xRow, yCol);

            }
            if(grid.getCell(previousCell) != Grid.FLAG && grid.getCell(previousCell) != Grid.EXIT)
                grid.setCell(previousCell, Grid.EMPTY);
            if(grid.getCell(currentCell) != Grid.FLAG && grid.getCell(currentCell) != Grid.EXIT)
                grid.setCell(currentCell, Grid.AGENT);
            visited.setCell(currentCell, Grid.VISITED);

            grid.printGrid();
            grid.waitForNextStep(slowMode);
            detect_neighbors(currentCell, target, stack);
            previousCell = currentCell;
            if(stack.size()>0)
                currentCell = pop(stack);
            currentDepth++;

            
            System.out.println("Current Cell: " + currentCell.getVector());

            

            simulation_count++;
        }
        return currentCell;
    }
}
