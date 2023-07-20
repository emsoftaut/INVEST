package core;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;


public abstract class PosetMorphism<X extends Element,Y extends Element> 
{
	public Poset<X> domain;
	public Poset<Y> range;
	public HashMap<X,Y> map;

	//------------------------------------------------------------------
	abstract protected void computeMap();
	abstract public void update(X x);
	//------------------------------------------------------------------
	public String toString()
	{
		return this.map.toString();
	}
	//------------------------------------------------------------------
	public void remove(X x)
	{
		Y y = this.map.get(x);
		this.domain.remove(x);
		this.range.remove(y);
		this.map.remove(x);	
	}
	//------------------------------------------------------------------
	public void remove(Poset<X> removes)
	{
		for(X x : removes)
		{
			remove(x);	
		}
	}
	//------------------------------------------------------------------
	/**
	 * Merge this with that, overwriting any matching entries.
	 * 
	 * */
	public void merge(PosetMorphism<X,Y> that)
	{



		for(X x : that.domain)
		{
			this.domain.add(x);
		}
		
		for(Y y : that.range)
		{
			this.range.add(y);
		}

		for(Entry<X,Y> entry : that.map.entrySet())
		{

			this.map.put(entry.getKey(), entry.getValue());	
		}
	}	
	//------------------------------------------------------------------
	/**
	 * 
	 * Given a g take the domain diff with this and return as a new morphism
	 * 
	 * */
	protected PosetMorphism<X,Y> diff(PosetMorphism<X,Y> g)
	{
		return g;
	}
	//------------------------------------------------------------------
	/**
	 * Generates a partial ordering on the models based on the partial
	 * ordering of domain.
	 * e.g. x <= y in domain => m_x <= m_y in range
	 * 
	 * */
	public void inferRangeOrdering()
	{
		this.range = new Poset<Y>();

		//first, add all the models to the poset
		for(Y m_y : this.map.values())
		{
			this.range.add(m_y);
		}

		//then induce the relationships based on the systems
		for(X x : this.domain)
		{
			for(X y : this.domain)
			{
				if(domain.isLessThan(x, y))
				{
					this.range.lessThan(this.map.get(x), this.map.get(y));
				}
			}
		}
	}
	//------------------------------------------------------------------
	public Poset<Y> image(X x)
	{
		Poset<X> subset = new Poset<X>();
		subset.add(x);
		return this.image(subset);
		
	}
	//------------------------------------------------------------------
	/**
	 * Returns the image of map of a specified subset (of a poset) 
	 * */
	public Poset<Y> image(Poset<X> subset)
	{

		HashSet<Y> elements = new HashSet<Y>();

		for(X x : subset) 
		{

			Y r = this.map.get(x);

			if(r!=null)
			{
				elements.add(r);
			}
		}

		return this.range.subset(elements);
	}
	//----------------------------------------------------------------------------------------
	/**
	 * Returns the image of x in map.
	 * 
	 * */
	public Y get(X x)
	{
		return this.map.get(x);
	}
	//----------------------------------------------------------------------------------------
	public PosetMorphism()
	{
		this(new Poset<X>());		
	}
	//----------------------------------------------------------------------------------------
	public PosetMorphism(Poset<X> domain)
	{
		this.domain = domain;
		this.range = new Poset<Y>();
		this.map = new HashMap<X,Y>();
	}
}
