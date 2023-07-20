package _parametricmodelprops;

import java.util.HashMap;

import _networkdescription.Entity;
import _patterns.Parallel;
/**
 * 
 * //./prism pargen.pm par.pctl -param  'p1=0.0:1.0,p2=0.0:1.0,c1=0.0:1.0,c2=0.0:1.0'
//Result (probability): ([0.0,1.0],[0.0,1.0],[0.0,1.0],[0.0,1.0]): { ( -1 ) p2 * p1 + p1 + p2 }
//Result (expected reward): ([0.0,1.0],[0.0,1.0],[0.0,1.0],[0.0,1.0]): { c1 * p1 + c2 * p2 }

dtmc

const int SUCC1 = 2;
const int SUCC2 = 3;
const int FAIL1 = 4;
const int FAIL2 = 5;
const int DONE = 6;

const double p1;
const double p2;
const double c1;
const double c2;

module standAlonePAR
state: [1..8] init 1;
succ : bool init false;
[] (state = 1) -> p1:(state'= SUCC1)&(succ'=true) + (1-p1):(state'=FAIL1);
[] (state = SUCC1) | (state=FAIL1) -> p2:(state'= SUCC2)&(succ'=true) + (1-p2):(state'=FAIL2);
[] (state = SUCC2) | (state=FAIL2) -> (state'=DONE);
[] (state=DONE) -> true;
endmodule
rewards
(state=SUCC1) : c1;
(state=SUCC2) : c2;
endrewards



 * 
 * 
 * */
public class ParallelModel extends PatternModel
{	
	Parallel pattern;
	private HashMap<PropertyType,String[]>  map = new HashMap<PropertyType,String[]> ();
	
	private void generateDatabaseOfResults()
	{
 
		String[] probResults = new String[6];
		probResults[0] = "";//no entities in pattern
		probResults[1] = "p1";//no entities in pattern
		probResults[2] = "( -1 ) * p2 * p1 + p1 + p2"; 
		probResults[3] = "p3 * p2 * p1 - p2 * p1 - p3 * p1 - p3 * p2 + p1 + p2 + p3";
		probResults[4] = "( -1 ) * p4 * p3 * p2 * p1 + p3 * p2 * p1 + p4 * p2 * p1 + p4 * p3 * p1 + p4 * p3 * p2 - p2 * p1 - p3 * p1 - p4 * p1 - p3 * p2 - p4 * p2 - p4 * p3 + p1 + p2 + p3 + p4 ";
		probResults[5] = "p5 * p4 * p3 * p2 * p1 - p4 * p3 * p2 * p1 - p5 * p3 * p2 * p1 - p5 * p4 * p2 * p1 - p5 * p4 * p3 * p1 - p5 * p4 * p3 * p2 + p3 * p2 * p1 + p4 * p2 * p1 + p5 * p2 * p1 + p4 * p3 * p1 + p5 * p3 * p1 + p5 * p4 * p1 + p4 * p3 * p2 + p5 * p3 * p2 + p5 * p4 * p2 + p5 * p4 * p3 - p2 * p1 - p3 * p1 - p4 * p1 - p5 * p1 - p3 * p2 - p4 * p2 - p5 * p2 - p4 * p3 - p5 * p3 - p5 * p4 + p1 + p2 + p3 + p4 + p5";
		map.put(PropertyType.prob, probResults);

		String[] costResults = new String[6];
		costResults[0] = "";//no entities in pattern
		costResults[1] = "c1 * p1";
		costResults[2] = "c1 * p1 + c2 * p2";
		costResults[3] = "c1 * p1 + c2 * p2 + c3 * p3";
		costResults[4] = "c1 * p1 + c2 * p2 + c3 * p3 + c4 * p4";
		costResults[5] = "c1 * p1 + c2 * p2 + c3 * p3 + c4 * p4 + c5 * p5";
		map.put(PropertyType.cost, costResults);
	}
	
	public ParallelModel(Parallel parallel) {
		super(parallel.label);
		this.pattern = parallel;
		this.generateDatabaseOfResults();
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
		Entity[] entitiesOrdering = this.pattern.par;

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
		return output;
	}
	//------------------------------------------------------------------------
	public int length()
	{
		return this.pattern.par.length;
	}
	//------------------------------------------------------------------------

	//------------------------------------------------------------------------
	@Override
	public String generateDTMC() 
	{
		int n = this.length();

		String dtmc = "dtmc";

		String[] succState = new String[n];
		String[] failState = new String[n];
		int[] succStateIndex = new int[n];
		int[] failStateIndex = new int[n];
				
		for(int i = 1;i<=n;i++)
		{
			succState[i-1] = succStateConstant(i);
			failState[i-1] = failStateConstant(i);
			succStateIndex[i-1] = i+1;
			failStateIndex[i-1] = n+i+1;
			dtmc += "\nconst int "+succState[i-1]+" = "+succStateIndex[i-1]+";";
			dtmc += "\nconst int "+failState[i-1]+" = "+failStateIndex[i-1]+";";
			
		}
		
		String doneState = doneStateConstant();
		String state = stateName();
	
		int done = (n*2)+2;
		
		dtmc += "\nconst int "+doneState+" = "+done+";";

		String moduleBody = "";
		String succTrueClause = "&("+successVariable()+"'=true)";
		
		int i=1;
		moduleBody+="\n[](state = "+i+") -> "
					+ ""+rhsProbParameter+i+":("+state+"'= "+succState[i-1]+")"+succTrueClause + "+(1-"+rhsProbParameter+i+"):("+state+"'="+failState[i-1]+");";
		
		String guard = "";
		for(i = 1;i<n;i++)
		{
			guard = "("+state+"="+succStateConstant(i) + ") | ("+state+"="+failStateConstant(i)+")"; 
			moduleBody += "\n[]"+guard+" -> "
					+ ""+rhsProbParameter+(i+1)+":("+state+"'= "+succState[i]+")"+succTrueClause + "+(1-"+rhsProbParameter+(i+1)+"):("+state+"'="+failState[i]+");";
			
		}
		guard = "("+state+"="+succStateConstant(i) + ") | ("+state+"="+failStateConstant(i)+")";
		moduleBody+="\n[]"+guard+" -> ("+state+"'="+done+");"; 
	
		moduleBody+="\n[]"+state+"="+done+" -> true;";
		
		 i = n;//n total entities have been invoked.

		dtmc += this.generateDoubleDeclaration(rhsProbParameter,n);		
		dtmc += this.generateDoubleDeclaration(rhsCostParameter,n);

		dtmc +="\n\nmodule PAR"+this.getLabel();
		dtmc +="\n "+state+": [1.."+done+"] init 1;";
		dtmc += "\n"+successVariable()+": bool init false;";
		dtmc += moduleBody;


		dtmc +="\nendmodule\n";

		//rewards structure
		String rewards = "rewards\n";


		for(int j = 1;j<=n;j++)
		{
			rewards +="("+state+"="+succStateConstant(j)+") : "+rhsCostParameter+j+";\n";
		}

		rewards += "endrewards\n";

		return dtmc+rewards;
	}

}
