package circularArray;
import java.util.ArrayList;
import java.util.List;
/**
 * A Circular Array, where the element after the last is the first.
 * All methods should be capable of handling overflow
 * 
 * Overflow in this context means that we have added more elements than the size of the
 * underlying array, and therefore starts at the beginning again.
 * 
 * For example: A CircularArray with size 3. Add four elements. The fourth element will not fit
 * and thereby overflow onto the beginning of the array and overwrite the first element.
 * 
 * @author Joakim Schmidt
 * @version 1.0
 */
public class CircularArray<E> extends ArrayList<E> {
    private int writePtr;
    private int readPtr;
    private int capacity;

    private static final String TAG = "circlearray";

    /**
     * Constructor, will set the capacity to 10.
     */
    public CircularArray() {
        super(10);
        capacity = 10;
        writePtr = 0;
        readPtr = 0;
        initiateElements();
    }
    
    /**
     * Constructor, will set the capacity to size. 
     * @param size the capacity of the list
     * @throws IllegalArgumentException if size is less than 1
     */
    public CircularArray(int size)
        throws IllegalArgumentException {
        super(size);
        if(size <= 0)
            throw new IllegalArgumentException("Size must be a positive integer");
        else {
            capacity = size;
            writePtr = 0;
            readPtr = 0;
            initiateElements();
        }
    }
    
    /**
     * Ensures we have an initial capacity by adding null-elements
     */
    private void initiateElements() {
    	for(int i = 0; i < capacity; i++) {
        	super.add(null);
        }
    }

    /**
     * Adds the element after the last element in this list.
     * This method will overwrite any element found in the place it attempts to write to.
     */
    @Override
    public synchronized boolean add(E e) {
        super.set(writePtr, e);
        writePtr++;
        if(writePtr >= capacity)
            writePtr = 0;
        return true;
    }

    /**
     * This method should not be used in implementation. 
     * It should only be used for testing purposes.
     */
    @Override
    public void add(int index, E e) {
    	if(index > capacity)
    		index %= capacity;
        //NOTE not sure this method should even do anything. If it should do something, then we need
        //NOTE to also be able to either remove the "last" element, or temporarily grow the array

    }
    
    /**
     * Overwrite the element at specified index, and returns the old element.
     * @return the element previously at the specified index
     */
    @Override
    public synchronized E set(int index, E e) {
    	if(index > capacity)
    		index %= capacity;
    	return super.set(index, e);
    }
    /**
     * Removes and returns the next element in sequence.
     * @return the next element in sequence.
     */
    public synchronized E getNext() {
    	int i = 0;
    	E temp;
        while((temp = remove(readPtr)) == null && i < capacity) {
        	i++;
        	moveReadPtr();
        }
        moveReadPtr();
        return temp;
    }
    
    /**
     * Move the readPtr forward one step, and handle any overflow
     */
    private void moveReadPtr() {
    	readPtr++;
    	if(readPtr >= capacity)
    		readPtr = 0;
    }

    /**
     * Returns a portion of the list, beginning at fromIndex and ending at toIndex
     * There is no requirement for fromIndex to be smaller than toIndex. 
     * As all other methods, overflow is supported.
     * @return a list containing all the elements found between fromIndex and toIndex in the correct sequence.
     */
    @Override
    public synchronized List<E> subList(int fromIndex, int toIndex) {
    	if(toIndex > capacity) 
    		toIndex %= capacity;
        if(fromIndex <= toIndex)
            return super.subList(fromIndex, toIndex);
        else {
            List<E> temp = super.subList(fromIndex, capacity);
            temp.addAll(super.subList(0, toIndex));
            return temp;
        }
    }

    /**
     * Removes and returns the element found at the specified index
     * @return the element found at the specified index
     */
    @Override
    public synchronized E remove(int index) {
    	if(index > capacity)
    		index %= capacity;
        return super.set(index, null);
    }

    /**
     * Returns true if this list contains no elements
     * @return true if this list contains no elements
     */
    @Override
    public synchronized boolean isEmpty() {
    	//TODO this method could use a better (simpler) check
        for(E element : super.subList(0, capacity)) {
            if(element != null)
                return false;
        }
        return true;
    }

    /**
     * Returns an array containing all the elements in this list in the proper sequence
     * (from writePtr and X steps forward, where X is capacity). This includes overflow.
     * @return an array containing all of the elements in this list in proper sequence
     */
    @Override
    public Object[] toArray() {
        return subList(writePtr, writePtr+capacity).toArray();
    }
}
