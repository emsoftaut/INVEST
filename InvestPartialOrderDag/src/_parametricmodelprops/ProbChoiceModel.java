package _parametricmodelprops;

import java.util.HashMap;

import _networkdescription.Entity;
import _patterns.ProbChoice;

public class ProbChoiceModel extends PatternModel
{

	ProbChoice pattern;
	private HashMap<PropertyType,String[]>  map = new HashMap<PropertyType,String[]> ();




	private void generateDatabaseOfResults()
	{

		String[] probResults = new String[6];
		probResults[0] = "";
		probResults[1]  = "p1";
		probResults[2]  = "ch1 * p1 + ch2 * p2"; 
		probResults[3]  = "ch1 * p1 + ch2 * p2 + ch3 * p3";
		probResults[4]  = "ch1 * p1 + ch2 * p2 + ch3 * p3 + ch4 * p4";
		probResults[5]  = "ch1 * p1 + ch2 * p2 + ch3 * p3 + ch4 * p4 + ch5 * p5";
		map.put(PropertyType.prob, probResults);

		String[] costResults = new String[6];
		costResults[0]  = "";
		costResults[1]  = "c1 * p1";
		costResults[2]  = "ch1 * c1 * p1 + ch2 * c2 * p2";
		costResults[3]  = "ch1 * c1 * p1 + ch2 * c2 * p2 + ch3 * c3 * p3";
		costResults[4]  = "ch1 * c1 * p1 + ch2 * c2 * p2 + ch3 * c3 * p3 + ch4 * c4 * p4";
		costResults[5]  = "ch1 * c1 * p1 + ch2 * c2 * p2 + ch3 * c3 * p3 + ch4 * c4 * p4 + ch5 * c5 * p5";
		map.put(PropertyType.cost, costResults);
	}






	public ProbChoiceModel(ProbChoice probChoice) 
	{
		super(probChoice.label);
		this.generateDatabaseOfResults();
		this.pattern = probChoice;
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
	private String instance(String expressionResult)
	{

		String output = expressionResult;			
		Entity[] entitiesOrdering = this.pattern.choice;

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
		
		
		return output;
	}


	public static String choiceProbabilityParameter(int i)
	{
		return "ch"+i;
	}

	public static String choiceStateParameter(int i)
	{
		return "CH"+i;
	}

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


		declares += this.generateDoubleDeclaration(rhsProbParameter,n);		
		declares += this.generateDoubleDeclaration(rhsCostParameter,n);

		String choiceTransition = "[] ("+stateName()+" = 0) -> ";

		//probabilistic choice variables.
		for(int i = 0;i<n;i++)
		{
			String chProbvalue = this.getLabel()+choiceProbabilityParameter(i+1);
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
			trans += rhsProbParameter+(i+1)+":("+stateName()+"'="+succStateConstant(i+1)+")"+succTrueClause+" + (1-"+rhsProbParameter+(i+1)+"):("+stateName()+"'="+failStateConstant()+");";
			transitions += trans;
		}

		String doneGuard = "";
		for(int i = 0;i<n;i++)
		{
			doneGuard += "("+stateName()+"="+succStateConstant(i+1)+")|";			
		}					
		doneGuard = doneGuard.subSequence(0, doneGuard.lastIndexOf("|"))+"";
		doneGuard += "| ("+stateName()+" = "+failStateConstant()+")";

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

		rewards += "\nendrewards";

		dtmc += rewards;

		return dtmc;
	}


	@Override 
	public String[] paramNames()
	{
		int n = this.length();
		String[] paramNames = new String[n*3];

		String[] probs = probabilityParameters();
		String[] costs = costParameters();
		//String[] choice = new String[n];

		for(int i = 0;i<n;i++)
		{
			paramNames[i] = probs[i];
			paramNames[i+n] = costs[i];		
			//updated to have choice variables parameterised according to the building.
			paramNames[i+(n*2)] = this.getLabel()+choiceProbabilityParameter(i+1);
		}

		return paramNames;
	}
	//--------------------------------------------------------------------------------
	public String[] choiceProbabililityParameters()
	{
		int n = this.length();
		String[] choice = new String[n];
		
		
		for(int i=1;i<=n;i++)
		{
			
			//e.g. bch1 and bch2 instead of ch1 and ch2.
			//so that we can have more than one instance of probchoice in 
			//the network.
			choice[i-1]=this.getLabel()+choiceProbabilityParameter(i);
			
		}
		
		
		return choice;
	}
	//--------------------------------------------------------------------------------
	@Override
	public String[] paramLowerBounds()
	{
		int n = this.length();
		String[] paramLowerBounds = new String[n*3];
		for(int i = 0;i<n*3;i++)
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
		String[] paramUpperBounds = new String[n*3];
		for(int i = 0;i<n*3;i++)
		{			 		
			paramUpperBounds[i] = "1.0";
		}

		return paramUpperBounds;
	}

}














