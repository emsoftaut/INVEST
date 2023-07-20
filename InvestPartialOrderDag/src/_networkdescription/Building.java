package _networkdescription;


import _parametricmodelprops.PrismParametricModel;
import _parametricmodelprops.PrismParametricProperty;
import _patterns.Pattern;

public class Building extends Entity    
{

	
	//Double requirement = 0.97;
	public	Pattern pattern;
	//public HourlyEnergyRequirement usage;
	//	public EnergyConsumptionPolicy policy;
	//
	//	//24 hour load profile of the entity
	//	//profile[t] = HourlyUsageRequirement

	public Building(String label) 
	{
		super(label);
		// TODO Auto-generated constructor stub
	}
	//
	//	@Override
	//	public EnergyConsumptionPolicy getPolicy() {
	//		// TODO Auto-generated method stub
	//		return null;
	//	}
	//
	//	@Override
	//	public HourlyEnergyRequirement getUsage() {
	//		// TODO Auto-generated method stub
	//		return null;
	//	}

	@Override
	public PrismParametricModel generateModel() 
	{

		return this.pattern.generateModel();
	}

	@Override
	public PrismParametricProperty generateProperty() 
	{		
		return this.pattern.generateProperty();
	}
}
