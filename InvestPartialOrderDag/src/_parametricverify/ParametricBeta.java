package _parametricverify;

import core.Description;
import core.Poset;
import core.PosetMorphism;
import _networkdescription.Entity;
//import _networkrequirements.HourlyEnergyRequirement;
import _parametricmodelprops.PrismParametricProperty;
public class ParametricBeta  extends PosetMorphism<Description,PrismParametricProperty>{


	public ParametricBeta(Poset<Description> system)
	{
		super(system);		
		this.computeMap();
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
		this.map.put(d, e.generateProperty());
		this.inferRangeOrdering();
	}

	@Override
	protected void computeMap() 
	{
		for(Description d : this.domain)
		{
			Entity e = (Entity) d;			
			PrismParametricProperty prop = e.generateProperty();
			this.map.put(d, prop);				
		}		
	}

	@Override
	public void update(Description x) 
	{

	}

}
