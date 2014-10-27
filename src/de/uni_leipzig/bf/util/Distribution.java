/*
 * Distribution.java
 *
 * Created on 18. Juni 2008, 23:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.uni_leipzig.bf.util;
import java.io.*;
import java.util.*;
/**
 *
 * @author an
 */
public class Distribution
{
    
    /** Creates a new instance of Distribution */
    public Distribution()
    {
    }
    
    public static void analyze(String file)
    {
        int count[] = new int[1000];
        for(int i=0; i<1000; i++)
                count[i] = 0;
            
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s = reader.readLine();
            double weight;            
            
            while(s!=null)
            {
                weight = Double.parseDouble(s.split(" ")[2])*1000;
                count[(int)weight]++;
                s = reader.readLine();
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        for(int i=0; i<1000; i++)
                System.out.println((i+1)+" "+count[i]);
    }
    
    public static void main(String args[])
    {
        analyze("F:/Sicherung II/trec.filtered.20k.sim");
    }
}
