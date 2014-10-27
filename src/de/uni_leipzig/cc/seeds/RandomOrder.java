package de.uni_leipzig.cc.seeds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
/**
* @author rspeck
*/
public class RandomOrder extends SeedOrder {

	@Override
	public boolean addAll(Set<Integer> seeds){		
		boolean ret = super.addAll(seeds);	
		rand();	
		return ret;
	}

	@Override
	public boolean add(int seed){		
		boolean ret = super.add(seed);	
		rand();	
		return ret;
	}
	private void rand(){
		List<Integer> random = new ArrayList<Integer>(m_seeds);
		m_seeds.clear();
		Collections.shuffle(random);
		m_seeds.addAll(random);	
	}
}