package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import _networkdescription.Entity;
/**
 * 
 * 
 * Models a Partially ordered set S as 
 * a Directed Acyclic Graph (dag)
 * represented by a adjacency list.
 * 
 * Given an x in S we associate a set l such that
 *  {y in l |  x <= y}

 * 
 * */
public class Poset<E extends Element> implements Element,Iterable<E>
{
	private HashMap<E,HashSet<E>> dag;
	
	
	//---------------------------------------------------------------
	private void replace(E x,E xp)
	{

		if(this.contains(x))
		{

			this.add(xp);

			for(E a : this)
			{
				if(this.isLessThan(a, x))
				{
					this.lessThan(a, xp);
				}
				if(this.isLessThan(x, a))
				{
					this.lessThan(xp, a);
				}
			}



			this.remove(x);


		}
	}
	//---------------------------------------------------------------
	/**
	 * Returns a new intersection of this and that
	 * 
	 * 
	 * */
	public Poset<E> intersection(Poset<E> that)
	{

		Poset<E> inter = new Poset<E>();

		for(E e : this)
		{
			if(that.contains(e))
			{
				inter.add(e);
			}
		}




		for(E x : inter)
		{
			for(E y : inter)
			{
				if(this.isLessThan(x, y))
				{
					inter.lessThan(x, y);
				}
			}
		}



		return inter;


	}
	//---------------------------------------------------------------
	/**
	 * combine two disjoint posets this and that such that
	 * this = this union that.
	 * */
	public void union(Poset<E> that)
	{
		for(E x : that)
		{
			this.add(x);
		}

		for(E x : that)
		{
			for(E y : that)
			{
				if(that.isLessThan(x, y))
				{
					this.lessThan(x, y);
				}
			}
		}
	}
	//---------------------------------------------------------------
	public E maximal()
	{

		for(E x : this)
		{
			boolean maximal=true;
			for(E y : this)
			{
				if(this.dag.get(y).contains(x))
				{
					maximal = false;
				}
			}

			if(maximal)
			{
				return x;
			}
		}
		return null;
	}
	//---------------------------------------------------------------
	public HashSet<E> maximals()
	{
		HashSet<E> maxs = new HashSet<E>();


		for(E x : this)
		{
			boolean maximal=true;
			for(E y : this)
			{
				if(this.dag.get(y).contains(x))
				{
					maximal = false;
				}
			}

			if(maximal)
			{
				maxs.add(x);
			}
		}

		return maxs;
	}
	//---------------------------------------------------------------
	public HashSet<E> minimals()
	{

		HashSet<E> mins = new HashSet<E>();
		for(E x : this)
		{
			if(this.isMinimal(x))
			{
				mins.add(x);
			}
		}		
		return mins;
	}
	//---------------------------------------------------------------
	public boolean isMinimal(E x)
	{
		if(this.contains(x))
		{
			return (this.dag.get(x).isEmpty());
		}
		return false;
	}
	//---------------------------------------------------------------
	public Poset<E> subset(HashSet<E> elements)
	{	
		return this.inducedOrder(elements);
	}
	//---------------------------------------------------------------
	/**
	 * 
	 * creates a poset from this order with "elements".
	 * 
	 * 
	 * */
	public Poset<E> subPoset(E...elements)
	{
		HashSet<E> ss = new HashSet<E>();
		for(E e : elements)
		{
			ss.add(e);
		}

		return this.inducedOrder(ss);
	}
	//---------------------------------------------------------------
	public boolean isEmpty()
	{
		return this.dag.isEmpty();
	}
	//---------------------------------------------------------------
	//assumes that x is an element of this poset
	private HashSet<E> _downsetSubset(E x)
	{
		HashSet<E> down = new HashSet<E>();

		HashSet<E> aList = this.dag.get(x);
		for(E y : aList)
		{
			down.add(y);
			down.addAll(_downsetSubset(y));
		}

		return down;
	}
	//---------------------------------------------------------------
	/**
	 * Computes and returns the poset <= down set (semi-ideal) D[x] for a given x.
	 * @param x in this poset
	 * @return {y in this | y <= x}.
	 * */
	//---------------------------------------------------------------
	public Poset<E> downSet(E x)
	{

		if(!this.contains(x))
		{
			return null;
		}

		HashSet<E> down = _downsetSubset(x);
		down.add(x);//add x itself..
		return this.inducedOrder(down);
	}
	//---------------------------------------------------------------
	/**
	 * Computes and returns the strict poset < down set (semi-ideal) D[x] for a given x.
	 * @param x in this poset
	 * @return {y in this | y < x}.
	 * */
	public Poset<E> strictDownSet(E x)
	{

		if(!this.contains(x))
		{
			return null;
		}

		HashSet<E> down = _downsetSubset(x);
		//if(!this.strict)
		//	{
		//		down.add(x);//add x itself..
		//	}
		return this.inducedOrder(down);
	}
	//---------------------------------------------------------------
	private HashSet<E> _upsetSubset(E x)//helper method.
	{
		HashSet<E> up = new HashSet<E>();

		for(E k : this.dag.keySet())
		{
			HashSet<E> aList = this.dag.get(k);
			if(aList.contains(x))
			{
				up.add(k);
				up.addAll(_upsetSubset(k));
			}
		}
		return up;
	}
	//---------------------------------------------------------------
	/**
	 * Given a set, implements this partial ordering on the elements.
	 * 
	 * @param input hashset "set" 
	 * 
	 *  @return a poset on set such that 
	 *  x <= y in set whenever x <= y in this. 
	 * 
	 * */
	private Poset<E> inducedOrder(HashSet<E> set)
	{

		Poset<E> induced = new Poset<E>();
		for(E x : set)
		{
			induced.add(x);

			for(E y : set)
			{
				induced.add(y);
				if(this.isLessThan(x, y))
				{
					induced.lessThan(x, y);

				}
			}
		}
		return induced;
	}
	//---------------------------------------------------------------
	/**
	 * Computes and returns the up set U[x] (principle order filter) for a given x
	 * @param x in this poset
	 * @return {y in this | x <= y}
	 * */
	public Poset<E> upset(E x)
	{
		HashSet<E> up = _upsetSubset(x);
		up.add(x);//add x itself...
		return this.inducedOrder(up);
	}
	//---------------------------------------------------------------
	public Poset<E> strictUpset(E x)
	{
		HashSet<E> up = _upsetSubset(x);		
		return this.inducedOrder(up);
	}
	//---------------------------------------------------------------
	/**
	 * Remove element e from the poset by first
	 * removing the e key and then removing e from each
	 * adjacency list.
	 * */
	public void remove(E e)
	{
		//remove key e from Hash Map.
		this.dag.remove(e);

		//remove e from every aList
		for(E k : this.dag.keySet())
		{
			HashSet<E> aList = this.dag.get(k);
			aList.remove(e);
		}
	}
	//---------------------------------------------------------------
	/**
	 * Compares elements x and y in this partial order.
	 * @param x an element in this poset
	 * @param y an element in this poset
	 * @return true if x <= y and false otherwise	 
	 * */
	public boolean isLessThan(E x,E y)
	{
		if(this.dag.containsKey(y))
		{
			boolean result = this.dag.get(y).contains(x);			
			return result;
		}
		return false;
	}
	//---------------------------------------------------------------
	/**
	 * 
	 * adds a directed edge from y to x. e.g. x <= y.
	 * if x and/or y is not already in the poset, the elements are added.
	 * 
	 * */
	public void lessThan(E x,E y)
	{	
		if(!this.contains(x))
		{
			this.add(x);
		}

		if(!this.contains(y))
		{
			this.add(y);
		}

		this.dag.get(y).add(x);//add x to y's aList. e.g. x <= y
	}
	//---------------------------------------------------------------
	public void removeLessThan(E x, E y)
	{	
		if(this.contains(y))
		{
			this.dag.get(y).remove(x);//remove x from y's aList. e.g. goodbye x <= y		
		}	
	}
	//---------------------------------------------------------------
	public void add(E...elements)
	{
		for(E s : elements)
		{
			this.add(s);
		}
	}
	//---------------------------------------------------------------
	private void add(E s)
	{
		if (!this.contains(s))
		{
			this.dag.put(s, new HashSet<E>());			
		}
	}
	//---------------------------------------------------------------
	public boolean contains(E s) 
	{

		/*for(E k : this.dag.keySet())
		{
			if(k == s)
			{
				return true;
			}
		}

		return false;*/
		return this.dag.containsKey(s);
	}
	//---------------------------------------------------------------
	public Poset()
	{
		this.dag = new HashMap<E,HashSet<E>>();
	}
	//---------------------------------------------------------------
	/**
	 * Copy constructor.
	 * @param reverify poset to copy
	 * @return a new poset with the identical elements and ordering
	 * */
	public Poset(Poset<E> tocopy) 
	{
		this();		

		for(E x : tocopy)
		{
			this.add(x);
		}
		for(E x : tocopy)
		{
			for(E y : tocopy)
			{

				if(tocopy.isLessThan(x, y))
				{
					this.lessThan(x, y);

				}
			}
		}		
	}
	//---------------------------------------------------------------
	public String toString()
	{
		return this.dag.toString();
	}
	//---------------------------------------------------------------
	@Override
	public Iterator<E> iterator() 
	{	
		return new PosetIterator();
	}
	//------------------------------------------------------------------------
	private class PosetIterator implements Iterator<E> 
	{
		private HashMap<E,Boolean> visited;
		
		//------------------------------------------------------------------------
		private void remove(E e)
		{
			System.out.println("Removing: "+e);
			//remove key e from Hash Map.
			dag.remove(e);

			//remove e from every aList
			for(E k : dag.keySet())
			{
				HashSet<E> aList = dag.get(k);
				aList.remove(e);
			}
		}
		//------------------------------------------------------------------------
		
		//------------------------------------------------------------------------
		public void remove()
		{
						
			this.remove(this.minimum());
		}
		
		//------------------------------------------------------------------------
		public PosetIterator()
		{
			this.visited = new HashMap<E,Boolean>();
		}		
		//---------------------------------------------------------------
		/**
		 * Returns true if the aList of s is empty or
		 * whenever every element of that aList is "marked" for removal
		 * 
		 * 
		 * */
		private boolean hasEmptyAList(E s)
		{
			if (dag.get(s).isEmpty())
			{
				return true;
			}
			else
			{			
				HashSet<E> aList = dag.get(s);
				for(E e : aList)
				{
					if(!this.visited.containsKey(e))
					{
						return false;
					}
				}
				return true;
			}
		}
		//---------------------------------------------------------------
		@Override
		public boolean hasNext() 
		{
			for(E s : dag.keySet())
			{
				if(!this.visited.containsKey(s))
				{
					return true;
				}
			}
			return false;
		}
		//------------------------------------------------------------------------
		/**
		 * 
		 * Returns the minimum element of this partial order
		 * @return min such that min <= x for all x in this poset
		 * */
		//------------------------------------------------------------------------
		private E minimum()
		{
			if(dag.isEmpty())
			{
				return null;
			}

			for(E s : dag.keySet())
			{
				if (!this.visited.containsKey(s))
				{
					if(hasEmptyAList(s))
					{
						this.visited.put(s, true);						
						return s;
					}
				}
			}
			return null;
		}
		//---------------------------------------------------------------
		@Override
		public E next() 
		{
			return this.minimum();
		}
		//------------------------------------------------------------------------

	}
	//----------------------------------------------------------------------------------------
	/**
	 * Returns a poset of all elements x in X provided that there is no y such that x < y < z
	 * the elements x form a lower cover of element z in this poset
	 * */
	public Poset<E> lowerCover(E z)
	{
		HashSet<E> locov = new HashSet<E>();


		for(E x : this.dag.keySet())
		{
			if(this.isLessThan(x, z))
			{
				locov.add(x);
			}
		}


		return this.inducedOrder(locov);
	}
	//----------------------------------------------------------------------------------------
	
	/**
	 * Returns the upper covers of all elements in X.
	 * 
	 * */
		private Poset<E> upperCovers(Poset<E> X) {
		
			HashSet<E> covers = new HashSet<E>();
			for(E x : X)
			{
				Poset<E> C = this.upperCover(x);
				for(E y : C)
				covers.add(y);
			}
						
			return this.inducedOrder(covers);
			 
		}
		//------------------------------------------------------------------------
	/**
	 * returns a poset of all elements z in X provided there is no y such that x < y < z...
	 * the elements z form an upper cover of element x in this poset.
	 * 
	 * */
	public Poset<E> upperCover(E x) 
	{

		HashSet<E> upcov = new HashSet<E>();

		for(E z : this.dag.keySet())
		{
			if(this.dag.get(z).contains(x))
			{
				//this means x < z and is part of the upper covering of x.
				upcov.add(z);
			}
		}
		return this.inducedOrder(upcov);
	}
	//------------------------------------------------------------------------
	/**
	 * Creates a linear order compatible with the partial order. 
	 * 
	 * */
	public ArrayList<E> toArrayList()
	{
		ArrayList<E> elements = new ArrayList<E>();

		for(E x : this)
		{
			elements.add(x);
		}

		return elements;
	}
	//------------------------------------------------------------------------
	/**
	 * 
	 * returns a new poset with the same elements
	 * as this one except for those elements in "exclude" 
	 * this poset remains unmodified.
	 * 
	 * */
	public Poset<E> remove(Poset<E> exclude) 
	{
		HashSet<E> set = new HashSet<E>();
		for(E x : this)
		{
			if(!exclude.contains(x))
			{
				set.add(x);
			}
		}
		return this.inducedOrder(set);
	}
	//------------------------------------------------------------------------
	public boolean intersectsWith(Poset<E> that) 
	{


		for(E e : this)
		{
			if(that.contains(e))
			{
				return true;
			}
		}
		return false;
	}
	
}












