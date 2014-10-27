/*
 * FilterSimilarityFile.java
 *
 * Created on 8. Juli 2008, 10:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.uni_leipzig.bf.eval;
import java.io.*;
/**
 *
 * @author an
 */
public class FilterSimilarityFile
{
    
    /** Creates a new instance of FilterSimilarityFile */
    public FilterSimilarityFile()
    {
    }
    
    /** Filters similarity files and allows only values above a given threshold
     */
    
    public static void filterSimFile(String file, double threshold, String output)
    {
        try
        {
            System.out.println("Reading in gold standard");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(output)));
            String s=reader.readLine();
            String split[];
            while(s!=null)
            {
                split = s.split(" ");
                if(Double.parseDouble(split[2]) > threshold) writer.println(s);
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
        filterSimFile("E:/Transfer/sim/bmc.f100.s200.sim", 0.05, "E:/Transfer/sim/bmc.clean.f100.s200.sim");
        filterSimFile("E:/Transfer/sim/bmc.f100.s400.sim", 0.05, "E:/Transfer/sim/bmc.clean.f100.s400.sim");
        filterSimFile("E:/Transfer/sim/bmc.f250.s100.sim", 0.05, "E:/Transfer/sim/bmc.clean.f250.s100.sim");
        filterSimFile("E:/Transfer/sim/bmc.f250.s200.sim", 0.05, "E:/Transfer/sim/bmc.clean.f250.s200.sim");
        filterSimFile("E:/Transfer/sim/bmc.f250.s400.sim", 0.05, "E:/Transfer/sim/bmc.clean.f250.s400.sim");
    }
}
