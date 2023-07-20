package _parametricmodelprops;

import java.util.HashMap;
import core.Element;
import core.Property;


public class PrismParametricProperty extends Property implements Element
{

	//contains expressions for prob and cost reachability	
	public HashMap<PropertyType,String> pctlFormulas;
	
	public PrismParametricProperty(String label) 
	{
		super(label);
		pctlFormulas = new HashMap<PropertyType,String>();
	}

}
