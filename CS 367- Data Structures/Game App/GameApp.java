/////////////////////////////////////////////////////////////////////////////
// Semester:         CS367 Spring 2017 
// PROJECT:          p1
// FILE:             GameApp.java
// Team 1:  		 Coding Crew
// Authors:
// Author1: (Ali Zaidi, azaidi3@wisc.edu, NetID: 9075844309, lecture 001)
// Author2: (Payton Garland, pgarland2@wisc.edu, NetID: 9074284267,lecture 001)
// Author3: (Linfeng Jiang, ljiang82@wisc.edu, NetID: 9075936618,lecture 001)
// Author4: (Zian Wang, zwang883@wisc.edu, NetID: 9075902156, lecture 001)
// Author5: (Xinyu Hu, xhu243@wisc.edu, NetID: 9075185109, lecture 001)
// Author6: (Vedantika Chaudhary, vchaudhary3@wisc.edu,Net ID: 9075006313,
//																lecture 001)
//////////////////////////// 80 columns wide //////////////////////////////////
import java.util.Scanner;
/**
 * This class is the core part of the interactive game
 * @author Coding Crew
 *
 */
public class GameApp{
    /**
     * Scanner instance for reading input from console
     */
    private static final Scanner STDIN = new Scanner(System.in);
    private Game game;

    /**
     * Constructor for instantiating game class
     * @param seed: Seed value as processed in command line
     * @param timeToPlay: Total time to play from command line
     */
    public GameApp(int seed, int timeToPlay){
    	// create a new instance of game
    	game = new Game(seed, timeToPlay);
    }

    /**
     * Main function which takes the command line arguments and instantiate the
     * GameApp class.
     * The main function terminates when the game ends.
     * Use the getIntegerInput function to read inputs from console
     *
     * @param args: Command line arguments <seed> <timeToPlay>
     */
    public static void main(String[] args){
    	
        System.out.println("Welcome to the Job Market!");
        int s = 0;
        int t = 0;
        // check for the length of command line arguments
        if (args.length != 2) {
        	System.out.println("Command Line Input Error");
        	System.exit(0);
        }
        // check whether they are valid integers
        try {
        	s = Integer.parseInt(args[0]);
        	t = Integer.parseInt(args[1]);
        	if(s < 0 || t < 0) {
        		System.out.println("Command Line Input Error");
        		System.exit(0);
        	}
        }
        catch(NumberFormatException e) {
        	System.out.println("Command Line Input Error");
        	System.exit(0);
        }
        // create a instance of gameapp to operate on the game
        GameApp gameEngine = new GameApp(s, t);
        gameEngine.start();
    }

    /**
     * Add Comments as per implementation
     */
    private void start(){
        // create the jobs before the first iteration,
    	// then other jobs will be created during complete iteration
    	game.createJobs();
    	// keep looping until game over
    	while(!game.isOver()) {
	    	main_menu_loop();
    	}
    	System.out.println("Game Over!");
    	System.out.println("Your final score: " + game.getTotalScore());
    }
    
    private void main_menu_loop() {
    	System.out.println("You have " + game.getTimeToPlay() +" left in the game!");
    	game.displayActiveJobs();
    	int index = getIntegerInput("Select a job to work on: ");
    	// the iteration starts over if the input is not a valid value
    	while (index < 0 || index >= game.getNumberOfJobs()) {
    		return;
    	}
    	int duration = getIntegerInput("For how long would you like to work on this job?: ");
    	// reset the duration if it is larger than the time left to play
    	if (duration > game.getTimeToPlay()) duration = game.getTimeToPlay();
    	Job curr = game.updateJob(index, duration);
    	
    	// after the job is successfully processed, create new jobs
    	if (curr.isCompleted()) {
    		System.out.println("Job completed! Current Score: " + game.getTotalScore());
    		System.out.println("Total Score: " + game.getTotalScore());
    		game.displayCompletedJobs();
    		game.createJobs();
    	}
    	else {
    		int insertPos = getIntegerInput("At what position would you like to insert the job back into the list?\n");
    		game.addJob(insertPos, curr);
    		game.createJobs();
    	}
    }
    
    /**
     * Displays the prompt and returns the integer entered by the user
     * to the standard input stream.
     *
     * Does not return until the user enters an integer.
     * Does not check the integer value in any way.
     * @param prompt The user prompt to display before waiting for this integer.
     */
    public static int getIntegerInput(String prompt) {
        System.out.print(prompt);
        while ( ! STDIN.hasNextInt() ) {
            System.out.print(STDIN.next()+" is not an int.  Please enter an integer.");
        }
        return STDIN.nextInt();
    }
}