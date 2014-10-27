/*
 * SilhouetteCleaner.java
 *
 * Created on 1. Juli 2008, 12:08
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
public class SilhouetteCleaner
{
    
    /** Creates a new instance of SilhouetteCleaner */
    public SilhouetteCleaner()
    {
    }
    
    public static void clean(String in, String out)
    {
        try
        {
            BufferedReader reader= new BufferedReader(new FileReader(in));
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(out)));
            String s = reader.readLine();
            s = reader.readLine();
            while(s!=null)
            {
                s = s.split(",")[0];
                if(Double.parseDouble(s) > 1) writer.println(1);
                else writer.println(s);
                s = reader.readLine();
            }
            reader.close();
            writer.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static void main(String args[])
    {
        clean("C:/Diss/Evaluation/Clustering/PMC/silhouette/bmcf100s100sil.csv", "C:/Diss/Evaluation/Clustering/PMC/bmcf100s100sil.clean.csv");
        clean("C:/Diss/Evaluation/Clustering/PMC/silhouette/bmcf100s200sil.csv", "C:/Diss/Evaluation/Clustering/PMC/bmcf100s200sil.clean.csv");
        clean("C:/Diss/Evaluation/Clustering/PMC/silhouette/bmcf100s400sil.csv", "C:/Diss/Evaluation/Clustering/PMC/bmcf100s400sil.clean.csv");
        clean("C:/Diss/Evaluation/Clustering/PMC/silhouette/bmcf250s100sil.csv", "C:/Diss/Evaluation/Clustering/PMC/bmcf250s100sil.clean.csv");
        clean("C:/Diss/Evaluation/Clustering/PMC/silhouette/bmcf250s200sil.csv", "C:/Diss/Evaluation/Clustering/PMC/bmcf250s200sil.clean.csv");
        clean("C:/Diss/Evaluation/Clustering/PMC/silhouette/bmcf250s400sil.csv", "C:/Diss/Evaluation/Clustering/PMC/bmcf250s400sil.clean.csv");
    }
}
