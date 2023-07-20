package _patterns;



import _networkdescription.Entity;
import _parametricmodelprops.PrismParametricModel;
import _parametricmodelprops.PrismParametricProperty;

abstract public class Pattern 
{

	public abstract PrismParametricModel generateModel();
	public abstract PrismParametricProperty generateProperty();

	public String label;


	public Pattern(String label)
	{
		this.label = label;
	}

	public PrismParametricModel getParametricModel() {
		// TODO Auto-generated method stub
		return this.generateModel();
	}

	public PrismParametricProperty getParametricProperty()
	{
		return this.generateProperty();
	}
	public abstract Entity[] getEntities();

}
