/*
 * Evaluator.java
 *
 * Created on 7. Juli 2008, 12:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.uni_leipzig.bf.eval;
import java.util.*;
import java.io.*;
/**
 *
 * @author an
 */
public class Evaluator
{
    
    /** Creates a new instance of Evaluator */
    public Evaluator()
    {
    }
    
    /** Evaluates one cluster against a given map
     */
    public static double getPurity(HashMap<String, TreeSet<String>> map, TreeSet<String> cluster)
    {
        TreeSet<String> cats = new  TreeSet<String>();
        HashMap<String, Integer> catCount = new HashMap<String, Integer>();
        Iterator iter = cluster.iterator();
        String node;
        String cat;
        ArrayList<String> nodes = new ArrayList<String>();
        double counter = 0;
        while(iter.hasNext())
        {
            node = (String)iter.next();
            if(map.containsKey(node))
            {
                nodes.add(node);
                counter ++;
                cats.addAll(map.get(node));
            }
        }
        
        iter = cats.iterator();
        while(iter.hasNext())
        {
            catCount.put((String)iter.next(), new Integer(0));
        }
        
        iter = cats.iterator();
        while(iter.hasNext())
        {
            cat = (String)iter.next();
            for(int i=0; i < nodes.size(); i++)
            {
                if(map.get(nodes.get(i)).contains(cat))
                    catCount.put(cat, new Integer(catCount.get(cat).intValue()+1));
            }
        }
        
        double max = 0;
        double value;
        iter = catCount.keySet().iterator();
        while(iter.hasNext())
        {
            value = catCount.get(iter.next()).doubleValue();
            if(value > max) max = value;
        }
        
        return max/counter;
    }
    
    public static void computePurity(String file, String mesh, String output, int level)
    {
        HashMap<String, TreeSet<String>> map = MeshEval.readMesh(mesh, level);
        try
        {
            BufferedReader reader= new BufferedReader(new FileReader(file));
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(output)));
            String s = reader.readLine();
            String split[];
            TreeSet<String> tree;
            double purity;
            while(s!=null)
            {
                tree = new TreeSet<String>();
                split = s.split(" ");
                for(int i=0; i<split.length; i++)
                {
                    tree.add(split[i]);
                }
                purity = getPurity(map, tree);
                if(purity > 0)
                {
                    writer.println(purity);
                }
                s = reader.readLine();
            }
            writer.close();
            reader.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static void main(String args[])
    {
        
        computePurity("C:/Diss/Evaluation/Clustering/PMC/purity/f100s100.bmc.txt", "C:/Diss/Evaluation/Clustering/d2008.txt",
                "C:/Diss/Evaluation/Clustering/TREC features/clusters/f100s100.l1.bmc.txt", 1);
        computePurity("C:/Diss/Evaluation/Clustering/PMC/purity/f100s100.bmc.txt", "C:/Diss/Evaluation/Clustering/d2008.txt",
                "C:/Diss/Evaluation/Clustering/TREC features/clusters/f100s100.l2.bmc.txt", 2);
        computePurity("C:/Diss/Evaluation/Clustering/PMC/purity/f100s100.bmc.txt", "C:/Diss/Evaluation/Clustering/d2008.txt",
                "C:/Diss/Evaluation/Clustering/TREC features/clusters/f100s100.l3.bmc.txt", 3);
        
        computePurity("C:/Diss/Evaluation/Clustering/PMC/purity/f100s200.bmc.txt", "C:/Diss/Evaluation/Clustering/d2008.txt",
                "C:/Diss/Evaluation/Clustering/TREC features/clusters/f100s200.l1.bmc.txt", 1);
        computePurity("C:/Diss/Evaluation/Clustering/PMC/purity/f100s200.bmc.txt", "C:/Diss/Evaluation/Clustering/d2008.txt",
                "C:/Diss/Evaluation/Clustering/TREC features/clusters/f100s200.l2.bmc.txt", 2);
        computePurity("C:/Diss/Evaluation/Clustering/PMC/purity/f100s200.bmc.txt", "C:/Diss/Evaluation/Clustering/d2008.txt",
                "C:/Diss/Evaluation/Clustering/TREC features/clusters/f100s200.l3.bmc.txt", 3);

                computePurity("C:/Diss/Evaluation/Clustering/PMC/purity/f100s400.bmc.txt", "C:/Diss/Evaluation/Clustering/d2008.txt",
                "C:/Diss/Evaluation/Clustering/TREC features/clusters/f100s400.l1.bmc.txt", 1);
        computePurity("C:/Diss/Evaluation/Clustering/PMC/purity/f100s400.bmc.txt", "C:/Diss/Evaluation/Clustering/d2008.txt",
                "C:/Diss/Evaluation/Clustering/TREC features/clusters/f400s100.l2.bmc.txt", 2);
        computePurity("C:/Diss/Evaluation/Clustering/PMC/purity/f100s400.bmc.txt", "C:/Diss/Evaluation/Clustering/d2008.txt",
                "C:/Diss/Evaluation/Clustering/TREC features/clusters/f100s400.l3.bmc.txt", 3);

                computePurity("C:/Diss/Evaluation/Clustering/PMC/purity/f250s100.bmc.txt", "C:/Diss/Evaluation/Clustering/d2008.txt",
                "C:/Diss/Evaluation/Clustering/TREC features/clusters/f250s100.l1.bmc.txt", 1);
        computePurity("C:/Diss/Evaluation/Clustering/PMC/purity/f250s100.bmc.txt", "C:/Diss/Evaluation/Clustering/d2008.txt",
                "C:/Diss/Evaluation/Clustering/TREC features/clusters/f250s100.l2.bmc.txt", 2);
        computePurity("C:/Diss/Evaluation/Clustering/PMC/purity/f250s100.bmc.txt", "C:/Diss/Evaluation/Clustering/d2008.txt",
                "C:/Diss/Evaluation/Clustering/TREC features/clusters/f250s100.l3.bmc.txt", 3);

                computePurity("C:/Diss/Evaluation/Clustering/PMC/purity/f250s200.bmc.txt", "C:/Diss/Evaluation/Clustering/d2008.txt",
                "C:/Diss/Evaluation/Clustering/TREC features/clusters/f250s200.l1.bmc.txt", 1);
        computePurity("C:/Diss/Evaluation/Clustering/PMC/purity/f250s200.bmc.txt", "C:/Diss/Evaluation/Clustering/d2008.txt",
                "C:/Diss/Evaluation/Clustering/TREC features/clusters/f250s200.l2.bmc.txt", 2);
        computePurity("C:/Diss/Evaluation/Clustering/PMC/purity/f250s200.bmc.txt", "C:/Diss/Evaluation/Clustering/d2008.txt",
                "C:/Diss/Evaluation/Clustering/TREC features/clusters/f250s200.l3.bmc.txt", 3);

                computePurity("C:/Diss/Evaluation/Clustering/PMC/purity/f250s400.bmc.txt", "C:/Diss/Evaluation/Clustering/d2008.txt",
                "C:/Diss/Evaluation/Clustering/TREC features/clusters/f250s400.l1.bmc.txt", 1);
        computePurity("C:/Diss/Evaluation/Clustering/PMC/purity/f250s400.bmc.txt", "C:/Diss/Evaluation/Clustering/d2008.txt",
                "C:/Diss/Evaluation/Clustering/TREC features/clusters/f250s400.l2.bmc.txt", 2);
        computePurity("C:/Diss/Evaluation/Clustering/PMC/purity/f250s400.bmc.txt", "C:/Diss/Evaluation/Clustering/d2008.txt",
                "C:/Diss/Evaluation/Clustering/TREC features/clusters/f250s400.l3.bmc.txt", 3);

//        
//        HashMap<String, TreeSet<String>> map = new HashMap<String, TreeSet<String>>();
//
//        TreeSet<String> cluster = new TreeSet<String>();
//        cluster.add("A");
//        cluster.add("B");
//        cluster.add("C");
//        cluster.add("D");
//
//        TreeSet<String> cats = new TreeSet<String>();
//        cats.add("Omega");
//        cats.add("Alpha");
//        map.put("A", cats);
//
//        cats = new TreeSet<String>();
//        cats.add("Alpha");
//        map.put("B", cats);
//
//        cats = new TreeSet<String>();
//        cats.add("Gamma");
//        map.put("C", cats);
//
//        System.out.println("Purity is "+getPurity(map, cluster));
//
    }
}
