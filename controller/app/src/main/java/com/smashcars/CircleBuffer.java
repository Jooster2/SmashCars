package com.smashcars;

import java.util.ArrayList;

/**
 * @author Joakim Schmidt
 */
public class CircleBuffer {
    private int index;
    private int capacity;
    private ArrayList<Character> array;

    public CircleBuffer(int size) {
        array = new ArrayList<>(size);
        index = 0;
        capacity = size;
    }

    public void add(char element) {
        array.add(index, element);
        index++;
        if(index >= capacity)
            index = 0;
    }

    public char[] getArray() {
        char[] temp = new char[capacity];
        for(int i = index; i < array.size(); i++) {
            temp[i] = array.get(i);
        }
        for(int i = 0; i < index; i++) {
            temp[i] = array.get(i);
        }
        return temp;
    }

}
