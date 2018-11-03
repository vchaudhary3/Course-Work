/////////////////////////////////////////////////////////////////////////////
// Semester:         CS367 Spring 2017 
// PROJECT:          p1
// FILE:             JobList.java
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
 * This class implements ListADT of type job, perform as a list of Job type.
 * It comes with a head reference to a header node.
 * @author Coding crew
 *
 */
public class JobList implements ListADT<Job> {
	// the head reference
	private Listnode<Job> head;
	// keeping track of the number of nodes in total in the list
	private int numItems;
	
	/**
	 * The constructor for JobList
	 */
	public JobList()
	{
		// set the head reference to a header node
		head = new Listnode<Job>(null);
		// initiate the number of nodes to 0
		numItems =0;
	}

	@Override
	public Iterator<Job> iterator() {
		return new JobListIterator(head, numItems);
	}

	@Override
    /** Adds an item at the end of the list
    * @param item
     *              an item to add to the list
     *@throws IllegalArgumentException
     *              if item is null
     */
	public void add(Job item) {
		// throw exception if the item to add is null
		if(item == null)
			throw new IllegalArgumentException();
		Listnode<Job> newnode = new Listnode<Job>(item);
		// set current node to header node
		Listnode <Job> curr = head;
		// find the node which has a null reference in next field
		while(curr.getNext() != null)
			curr = curr.getNext();
		// set the next field of such a node to the new node
		curr.setNext(newnode);
		// increment of numItems
		numItems++;

	}

	@Override
    /** Add an item at any position in the list
     * @param item
     *              an item to be added to the list
     * @param pos
     *              position at which the item must be added. 
     *              Indexing starts from 0
     * @throws IllegalArgumentException
     *              if item is null
     * @throws IndexOutOfBoundsException
     *              if pos is less than 0 or greater than size() - 1
     */
	public void add(int pos, Job item) {
		// throw exception if the item to add is null
		if(item == null)
			throw new IllegalArgumentException();
		// also when the position given is invalid
		if(pos <0 || pos > numItems)
			throw new IndexOutOfBoundsException();
		Listnode<Job> newNode = new Listnode<Job>(item);
		// when adding at end, let other method do the job
		if (pos == numItems) add(item);
		// set the current position to header node
		Listnode<Job> curr = head;
		// traverse to the position indicated
		for (int i = 0; i < pos; i++) {
			curr = curr.getNext();
		}
		// first set the next field of new node to the node originally at that
		// position
		newNode.setNext(curr.getNext());
		// then set the next field of the current node to the new node
		curr.setNext(newNode);
		// increment numItems
		numItems++;
	}

	@Override
    /** Check if a particular item exists in the list
     * @param item
     *              the item to be checked for in the list
     * @return true
     *              if value exists, else false
     * @throws IllegalArgumentException
     *              if item is null
     */
	public boolean contains(Job item) {
		// start with the next node of the header node
		Listnode<Job> curr = head.getNext();
		// traverse through all valid node and check whether equals
		while(curr != null)
		{
			if(curr.getData().equals(item))
				return true;
			curr = curr.getNext();
		}
		// return false if there is node matching data
		return false;
	}

	@Override
    /** Returns the position of the item to return
     * @param pos
     *              position of the item to be returned
     * @throws IndexOutOfBoundsException
     *              if position is less than 0 or greater than size() - 1
     */
	public Job get(int pos) {
		// throw exception if the position given is invalid
		if(pos <0 || pos >= numItems)
			throw new IndexOutOfBoundsException();
		// set current node to the next node of the haeder node
		Listnode<Job> curr = head.getNext();
		// traverse to the position indicated
		for(int p = 0; p <pos; p++)
			curr = curr.getNext();
		// return the data contained in the node
		return curr.getData();
	}

	@Override
	/** Returns true if the list is empty
     * @return value is true if the list is empty
     *              else false
     */
	public boolean isEmpty() {
		return numItems <= 0;
	}

	@Override
    /** Removes the item at the given positions
     * @param pos
     *          the position of the item to be deleted from the list
     * @return returns the item deleted
     * @throws IndexOutOfBoundsException
     *          if the pos value is less than 0 or greater than size() - 1
     */
	public Job remove(int pos) {
		// throw exception if the index given is invalid
		if(pos < 0 || pos >= numItems)
			throw new IndexOutOfBoundsException();
		// set current position to header node, since remove requires
		// operations on the previous node of the node to be removed
		Listnode<Job> curr = head;
		// traverse to the previous node of the node indicated
		for(int p =0; p< pos; p++)
			curr = curr.getNext();
		// keep the node to be removed in a temp variable for later to return
		Listnode<Job> rem = curr.getNext();
		// modify the next field of the previous node of the node to be removed
		curr.setNext(curr.getNext().getNext());
		// decrement of numItems
		numItems--;
		// and finally return the node removed
		return rem.getData();
	}

	@Override
	/** Returns the size of the singly linked list
     * @return the size of the singly linked list
     */
	public int size() {
		return numItems;
	}

}
