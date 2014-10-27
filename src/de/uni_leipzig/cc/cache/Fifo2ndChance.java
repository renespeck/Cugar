package de.uni_leipzig.cc.cache;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
/**
* @author rspeck
*/
public class Fifo2ndChance extends AbstractCache{

	protected final Queue<Fifo2ndChanceElement> m_access = new LinkedList<Fifo2ndChanceElement>();

	public Fifo2ndChance(int size, int evictCount) {
		super(size,evictCount);
	}

	@Override
	protected void evict() {		
		
		int i = 0;
		while(i < m_evictCount){
			Fifo2ndChanceElement fifo = m_access.poll();
			if(fifo.m_state == Fifo2ndChanceElement.State.HIT){
				fifo.m_state = Fifo2ndChanceElement.State.SECOND;
				m_access.add(fifo);
			}else{				
				i++;
				m_cacheMap.remove(fifo.m_o);
			}		
		}	
	}
	
	@Override
	protected void hitAccess(Object key) {
		Iterator<Fifo2ndChanceElement> iter = m_access.iterator();
		boolean found = false;
		while(iter.hasNext()){
			Fifo2ndChanceElement fifo2ndChanceElement = iter.next();
			if(fifo2ndChanceElement.equals(new Fifo2ndChanceElement(key))){
				found = true;
				if(fifo2ndChanceElement.m_state == Fifo2ndChanceElement.State.NONE)
					fifo2ndChanceElement.m_state = Fifo2ndChanceElement.State.HIT;
				break;
			}
		}	
		// debug
		if(log4j.isDebugEnabled() && !found) log4j.debug("hit Access cant find key. realy a hit?");

	}
	
	@Override
	protected void putAccess(Object key) {
		// debug
		if(log4j.isDebugEnabled()){
			boolean found = m_access.contains(new Fifo2ndChanceElement(key));
			if(found){
				m_access.remove(found);
				log4j.debug( "putAccess find key. key in cache!");
			}
		}
		// debug end
		m_access.add(new Fifo2ndChanceElement(key));
	}
	
	@Override
	public List<Object> removeValues(Object value){
		
		List<Object> removed = super.removeValues(value);
		for(Object o : removed)
			m_access.remove(new Fifo2ndChanceElement(o));
		
		return removed;		
	}
	
	@Override
	public String toString(){		
		return super.toString() + "\n" + m_access.toString();		
	}

}
/**
* @author rspeck
*/
class Fifo2ndChanceElement extends Object{

	public enum State{ NONE, HIT, SECOND }
	public State m_state = State.NONE;

	public Object m_o = null;

	public Fifo2ndChanceElement(Object o){
		m_o = o;
	}
	@Override
	public boolean equals(Object e){	
		if(e instanceof Fifo2ndChanceElement)
			return m_o.equals(((Fifo2ndChanceElement)e).m_o);
		else return false;
	}

	@Override
	public int hashCode(){
		return 	m_o.hashCode();	
	}

	@Override
	public String toString(){
		return m_o.toString() + ", " + m_state.toString() ;
	}
}