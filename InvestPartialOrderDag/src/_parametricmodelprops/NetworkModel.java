package _parametricmodelprops;

import java.util.ArrayList;
import java.util.HashMap;

import _networkdescription.Entity;
import _networkdescription.Network;
import _networkdescription.Renewable;
import core.Description;
import core.Poset;

public class NetworkModel extends PrismParametricModel
{
	Network network;
	private HashMap<PropertyType,String[]>  map = new HashMap<PropertyType,String[]> ();

	@Override
	public String[] probabilityParameters() {
		int len = this.network.buildings().size();
		String[] probs = new String[len];


		int i = 1;
		for(Description d : this.network.buildings())
		{
			probs[i-1] = "prob"+d.getLabel();
			i++;
		}

//		for(Description d : this.network.renewableEnergy())
//		{
//			probs[i-1] = "prob"+d.getLabel();
//			i++;
//		}


		return probs;
	}

	@Override
	public String[] costParameters() {
		int len = this.network.buildings().size();
		String[] costs = new String[len];
		int i = 1;
		for(Description d : this.network.buildings())
		{
			costs[i-1] = "cost"+d.getLabel();
			i++;
		}

//		for(Description d : this.network.renewableEnergy())
//		{
//			costs[i-1] = "cost"+d.getLabel();
//			i++;
//		}
		return costs;
	}





	public NetworkModel(String label, Network network) 
	{
		super(label);
		this.network = network;
		this.generateDatabaseOfResults();
	}

	@Override
	public
	int length() //number of buildings + renewables.
	{		
		return this.network.buildings().size();
	}

	private void generateDatabaseOfResults()
	{
		
		String[] probResults = new String[21];
		probResults[0] = "";//no entities in pattern
		probResults[1] = "p1";//no entities in pattern
		probResults[2] = "p1*p2";//replaced ( -1 ) with ( - 1) * 
		probResults[3] = "p1*p2*p3";
		probResults[4] = "p1*p2*p3*p4";
		probResults[5] = "p1*p2*p3*p4*p5";
		probResults[6] = "p1*p2*p3*p4*p5*p6";
		probResults[7] = "p1*p2*p3*p4*p5*p6*p7";
		probResults[8] = "p1*p2*p3*p4*p5*p6*p7*p8";
		probResults[9] = "p1*p2*p3*p4*p5*p6*p7*p8*p9";
		probResults[10] = "p1*p2*p3*p4*p5*p6*p7*p8*p9*p_10";
		probResults[11] = "p1*p2*p3*p4*p5*p6*p7*p8*p9*p_10*p_11";
		probResults[12] = "p1*p2*p3*p4*p5*p6*p7*p8*p9*p_10*p_11*p_12";
		probResults[13] = "p1*p2*p3*p4*p5*p6*p7*p8*p9*p_10*p_11*p_12*p_13";
		probResults[14] = "p1*p2*p3*p4*p5*p6*p7*p8*p9*p_10*p_11*p_12*p_13*p_14";
		probResults[15] = "p1*p2*p3*p4*p5*p6*p7*p8*p9*p_10*p_11*p_12*p_13*p_14*p_15";
		probResults[16] = "p1*p2*p3*p4*p5*p6*p7*p8*p9*p_10*p_11*p_12*p_13*p_14*p_15*p_16";
		probResults[17] = "p1*p2*p3*p4*p5*p6*p7*p8*p9*p_10*p_11*p_12*p_13*p_14*p_15*p_16*p_17";
		probResults[18] = "p1*p2*p3*p4*p5*p6*p7*p8*p9*p_10*p_11*p_12*p_13*p_14*p_15*p_16*p_17*p_18";
		probResults[19] = "p1*p2*p3*p4*p5*p6*p7*p8*p9*p_10*p_11*p_12*p_13*p_14*p_15*p_16*p_17*p_18*p_19";
		probResults[20] = "p1*p2*p3*p4*p5*p6*p7*p8*p9*p_10*p_11*p_12*p_13*p_14*p_15*p_16*p_17*p_18*p_19*p_20";
		map.put(PropertyType.prob, probResults);

		String[] costResults = new String[21];
		costResults[0] = "";//no entities in pattern
		costResults[1] = "c1*p1";
		costResults[2] = "2*c2*p2 + 1*c1*p1";
		costResults[3] = "3*c3*p3 + 2*c2*p2 + 1*c1*p1";
		costResults[4] = "4*c4*p4+3*c3*p3 + 2*c2*p2 + 1*c1*p1";
		costResults[5] = "5*c5*p5+4*c4*p4+3*c3*p3 + 2*c2*p2 + 1*c1*p1";
		costResults[6] = "6*c6*p6+5*c5*p5+4*c4*p4+3*c3*p3 + 2*c2*p2 + 1*c1*p1";
		costResults[7] = "7*c7*p7+6*c6*p6+5*c5*p5+4*c4*p4+3*c3*p3 + 2*c2*p2 + 1*c1*p1";
		costResults[8] = "8*c8*p8+7*c7*p7+6*c6*p6+5*c5*p5+4*c4*p4+3*c3*p3 + 2*c2*p2 + 1*c1*p1";
		costResults[9] = "9*c9*p9+8*c8*p8+7*c7*p7+6*c6*p6+5*c5*p5+4*c4*p4+3*c3*p3 + 2*c2*p2 + 1*c1*p1";
		costResults[10] = "10*c_10*p_10+9*c9*p9+8*c8*p8+7*c7*p7+6*c6*p6+5*c5*p5+4*c4*p4+3*c3*p3 + 2*c2*p2 + 1*c1*p1";
		costResults[11] = "11*c_11*p_11+10*c_10*p_10+9*c9*p9+8*c8*p8+7*c7*p7+6*c6*p6+5*c5*p5+4*c4*p4+3*c3*p3 + 2*c2*p2 + 1*c1*p1";
		costResults[12] = "12*c_12*p_12+11*c_11*p_11+10*c_10*p_10+9*c9*p9+8*c8*p8+7*c7*p7+6*c6*p6+5*c5*p5+4*c4*p4+3*c3*p3 + 2*c2*p2 + 1*c1*p1";
		costResults[13] = "13*c_13*p_13+12*c_12*p_12+11*c_11*p_11+10*c_10*p_10+9*c9*p9+8*c8*p8+7*c7*p7+6*c6*p6+5*c5*p5+4*c4*p4+3*c3*p3 + 2*c2*p2 + 1*c1*p1";
		costResults[14] = "14*c_14*p_14+13*c_13*p_13+12*c_12*p_12+11*c_11*p_11+10*c_10*p_10+9*c9*p9+8*c8*p8+7*c7*p7+6*c6*p6+5*c5*p5+4*c4*p4+3*c3*p3 + 2*c2*p2 + 1*c1*p1";
		costResults[15] = "15*c_15*p_15+14*c_14*p_14+13*c_13*p_13+12*c_12*p_12+11*c_11*p_11+10*c_10*p_10+9*c9*p9+8*c8*p8+7*c7*p7+6*c6*p6+5*c5*p5+4*c4*p4+3*c3*p3 + 2*c2*p2 + 1*c1*p1";
		costResults[16] = "16*c_16*p_16+15*c_15*p_15+14*c_14*p_14+13*c_13*p_13+12*c_12*p_12+11*c_11*p_11+10*c_10*p_10+9*c9*p9+8*c8*p8+7*c7*p7+6*c6*p6+5*c5*p5+4*c4*p4+3*c3*p3 + 2*c2*p2 + 1*c1*p1";
		costResults[17] = "17*c_17*p_17+16*c_16*p_16+15*c_15*p_15+14*c_14*p_14+13*c_13*p_13+12*c_12*p_12+11*c_11*p_11+10*c_10*p_10+9*c9*p9+8*c8*p8+7*c7*p7+6*c6*p6+5*c5*p5+4*c4*p4+3*c3*p3 + 2*c2*p2 + 1*c1*p1";
		costResults[18] = "18*c_18*p_18+17*c_17*p_17+16*c_16*p_16+15*c_15*p_15+14*c_14*p_14+13*c_13*p_13+12*c_12*p_12+11*c_11*p_11+10*c_10*p_10+9*c9*p9+8*c8*p8+7*c7*p7+6*c6*p6+5*c5*p5+4*c4*p4+3*c3*p3 + 2*c2*p2 + 1*c1*p1";
		costResults[19] = "19*c_19*p_19+18*c_18*p_18+17*c_17*p_17+16*c_16*p_16+15*c_15*p_15+14*c_14*p_14+13*c_13*p_13+12*c_12*p_12+11*c_11*p_11+10*c_10*p_10+9*c9*p9+8*c8*p8+7*c7*p7+6*c6*p6+5*c5*p5+4*c4*p4+3*c3*p3 + 2*c2*p2 + 1*c1*p1";
		costResults[20] = "20*c_20*p_20+19*c_19*p_19+18*c_18*p_18+17*c_17*p_17+16*c_16*p_16+15*c_15*p_15+14*c_14*p_14+13*c_13*p_13+12*c_12*p_12+11*c_11*p_11+10*c_10*p_10+9*c9*p9+8*c8*p8+7*c7*p7+6*c6*p6+5*c5*p5+4*c4*p4+3*c3*p3 + 2*c2*p2 + 1*c1*p1";
		map.put(PropertyType.cost, costResults);
	}
	
	
	
	
	private String instance(String expressionResult)
	{

		String output = expressionResult;			
		ArrayList<Description> entitiesOrdering = this.network.buildings();

		int i = 1;
		String[] probparams = this.probabilityParameters();
		for(String pLabel : probparams)
		{		
			String pi = "p"+i;
			if(i >= 10)
			{
				pi = "p_"+i;
			}
			output = output.replace(pi,pLabel);
			i++;
		}
		i = 1;
		for(String cLabel : this.costParameters())
		{	
			String ci = "c"+i;
			if(i>=10)
			{
				ci = "c_"+i;
			}
			output = output.replace(ci, cLabel);
			i++;
		}
		return output;
	}
	
	
	
	
	public PrismParametricResult getResult(int n) 
	{

		PrismParametricResult result = new PrismParametricResult(this.getLabel());

		for(PropertyType pt : PropertyType.values())
		{		
			String[] results = this.map.get(pt);
			String lhs = pt+this.network.getLabel();
			String rhs = this.instance(results[n]);			
			result.equations.put(pt,lhs+" = "+rhs);						
		}

		return result;
	}

	private String transition(String label,int step)
	{
		String out = "\n"
				+ "["+label+"Op] (state"+label+" = 0)&(step="+step+") -> prob"+
				label+":(state"+label+"'=SUCC)&(step'=step+1) + (1-prob"+label+"):(state"+label+
				"'=FAIL)&(step'=step+1);";
		return out;
	}

	@Override
	public String generateDTMC() 
	{

		String model = "\ndtmc";

		String states = "\n";
		String transitions = "\n";
		String declarations = "\n const int SUCC = 1;\n const int FAIL = 2;";

		String rewards = "\nrewards\n";

		ArrayList<Description> re = network.renewableEnergy();
		ArrayList<Description> buildings = network.buildings();

		int step = 0;
		
		String buildingSuccPrec = "";
		for(Description b : buildings)
		{
			rewards +="(state"+b.getLabel()+"=SUCC) : cost"+b.getLabel()+";\n";
			buildingSuccPrec += "(state"+b.getLabel()+"=SUCC)&";
			declarations+="\n const double prob"+b.getLabel()+";";
			declarations+="\n const double cost"+b.getLabel()+";";
			states += "\nstate"+b.getLabel()+": [0..2] init 0;";
			transitions += transition(b.getLabel(),step);
			step++;		
		}

		rewards+="\nendrewards\n";

		buildingSuccPrec = buildingSuccPrec.substring(0,buildingSuccPrec.length()-1);

		//done transition
		String doneTrans = "\n [done](step="+step+") -> (done'=true)&(succ'="+buildingSuccPrec+");";


		model +=    declarations+"\nmodule network"+this.getLabel()+"\n"
				+"\nstep : [0.."+step+"] init 0;"
				+"\n done  : bool init false;"
				+"\n succ  : bool init false;"
				+"\n"+states+"\n\n"+transitions+doneTrans+"\nendmodule\n"+rewards;
		
		return model;
	}

	@Override
	public String getReliabilityParameter() 
	{
		// TODO Auto-generated method stub
		return this.probParameterForEntity(this.getLabel());
	}

}
