/*
 * DataReader.java
 *
 * Created on 20. März 2009, 16:56
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
public class DataReader {

    HashMap<String, Integer> complexIds;
    HashMap<Integer, TreeSet<String>> complex2protMap;
    HashMap<Integer, TreeSet<String>> cluster2protMap;
    TreeSet<String> complexList;
    double contingencyMatrix[][];
    double separationMatrix[][];

    /** Creates a new instance of DataReader */
    public DataReader(double _contingencyMatrix[][],
            HashMap<String, Integer> _complexIds) {
        contingencyMatrix = _contingencyMatrix;
        complexIds = _complexIds;
    }

    public DataReader(String complexes, String clusters) {
        complexIds = new HashMap<String, Integer>();
        buildContingencyTable(complexes, clusters);
    }

    /**
     * Cleans graph from unclassified proteins
     */
    public static void cleanGraph(String input, String complexes, String output) {
        try {
            // figure out which proteins we've got in the annotated file
            BufferedReader reader = new BufferedReader(
                    new FileReader(complexes));
            TreeSet<String> proteins = new TreeSet<String>();
            String s = reader.readLine();
            String split[];
            while (s != null) {
                split = s.split("\t");
                proteins.add(split[0]);
                s = reader.readLine();
            }
            reader.close();
            System.out.println("Found " + proteins.size() + " proteins");

            // now clean the graph
            reader = new BufferedReader(new FileReader(input));
            PrintWriter writer = new PrintWriter(new BufferedWriter(
                    new FileWriter(output)));
            s = reader.readLine();
            while (s != null) {
                split = s.split("\t");
                if (proteins.contains(split[0]) && proteins.contains(split[1])) {
                    writer.println(s);
                }
                s = reader.readLine();
            }
            writer.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Computes separation by row
     */
    public void computeSeparationMatrix() {
        int n = contingencyMatrix.length;
        int m = contingencyMatrix[0].length;
        double sum;
        double separationByRow[][] = new double[n][m];
        double separationByCol[][] = new double[n][m];
        separationMatrix = new double[n][m];
        for (int i = 0; i < n; i++) {
            sum = 0;
            for (int j = 0; j < m; j++) {
                sum = sum + contingencyMatrix[i][j];
            }
            for (int j = 0; j < m; j++) {
                separationByRow[i][j] = contingencyMatrix[i][j] / sum;
            }
        }

        for (int j = 0; j < m; j++) {
            sum = 0;
            for (int i = 0; i < n; i++) {
                sum = sum + contingencyMatrix[i][j];
            }
            for (int i = 0; i < n; i++) {
                separationByCol[i][j] = contingencyMatrix[i][j] / sum;
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                separationMatrix[i][j] = separationByRow[i][j]
                        * separationByCol[i][j];
            }
        }
    }

    public double[] computeSeparations() {
        computeSeparationMatrix();
        int n = separationMatrix.length;
        int m = separationMatrix[0].length;
        double sum = 0;
        double[] seps = new double[3];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (separationMatrix[i][j] > 0) {
                    sum = sum + separationMatrix[i][j];
                }
            }
        }

        seps[0] = sum / n;
        seps[1] = sum / m;
        seps[2] = java.lang.Math.sqrt(seps[0] * seps[1]);
        return seps;
    }

    public double computeComplexwiseSperation() {
        int n = contingencyMatrix.length;
        int m = contingencyMatrix[0].length;
        double sum = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                sum = sum + separationMatrix[i][j];
            }
        }
        return sum / n;
    }

    /**
     * Computes separation by column
     */
    public void computeSeparationByColumn() {
        int n = contingencyMatrix.length;
        int m = contingencyMatrix[0].length;
        double sum;

    }

    /**
     *
     * public double computeComplexwiseSeparation() {
     *
     * }
     *
     * /** Compute contingency table from cluster2protMap and complex2protMap
     */
    public void buildContingencyTable(String complexes, String clusters) {
        initClusterIds(clusters);
        /*
         * System.out.println("Found " + complexList.size() +
         * " complexes in clusters");
         */
        initComplexIds(complexes);
        contingencyMatrix = new double[complex2protMap.keySet().size()][cluster2protMap.keySet().size()];
        Iterator<Integer> complexIterator, clusterIterator;
        complexIterator = complex2protMap.keySet().iterator();
        Integer complexId, clusterId;
        TreeSet<String> complex, cluster;
        double test;
        while (complexIterator.hasNext()) {
            test = 0;
            complexId = complexIterator.next();
            complex = complex2protMap.get(complexId);

            clusterIterator = cluster2protMap.keySet().iterator();
            while (clusterIterator.hasNext()) {
                clusterId = clusterIterator.next();
                cluster = cluster2protMap.get(clusterId);
                // compute number of common items
                // System.out.println(clusterId.intValue());
                contingencyMatrix[complexId.intValue()][clusterId.intValue()] = intersection(
                        cluster, complex);
                // if(contingencyMatrix[complexId.intValue()][clusterId.intValue()]
                // > 0)
                // System.out.println(complex2protMap.get(complexId)+" "+cluster2protMap.get(clusterId));
                test = test
                        + contingencyMatrix[complexId.intValue()][clusterId.intValue()];
            }
            // System.out.println();
            if (test == 0) {
                System.err.println("No cluster for complex "
                        + complex2protMap.get(complexId));
            }
        }
        // System.out.println("Complex to prot = "+complex2protMap);

        // System.out.println("\n\n\nCluster to prot = "+cluster2protMap);
    }

    /**
     * Compute sensitivity of a clustering
     */
    public double computeSensitivity() {
        int n = contingencyMatrix.length;
        int m = contingencyMatrix[0].length;
        double max, sensitivity = 0, counter = 0;
        for (int i = 0; i < n; i++) {
            max = 0;
            for (int j = 0; j < m; j++) {
                if (contingencyMatrix[i][j] > max) {
                    max = contingencyMatrix[i][j];
                }
            }

            // count only complexes that were contained in initial data
            if (max > 0) {
                counter = counter + complex2protMap.get(new Integer(i)).size();
                sensitivity = sensitivity + max;
            }
        }
        return sensitivity / counter;
    }

    /**
     * Compute sensitivity (and print debug informations) of a clustering
     */
    public double computeSensitivityDebug() {
        int n = contingencyMatrix.length;
        int m = contingencyMatrix[0].length;
        double max, sensitivity = 0, counter = 0;
        int j_m = 0;
        for (int i = 0; i < n; i++) {
            max = 0;
            for (int j = 0; j < m; j++) {
                if (contingencyMatrix[i][j] > max) {
                    max = contingencyMatrix[i][j];
                    j_m = j;
                }
            }

            // count only complexes that were contained in initial data
            if (max > 0) {

                counter += complex2protMap.get(new Integer(i)).size();
                sensitivity += max;
                if (complex2protMap.get(new Integer(i)).size() != max) {
                    System.out.println("Complex: "
                            + complex2protMap.get(new Integer(i)).size() + "	"
                            + complex2protMap.get(new Integer(i)));
                    System.out.println("Cluster: " + max + "	"
                            + cluster2protMap.get(new Integer(j_m)));
                }

            }
        }
        return sensitivity / counter;
    }

    /**
     * Compute PPV of a clustering
     */
    public double computePPV() {
        int n = contingencyMatrix.length;
        int m = contingencyMatrix[0].length;
        double max, sum = 0, ppv = 0, counter = 0, finalSum = 0;
        for (int j = 0; j < m; j++) {
            max = 0;
            for (int i = 0; i < n; i++) {
                if (contingencyMatrix[i][j] > max) {
                    max = contingencyMatrix[i][j];
                }
                sum = sum + contingencyMatrix[i][j];
            }
            ppv = ppv + max;
        }
        return ppv / sum;
    }

    /**
     * Compute PPV (and print debug informations) of a clustering
     */
    public double computePPVDebug() {
        int n = contingencyMatrix.length;
        int m = contingencyMatrix[0].length;
        double max, sum = 0, ppv = 0, counter = 0, finalSum = 0;
        double old_sum = 0;
        for (int j = 0; j < m; j++) {
            max = 0;
            for (int i = 0; i < n; i++) {
                if (contingencyMatrix[i][j] > max) {
                    max = contingencyMatrix[i][j];
                }
                sum = sum + contingencyMatrix[i][j];
                /*
                 * if (contingencyMatrix[i][j] > 0) {
                 * System.out.println(contingencyMatrix[i][j]);
                 * System.out.println(complex2protMap.get(new Integer(i)));
                 * System.out.println(cluster2protMap.get(new Integer(j))); }
                 */
            }
            // System.out.println(max + " - " + (sum - old_sum));
            old_sum = sum;
            ppv = ppv + max;
        }
        return ppv / sum;
    }

    /**
     * Accuracy is the geometric mean of PPV and sensitivity
     */
    public double computeAccuracy() {
        return java.lang.Math.sqrt(computePPV() * computeSensitivity());
    }

    /**
     * Computes the intersection of two TreeSet<String>
     */
    public static int intersection(TreeSet<String> a, TreeSet<String> b) {
        int counter = 0;
        Iterator<String> iter = a.iterator();
        while (iter.hasNext()) {
            if (b.contains(iter.next())) {
                counter++;
            }
        }
        return counter;
    }

    public void initComplexIds(String file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int counter = 0;
            String s = reader.readLine();
            String split[];
            complex2protMap = new HashMap<Integer, TreeSet<String>>();
            Integer key;
            int protCounter = 0;
            while (s != null) {
                split = s.split("\t");

                if (complexList.contains(split[0])) {
                    if (!complexIds.containsKey(split[1])) {
                        complexIds.put(split[1], new Integer(counter));
                        counter++;
                    }
                    key = complexIds.get(split[1]);
                    if (!complex2protMap.containsKey(key)) {
                        complex2protMap.put(key, new TreeSet<String>());
                        complex2protMap.get(key).add(split[0]);

                    } else {
                        complex2protMap.get(key).add(split[0]);
                    }
                } else {
                    protCounter++;

                    // System.out.println(split[0]);

                }
                s = reader.readLine();
            }
            // checks whether the protein is also in the list to cluster
            // if(protCounter>0)
            System.err.println("Could not find " + protCounter + " proteins");

            Iterator<Integer> iter = complex2protMap.keySet().iterator();
            TreeSet<String> value;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Transforms strings of the form [X(, Y)*] to TreeSet
     */
    public TreeSet<String> string2treeSet(String s) {
        // System.out.println("string2treeSet("+s+")");
        TreeSet<String> result = new TreeSet<String>();
        // no need for the substring on cw output
        s = s.substring(1, s.length() - 1);
        s = s.trim();
        String split[] = s.split(", ");
        for (int i = 0; i < split.length; i++) {
            result.add(split[i].trim());
        }
        return result;
    }

    /**
     * Read results of BorderFlow and write them in cluster2protMap
     */
    public void initClusterIds(String file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s = reader.readLine();
            // skip first line
            s = reader.readLine();
            String split[];
            cluster2protMap = new HashMap<Integer, TreeSet<String>>();
            TreeSet<String> members;
            complexList = new TreeSet<String>();
            Iterator<String> iter;
            while (s != null) {
                split = s.split("\t");
                // System.out.println(split[0] + "\t" + split[2]);
                members = string2treeSet(split[1]);
                iter = members.iterator();
                while (iter.hasNext()) {
                    complexList.add(iter.next());
                }
                cluster2protMap.put(
                        new Integer(Integer.parseInt(split[0])), members);

                s = reader.readLine();
            }
             //System.out.println(complexList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, TreeSet<String>> readComplexes(String file) {
        try {
            System.out.println("Reading " + file);
            // init map
            HashMap<String, TreeSet<String>> map = new HashMap<String, TreeSet<String>>();

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s = reader.readLine();
            String split[];

            while (s != null) {
                split = s.split("\t");
                // if complex in list simply add protein to it. Else add
                // complex, then add protein
                if (!map.containsKey(split[1])) {
                    map.put(split[1], new TreeSet<String>());
                }
                map.get(split[1]).add(split[0]);
                s = reader.readLine();
            }
            reader.close();
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Computes complex-wise sensitivity of a clustering
     */
    public static double complexWiseSensitivity(
            HashMap<String, TreeSet<String>> complexes,
            ArrayList<TreeSet<String>> clusters) {
        // maps each complex to its sensitivity
        HashMap<String, Double> results = new HashMap<String, Double>();
        TreeSet<String> complex;
        TreeSet<String> cluster;
        String id;
        String prot;
        Iterator<String> iter = complexes.keySet().iterator();
        Iterator<String> iter2;
        double counter;
        double max = 0;
        while (iter.hasNext()) {
            id = iter.next();
            complex = complexes.get(id);
            max = 0;
            for (int i = 0; i < clusters.size(); i++) {
                counter = 0;
                cluster = clusters.get(i);
                iter2 = complex.iterator();
                while (iter2.hasNext()) {
                    prot = iter2.next();
                    if (cluster.contains(prot)) {
                        counter++;
                    }
                }
                counter = counter / (complex.size());
                if (max < counter) {
                    max = counter;
                }
            }
            results.put(id, new Double(max));
        }
        double sensitivity = 0;
        iter = results.keySet().iterator();
        counter = 0;
        while (iter.hasNext()) {
            id = iter.next();
            if (results.get(id).doubleValue() > 0) {
                sensitivity = sensitivity + (complexes.get(id).size())
                        * results.get(id).doubleValue();
                counter++;
            }
        }
        return sensitivity / counter;
    }

    // This method implements the matrices given as exmaples in
    // Evaluation of clustering algorithms for protein-protein interaction
    // networks
    // and gives a flavor of how the whole program works and the kind of values
    // to expect
    public static void runtest() {
        double matrix[][] = new double[4][5];
        matrix[0][0] = 7;
        matrix[1][0] = 0;
        matrix[2][0] = 0;
        matrix[3][0] = 0;

        matrix[0][1] = 0;
        matrix[1][1] = 6;
        matrix[2][1] = 0;
        matrix[3][1] = 0;

        matrix[0][2] = 0;
        matrix[1][2] = 8;
        matrix[2][2] = 0;
        matrix[3][2] = 0;

        matrix[0][3] = 0;
        matrix[1][3] = 0;
        matrix[2][3] = 14;
        matrix[3][3] = 4;

        matrix[0][4] = 0;
        matrix[1][4] = 0;
        matrix[2][4] = 3;
        matrix[3][4] = 5;

        HashMap<Integer, TreeSet<String>> complex2protMap = new HashMap<Integer, TreeSet<String>>();

        TreeSet<String> complex = new TreeSet<String>();
        for (int i = 0; i < 7; i++) {
            complex.add("A" + i);
        }
        complex2protMap.put(new Integer(0), complex);

        complex = new TreeSet<String>();
        for (int i = 0; i < 14; i++) {
            complex.add("A" + i);
        }
        complex2protMap.put(new Integer(1), complex);

        complex = new TreeSet<String>();
        for (int i = 0; i < 20; i++) {
            complex.add("A" + i);
        }
        complex2protMap.put(new Integer(2), complex);

        complex = new TreeSet<String>();
        for (int i = 0; i < 8; i++) {
            complex.add("A" + i);
        }
        complex2protMap.put(new Integer(3), complex);

        HashMap<String, Integer> complexIds = new HashMap<String, Integer>();
        for (int i = 0; i < 4; i++) {
            complexIds.put("C" + i, new Integer(i));
        }

        DataReader dr = new DataReader(matrix, complexIds);
        dr.complex2protMap = complex2protMap;
        System.out.println("PPV = " + dr.computePPV());
        System.out.println("S = " + dr.computeSensitivity());
        System.out.println("A = " + dr.computeAccuracy());
        double sep[] = dr.computeSeparations();
        System.out.println("CoSep = " + sep[0] + ", ClSep = " + sep[1]
                + ", sep = " + sep[2]);

    }

    public static void main(String args[]) {
        //runtest();
        DataReader dr = new DataReader("D:/Work/Papers/Eigene/2009/Bioinformatics/Reference data/mips_complexes.tab",
                "D:/Work/Papers/Eigene/2010/MLSB/HH/uetz_2000_int.tab");
        System.out.println("PPV = " + dr.computePPV()*100);
        System.out.println("S = " + dr.computeSensitivity()*100);
        System.out.println("A = " + dr.computeAccuracy()*100);
        double sep[] = dr.computeSeparations();
        System.out.println("CoSep = " + sep[0]*100 + ", ClSep = " + sep[1]*100
                + ", sep = " + sep[2]*100);
        
        dr = new DataReader("D:/Work/Papers/Eigene/2009/Bioinformatics/Reference data/mips_complexes.tab",
        "D:/Work/Papers/Eigene/2010/MLSB/HH/gavin_2002_int.tab");
System.out.println("PPV = " + dr.computePPV()*100);
System.out.println("S = " + dr.computeSensitivity()*100);
System.out.println("A = " + dr.computeAccuracy()*100);
sep = dr.computeSeparations();
System.out.println("CoSep = " + sep[0]*100 + ", ClSep = " + sep[1]*100
        + ", sep = " + sep[2]*100);

dr = new DataReader("D:/Work/Papers/Eigene/2009/Bioinformatics/Reference data/mips_complexes.tab",
"D:/Work/Papers/Eigene/2010/MLSB/HH/gavin_2006_int.tab");
System.out.println("PPV = " + dr.computePPV()*100);
System.out.println("S = " + dr.computeSensitivity()*100);
System.out.println("A = " + dr.computeAccuracy()*100);
sep= dr.computeSeparations();
System.out.println("CoSep = " + sep[0]*100 + ", ClSep = " + sep[1]*100
+ ", sep = " + sep[2]*100);

 dr = new DataReader("D:/Work/Papers/Eigene/2009/Bioinformatics/Reference data/mips_complexes.tab",
"D:/Work/Papers/Eigene/2010/MLSB/HH/ho_2002_int.tab");
System.out.println("PPV = " + dr.computePPV()*100);
System.out.println("S = " + dr.computeSensitivity()*100);
System.out.println("A = " + dr.computeAccuracy()*100);
sep = dr.computeSeparations();
System.out.println("CoSep = " + sep[0]*100 + ", ClSep = " + sep[1]*100
+ ", sep = " + sep[2]*100);

 dr = new DataReader("D:/Work/Papers/Eigene/2009/Bioinformatics/Reference data/mips_complexes.tab",
"D:/Work/Papers/Eigene/2010/MLSB/HH/ito_2001_int.tab");
System.out.println("PPV = " + dr.computePPV()*100);
System.out.println("S = " + dr.computeSensitivity()*100);
System.out.println("A = " + dr.computeAccuracy()*100);
 sep = dr.computeSeparations();
System.out.println("CoSep = " + sep[0]*100 + ", ClSep = " + sep[1]*100
+ ", sep = " + sep[2]*100);

 dr = new DataReader("D:/Work/Papers/Eigene/2009/Bioinformatics/Reference data/mips_complexes.tab",
"D:/Work/Papers/Eigene/2010/MLSB/HH/krogan_2006_int.tab");
System.out.println("PPV = " + dr.computePPV()*100);
System.out.println("S = " + dr.computeSensitivity()*100);
System.out.println("A = " + dr.computeAccuracy()*100);
sep = dr.computeSeparations();
System.out.println("CoSep = " + sep[0]*100 + ", ClSep = " + sep[1]*100
+ ", sep = " + sep[2]*100);
    }
}
