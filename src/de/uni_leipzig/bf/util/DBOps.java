/*
 * DBOps.java
 *
 * Created on 6. Dezember 2007, 14:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.uni_leipzig.bf.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.TreeSet;
import java.io.*;
import net.sourceforge.jtds.jdbc.*;
import gnu.getopt.Getopt;
/**
 *
 * @author an
 */
public class DBOps
{
    
    /** Creates a new instance of DBOps */
    public DBOps()
    {
    }
    
    /** Get connection to database
     */
    public static Connection getMySQLConnection(String ip, String database, String user, String password)
    {
        Connection con = null;
        try
        {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            con = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+database, user, password);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return con;
    }
    
    public static Connection getConnection(String ip, String db, String user, String passwd) throws Exception
    {
        Connection conn = null;
        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String conString = "jdbc:jtds:sqlserver://" + ip + ":1433/" + db;
            conn = DriverManager.getConnection(conString, user, passwd);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return conn;
    }
    
    /** Reads in an n-gram file and writes it in the specified database
     */
    public static void importIDs(Connection conn, String file, String table)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s = reader.readLine(), key;
            Statement stmt = conn.createStatement();
            while (s != null)
            {
                key = s.substring(0, s.indexOf(" ") - 1);
                String query = "INSERT into " + table + "(catname) values ('" + key + "')";
                stmt.executeUpdate(query);
                s = reader.readLine();
            }
        }
        catch (IOException ioe)
        {
            System.err.println("I/O Exception");
            ioe.printStackTrace();
        }
        catch (SQLException sql)
        {
            System.err.println("I/O Exception");
            sql.printStackTrace();
        }
        
    }
    
    /** Import biology results
     */
    public static void importBioResults(Connection conn, String file, String catTable, String simTable)
    {
        try
        {
            // first get indexes
            ArrayList indexInDB = new ArrayList();
            ArrayList otherIndexes = new ArrayList();
            
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s = reader.readLine();
            String split[] = s.split(" ");
            indexInDB.add(split[0]);
            otherIndexes.add(split[1]);
            
            System.out.println("Computing indexes ...");
            s = reader.readLine();
            while(s!=null)
            {
                split = s.split(" ");
                // get indexes in right order
                if(!indexInDB.get(indexInDB.size() - 1).equals(split[0]))
                {
                    indexInDB.add(split[0]);
                }
                // get list of all indexes to make sure we have them all later
                if(!otherIndexes.contains(split[1]))
                {
                    otherIndexes.add(split[1]);
                }
                s = reader.readLine();
            }
            for(int i=0; i < otherIndexes.size(); i++)
            {
                if(!indexInDB.contains(otherIndexes.get(i)))
                    indexInDB.add(otherIndexes.get(i));
            }
            //System.out.println(indexInDB);
            reader.close();
            
            //write indexes in database
            Statement stmt = conn.createStatement();
            System.out.println("Writing indexes to database ...");
            
            for(int i=0; i<indexInDB.size(); i++)
            {
                String query = "INSERT into " + catTable + "(catname) values ('" + indexInDB.get(i) + "')";
                try
                {
                    stmt.executeUpdate(query);
                }
                catch(Exception e)
                {
                    System.err.println("Error while inserting "+indexInDB.get(i));
                    e.printStackTrace();
                }
            }
            
            // now write similarity database
            System.out.println("Writing similarities in database ...");
            reader = new BufferedReader(new FileReader(file));
            s = reader.readLine();
            String query;
            double score;
            while (s != null)
            {
                split = s.split(" ");
                score = Double.parseDouble(split[2]);
                score = score*score;
                if(score > 0.1)
                {
                    if(score > 1) score = 1;
                    query = "INSERT into " + simTable + "(catID1, catID2, similarity) values ('" + (indexInDB.indexOf(split[0]) + 1) + "', '" + (indexInDB.indexOf(split[1]) + 1) + "', '" + split[2] + "')";
                    stmt.executeUpdate(query);
                }
                s = reader.readLine();
            }
            System.out.println("Done.");
        }
        catch (IOException ioe)
        {
            System.err.println("I/O Exception");
            ioe.printStackTrace();
        }
        catch (SQLException sql)
        {
            System.err.println("Database Exception");
            sql.printStackTrace();
        }
    }
    
    public static void symmetrify(String in, String out)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(in));
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(out)));
            AlphaList keys = new AlphaList();
            ArrayList<Double> scores = new ArrayList<Double>();
            String s = reader.readLine(), query, key1, key2;
            int index;
            double score;
            while (s != null)
            {
                key1 = s.substring(0, s.indexOf(" "));
                key2 = s.substring(s.indexOf(" ") + 1, s.indexOf(";") - 1);
                // transform scores s to -1/log10(s)
                score = -1 / java.lang.Math.log10(Double.parseDouble(s.substring(s.indexOf(";") + 1)));
                // score = Double.parseDouble(s.substring(s.indexOf(";")+1));
                index = keys.indexOf(key1 + " " + key2);
                if (index == -1)
                {
                    keys.add(key2 + " " + key1);
                    scores.add(new Double(score));
                }
                else
                {
                    scores.set(index, new Double(scores.get(index).doubleValue() + score));
                }
                s = reader.readLine();
            }
            int size = scores.size();
            for (int i = 0; i < size; i++)
            {
                score = scores.get(i).doubleValue();
                if (score > java.lang.Math.pow(10, -6))
                {
                    writer.println(keys.get(i) + "; " + score);
                }
            }
            writer.close();
        }
        catch (IOException ioe)
        {
            System.err.println("I/O Exception");
            ioe.printStackTrace();
        }
    }
    
    public static void importNGram(Connection conn, String file, String ids, String table)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s = reader.readLine(), query, key1, key2;
            int id1, id2, help;
            double score;
            Statement stmt = conn.createStatement();
            ResultSet rset;
            AlphaList l = new AlphaList();
            int counter = 0;
            while (s != null)
            {
                counter++;
                key1 = s.substring(0, s.indexOf(" "));
                key2 = s.substring(s.indexOf(" ") + 1, s.indexOf(";"));
                
                // transform scores s to -1/log10(s)
                score = -1 / java.lang.Math.log10(Double.parseDouble(s.substring(s.indexOf(";") + 1)));
                // do not insert loops
                if (!key1.equals(key2))
                {
                    // get id of first wordform
                    rset = stmt.executeQuery("SELECT ID FROM " + ids + " WHERE catname = '" + key1 + "'");
                    rset.next();
                    id1 = rset.getInt("ID");
                    
                    // get id of second wordform
                    rset = stmt.executeQuery("SELECT ID FROM " + ids + " WHERE catname = '" + key2 + "'");
                    rset.next();
                    id2 = rset.getInt("ID");
                    
                    // entries are ordered
                    if (id1 > id2)
                    {
                        help = id1;
                        id1 = id2;
                        id2 = help;
                    }
                    
                    // if entry in database then update score
                    if (l.idxOf(id1 + "?" + id2)!=-1)
                    {
                        query = "SELECT similarity from " + table + " where catID1 = " + id1 + " AND catID2 = " + id2 + ";";
                        
                        rset = stmt.executeQuery(query);
                        if (rset.next())
                        {
                            score = score + rset.getDouble("similarity");
                        }
                        
                        if (score > java.lang.Math.pow(10, -5))
                        {
                            query = "UPDATE " + table + " set similarity = " + score + " where catID1 = " + id1 + " AND catID2 = " + id2 + ";";
                            stmt.executeUpdate(query);
                        }
                    }
                    // else simply write in database
                    else
                    {
                        if (score > java.lang.Math.pow(10, -5))
                        {
                            query = "INSERT into " + table + "(catID1, catID2, similarity) values ('" + id1 + "', '" + id2 + "', '" + score + "')";
                            stmt.executeUpdate(query);
                            l.addd(id1 + "?" + id2);
                        }
                    }
                }
                s = reader.readLine();
                if (counter % 10000 == 0)
                {
                    System.out.println(counter + " lines processed ...");
                }
            }
        }
        catch (IOException ioe)
        {
            System.err.println("I/O Exception");
            ioe.printStackTrace();
        }
        catch (SQLException sql)
        {
            System.err.println("I/O Exception");
            sql.printStackTrace();
        }
    }
        /** Downloads the clusters from the database
     */
    public static void downloadClusters(Connection conn, String clusterTable, String idTable, String out)
    {
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rset, rset2, rset3;
            // first get all cluster ids
            System.out.println("Getting cluster ids ...");
            ArrayList<Integer> l = new ArrayList<Integer>();
            ArrayList<Integer> ids = new ArrayList<Integer>();
            rset = stmt.executeQuery("SELECT DISTINCT clusterID FROM " + clusterTable);
            
            System.out.println("Getting clusters ...");
            while(rset.next())
            {
                l.add(new Integer(rset.getInt("clusterID")));
            }
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(out)));
            
            System.out.println("Writing out categories ...");
            for(int i=0; i< l.size(); i++)
            {
                rset = stmt.executeQuery("SELECT catname FROM " + idTable + " WHERE ID IN " +
                        "(SELECT catID FROM " + clusterTable + " WHERE clusterID = " + l.get(i).intValue() +")" );
                while(rset.next())
                {
                    writer.print(rset.getString("catname")+" ");
                }
                writer.println();
            }
            writer.close();
            
            System.out.println("Done!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /** Downloads the clusters from the database
     */
    public static void downloadClustersErroneous(Connection conn, String clusterTable, String idTable, String out)
    {
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rset, rset2, rset3;
            // first get all cluster ids
            System.out.println("Getting cluster ids ...");
            ArrayList<Integer> l = new ArrayList<Integer>();
            ArrayList<Integer> seeds = new ArrayList<Integer>();
            ArrayList<Integer> ids = new ArrayList<Integer>();
            
            rset = stmt.executeQuery("SELECT DISTINCT seedID FROM seeds order by seedID asc");
            
            System.out.println("Getting seeds ...");
            while(rset.next())
            {
                seeds.add(new Integer(rset.getInt("seedID")));
            }
            
            System.out.println("Getting clusters ...");
            for(int i=0; i<seeds.size(); i++)
            {
                rset = stmt.executeQuery("SELECT clusterID FROM seeds WHERE seedID = " + seeds.get(i));
                PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(out)));
                                
                while(rset.next())
                {
                    l.add(new Integer(rset.getInt("clusterID")));
                }
            }
            
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(out)));
            
            System.out.println("Writing out categories ...");
            for(int i=0; i< l.size(); i++)
            {
                rset = stmt.executeQuery("SELECT catname FROM " + idTable + " WHERE ID IN " +
                        "(SELECT catID FROM " + clusterTable + " WHERE clusterID = " + l.get(i).intValue() +")" );
                while(rset.next())
                {
                    writer.print(rset.getString("catname")+" ");
                }
                writer.println();
            }
            writer.close();
            
            System.out.println("Done!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /** Removes the n*100% most frequent word from the similarity database
     */
    public static void clean(Connection conn, String idFile, String idTable, String similarityTable, double n)
    {
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rset;
            ArrayList l = getMostFrequent(idFile, n);
            int id;
            System.out.println("Cleaning database ...");
            for(int i=0; i< l.size(); i++)
            {
                rset = stmt.executeQuery("SELECT ID FROM " + idTable + " WHERE catname = '" + l.get(i) + "'");
                rset.next();
                id = rset.getInt("ID");
                System.out.println("Cleaning all entries containing "+l.get(i)+ " with ID = "+id+"... ");
                stmt.executeUpdate("DELETE FROM " + similarityTable + " WHERE (catid1 = '" + id + "' OR catid2 = '" + id +"')");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /** Works only for folded graphs
     */
    public static void importNGramV2(Connection conn, String file, String ids, String table)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s = reader.readLine(), query, key1, key2;
            int id1, id2, help;
            double score;
            Statement stmt = conn.createStatement();
            ResultSet rset;
            int counter = 0;
            while (s != null)
            {
                counter++;
                key1 = s.substring(0, s.indexOf("?"));
                key2 = s.substring(s.indexOf("?") + 1, s.lastIndexOf("?"));
                score = Double.parseDouble(s.substring(s.lastIndexOf("?") + 1));
                // do not insert loops
                if (!key1.equals(key2))
                {
                    try
                    {
                        // get id of first wordform
                        rset = stmt.executeQuery("SELECT ID FROM " + ids + " WHERE catname = '" + key1 + "'");
                        rset.next();
                        id1 = rset.getInt("ID");
                        
                        // get id of second wordform
                        rset = stmt.executeQuery("SELECT ID FROM " + ids + " WHERE catname = '" + key2 + "'");
                        rset.next();
                        id2 = rset.getInt("ID");
                        query = "INSERT into " + table + "(catID1, catID2, similarity) values ('" + id1 + "', '" + id2 + "', '" + score + "')";
                        stmt.executeUpdate(query);
                    }
                    catch(Exception e)
                    {
                        System.err.println("SQL Error when inserting "+key1+" "+key2);
                    }
                }
                
                s = reader.readLine();
                if (counter % 10000 == 0)
                {
                    System.out.println(counter + " lines processed ...");
                }
            }
        }
        catch (IOException ioe)
        {
            System.err.println("I/O Exception");
            ioe.printStackTrace();
        }
        catch (SQLException sql)
        {
            System.err.println("Database Exception");
            sql.printStackTrace();
        }
    }
    
    
    public static void symV2(String in, String out, int number)
    {
        int counter = 0;
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(in));
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(out)));
            ValuedAlphaList entries = new ValuedAlphaList();
            String s = reader.readLine(), key1, key2, help;
            int index;
            double score;
            while (s != null && counter < number)
            {
                counter ++;
                key1 = s.substring(0, s.indexOf(" "));
                key2 = s.substring(s.indexOf(" ") + 1, s.indexOf(";"));
                // transform scores s to -1/log10(s)
                score = -1 / java.lang.Math.log10(Double.parseDouble(s.substring(s.indexOf(";") + 1)));
                //score = Double.parseDouble(s.substring(s.indexOf(";")+1));
                
                if(key1.compareTo(key2) > 0)
                {
                    help = key1;
                    key1 = key2;
                    key2 = help;
                }
                entries.addd(key1+"?"+key2+"?"+score);
                s = reader.readLine();
                if(counter % 10000 == 0) System.out.println(counter + " lines were processed ...");
            }
            int size = entries.size();
            String entry;
            for (int i = 0; i < size; i++)
            {
                entry = (String)(entries.get(i));
                if (Double.parseDouble(entry.substring(entry.lastIndexOf("?")+1)) > java.lang.Math.pow(10, -6))
                {
                    writer.println(entry);
                }
            }
            writer.close();
        }
        catch (IOException ioe)
        {
            System.err.println("I/O Exception");
            ioe.printStackTrace();
        }
    }
    
    public static ArrayList getMostFrequent(String file, double n)
    {
        if(n > 1) n = 1;
        ArrayList<String> result=new ArrayList<String>();
        try
        {
            System.out.println("Reading in list ...");
            ArrayList<WeightedKeyword> words = new ArrayList<WeightedKeyword>();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s = reader.readLine();
            while(s!=null)
            {
                words.add(new WeightedKeyword(s.substring(0,s.indexOf(" ")-1), Double.parseDouble(s.substring(s.indexOf(" ")+1, s.lastIndexOf(" ")))));
                s = reader.readLine();
            }
            
            System.out.println("Sorting list ...");
            words = QuickSort.quickSort(words);
            
            System.out.println("Computing results ...");
            n = n*(double)words.size();
            for(int i=0; i<n && i<words.size(); i++)
            {
                result.add(words.get(i).getKeyword());
            }
            System.out.println("Done.");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return result;
    }
    
    
    public static void main(String[] args)
    {
        /*
        DBOps d = new DBOps();
        Runtime r = Runtime.getRuntime();
        System.out.println("Maximal memory = "+r.maxMemory());
        System.out.println("Free memory = "+r.freeMemory());
        try
        {
            // get the cmd-line parameters and parse them
         
            Getopt g = new Getopt(d.getClass().getName(), args, "i:o:n:w");
         
            int arg, number = 10000;
            String input=null;
            String output=null;
            while ((arg = g.getopt()) != -1)
            {
                switch (arg)
                {
                    case 'i':
                        input = g.getOptarg();
         
                        break;
                    case 'o':
                        output = g.getOptarg();
         
                        break;
         
                    case 'n':
                        number = Integer.parseInt(g.getOptarg());
         
                        break;
         
                    default:
                        System.out.println("Use -i input -o output -n number_of_nodes");
                        break;
                }
            }
         
            GraphFold gr = new GraphFold();
            System.out.println("Using GraphFold");
            gr.foldGraph(input, output, number);
            //symV2
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
//         */
        String ip = "localhost";
        String database = "allvocab";
        String user = "axel";
        String password = "mineral";
        
        String ids = "C:/Berechnungen/Databases/dist/result.1G";
        String sims = "C:/Berechnungen/Databases/dist/result.SRE.sort.ALL.folded.csv";
        String catTable = "category";
        String clusterTable = "catInCluster";
        String simTable = "similarity";
        Connection conn;
        
        try
        {

            conn = getMySQLConnection(ip, "bmcf100s100", user, password);
            System.out.println("Connection to catalog " + conn.getCatalog() + " exists.");
            //importBioResults(conn, "C:/Berechnungen/output2_tau1_go.txt", catTable, simTable);
            downloadClusters(conn, clusterTable, catTable, "C:/Berechnungen/pmc/dl/f100s100.bmc.txt");
            conn.close();
            
            conn = getMySQLConnection(ip, "bmcf100s200", user, password);
            System.out.println("Connection to catalog " + conn.getCatalog() + " exists.");
            //importBioResults(conn, "C:/Berechnungen/output2_tau1_go.txt", catTable, simTable);
            downloadClusters(conn, clusterTable, catTable, "C:/Berechnungen/pmc/dl/f100s200.bmc.txt");
            conn.close();
            
            conn = getMySQLConnection(ip, "bmcf100s400", user, password);
            System.out.println("Connection to catalog " + conn.getCatalog() + " exists.");
            //importBioResults(conn, "C:/Berechnungen/output2_tau1_go.txt", catTable, simTable);
            downloadClusters(conn, clusterTable, catTable, "C:/Berechnungen/pmc/dl/f100s400.bmc.txt");
            conn.close();
            
            conn = getMySQLConnection(ip, "bmcf250s100", user, password);
            System.out.println("Connection to catalog " + conn.getCatalog() + " exists.");
            //importBioResults(conn, "C:/Berechnungen/output2_tau1_go.txt", catTable, simTable);
            downloadClusters(conn, clusterTable, catTable, "C:/Berechnungen/pmc/dl/f250s100.bmc.txt");
            conn.close();
            
            conn = getMySQLConnection(ip, "bmcf250s200", user, password);
            System.out.println("Connection to catalog " + conn.getCatalog() + " exists.");
            //importBioResults(conn, "C:/Berechnungen/output2_tau1_go.txt", catTable, simTable);
            downloadClusters(conn, clusterTable, catTable, "C:/Berechnungen/pmc/dl/f250s200.bmc.txt");
            conn.close();
            
            conn = getMySQLConnection(ip, "bmcf250s400", user, password);
            System.out.println("Connection to catalog " + conn.getCatalog() + " exists.");
            //importBioResults(conn, "C:/Berechnungen/output2_tau1_go.txt", catTable, simTable);
            downloadClusters(conn, clusterTable, catTable, "C:/Berechnungen/pmc/dl/f250s400.bmc.txt");
            conn.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("No connection");
        }
        
        
    }
}
