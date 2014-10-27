/*
 * QuickSort.java
 *
 * Created on 5. Juli 2006, 17:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.uni_leipzig.bf.util;
import java.util.ArrayList;
import java.lang.Comparable;
/**
 *
 * @author an
 */
public class QuickSort
{
    /**
     * vertauscht die Elemente mit Index m und n im Array a
     */
    
    private static void exchange(ArrayList list, int m, int n)
    {
        Object o = list.get(m);
        list.set(m, list.get(n));
        list.set(n,o);
    }
    
    /**
     * gibt das mittlere Element des (umgeordneten Arrays) zurück
     */
    
    private static int partition(ArrayList<Comparable> list, int m, int n)
    {
        Comparable x = list.get(m);  // Pivot-Element
        int j = n + 1;
        int i = m - 1;
        
        while (true)
        {
            j--;
            while (list.get(j).compareTo(x) > 0) j--;
            i++;
            while (list.get(i).compareTo(x) < 0) i++;
            if (i < j) exchange(list, i, j);
            else return j;
        }
    }
    
    /**
     * eigentlicher Quicksort-Algorithmus. Nutzt die Methode partition().
     */
    
    public static void qsort(ArrayList list, int l, int r)
    {
        // nur absteigen, wenn Array mehr als 1 Element hat (l < r)
        if (l < r)
        {
            int r2 = partition(list, l, r);
            qsort(list, l, r2);
            qsort(list, r2 + 1, r);
        }
    }
    
    
    
    /**
     * Sorts a list of WeightedKeywords
     */
    public static ArrayList quickSort(ArrayList _list)
    {
        qsort(_list, 0, _list.size() - 1);
        return _list;
    }
    
    public static void main(String argfs[])
    {
        
    }
    
}
