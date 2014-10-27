package de.uni_leipzig.cc.seeds;

import java.util.Set;
/**
* @author rspeck
*/
public interface SeedOrderInterface {		
	/**
	 * Adds seeds that should be used. Ignores all seeds that was added after last {@link #clear()} call.
	 * 
	 * @param seeds
	 */
	public boolean addAll(Set<Integer> seeds);
	/**
	 * Adds seeds that should be used. Ignores all seeds that was added after last {@link #clear()} call.
	 * 
	 * @param seed
	 */
	public boolean add(int seed);		
	/** 
	 * 
	 * Updates the seed order. Order depends on the parameter.
	 * 
	 * @param a cluster, the size of the cluster is used by some policies
	 * @return false if no update
	 */
	public boolean update(Set<Integer> cluster);
	
	/**
	 * 
	 * Gets true if nodes contains seeds that we use next times.
	 * 
	 * @param nodes
	 * @return
	 */
	// more or less for testing
	public Set<Integer> getUnusedSeeds(Set<Integer> nodes);
	/**
	 * 
	 * Gets true if nodes contains seeds that we use next times.
	 * 
	 * @param nodes
	 * @return
	 */
	public boolean hasUnusedSeeds(Set<Integer> nodes);
	/**
	 *  Gets the next best seed that should be used for clustering.
	 *  
	 * @return the next seed or null
	 */
	public Integer getBestSeed();
	/**
	 * Clears all data.
	 */
	public void clear();
}
