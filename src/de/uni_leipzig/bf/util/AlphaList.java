/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uni_leipzig.bf.util;

import java.util.ArrayList;

/**
 * Implements an alphabetically sorted set. The retrieval of objects is binary 
 *  and thus has a logarithmic worst case time complexity
 * @author Ngonga
 */
public class AlphaList extends ArrayList<Comparable> {

        public boolean containss(Comparable o) {
        if(idxOf(o) == -1) return false;
        return true;
    }
        
    /** Implements a binary search to find objects in the list. Time complexity 
     * is thus logarithmic.
     * @param o Object to look for
     * @return -1 if the object is not in the list, else the index of the object
     */

    public int idxOf(Comparable o) {
        int low = 0;
        int high = size() - 1;
        int mid, comparison;
        while (low <= high) {
            mid = (low + high) / 2;
            comparison = get(mid).compareTo(o);
            if (comparison > 0) {
                high = mid - 1;
            }
            else if (comparison < 0) {
                low = mid + 1;
            }
            else {
                return mid;
            } // found
        }
        return -1; // not found
    }
    
    /** Insert an element at a given position
     * 
     * @param index Position where the object is to be added
     * @param o Ojbect to add
     * @return true when insertion was completed
     */
    protected boolean insert(int index, Comparable o) {
        if (index < 0) {
            index = 0;
        }
        if (index >= size()) {
            return super.add(o);
        }
        int size = size();
        Comparable oldv;
        Comparable newv = get(index);
        set(index, o);
        for (int i = index + 1; i < size; i++) {
            oldv = get(i);
            set(i, newv);
            newv = oldv;
        }
        super.add(newv);
        return true;
    }

    /** add elements in alphabetic order
     * @param o The object to add
     * @return true is everything went well
     */
   // @Override
    public boolean addd(Comparable o) {
        if(containss(o)) return true;
        //else System.out.println(toString()+" does not contain "+o);
        int low = 0;
        int high = size() - 1;
        int mid, comparison;

        if (size() == 0) {
            return super.add(o);
        }

        if (get(size() - 1).compareTo(o) == 0) {
            super.add(o);
        }

        while (low <= high) {
            mid = (low + high) / 2;
            comparison = get(mid).compareTo(o);
            if (comparison > 0) {
                high = mid - 1;
            }
            else if (comparison < 0) {
                low = mid + 1;
            }
            else {
                return true;
            } // found
        }

        if (low == high) {
            return insert(low, o);
        }
        else {
            return insert(high + 1, o);
        }
    }

/* For testing
    public static void main(String args[])
    {
        AlphaList l = new AlphaList();
        for(int i=0; i<1000; i++)
        {
            l.add(new Integer((int)(java.lang.Math.random()*1000)));
        }
        System.out.println(l);
    }
*/
}


