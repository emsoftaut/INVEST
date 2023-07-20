package _parametricmodelprops;

abstract public class PatternModel extends PrismParametricModel
{
	
	
	@Override
	public String probParameterForEntity(String entityLabel)
	{
		return "prob"+entityLabel;		
	}
	
	/**
	 * 
	 * Returns the cost parameter of the entity
	 * 
	 * */
	@Override
	public String costParameterForEntity(String entityLabel)
	{
		
		return "cost"+entityLabel;		
	}
	

	public PatternModel(String label) {
		super(label);		
	}

}
