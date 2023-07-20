package _parametricmodelprops;

import java.util.HashMap;

import _networkdescription.Entity;
import _patterns.Sequence;
/**
 * 
 * ./prism seq.pm seq.pctl -param 
 * 'p1=0.0:1.0,p2=0.0:1.0,p3=0.0:1.0,
 *  c1=0.0:1.0,c2=0.0:1.0,c3=0.0:1.0'
 *  
 *  Parametric model checking: 
 *  P=? [ F (state=SUCC1 | state=SUCC2) ]
 *  Result (probability): { p3 * p2 * p1 - p2 * p1 - p3 * p1 - p3 * p2 + p1 + p2 + p3 }
 *
 * R=? [ F ((state=SUCC)|(state=FAIL)) ]
 * Result (cost) { c3 * p2 * p1 - c2 * p1 - c3 * p1 - c3 * p2 + c1 + c2 + c3 }
 * */

public class SequenceModel extends PatternModel
{

	private Sequence pattern;
	private HashMap<PropertyType,String[]>  map = new HashMap<PropertyType,String[]> ();
	//------------------------------------------------------------------------	
	private void generateDatabaseOfResults()
	{

		//		HashMap<PropertyTypes,String[]>  map = new HashMap<PropertyTypes,String[]> ();

		String[] probResults = new String[6];
		probResults[0] = "";//no entities in pattern
		probResults[1] = "p1";//no entities in pattern
		probResults[2] = "( -1 ) * p2 * p1 + p1 + p2";//replaced ( -1 ) with ( - 1) * 
		probResults[3] = "p3 * p2 * p1 - p2 * p1 - p3 * p1 - p3 * p2 + p1 + p2 + p3";
		probResults[4] = "( -1 ) * p4 * p3 * p2 * p1 + p3 * p2 * p1 + p4 * p2 * p1 + p4 * p3 * p1 + p4 * p3 * p2 - p2 * p1 - p3 * p1 - p4 * p1 - p3 * p2 - p4 * p2 - p4 * p3 + p1 + p2 + p3 + p4";
		probResults[5] = "p5 * p4 * p3 * p2 * p1 - p4 * p3 * p2 * p1 - p5 * p3 * p2 * p1 - p5 * p4 * p2 * p1 - p5 * p4 * p3 * p1 - p5 * p4 * p3 * p2 + p3 * p2 * p1 + p4 * p2 * p1 + p5 * p2 * p1 + p4 * p3 * p1 + p5 * p3 * p1 + p5 * p4 * p1 + p4 * p3 * p2 + p5 * p3 * p2 + p5 * p4 * p2 + p5 * p4 * p3 - p2 * p1 - p3 * p1 - p4 * p1 - p5 * p1 - p3 * p2 - p4 * p2 - p5 * p2 - p4 * p3 - p5 * p3 - p5 * p4 + p1 + p2 + p3 + p4 + p5";
		map.put(PropertyType.prob, probResults);

		String[] costResults = new String[6];
		costResults[0] = "";//no entities in pattern
		costResults[1] = "c1";
		costResults[2] = "( -1 ) * c2 * p1 + c1 + c2";
		costResults[3] = "c3 * p2 * p1 - c2 * p1 - c3 * p1 - c3 * p2 + c1 + c2 + c3";
		costResults[4] = "( -1 ) * c4 * p3 * p2 * p1 + c3 * p2 * p1 + c4 * p2 * p1 + c4 * p3 * p1 + c4 * p3 * p2 - c2 * p1 - c3 * p1 - c4 * p1 - c3 * p2 - c4 * p2 - c4 * p3 + c1 + c2 + c3 + c4";
		costResults[5] = "c5 * p4 * p3 * p2 * p1 - c4 * p3 * p2 * p1 - c5 * p3 * p2 * p1 - c5 * p4 * p2 * p1 - c5 * p4 * p3 * p1 - c5 * p4 * p3 * p2 + c3 * p2 * p1 + c4 * p2 * p1 + c5 * p2 * p1 + c4 * p3 * p1 + c5 * p3 * p1 + c5 * p4 * p1 + c4 * p3 * p2 + c5 * p3 * p2 + c5 * p4 * p2 + c5 * p4 * p3 - c2 * p1 - c3 * p1 - c4 * p1 - c5 * p1 - c3 * p2 - c4 * p2 - c5 * p2 - c4 * p3 - c5 * p3 - c5 * p4 + c1 + c2 + c3 + c4 + c5";
		map.put(PropertyType.cost, costResults);



	}
	//------------------------------------------------------------------------
	//convert p1 -> pres1, p2 -> pgrid etc 
	//
	private String instance(String expressionResult)
	{

		String output = expressionResult;			
		Entity[] entitiesOrdering = this.pattern.seq;

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
	public SequenceModel(Sequence pattern) 
	{		
		super(pattern.label);

		this.pattern = pattern;
		this.generateDatabaseOfResults();
	}
	//------------------------------------------------------------------------
	static public String failStateConstant()
	{
		String failState = "FAIL";
		return failState;
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
	static public String useStateConstant(int i)
	{
		return "USE"+i;

	}
	//------------------------------------------------------------------------
	static public String doneStateConstant()
	{
		return "DONE";		
	}
	//------------------------------------------------------------------------
	//returns the number of entities involved in this pattern
	public int length()
	{
		return this.pattern.seq.length;
	}
	//------------------------------------------------------------------------
	/**
	 * 
	 * returns state proposition of the form (state=SUCC1) | (state=SUCC2) | ... | (state=SUCCN)
	 * */
	public  String successInvokeProposition()
	{
		String out = "";
		for(int i = 1;i<=this.length();i++)
		{			
			out+="("+SequenceModel.stateName()+" = "+SequenceModel.succStateConstant(i)+")|";			
		}

		out = out.substring(0, out.length()-1);
		return "("+out+")";

	}
	//----------------------------------------------------------------------------------------
	@Override
	public String generateDTMC() 
	{
		int n = this.length();

		String dtmc = "dtmc";

		String[] succState = new String[n];
		int[] succStateIndex = new int[n];
		String[] useState = new String[n];

		for(int i = 1;i<=n;i++)
		{
			succState[i-1] = succStateConstant(i);
			succStateIndex[i-1] = n+i;
			useState[i-1] = useStateConstant(i);
			dtmc += "\nconst int "+succState[i-1]+" = "+succStateIndex[i-1]+";";
		}

		String failState = failStateConstant();
		String doneState = doneStateConstant();
		String state = stateName();

		int fail = (n*2)+2;
		int done = (n*2)+3;

		dtmc += "\nconst int "+failState+" = "+fail+";";
		dtmc += "\nconst int "+doneState+" = "+done+";";

		String moduleBody = "";
		//compute transitions for using entities from 1 to n-1
		for(int i = 1;i<n;i++)
		{
			//[] (state=1) -> p1:(state'=SUCC) + (1 - p1):(state'=2);
			moduleBody+="\n[](state = "+i+") -> "
					+ ""+rhsProbParameter+i+":("+state+"'= "+succState[i-1]+") + (1-"+rhsProbParameter+i+"):("+state+"'="+(i+1)+");";
		}

		//compute transition for using last entity in the sequence
		int i = n;//n total entities have been invoked.
		moduleBody+="\n[](state = "+i+") -> "
				+ ""+rhsProbParameter+i+":("+state+"'= "+succState[i-1]+") + (1-"+rhsProbParameter+i+"):("+state+"'="+(fail)+");";

		dtmc += this.generateDoubleDeclaration(rhsProbParameter,n);		
		dtmc += this.generateDoubleDeclaration(rhsCostParameter,n);

		dtmc +="\n\nmodule SEQ"+this.getLabel();
		dtmc +="\n "+state+": [1.."+done+"] init 1;";
		dtmc += moduleBody;

		//dtmc += "\n[] ("+state+"="+succ+") -> true;";
		dtmc += "\n[] ("+state+"="+fail+") -> ("+state+"'="+doneStateConstant()+");";


		dtmc += "\n[] ("+this.successInvokeProposition()+") -> ("+state+"'="+doneStateConstant()+");";
		dtmc += "\n[] ("+state+"="+doneStateConstant()+") -> true;";

		dtmc +="\nendmodule\n";

		//rewards structure
		String rewards = "rewards\n";


		for(int j = 1;j<=n;j++)
		{
			rewards +="("+state+"="+j+") : "+rhsCostParameter+j+";\n";
		}

		rewards += "endrewards\n";

		return dtmc+rewards;
	}
	//--------------------------------------------------------------------------------
	
}
