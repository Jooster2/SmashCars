package com.smashcars;

import android.util.Log;

import java.util.ArrayList;

/**
 * @author Joakim Schmidt
 */
public class CircleBuffer {
    private int index;
    private int cmdPointer;
    private int capacity;
    private ArrayList<Character> array;
    private static final String TAG = "circle";

    /**
     * Constructor. Sets the size of the buffer to parameter. This is not changeable afterwards
     * @param size the size of the buffer
     */
    public CircleBuffer(int size) {
        array = new ArrayList<>(size);
        index = 0;
        cmdPointer = 0;
        capacity = size;
        //Fill the buffer with dashes, so it's indexable
        for(int i = 0; i<size; i++) {
                array.add('-');
        }
        Log.i(TAG, "Buffer size is: " + array.size());
    }

    /**
     * Add an element to the buffer, overwriting the oldest element if the buffer is full
     * @param element char to add
     */
    public void add(char element) {
        array.set(index, element);
        index++;
        if(index >= capacity)
            index = 0;
    }

    /**
     * Return the entire buffer in old-to-new order
     * @return char[] with oldest element at index 0
     */
    public char[] getArray() {
        char[] temp = new char[capacity];
        //Get chars from index to end
        for(int i = index; i < array.size(); i++) {
            temp[i] = array.get(i);
        }
        //Get chars from beginning to index
        for(int i = 0; i < index; i++) {
            temp[i] = array.get(i);
        }
        return temp;
    }

    /**
     * Get the oldest char in the CircleBuffer,
     * @return a single char
     */
    public char getChar() {
        char temp = array.get(cmdPointer);
        //"Delete" the char we just got
        array.set(cmdPointer, '-');
        //Fast forward through all dashes until we find a valid value, or we get to index
        //No reason to look beyond index
        while(array.get(cmdPointer) == '-') {
            cmdPointer++;
            if(cmdPointer >= capacity)
                cmdPointer = 0;
            if(cmdPointer == index)
                break;
        }
        return temp;
    }

}
