package de.uni_leipzig.cugar.harden;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;
/**
 * Is a {@link de.uni_leipzig.cugar.harden.Harden} class.
 */
public class HardenSuperset extends Harden {

	@Override
	protected Map<TreeSet<Integer>, TreeSet<Integer>> harden(Map<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap) {
		int size = m_clusterGraph.size();
		if (VERBOSE)
			System.out.println("Hardening started with "
					+ clusterSeedMap.size() + " Clusters!");
		// Part1, remove Supersets;
		clusterSeedMap = removeSupersets(clusterSeedMap, size);
		if (VERBOSE)
			System.out.println("Part1, remove Supersets "
					+ clusterSeedMap.size() + " Clusters!");
		// Part1.b, cut over similar clusters
		/*if (VERBOSE)
			System.out.println("Part1.b, cut over similar clusters "
					+ clusterSeedMap.size() + " Clusters!");
		clusterSeedMap = cutSimilarSets(clusterSeedMap, size);*/
		// Part2, Only add Clusters with many Seeds and till all Nodes are
		// added at least once!
		if (VERBOSE)
			System.out.println("Part2, Only add till added once "
					+ clusterSeedMap.size() + " Clusters!");
		clusterSeedMap = reduceSet(clusterSeedMap, size);
		// Part 3, find (relative) best cluster for each node
		if (VERBOSE)
			System.out
			.println("Part 3, find (relative) best cluster for each node "
					+ clusterSeedMap.size() + " Clusters!");
		clusterSeedMap = rematchCluster(clusterSeedMap, size);
		// remove Empty Set
		clusterSeedMap.remove(new TreeSet<Integer>());
		// remove Duplicate Sets
		clusterSeedMap = new HashMap<TreeSet<Integer>, TreeSet<Integer>>(
				clusterSeedMap);
		if (VERBOSE)
			System.out.println("Hardening ended with " + clusterSeedMap.size()
					+ " Clusters!");
		
		return clusterSeedMap;
	}

	private Map<TreeSet<Integer>, TreeSet<Integer>> removeSupersets(
			Map<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap, int size) {

		HashMap<TreeSet<Integer>, TreeSet<Integer>> tmpClusterSeedMap = new HashMap<TreeSet<Integer>, TreeSet<Integer>>();

		clusterSeedMap.remove(new TreeSet<Integer>());
		clusterSeedMap = new HashMap<TreeSet<Integer>, TreeSet<Integer>>(
				clusterSeedMap);
		for (Entry<TreeSet<Integer>, TreeSet<Integer>> ent : clusterSeedMap
				.entrySet()) {
			boolean remove = false;
			for (Entry<TreeSet<Integer>, TreeSet<Integer>> ent2 : clusterSeedMap
					.entrySet()) {
				if (ent.getKey().containsAll(ent2.getKey())
						&& !ent2.getKey().containsAll(ent.getKey()))
					remove = true;
			}
			if (!remove) {
				// System.out.println("put: " +
				// ent.getKey()+"\t"+ent.getValue());
				mergeAddMap(tmpClusterSeedMap, ent.getKey(), ent.getValue());
			}

		}
		clusterSeedMap = tmpClusterSeedMap;
		return clusterSeedMap;
	}

	private HashMap<TreeSet<Integer>, TreeSet<Integer>> cutSimilarSets(
			HashMap<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap, int size) {
		HashMap<TreeSet<Integer>, TreeSet<Integer>> tmpClusterSeedMap = new HashMap<TreeSet<Integer>, TreeSet<Integer>>();
		boolean changes = true;
		while (changes) {
			tmpClusterSeedMap = new HashMap<TreeSet<Integer>, TreeSet<Integer>>();
			changes = false;
			// getting ConcurrentModificationException (sometimes)
			for (Entry<TreeSet<Integer>, TreeSet<Integer>> ent : clusterSeedMap
					.entrySet()) {
				double compVal = 0;
				Entry<TreeSet<Integer>, TreeSet<Integer>> compEnt = null;
				for (Entry<TreeSet<Integer>, TreeSet<Integer>> ent2 : clusterSeedMap
						.entrySet()) {
					if (ent == ent2)
						continue;
					double val = compareSet(ent.getKey(), ent2.getKey());
					if (val > compVal) {
						compVal = val;
						compEnt = ent2;
					}
				}
				if (compVal > 0.9) {
					if (VERBOSE) {
						System.out.println(ent.getKey());
						System.out.println("cut");
						System.out.println(compEnt.getKey());
						System.out.println("=");
					}
					TreeSet<Integer> key = new TreeSet<Integer>();
					TreeSet<Integer> value = new TreeSet<Integer>();
					value.addAll(compEnt.getValue());
					value.addAll(ent.getValue());
					for (Integer i : ent.getKey())
						if (compEnt.getKey().contains(i))
							key.add(i);
					if (VERBOSE)
						System.out.println(key);
					mergeAddMap(tmpClusterSeedMap, key, value);
					// ent.getKey().addAll(compEnt.getKey());
					changes = true;
				} else {
					mergeAddMap(tmpClusterSeedMap, ent.getKey(), ent.getValue());
				}
			}
			clusterSeedMap = tmpClusterSeedMap;
		}
		return clusterSeedMap;
	}

	private Map<TreeSet<Integer>, TreeSet<Integer>> reduceSet(
			Map<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap, int size) {
			Map<TreeSet<Integer>, TreeSet<Integer>> tmpClusterSeedMap = new HashMap<TreeSet<Integer>, TreeSet<Integer>>();
		HashSet<Integer> countSet = new HashSet<Integer>();
		for (Entry<TreeSet<Integer>, TreeSet<Integer>> ent : clusterSeedMap
				.entrySet()) {
			countSet.add(ent.getValue().size());
		}
		ArrayList<Integer> sortList = new ArrayList<Integer>();
		for (Integer i : countSet) {
			sortList.add(i);
		}
		Collections.sort(sortList);
		HashSet<Integer> nodeSet = new HashSet<Integer>();
		tmpClusterSeedMap = new HashMap<TreeSet<Integer>, TreeSet<Integer>>();
		for (int i = sortList.size() - 1; i >= 0 && size > nodeSet.size(); i--) {
			int cSize = sortList.get(i);
			for (Entry<TreeSet<Integer>, TreeSet<Integer>> ent : clusterSeedMap
					.entrySet()) {
				if (ent.getValue().size() == cSize) {
					nodeSet.addAll(ent.getKey());
					mergeAddMap(tmpClusterSeedMap, ent.getKey(), ent.getValue());
				}
			}
		}

		clusterSeedMap = tmpClusterSeedMap;

		return clusterSeedMap;
	}

	private Map<TreeSet<Integer>, TreeSet<Integer>> assureMembership(
			Map<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap, int size) {

		boolean tryagain = true;
		while (tryagain) {
			tryagain = false;
			for (int i = 0; i < size; i++) {
				boolean found = false;
				for (Entry<TreeSet<Integer>, TreeSet<Integer>> ent : clusterSeedMap
						.entrySet()) {
					if (ent.getKey().contains(i))
						found = true;
				}
				if (!found) {
					double max = 0;
					Entry<TreeSet<Integer>, TreeSet<Integer>> maxEnt = null;
					for (Entry<TreeSet<Integer>, TreeSet<Integer>> ent : clusterSeedMap
							.entrySet()) {
						double val = 0;
						// if (ent.getKey().contains(i))
						val = getFlowFromNodeToSet(ent.getKey(), i);
						// check for relative flow
						if (val / ent.getKey().size() > max) {
							if (maxEnt != null) {
								maxEnt.getKey().remove(i);
							}
							maxEnt = ent;
							max = val / ent.getKey().size();
						} else {
							ent.getKey().remove(i);
						}
					}
					if (max > 0)
						maxEnt.getKey().add(i);
					else
						tryagain = true;
				}
			}
		}
		return clusterSeedMap;
	}

	private Map<TreeSet<Integer>, TreeSet<Integer>> rematchCluster(
			Map<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap, int size) {
		clusterSeedMap = assureMembership(clusterSeedMap, size);
		boolean changes = true;
		int counter = 0;
		while (changes && counter < 5) {
			if (VERBOSE)
				System.out.println(counter);
			changes = false;
			counter++;
			for (int i = 0; i < size; i++) {
				double max = -1;
				Entry<TreeSet<Integer>, TreeSet<Integer>> maxEnt = null;
				for (Entry<TreeSet<Integer>, TreeSet<Integer>> ent : clusterSeedMap
						.entrySet()) {
					double val = 0;
					if (ent.getKey().contains(i)) {
						val = getFlowFromNodeToSet(ent.getKey(), i);
						// check for relative flow
						if (val / ent.getKey().size() > max) {
							if (maxEnt != null) {
								if (maxEnt.getKey().contains(i))
									changes = true;
								maxEnt.getKey().remove(i);
							}
							maxEnt = ent;
							max = val / ent.getKey().size();
						} else {
							ent.getKey().remove(i);
						}

					}
				}
				maxEnt.getKey().add(i); 
			}
		}
		if (VERBOSE)
			System.out.println("took " + counter + " whiles");
		return clusterSeedMap;
	}
}