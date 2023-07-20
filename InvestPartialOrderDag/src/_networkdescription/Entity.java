package _networkdescription;

import _parametricmodelprops.PrismParametricModel;
import _parametricmodelprops.PrismParametricProperty;
import core.Description;

abstract public class Entity extends Description 
{
	
	public abstract PrismParametricModel generateModel();
	public abstract PrismParametricProperty generateProperty();

	public Entity(String label) 
	{
		super(label);
		
	}
	
	public String toString()
	{
		return this.getLabel();
	}
}
