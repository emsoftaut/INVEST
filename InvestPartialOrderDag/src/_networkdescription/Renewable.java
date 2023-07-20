package _networkdescription;

import _parametricmodelprops.PrismParametricModel;
import _parametricmodelprops.PrismParametricProperty;
import _parametricmodelprops.PropertyType;
import _parametricmodelprops.RenewableModel;


public class Renewable extends Entity
{	
	public Renewable(String label) 
	{
		super(label);		
	}

	@Override
	public PrismParametricModel generateModel() 
	{	
		return new RenewableModel(this.getLabel());
	}

	@Override
	public PrismParametricProperty generateProperty() 
	{		
		PrismParametricProperty prop = new PrismParametricProperty(this.getLabel());		
		prop.pctlFormulas.put(PropertyType.prob, "P=?[F(state=SUCC)]");
		prop.pctlFormulas.put(PropertyType.cost, "R=?[F(state=DONE)]");
		
		return prop;
	}

}
