/*
 * MeshEval.java
 *
 * Created on 7. Juli 2008, 12:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.uni_leipzig.bf.eval;
import java.io.*;
import java.util.*;
/**
 *
 * @author an
 */
public class MeshEval
{
    
    /** Creates a new instance of MeshEval */
    public MeshEval()
    {
    }
    
    /** Read in the mesh mapping
     */
    
    public static HashMap<String, TreeSet<String>> readMesh(String file, int depth)
    {
        HashMap<String, TreeSet<String>> map = new HashMap<String, TreeSet<String>> ();
        if (depth > 3) depth = 3;
        try
        {
            BufferedReader reader= new BufferedReader(new FileReader(file));
            String s = reader.readLine();
            TreeSet<String> category = new TreeSet<String>();
            TreeSet<String> entries = new TreeSet<String>();
            String cat;
            String name = null, entry;
            String split[];
            while(s!=null)
            {
                s = reader.readLine();
                if(s.startsWith("MH = "))
                {
                    if(name!=null)
                    {
                        // put name
                        if(map.containsKey(name))
                        {
                            category.addAll(map.get(name));
                        }
                        map.put(name, category);
                        // put other entries
                        Iterator iter = entries.iterator();
                        while(iter.hasNext())
                        {
                            entry = (String) iter.next();
                            if(map.containsKey(entry))
                            {
                                category.addAll(map.get(entry));
                            }
                            map.put(name, category);
                        }
                        
                        // reninit
                        category = new TreeSet<String>();
                        entries = new TreeSet<String>();
                        name = "";
                    }
                    name = s.substring(5);
                    name = name.replaceAll(",", "");
                    name = name.replaceAll(" ", "_").toLowerCase();
                }
                else if(s.startsWith("MN = "))
                {
                    //System.out.println(s);
                    //System.out.println(s.substring(5));
                    split = s.substring(5).split("\\.");
                    //System.out.println(split.length);
                    cat = split[0];
                    for(int i=1; i<depth && i<split.length; i++)
                        cat = cat+"."+split[i];
                    category.add(cat);
                }
                else if(s.startsWith("ENTRY = "))
                {
                    if(s.contains("|"))
                        entry = s.substring(8).split("|")[0];
                    else
                        entry = s;
                    entry = entry.replaceAll(",", "");
                    entry = entry.replaceAll(" ", "_");
                    entries.add(entry.toLowerCase());
                }
                s = reader.readLine();
            }
            
            // get last entry set
            if(name!=null)
            {
                // put name
                if(map.containsKey(name))
                {
                    category.addAll(map.get(name));
                }
                map.put(name, category);
                // put other entries
                Iterator iter = entries.iterator();
                while(iter.hasNext())
                {
                    entry = (String) iter.next();
                    if(map.containsKey(entry))
                    {
                        category.addAll(map.get(entry));
                    }
                    map.put(name, category);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return map;
    }
    
    public static void main(String args[])
    {
        readMesh("F:/DSIS/d2008.txt", 1);
    }
}
