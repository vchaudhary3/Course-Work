/////////////////////////////////////////////////////////////////////////////
// Semester:         CS367 Spring 2017 
// PROJECT:          p1
// FILE:             Scoreboard.java
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
import java.util.Iterator;
/**
 * This is an implementation of ScorebaordADT
 * @author zihanwang
 *
 */
public class Scoreboard implements ScoreboardADT{
	// the List which store all the score objs
	ListADT<Job> myList;
	/*
	 * constructor for Score board, initialize the list to a new list
	 */
	public Scoreboard() {
		myList = new JobList();
	}
	/**
     * Calculates the total combined number of points for every job 
     * in the scoreboard.
     * 
     * @return The summation of all the points for every job currently stored 
     * in the score board
     */
	public int getTotalScore(){
		int totalScore = 0;
		// the loop that add up all the points of the jobs in the list
		for (Job a : myList) {
			totalScore += a.getPoints();
		}
		return totalScore;
	}
	
	/**
     * Inserts the given job at the end of the scoreboard.
     * 
     * @param job 
     * 		The job that has been completed and is to be inserted 
     * into the list.
     */
	public void updateScoreBoard(Job a) {
		myList.add(a);
	}
	
	/**
     * Prints out a summary of all jobs currently stored in the scoreboard. 
     * The formatting must match the example exactly.
     */
	public void displayScoreBoard() {
		System.out.println("The jobs completed:");
		for (Job a : myList) {
			System.out.println("Job Name: " + a.getJobName());
			System.out.println("Points earned for this job: " + a.getPoints());
			System.out.println("--------------------------------------------");
		}
	}
}
