/*
 * WeightedKeyword.java
 *
 * Created on 6. August 2004, 17:27
 */

package de.uni_leipzig.bf.util;

/**
 * This class is a help class with objects composed of keyword and
 * its weight for later retrieval. This weight should be between 0 and 1 inclusive.
 * @author  an
 */
public class WeightedKeyword implements java.lang.Comparable
{
    
    /** Keyword itself */
    private String keyword;
    /** Associated weight */
    private double weight;
    
    /**
     * Creates a new instance of WeightedKeyword
     * @param _word Keyword
     * @param _weight Associated weight
     */
    public WeightedKeyword(String _word, double _weight)
    {
        keyword=_word;
        weight=_weight;
    }
    
    /**
     * Writes the keyword and the associated weight in a String
     * @return see description
     */
    public String toString()
    {
        return "{"+keyword+", "+weight+"}";
    }
    
    /** Get the actual value of keyword
     *@return The actual value of keyword
     */
    public String getKeyword()
    {
        return keyword;
    }
    /** Get the actual value of the weight
     * @return The actual value of weight
     */
    public double getWeight()
    {
        return weight;
    }
    
    /**
     * Sets the weight of the keyword. Returns true when the parameter _weight
     * is in the interval [0,1], else false.
     * @param _weight New weight
     * @return see description
     */
    public boolean setWeight(double _weight)
    {
        weight=_weight;
        return true;
    }
    
    public int compareTo(Object o)
    {
        if (!o.getClass().toString().equals(this.getClass().toString()))
        {
            return 1;
        }
        else
        {
            if(((WeightedKeyword)o).getWeight() > this.getWeight()) return 1;
            if(((WeightedKeyword)o).getWeight() == this.getWeight()) return 0;
            else return -1;
        }
    }
}
