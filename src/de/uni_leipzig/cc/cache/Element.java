package de.uni_leipzig.cc.cache;

import java.util.Set;
import java.util.TreeSet;
/**
* Element to cache, wraps an Object.
* 
* @author rspeck
*/
public class Element extends Object implements Comparable<Element>{
	
	public static Element getRandomElement(){
		return getRandomElement(0);
	}	
	public static Element getRandomElement(double cost){
		Set<Integer> treeSet = new TreeSet<Integer>();		
		for(int i = 0 ; i < 10 ; i++)
			treeSet.add((int)Math.round(Math.random() * 1000));
		return  new Element(treeSet,cost);	
	}
	
	// this object is wrapped by this class
	private Object m_object;
	
	// id for the wrapped object
	private String m_id = "";
	
	// the cost of the wrapped object used by Cost evict policy
	private double m_cost;
	
	public Element(Object object, double cost){		
		m_object = object;
		m_id = String.valueOf(object.hashCode());
		m_cost = cost;
	}
	
	@Override
	public int compareTo(Element element) {
		return element.m_id.compareTo(m_id);		
	}
	
	@Override
	public boolean equals(Object e){		
		return (e instanceof Element) ? m_object.equals(((Element)e).m_object) : false;
	}
	
	public double getCost(){
		return m_cost;
	}
	public Object getObject(){
		return m_object;
	}

	@Override
	public int hashCode(){
		return 	m_object.hashCode();	
	}
	@Override
	public String toString(){
		return "[cost: " + m_cost + "; object: " + m_object + "]";
	}		
}