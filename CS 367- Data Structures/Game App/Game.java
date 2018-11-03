/////////////////////////////////////////////////////////////////////////////
// Semester:         CS367 Spring 2017 
// PROJECT:          p1
// FILE:             Game.java
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

/**
 * This class define a single game unit, with operations that can be called
 * in the game engine
 * @author Coding Crew
 *
 */
public class Game{

    /**
     * A list of all jobs currently in the queue.
     */
    private ListADT<Job> list;
    /**
     * Whenever a Job is completed it is added to the scoreboard
     */
    private ScoreboardADT scoreBoard;
    // time left in the game
    private int timeToPlay;
    // job generator used
    private JobSimulator jobSimulator;

    /**
     * Constructor. Initializes all variables.
     * @param seed
     * seed used to seed the random number generator in the Jobsimulator class.
     * @param timeToPlay
     * duration used to determine the length of the game.
     */
    public Game(int seed, int timeToPlay){
    	// set the list of jobs need to work on to a new list
        list = new JobList();
        // also the scoreBoard to store completed jobs
        scoreBoard = new Scoreboard();
        // set the time left in the game to the given value
        this.timeToPlay = timeToPlay;
        jobSimulator = new JobSimulator(seed);
    }

    /**
     * Returns the amount of time currently left in the game.
     * @returns the amount of time left in the game.
     */
    public int getTimeToPlay() {
        return timeToPlay;
    }

    /**
     * Sets the amount of time that the game is to be executed for.
     * Can be used to update the amount of time remaining.
     * @param timeToPlay
     *        the remaining duration of the game
     */
    public void setTimeToPlay(int timeToPlay) {
        this.timeToPlay = timeToPlay;
    }

    /**
     * States whether or not the game is over yet.
     * @returns true if the amount of time remaining in
     * the game is less than or equal to 0,
     * else returns false
     */
    public boolean isOver(){
        return timeToPlay <= 0;
    }
    /**
     * This method simply invokes the simulateJobs method
     * in the JobSimulator object.
     */
    public void createJobs(){
        jobSimulator.simulateJobs(list, timeToPlay);

    }

    /**
     * @returns the length of the Joblist.
     */
    public int getNumberOfJobs(){
        return list.size();
    }

    /**
     * Adds a job to a given position in the joblist.
     * Also requires to calculate the time Penalty involved in
     * adding a job back into the list and update the timeToPlay
     * accordingly
     * @param pos
     *      The position that the given job is to be added to in the list.
     * @param item
     *      The job to be inserted in the list.
     */
    public void addJob(int pos, Job item){
    	// time penalty for invalid position
    	if (pos < 0 || pos > list.size()) {
    		addJob(item);
    		setTimeToPlay(timeToPlay - list.size());
    	}
    	// time penalty for valid position, there will be no penalty for
    	// adding at index 0
    	else {
	        list.add(pos, item);
	        setTimeToPlay(timeToPlay - pos);
    	}
    }

    /**
     * Adds a job to the joblist.
     * @param item
     *      The job to be inserted in the list.
     */
    public void addJob(Job item){
    	list.add(item);
    }

    /**
     * Given a valid index and duration,
     * executes the given job for the given duration.
     *
     * This function should remove the job from the list and
     * return it after applying the duration.
     *
     * This function should set duration equal to the
     * amount of time remaining if duration exceeds it prior
     * to executing the job.
     * After executing the job for a given amount of time,
     * check if it is completed or not. If it is, then
     * it must be inserted into the scoreBoard.
     * This method should also calculate the time penalty involved in
     * executing the job and update the timeToPlay value accordingly
     * @param index
     *      The job to be inserted in the list.
     * @param duration
     *      The amount of time the given job is to be work ed on for.
     */
    public Job updateJob(int index, int duration){
    	// first remove the job selected from to be done list
    	Job curr = list.remove(index);
    	// calculate the steps left to complete this job
    	int timeLeftForJob = curr.getTimeUnits() - curr.getSteps();
    	// if given duration is lager than steps needed, set duration to
    	// that value
        if (duration > timeLeftForJob) duration = timeLeftForJob;
        // update the steps field of this job
        curr.setSteps(curr.getSteps() + duration);
        // deduction of the time penalty
        setTimeToPlay(timeToPlay - index);
        // deduction of duration, which is the time used here
        setTimeToPlay(timeToPlay - duration);
        // if the job is completed, add it to the score board
        if(curr.isCompleted()) {
        	scoreBoard.updateScoreBoard(curr);
        }
        return curr;
    }

    /**
     * This method produces the output for the initial Job Listing, IE:
     * "Job Listing
     *  At position: job.toString()
     *  At position: job.toString()
     *  ..."
     *
     */
    public void displayActiveJobs(){
        System.out.println("Job Listing");
        int counter = 0;
        for(Job a : list) {
        	System.out.println("At position: " + counter + " " + a.toString());
        	counter++;
        }
        System.out.println("");
    }

    /**
     * This function simply invokes the displayScoreBoard method in the 
     * ScoreBoard class.
     */
    public void displayCompletedJobs(){
        scoreBoard.displayScoreBoard();

    }

    /**
     * This function simply invokes the getTotalScore method of the ScoreBoard 
     * class.
     * @return the value calculated by getTotalScore
     */
    public int getTotalScore(){
        return scoreBoard.getTotalScore();
    }
}