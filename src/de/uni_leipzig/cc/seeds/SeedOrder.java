package de.uni_leipzig.cc.seeds;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
/**
* @author rspeck
*/
public abstract class SeedOrder implements SeedOrderInterface {
	/** logger */
	public static Logger log4j = Logger.getLogger(SeedOrder.class);
	/** 
	 * Seeds that should be used.
	 */
	protected Set<Integer> m_seeds = new LinkedHashSet<Integer>();
	/**
	 * Used seeds.
	 */
	protected Set<Integer> m_blackList = new TreeSet<Integer>();
	/**
	 * Best next seeds.
	 */
	protected Set<Integer> m_nextSeeds = null;

	/** 
	 * Constructor initializes the data structure for  #m_nextSeeds 
	 */
	public SeedOrder(){
		log4j.info("starting " + getClass().getSimpleName() + "...");		
	}
	@Override
	public boolean addAll(Set<Integer> seeds){

		if(seeds == null) 
			throw new NullPointerException("parameter is null.");		
		if(seeds.isEmpty()) 
			log4j.warn("parameter is empty.");

		Set<Integer> tmpSeeds = new LinkedHashSet<Integer>(seeds);			
		tmpSeeds.removeAll(m_blackList);

		if(m_nextSeeds != null)
			tmpSeeds.removeAll(m_nextSeeds);		

		return m_seeds.addAll(tmpSeeds);
	}
	@Override
	public boolean add(int seed){	
		if(!m_seeds.contains(seed) && !m_blackList.contains(seed))
			if(m_nextSeeds != null){
				if(!m_nextSeeds.contains(seed))
					return m_seeds.add(seed);				

			}else return m_seeds.add(seed);			
		return false;
	}

	@Override
	public Integer getBestSeed() {
		Integer removed = null;
		if(m_nextSeeds != null && !m_nextSeeds.isEmpty()){
			removed = m_nextSeeds.iterator().next();
			m_nextSeeds.remove(removed);
			m_blackList.add(removed);
		}
		else if(!m_seeds.isEmpty()){
			removed = m_seeds.iterator().next();
			m_seeds.remove(removed);
			m_blackList.add(removed);							
		}
		return removed;
	}
	// just for testing
	@Override
	public Set<Integer> getUnusedSeeds(Set<Integer> nodes) {
		
		if(!nodes.isEmpty()){
			Set<Integer> unused = new LinkedHashSet<Integer>(nodes);
			unused.removeAll(m_blackList);

			Set<Integer> future = new LinkedHashSet<Integer>();		
			if(m_nextSeeds != null)
				future.addAll(m_nextSeeds);
			future.addAll(m_seeds);

			unused.retainAll(future);
			return unused;
			
		} else{
			log4j.warn("parameter is empty.");
			return new LinkedHashSet<Integer>();
		}	
	}	
	@Override
	public boolean hasUnusedSeeds(Set<Integer> nodes){
		
		if(!nodes.isEmpty()){
			for(Integer i : nodes)
				if(m_seeds.contains(i) || (m_nextSeeds != null && m_nextSeeds.contains(i)))
					return true;				
		}else
			log4j.warn("parameter is empty.");
		//System.out.println("false");
		return false;
	}
	@Override
	public boolean update(Set<Integer> cluster) {
		return false;
	}
	@Override
	public String toString(){
		String rtn = ""+
		"blackList: " + m_blackList.toString() + 
		"\nseeds: " + m_seeds.toString();

		if(m_nextSeeds != null)
			rtn += "\nnextSeeds: " + m_nextSeeds.toString();

		return rtn;
	}	
	@Override
	public void clear(){	
		m_seeds.clear(); 	
		m_blackList.clear();
		if(m_nextSeeds != null)
			m_nextSeeds.clear();			
	}
}