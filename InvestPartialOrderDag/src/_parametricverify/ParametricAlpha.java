package _parametricverify;

import _networkdescription.Entity;
import _parametricmodelprops.PrismParametricModel;
import core.Description;
import core.Poset;
import core.PosetMorphism;

public class ParametricAlpha extends PosetMorphism<Description,PrismParametricModel>
{


	//timing is important as it determine the 
	//pattern that each entity is using based on their load profile.


	public ParametricAlpha(Poset<Description> system)
	{
		super(system);		
		this.computeMap();
		this.inferRangeOrdering();
	}
	//---------------------------------------------------

	/**
	 * 
	 * Associate each x in this.domain with an appropriate Prism Parametric Model.
	 * 
	 * */
	@Override
	public void computeMap() 
	{
		//for each entity, create the pattern according to
		//current configuration of the 
		for(Description d : this.domain)
		{
			//get the entity
			Entity e = (Entity) d;					
			this.map.put(d, e.generateModel());
		}
		this.inferRangeOrdering();
	}
	//---------------------------------------------------------------------------------------------------
	public void computeMap(Description[] ds)
	{
		
		for(Description d : ds)
		{
			this.computeMap(d);
		}
	}
	//---------------------------------------------------------------------------------------------------
	public void computeMap(Description d)
	{
		Entity e = (Entity) d;					
		this.map.put(d, e.generateModel());
		this.inferRangeOrdering();
	}
	//---------------------------------------------------------------------------------------------------
	@Override
	public void update(Description x) 
	{
		// TODO Auto-generated method stub	
	}
}
