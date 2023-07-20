package _parametricmodelprops;

import _networkdescription.Entity;
import core.Element;
import core.Model;

abstract public class PrismParametricModel extends Model implements Element
{
	public Entity entity;


	
	public String getCostParameter()
	{
		return this.costParameterForEntity(this.getLabel());
	}
	 public String getReliabilityParameter()
	 {
		 return this.probParameterForEntity(this.getLabel());
	 }

	//probability parameters in the template pattern.
	//these parameters are placed in expressions on the RHS of PMC algebraic expressions
	public static final String rhsProbParameter =PropertyType.prob.rhsVariable;
	public static final String rhsCostParameter = PropertyType.cost.rhsVariable;

	//these parameters belong on the lhs
	public static final String lhsProbParameter = PropertyType.prob.lhsVariable;
	public static final String lhsCostParameter = PropertyType.cost.lhsVariable;;



	abstract int length();
	//models are responsible for implementing these methods
	//for their particular model or user-defined type

	public String[] probabilityParameters() {
		String[] probs = new String[this.length()];
		for(int i = 1;i<=this.length();i++)
		{
			probs[i-1] = PrismParametricModel.rhsProbParameter+i;
		}
		return probs;
	}


	public String[] costParameters() {
		String[] costs = new String[this.length()];
		for(int i = 1;i<=this.length();i++)
		{
			costs[i-1] = PrismParametricModel.rhsCostParameter+i;
		}
		return costs;
	}


	public String[] paramNames()
	{
		int n = this.length();
		String[] paramNames = new String[n*2];

		String[] probs = probabilityParameters();
		String[] costs = costParameters();

		for(int i = 0;i<n;i++)
		{
			paramNames[i] = probs[i];
			paramNames[i+n] = costs[i];
		}

		return paramNames;
	}
	//--------------------------------------------------------------------------------

	public String[] paramLowerBounds()
	{
		int n = this.length();
		String[] paramLowerBounds = new String[n*2];
		for(int i = 0;i<n*2;i++)
		{
			paramLowerBounds[i] = "0.0";		
		}

		return paramLowerBounds;
	}
	//--------------------------------------------------------------------------------

	public String[] paramUpperBounds()
	{
		int n = this.length();
		String[] paramUpperBounds = new String[n*2];
		for(int i = 0;i<n*2;i++)
		{			 		
			paramUpperBounds[i] = "1.0";
		}

		return paramUpperBounds;
	}
	abstract public PrismParametricResult getResult(int n);



	public String toString()
	{
		return this.generateDTMC();
	}

	protected String generateDoubleDeclaration(String symbol,int n)
	{

		String declare="";

		for(int i = 1;i<=n;i++)
		{
			declare += "\nconst double "+symbol+i+";";
		}

		return declare;
	}

	/**
	 * 
	 * Returns the probability parameter of the entity
	 * 
	 * */
	public String probParameterForEntity(String entityLabel)
	{


		return rhsProbParameter+entityLabel;		
	}


	/**
	 * 
	 * Returns the cost parameter of the entity
	 * 
	 * */
	public String costParameterForEntity(String entityLabel)
	{

		return rhsCostParameter+entityLabel;		
	}




	static public String failStateConstant(int i)
	{
		String failState = "FAIL"+i;
		return failState;
	}	
	//------------------------------------------------------------------------
	static public String successVariable()
	{
		return "succ";
	}
	//------------------------------------------------------------------------
	static public String stateName()
	{		
		return "state";
	}
	//------------------------------------------------------------------------
	static public String succStateConstant(int i)
	{
		return "SUCC"+i;		
	}	
	//------------------------------------------------------------------------
	static public String doneStateConstant()
	{
		return "DONE";		
	}


	//------------------------------------------------------------------------
	static public String failStateConstant()
	{
		return "FAIL";		
	}




	public PrismParametricModel(String label) 
	{
		super(label);
	}

	abstract public String generateDTMC();


}
