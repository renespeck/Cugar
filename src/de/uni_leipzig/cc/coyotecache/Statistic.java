package de.uni_leipzig.cc.coyotecache;
/**
* @author rspeck
*/
public class Statistic {
	
	/** hits in cache */
	private int m_hits = 0;
	
	/** faults in cache */
	private int m_fault = 0;
	
	/** all gets (hit + misses) in cache */
	private int m_gets = 0;
	
	/** hits found in dictionary without a search in cache memory */
	private int m_dicHits = 0;
	
	/** the sum if cost of elements by a hit*/
	private Double m_hitCost = 0D;
	
	/** counts elements size < 2 */
	private int m_dismissed = 0;
	
	/** Maximal size of partial log*/
	private int m_maxLogSize = 0;
	
	/** Increase hits and gets*/
	public void incHits(){
		m_hits++;	
		m_gets++;
	}
	
	/** Increase faults and gets */
	public void incFaults(){
		m_fault++;	
		m_gets++;
	}
	
	/** Increase dismissed */
	public void incDismissed(){
		m_dismissed++;
	}
	
	/** Increase hits found in dictionary without a search in cache memory */
	public void incDictionaryHits(){
		m_dicHits++;
	}
	
	/** Increase  hits, gets and adds cost to hitCost */
	public void incHitCost(Double cost){
		m_hitCost+=cost;
		incHits();
	}
	
	/** Sets size to m_maxLogSize if size > m_maxLogSize */
	public void setMaxLogSize(int size){
		if(size > m_maxLogSize) m_maxLogSize = size;
	}
	
	public int getHits(){
		return this.m_hits;
	}
	@Override
	public String toString(){
		String s = "" +		
		
		m_hits + "\t" + 
		m_dicHits + "\t" + 
		m_fault + "\t" + 
		m_gets + "\t" + 
		m_hitCost + "\t" + 
		m_maxLogSize + "\t" + 
		m_dismissed + "\t" + 
		m_hitCost / (m_hits - m_dicHits);
		
		return s;
	}
}
