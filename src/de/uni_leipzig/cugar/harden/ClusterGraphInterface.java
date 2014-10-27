package de.uni_leipzig.cugar.harden;

import java.util.TreeSet;
// implement this for your graph data to use hardening
public interface ClusterGraphInterface {

	public double getEdgeWeight(Integer source, Integer target);
	public TreeSet<Integer> getNeighbours(int index);
	public int size();
}
