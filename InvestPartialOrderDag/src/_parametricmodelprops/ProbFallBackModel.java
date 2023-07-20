package _parametricmodelprops;

import java.util.HashMap;

import _networkdescription.Entity;
import _patterns.ProbFallBack;

public class ProbFallBackModel extends PatternModel
{
	static final String fallBack = "fallBack";
	ProbFallBack pattern;
	private HashMap<PropertyType,String[]>  map = new HashMap<PropertyType,String[]> ();




	//------------------------------------------------------------------------	
	private void generateDatabaseOfResults()
	{

		//	need to change chfb1 ==> ch1 and update parameters as in probchoice pattern.

		String[] probResults = new String[6];
		probResults[0] = "";//no entities in pattern
		probResults[1] = "( -1 )*pfallBack * p1 + p1 + pfallBack ";//no entities in pattern
		probResults[2] = "(( -1 )*pfallBack * ch1 * p1 - pfallBack * ch2 * p2 + ch1 * p1 + ch2 * p2 + pfallBack * ch1 + pfallBack * ch2) / (ch1 + ch2)"; 
		probResults[3] = "(( -1 )*pfallBack * ch1 * p1 - pfallBack * ch2 * p2 - pfallBack * ch3 * p3 + ch1 * p1 + ch2 * p2 + ch3 * p3 + pfallBack * ch1 + pfallBack * ch2 + pfallBack * ch3) / (ch1 + ch2 + ch3)";
		probResults[4] = "(( -1 )*pfallBack * ch1 * p1 - pfallBack * ch2 * p2 - pfallBack * ch3 * p3 - pfallBack * ch4 * p4 + ch1 * p1 + ch2 * p2 + ch3 * p3 + ch4 * p4 + pfallBack * ch1 + pfallBack * ch2 + pfallBack * ch3 + pfallBack * ch4) / (ch1 + ch2 + ch3 + ch4)";
		probResults[5] = "(( -1 )*pfallBack * ch1 * p1 - pfallBack * ch2 * p2 - pfallBack * ch3 * p3 - pfallBack * ch4 * p4 - pfallBack * ch5 * p5 + ch1 * p1 + ch2 * p2 + ch3 * p3 + ch4 * p4 + ch5 * p5 + pfallBack * ch1 + pfallBack * ch2 + pfallBack * ch3 + pfallBack * ch4 + pfallBack * ch5) / (ch1 + ch2 + ch3 + ch4 + ch5)";
		map.put(PropertyType.prob, probResults);

		String[] costResults = new String[6];
		costResults[0] = "";//no entities in pattern
		costResults[1] = "( -1 )*cfallBack * pfallBack * p1 + c1 * p1 + cfallBack * pfallBack";
		costResults[2] = "(( -1 )*cfallBack * pfallBack * ch1 * p1 - cfallBack * pfallBack * ch2 * p2 + ch1 * c1 * p1 + ch2 * c2 * p2 + cfallBack * pfallBack * ch1 + cfallBack * pfallBack * ch2) / (ch1 + ch2)";
		costResults[3] = "(( -1 )*cfallBack * pfallBack * ch1 * p1 - cfallBack * pfallBack * ch2 * p2 - cfallBack * pfallBack * ch3 * p3 + ch1 * c1 * p1 + ch2 * c2 * p2 + ch3 * c3 * p3 + cfallBack * pfallBack * ch1 + cfallBack * pfallBack * ch2 + cfallBack * pfallBack * ch3) / (ch1 + ch2 + ch3)";
		costResults[4] = "(( -1 )*cfallBack * pfallBack * ch1 * p1 - cfallBack * pfallBack * ch2 * p2 - cfallBack * pfallBack * ch3 * p3 - cfallBack * pfallBack * ch4 * p4 + ch1 * c1 * p1 + ch2 * c2 * p2 + ch3 * c3 * p3 + ch4 * c4 * p4 + cfallBack * pfallBack * ch1 + cfallBack * pfallBack * ch2 + cfallBack * pfallBack * ch3 + cfallBack * pfallBack * ch4) / (ch1 + ch2 + ch3 + ch4)";
		costResults[5] = "(( -1 )*cfallBack * pfallBack * ch1 * p1 - cfallBack * pfallBack * ch2 * p2 - cfallBack * pfallBack * ch3 * p3 - cfallBack * pfallBack * ch4 * p4 - cfallBack * pfallBack * ch5 * p5 + ch1 * c1 * p1 + ch2 * c2 * p2 + ch3 * c3 * p3 + ch4 * c4 * p4 + ch5 * c5 * p5 + cfallBack * pfallBack * ch1 + cfallBack * pfallBack * ch2 + cfallBack * pfallBack * ch3 + cfallBack * pfallBack * ch4 + cfallBack * pfallBack * ch5) / (ch1 + ch2 + ch3 + ch4 + ch5)";
		map.put(PropertyType.cost, costResults);
	}











	public ProbFallBackModel(String label) 
	{
		super(label);
	}

	public ProbFallBackModel(ProbFallBack probFallBack) 
	{
		super(probFallBack.label);
		this.pattern = probFallBack;
		this.generateDatabaseOfResults();
	}

	@Override
	public int length() 
	{
		return this.pattern.choice.length;
	}

	@Override
	public PrismParametricResult getResult(int n) 
	{

		PrismParametricResult result = new PrismParametricResult(this.getLabel());

		for(PropertyType pt : PropertyType.values())
		{		
			String[] results = this.map.get(pt);
			String lhs = pt+pattern.label;
			String rhs = this.instance(results[n]);			
			result.equations.put(pt,lhs+" = "+rhs);						
		}

		return result;
	}


	//------------------------------------------------------------------------
	static public String fallBackStateConstant()
	{
		return "FALLBACK";		
	}
	//------------------------------------------------------------------------
	static public String fallBackSuccStateConstant()
	{
		return "FALLBACKSUCC";		
	}
	//------------------------------------------------------------------------

	@Override
	public String generateDTMC() 
	{

		String dtmc = "\ndtmc\n";



		String declares = "";




		//Entity[] entities = this.probChoice.choice;
		Double[] chProb = this.pattern.chProb;
		int n = chProb.length;

		int stateNumbering = 1;
		for(int i = 1;i<=n;i++)
		{
			declares +="\n const int "+choiceStateParameter(i)+" = "+stateNumbering+";";
			stateNumbering++;
		}

		for(int i = 1;i<=n;i++)
		{
			declares +="\n const int "+succStateConstant(i)+" = "+stateNumbering+";";
			stateNumbering++;
		}



		declares += "\n const int "+failStateConstant()+" = "+stateNumbering+";";
		stateNumbering++;
		declares += "\n const int "+doneStateConstant()+" = "+stateNumbering+";";
		stateNumbering++;
		declares += "\n const int "+fallBackStateConstant()+" = "+stateNumbering+";";
		stateNumbering++;
		declares += "\nconst int "+fallBackSuccStateConstant()+" = "+stateNumbering+";";
		declares += this.generateDoubleDeclaration(rhsProbParameter,n);		
		declares += this.generateDoubleDeclaration(rhsCostParameter,n);

		declares += "\nconst double "+rhsProbParameter+fallBack+";";
		declares += "\nconst double "+rhsCostParameter+fallBack+";";

		//fallback declaration
		//declares += "\n const double "+;

		String choiceTransition = "[] ("+stateName()+" = 0) -> ";

		//probabilistic choice variables.
		for(int i = 0;i<n;i++)
		{
			String chProbvalue = choiceProbabilityParameter(i+1);
			String chState = choiceStateParameter(i+1);
			declares +="\nconst double "+chProbvalue+";";//" = "+chProb[i]+";";
			String piece = chProbvalue+":("+stateName()+"'="+chState+")";
			choiceTransition += piece+"+";
		}
		choiceTransition = choiceTransition.subSequence(0, choiceTransition.lastIndexOf("+"))+";";

		//now do all the choice transitions.
		String transitions = "";	
		String succTrueClause = "&("+successVariable()+"'=true)";
		for(int i = 0;i<n;i++)
		{
			String trans = "\n[] ("+stateName()+"="+choiceStateParameter(i+1)+") -> ";
			trans += rhsProbParameter+(i+1)+":("+stateName()+"'="+succStateConstant(i+1)+")"
					+succTrueClause+" + (1-"+rhsProbParameter+(i+1)+"):("
					+stateName()+"'="+fallBackStateConstant()+");";
			transitions += trans;
		}

		//now do the fallback transition
		transitions += "\n\n[] ("+stateName()+"="+fallBackStateConstant()+") -> "+
				rhsProbParameter+fallBack+":("+stateName()+"'="+fallBackSuccStateConstant()+")"+succTrueClause
				+"+ (1-"+rhsProbParameter+fallBack+"):("+stateName()+"'="+failStateConstant()+");";

		String doneGuard = "";
		for(int i = 0;i<n;i++)
		{
			doneGuard += "("+stateName()+"="+succStateConstant(i+1)+")|";			
		}					
		doneGuard = doneGuard.subSequence(0, doneGuard.lastIndexOf("|"))+"";
		doneGuard += "| ("+stateName()+" = "+failStateConstant()+") | ("+stateName()+" = "+fallBackSuccStateConstant()+")";

		String doneTransitions = "\n[]"+doneGuard+" -> ("+stateName()+"'="+doneStateConstant()+");";
		doneTransitions +="\n[] ("+stateName()+" = "+doneStateConstant()+") -> true;";

		dtmc += declares;
		dtmc += "\n module probChoice"+this.getLabel();
		dtmc += "\n "+stateName()+" : [0.."+stateNumbering+"] init 0;";
		dtmc += "\n"+successVariable()+": bool init false;";
		dtmc += "\n"+choiceTransition;
		dtmc += "\n"+transitions;
		dtmc += "\n"+doneTransitions;
		dtmc += "\nendmodule";

		String rewards = "\nrewards";
		for(int i = 1;i<=n;i++)
		{
			rewards +="\n  ("+stateName()+" = "+succStateConstant(i)+") : "+rhsCostParameter+i+";";				
		}

		rewards +="\n  ("+stateName()+" = "+fallBackSuccStateConstant()+") : "+rhsCostParameter+fallBack+";";

		rewards += "\nendrewards";

		dtmc += rewards;

		return dtmc;
	}













	private String instance(String expressionResult)
	{

		String output = expressionResult;			
		Entity[] entitiesOrdering = this.pattern.choice;



		//Entity fallbackEntity = entitiesOrdering[entitiesOrdering.length-1];

		int i = 0;
		for(String pi : this.probabilityParameters())
		{			
			String label = entitiesOrdering[i].getLabel();			
			String pLabel = this.probParameterForEntity(label);			
			output = output.replace(pi, pLabel);
			i++;
		}
		i = 0;
		for(String ci : this.costParameters())
		{
			String label = entitiesOrdering[i].getLabel();
			String cLabel = this.costParameterForEntity(label);
			output = output.replace(ci, cLabel);
			i++;
		}



		//now replace the choice parameters with their labels according to the building.
		i = 1;
		for(String chLabel : this.choiceProbabililityParameters())
		{			
			String chi = ProbChoiceModel.choiceProbabilityParameter(i);
			output = output.replace(chi,chLabel);
			i++;
		}


		//now replace the fallback prob and cost parameters for the supplied entity.
		String pLabel = this.probParameterForEntity(this.pattern.fallback.getLabel());
		String cLabel = this.costParameterForEntity(this.pattern.fallback.getLabel());

		output = output.replace(this.fallBackProbabilityParameter(), pLabel);
		output = output.replace(this.fallBackCostParameter(), cLabel);

		return output;
	}
	public static String choiceProbabilityParameter(int i)
	{		
		return "ch"+i;
	}

	public static String choiceStateParameter(int i)
	{
		return "CHFB"+i;
	}


	@Override 
	public String[] paramNames()
	{
		int n = this.length();
		String[] paramNames = new String[(n*3)+2];

		String[] probs = probabilityParameters();
		String[] costs = costParameters();
		//String[] choice = new String[n];

		for(int i = 0;i<n;i++)
		{
			paramNames[i] = probs[i];
			paramNames[i+n] = costs[i];		
			paramNames[i+(n*2)] = this.getLabel()+choiceProbabilityParameter(i+1);
		}
		paramNames[n*3] = rhsProbParameter+fallBack;
		paramNames[n*3+1] = rhsCostParameter+fallBack;


		return paramNames;
	}
	public String[] choiceProbabililityParameters()
	{
		int n = this.length();
		String[] choice = new String[n];


		for(int i=1;i<=n;i++)
		{
			choice[i-1]=this.getLabel()+choiceProbabilityParameter(i);
			//choice[i-1]=choiceProbabilityParameter(i);
		}
		return choice;
	}
	//--------------------------------------------------------------------------------
	public String fallBackCostParameter()
	{
		return rhsCostParameter+fallBack;						
	}
	//--------------------------------------------------------------------------------
	public String fallBackProbabilityParameter()
	{
		return rhsProbParameter+fallBack;						
	}
	//--------------------------------------------------------------------------------
	@Override
	public String[] paramLowerBounds()
	{
		int n = this.length();
		String[] paramLowerBounds = new String[n*3+2];
		for(int i = 0;i<n*3+2;i++)
		{
			paramLowerBounds[i] = "0.0";		
		}

		return paramLowerBounds;
	}
	//--------------------------------------------------------------------------------
	@Override
	public String[] paramUpperBounds()
	{
		int n = this.length();
		String[] paramUpperBounds = new String[n*3+2];
		for(int i = 0;i<n*3+2;i++)
		{			 		
			paramUpperBounds[i] = "1.0";
		}

		return paramUpperBounds;
	}


}
