/////////////////////////////////////////////////////////////////////////////
// Semester:         CS367 Spring 2017 
// PROJECT:          p1
// FILE:             JobListIterator.java
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
import java.util.NoSuchElementException;
/**
 * This class implements Iterator interface, perform as direct access iterator 
 * for ListADT of type JobList.
 * @author Coding Crew
 *
 */
public class JobListIterator implements Iterator<Job>{
	// the node under the current position of the pointer
	private Listnode<Job> curr;
	// the current position of pointer
	private int currPos;
	// the number of nodes in total
	private int numItems;
	
	/**
	 * The constructor for a iterator
	 * @param head the header node
	 * @param numItems the number of nodes in total
	 */
	public JobListIterator(Listnode<Job> head, int numItems) {
		// set the current position to the next node of the header node
		curr = head.getNext();
		// set the current position to 0
		currPos = 0;
		this.numItems = numItems;
	}
	
	public boolean hasNext() {
		return currPos < numItems;
	}
	
	public Job next() {
		// throw exception if there's no items left
		if (!hasNext()) throw new NoSuchElementException();
		// keep the Job obj in a temp variable for later to return
		Job temp = curr.getData();
		// advance the current position
		curr = curr.getNext();
		currPos++;
		return temp;
	}
	/**
	 * This is a unsupported operation
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
