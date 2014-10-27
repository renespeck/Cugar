package de.uni_leipzig.cc.seeds;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
/**
* @author rspeck
*/
public class FixRandomOrder extends SeedOrder{

	private final int RANDSEED = 872894457;
	private Random rand = new Random(RANDSEED);

	@Override
	public boolean addAll(Set<Integer> seeds){
		
		Set<Integer> tmpSeeds = new LinkedHashSet<Integer>(seeds);			
		tmpSeeds.removeAll(m_blackList);
		tmpSeeds.removeAll(m_seeds);

		boolean ret = false;
		for(Integer seed : tmpSeeds)
			if(add(seed))
				ret = true;	
		return ret;
	}

	@Override
	public boolean add(int seed){

		boolean ret = false;
		if(!m_blackList.contains(seed) && !m_seeds.contains(seed)){			
			if (rand.nextDouble() < 0.5)
				ret = m_seeds.add(seed);
			else{
				Set<Integer> tmpSeeds = new LinkedHashSet<Integer>(seed);	
				tmpSeeds.addAll(m_seeds);
				m_seeds = tmpSeeds;
				ret = true;
			}
		}
		return ret;
	}
}