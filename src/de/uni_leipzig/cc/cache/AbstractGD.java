package de.uni_leipzig.cc.cache;

import java.util.LinkedHashMap;
import java.util.Map;
/**
* @author rspeck
*/
public abstract class AbstractGD extends AbstractCache {
	
	protected Double m_cacheAge = 0D;
	protected Map<Object, GDElement> m_accessMap = new LinkedHashMap<Object,GDElement>();
			
	public AbstractGD(int size, int evictCount) {
		super(size, evictCount);
	}
	/**
	 * Inner class for an element to compare on special value
	 */
	protected class GDElement implements Comparable<GDElement> {
		
		protected Double m_credit = 0D;
		
		/** compare credit */		
		@Override
		public int compareTo(GDElement element) {
			return m_credit.compareTo(element.getCredit());
		}
		protected Double getCredit(){
			return m_credit;
		}		
		/** credit = age + special */
		protected void update(Double special){
			m_credit = m_cacheAge + special;
		}	
	}
}