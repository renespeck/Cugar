/*
 * GraphFold.java
 *
 * Created on 14. Januar 2008, 13:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.uni_leipzig.bf.util;
import java.io.*;
/**
 *
 * @author an
 */
public class GraphFold
{
    ValuedAlphaList smallBig;
    ValuedAlphaList bigSmall;
    /** Creates a new instance of GraphFold */
    public GraphFold()
    {
    }
    
    /** Splits a list contained in a file in 2 list and sorts it. Results are written in smallBig and bigSmall;
     */
    public void split(String in, int number)
    {
        int counter = 0;
        try
        {
            System.out.println("Splitting list in file "+in);
            BufferedReader reader = new BufferedReader(new FileReader(in));
            bigSmall = new ValuedAlphaList();
            smallBig = new ValuedAlphaList();
            String s = reader.readLine(), key1, key2, help;
            int index;
            double score;
            while (s != null && counter != number)
            {
                counter ++;
                key1 = s.substring(0, s.indexOf(" "));
                key2 = s.substring(s.indexOf(" ") + 1, s.indexOf(";"));
                // transform scores s to -1/log10(s)
                score = -1 / java.lang.Math.log10(Double.parseDouble(s.substring(s.indexOf(";") + 1)));
                //score = Double.parseDouble(s.substring(s.indexOf(";")+1));
                if(key1.compareTo(key2) < 0)
                {
                    smallBig.addd(key1+"?"+key2+"?"+score);
                }
                else if(key1.compareTo(key2) > 0)
                {
                    bigSmall.addd(key2+"?"+key1+"?"+score);
                }
                
                s = reader.readLine();
                if(counter % 10000 == 0) System.out.println(counter + " lines were processed ...");
            }
            System.out.println("Splitting completed ...");
        }
        catch (IOException ioe)
        {
            System.err.println("I/O Exception");
            ioe.printStackTrace();
        }
    }
    
    public void mergeAndWrite(String out)
    {
        try
        {
            System.out.println("Merging ...");
            int sbSize = smallBig.size();
            int bSSize = bigSmall.size();
            int i = 0, j = 0, comp = 0;
            double score = 0;
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(out)));
            while(i < sbSize && j < bSSize)
            {
                comp = smallBig.getKey(i).compareTo(bigSmall.getKey(j));
                if(comp == 0)
                {
                    score = smallBig.getValue(i) + bigSmall.getValue(j);
                    writer.println(smallBig.getKey(i)+"?"+score);
                    i++;
                    j++;
                }
                else if(comp < 0)
                {
                    writer.println(smallBig.get(i));
                    i++;
                }
                else if(comp > 0)
                {
                    writer.println(bigSmall.get(j));
                    j++;
                }
            }
            
            if(i < sbSize)
            {
                while(i < sbSize)
                {
                    writer.println(smallBig.get(i));
                    i++;
                }
            }
            
            else if(j < bSSize)
            {
                while(j < bSSize)
                {
                    writer.println(bigSmall.get(j));
                    j++;
                }
            }
            writer.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void foldGraph(String in, String out, int number)
    {
        split(in, number);
        mergeAndWrite(out);
    }
    
    public void countAndFilter()
    {
        
    }
    
    public static void main(String args[])
    {
        GraphFold g = new GraphFold();
        g.foldGraph("C:/Diss/Evaluation/ohsu-trec/MWU results/result.SRE.sort.csv","C:/Diss/Evaluation/ohsu-trec/MWU results/result.SRE.sort.100k.csv", 100000);
        //g.foldGraph("C:/Diss/Evaluation/ohsu-trec/MWU results/test.txt","C:/Diss/Evaluation/ohsu-trec/MWU results/test.folded.txt",100);
        System.out.println("SmallBig = "+g.smallBig.size()+", BigSmall = "+g.bigSmall.size()+".");
    }
}
