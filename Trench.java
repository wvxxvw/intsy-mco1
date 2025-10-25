import java.util.*;

public class Trench {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Grid grid = new Grid();
        boolean gameActive = true;

        while (gameActive) {
            System.out.println("\n\n  Welcome to the Dark and Damp Trench!");
            System.out.println("  Your mission: Capture the Flag, avoid the Mines, and reach the Exit safely!");
            grid.printGrid();

            System.out.println("\n\nChoose your traversal strategy:");
            System.out.println(" [1] The England's Game (BFS)");
            System.out.println(" [2] The Fisherman (DFS)");
            System.out.println(" [3] The Royal Flush (IDS)");
            System.out.print("Your choice: ");
            int choice = scan.nextInt();

            // ask for mode AFTER algorithm choice
            System.out.println("\nChoose Mode:");
            System.out.println(" [1] Slow Mode (Press C to step)");
            System.out.println(" [2] Fast Mode (1-second delay)");
            System.out.print("Your choice: ");
            int mode = scan.nextInt();
            boolean slowMode = (mode == 1);

            // run algorithm
            switch (choice) {
                case 1 -> {
                    BFS bfs = new BFS(grid, slowMode); // add the slowMode parameter for the choosing for your algorithm
                    bfs.start();
                }
                case 2 -> System.out.println("DFS not yet implemented.");
                case 3 -> System.out.println("IDS not yet implemented.");
                default -> System.out.println("Invalid choice.");
            }

            System.out.println("\nMission completed...");
            System.out.println("Would you like to try again?");
            System.out.println("  Type [Restart] to replay the same grid,");
            System.out.println("  Type [Reset] to start a new grid, or [Exit] to leave the trench.");
            System.out.print("Your choice: ");
            String ch = scan.next();

            switch (ch) {
                case "Restart" -> {
                    System.out.println("\nGoing back in time...");
                    grid.restoreGrid();
                }
                case "Reset" -> {
                    System.out.println("\nA new fate begins...");
                    grid = new Grid();
                }
                case "Exit" -> {
                    System.out.println("\nLeaving the trenches... Alas, the end is nigh.");
                    gameActive = false;
                }
                default -> {
                    System.out.println("\nInvalid input. The trenches claim the unprepared...");
                    gameActive = false;
                }
            }
        }
        scan.close();
    }
}
