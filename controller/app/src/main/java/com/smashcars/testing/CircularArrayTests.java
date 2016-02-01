package com.smashcars.testing;

import android.test.AndroidTestCase;

import com.smashcars.CircularArray;

/**
 * Tests for the CircularArray and its various methods.
 * Overflow in this context means that we have indexed more elements than the size of the
 * underlying array, and therefore starts at the beginning again.
 *
 * For example: A CircularArray with size 3. Add four elements. The fourth element will not fit
 * and thereby overflow onto the beginning of the array and overwrite the first element.
 *
 * @author Joakim Schmidt
 *
 */
public class CircularArrayTests extends AndroidTestCase {

    /**
     * Basic filling with overflow
     * @throws Exception
     */
    public void testFilling() throws Exception {
        CircularArray<Character> t = new CircularArray<>(5);
        t.add('a');
        t.add('b');
        t.add('c');
        t.add('d');
        t.add('e');
        t.add('f');
        assertTrue(t.size() == 5);
        assertEquals((Character) 'f', t.get(0));
        assertEquals((Character)'b', t.get(1));

    }

    /**
     * Testing getNext with overflow and isEmpty
     * @throws Exception
     */
    public void testEmptying() throws Exception {
        CircularArray<Character> t = new CircularArray<>(5);
        t.add('a');
        t.add('b');
        t.add('c');
        t.add('d');
        t.add('e');
        assertFalse(t.isEmpty());
        assertEquals((Character)'a', t.getNext());
        assertEquals((Character)'b', t.getNext());
        assertEquals((Character)'c', t.getNext());
        t.add('f');
        assertEquals((Character)'d', t.getNext());
        assertEquals((Character)'e', t.getNext());
        assertEquals((Character)'f', t.getNext());
        assertTrue(t.isEmpty());
        assertTrue(t.size() == 5);
    }

    /**
     * Testing subList with and without overflow
     * @throws Exception
     */
    public void testSubList() throws Exception {
        CircularArray<Character> t = new CircularArray<>(5);
        t.add('a');
        t.add('b');
        t.add('c');
        t.add('d');
        t.add('e');
        CircularArray<Character> x = new CircularArray<>(3);
        x.add('d');
        x.add('e');
        x.add('a');
        //Test basic sublisting
        assertEquals(t.subList(3, 5), x.subList(0, 2));
        //Test sublisting with overflow (get elements 3, 4 and 0 from array t)
        assertEquals(t.subList(3, 6), x.subList(0, 3));
    }
}
