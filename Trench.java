import java.util.*;

public class Trench {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Grid grid = new Grid();
        boolean gameActive = true;

        while (gameActive) {
            System.out.println("\n\n  Welcome to the Dark and Damp Trench!");
            System.out.println("  You have an important mission... Capture the Flag,");
            System.out.println("  avoid the Mines, and reach the Exit safely to survive!");
            grid.printGrid();

            System.out.println("\n\nChoose your traversal strategy:");
            System.out.println(" [1] The England's Game (BFS)");
            System.out.println(" [2] The Fisherman (DFS)");
            System.out.println(" [3] The Royal Flush (IDS)");
            System.out.print("Your choice: ");
            int choice = scan.nextInt();

            switch (choice) {
                case 1 -> {
                    BFS bfs = new BFS(grid);
                    bfs.start();
                }
                case 2 -> System.out.println("DFS not yet implemented.");
                case 3 -> System.out.println("IDS not yet implemented.");
                default -> System.out.println("Invalid choice.");
            }

            System.out.println("\nThe mission has ended...");
            System.out.println("Would you like to try again?");
            System.out.println("  Type [Restart] to replay the same grid,");
            System.out.println("  Type [Reset] to start a new grid, or [Exit] to leave the trench.");
            System.out.print("Your choice: ");
            String ch = scan.next();

            switch (ch) {
                case "Restart" -> {
                    System.out.println("\nReplaying the same mission...");
                    grid.restoreGrid(); // restore backup copy
                }
                case "Reset" -> {
                    System.out.println("\nA new fate begins...");
                    grid = new Grid(); // new grid with new backup
                }
                case "Exit" -> {
                    System.out.println("\nLeaving the trenches... Farewell, soldier.");
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
