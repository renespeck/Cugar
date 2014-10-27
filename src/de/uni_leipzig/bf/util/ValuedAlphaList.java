/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.uni_leipzig.bf.util;

/**
 * All elements are treated as strings A?B?Value 
 * @author Ngonga
 */
public class ValuedAlphaList extends AlphaList {

    public String convert(Comparable o)
    {
        return ((String)o).substring(0, ((String)o).lastIndexOf("?"));
    }
    
    @Override
 public int idxOf(Comparable o) {
        int low = 0;
        int high = size() - 1;
        int mid, comparison;
        while (low <= high) {
            mid = (low + high) / 2;
            comparison = convert(get(mid)).compareTo(convert(o));
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
    
    @Override
        public boolean addd(Comparable o) {
        int low = 0;
        int high = size() - 1;
        int mid, comparison;

        if (size() == 0) {
            return super.add(o);
        }

        if (convert(get(size() - 1)).compareTo(convert(o)) == 0) {
            mid = size() - 1;
            String s = convert(o);
                double value = Double.parseDouble(((String)o).substring(((String)o).lastIndexOf("?")+1)) + 
                        Double.parseDouble(((String)get(mid)).substring(((String)(get(mid))).lastIndexOf("?")+1));
                set(mid, convert(o)+"?"+value);
                return true;
        }

        while (low <= high) {
            mid = (low + high) / 2;
            comparison = convert(get(mid)).compareTo(convert(o));
            if (comparison > 0) {
                high = mid - 1;
            }
            else if (comparison < 0) {
                low = mid + 1;
            }
            else {
                // cumulate the values
                String s = convert(o);
                double value = Double.parseDouble(((String)o).substring(((String)o).lastIndexOf("?")+1)) + 
                        Double.parseDouble(((String)get(mid)).substring(((String)(get(mid))).lastIndexOf("?")+1));
                set(mid, convert(o)+"?"+value);
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
    
    public String getKey(int i)
    {
        if( i < 0 || i >= size()) return null;
        else
        {
            String s = (String)get(i);
            return s.substring(0, s.lastIndexOf("?"));
        }
    }
    
    public String getKeyOne(int i)
    {
        if( i < 0 || i >= size()) return null;
        else
        {
            String s = (String)get(i);
            return s.substring(0, s.indexOf("?"));
        }
    }
    
    public String getKeyTwo(int i)
    {
        if( i < 0 || i >= size()) return null;
        else
        {
            String s = (String)get(i);
            return s.substring(s.indexOf("?")+1, s.lastIndexOf("?"));
        }
    }

    public double getValue(int i)
    {
        if( i < 0 || i >= size()) return -1.0;
        else
        {
            String s = (String)get(i);
            return Double.parseDouble(s.substring(s.lastIndexOf("?")+1));
        }
    }
     public static void main(String args[])
    {
        ValuedAlphaList l = new ValuedAlphaList();
        l.addd("a?b?3.1");        
        l.addd("b?a?5.3");
        l.addd("a?b?5.3");
    }
}
