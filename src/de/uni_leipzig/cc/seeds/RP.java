package de.uni_leipzig.cc.seeds;

import java.util.LinkedHashSet;
import java.util.Set;

public class RP extends SeedOrder{

	public RP(){
		m_nextSeeds = new LinkedHashSet<Integer>();		
	}

	@Override
	public boolean update(Set<Integer> cluster) {	

		boolean update = false;
		for(Integer node : cluster){		

			if(m_blackList.contains(node) || m_nextSeeds.contains(node))
				continue;
					
			if(m_seeds.contains(node)){
				update = true;	
				m_nextSeeds.add(node);
				m_seeds.remove(node);				
			}			
		}
		// debug info
		if(log4j.isDebugEnabled())			 
			if(update)
				log4j.debug("state changed" + cluster.toString());
			else
				log4j.debug("nothing changed" + cluster.toString());

		return update;
	}
}