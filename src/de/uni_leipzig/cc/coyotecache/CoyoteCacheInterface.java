package de.uni_leipzig.cc.coyotecache;

import java.util.Set;
/**
* @author rspeck
*/
interface CoyoteCacheInterface {
	/**
	 * Puts a final cluster to the cache. 
	 * 
	 * @param finalCluster
	 * @return the finalCluster
	 */
	public Set<Integer> put(Set<Integer> finalCluster);
	/**
	 * Gets a final cluster, if the cache knows the given cluster 
	 * that was calculated before.
	 * 
	 * @param cluster 
	 * @return the finalCluster
	 */
	public Set<Integer> get(Set<Integer> cluster);
	/**
	 * Gets a final cluster, if the cache knows the given cluster 
	 * that was calculated before. 
	 * 
	 * @param cluster
	 * @param cost the cost of the cluster
	 * @return the finalCluster
	 */
	public Set<Integer> get(Set<Integer> cluster, Double cost);
	/**
	 * Gets the next seed, that should be use for clustering.
	 * 
	 * @return the next seed to use.
	 */
	public Integer getBestSeed();
	/**
	 * Initializes all seeds that should be use during the process.
	 * 
	 * @param all seeds that should be used
	 */
	public void setSeeds(Set<Integer> seeds);	
}