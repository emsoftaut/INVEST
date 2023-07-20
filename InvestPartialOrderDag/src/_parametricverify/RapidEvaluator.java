package _parametricverify;


import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import _parametricmodelprops.PrismParametricResult;
import _parametricmodelprops.PropertyType;
import core.Description;
import core.Poset;


public class RapidEvaluator 
{
	public ParametricRho rho;	
	public ParametricGamma gamma;
	public Assignment record;
//	DecimalFormat df = new DecimalFormat("#.####");

	//------------------------------------------------------------------------------------
	public int numberofVariables()
	{	
		return this.record.numberOfVariables();
	}
	//------------------------------------------------------------------------------------
	public RapidEvaluator(ParametricGamma gamma,ParametricRho rho)
	{
		this(gamma,rho,new HashMap<String,Double>());
	}
	//------------------------------------------------------------------------------------
	private RapidEvaluator(ParametricGamma gamma,ParametricRho rho,HashMap<String,Double> init)
	{		
		
		this.gamma = gamma;
		this.rho = rho;	
		
		System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
		this.record = new Assignment(24);
	}
	//------------------------------------------------------------------------------------
	private String[] splitEquation(String equation)
	{
		String[] sides = new String[2];
		int equalsindex = equation.indexOf('=');
		sides[0] = equation.substring(0, equalsindex).trim().stripLeading();
		sides[1] = equation.substring(equalsindex+1).trim().stripLeading();
		return sides;
	}
	
	
	//------------------------------------------------------------------------------------	
		private HashMap<String,Double> selectedEvaluateDescription(Poset<Description> u,
				HashMap<String,Double> previous)
		{
			
		//	System.out.println(previous);
			Iterator<Description> it = u.iterator();		
			while(it.hasNext())
			{

				//PrismParametricResult r = it.next();
				Description d = it.next();
				ParametricTask tx = gamma.get(d);
				PrismParametricResult r =rho.get(tx);
				
			//	System.out.println(r);
				for(PropertyType pt : r.equations.keySet())
				{

					String equation = r.equations.get(pt);
					String lhs = this.splitEquation(equation)[0];
					String rhs = this.splitEquation(equation)[1];									
					Double result = this.evaluate(rhs, previous);
					previous.put(lhs, result);				
				}			
			}	
			//System.out.println(previous);
			return previous;
		}
	
	
	//------------------------------------------------------------------------------------	
//	private HashMap<String,Double> selectedEvaluate(Poset<PrismParametricResult> u,
//			HashMap<String,Double> previous)
//	{
//		
//	//	System.out.println(previous);
//		Iterator<PrismParametricResult> it = u.iterator();		
//		while(it.hasNext())
//		{
//
//			PrismParametricResult r = it.next();
//		//	System.out.println(r);
//			for(PropertyType pt : r.equations.keySet())
//			{
//
//				String equation = r.equations.get(pt);
//				String lhs = this.splitEquation(equation)[0];
//				String rhs = this.splitEquation(equation)[1];									
//				Double result = this.evaluate(rhs, previous);
//				previous.put(lhs, result);				
//			}			
//		}	
//		//System.out.println(previous);
//		return previous;
//	}
	//------------------------------------------------------------------------------------
	public HashMap<String,Double> completeEvaluate(HashMap<String,Double> values)
	{
		return this.selectedEvaluateDescription(this.gamma.domain,values);
	}
	//------------------------------------------------------------------------------------
	private Double evaluate(String expression, HashMap<String,Double> values)
	{
		expression = expression+";";		    		
		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("graal.js");


		double result = Double.NaN;
		try {
			for (Entry<String, Double> entry : values.entrySet()) {
				String variable = entry.getKey();
				double value = entry.getValue();
				String initialiseVariableString = variable + " = " + value + ";\n";
				expression =  initialiseVariableString + expression;
			}			
			result = (double)scriptEngine.eval(expression);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}
	//--------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * When given the entire X network to evaluate, and empty initial records, this method is a complete evaluation
	 * 
	 * When given an updated U' upset of the network, and existing evaluation records, this method is the
	 * rapid evaluation method.
	 * 
	 * returns the result records computed from the repo.
	 * 
	 * 
	 * */
//	public EvaluationRecords rapidEvaluation(Poset<PrismParametricResult> X,EvaluationRecords initial)
//	{
//		EvaluationRecords results = new EvaluationRecords(initial.duration);
//
//		for(int t = 0;t<initial.duration;t++)
//		{						
//			HashMap<String,Double> result = this.selectedEvaluate(X,initial.getRecord(t));
//			//add record to the results stream
//			//System.out.println(result);
//			results.setRecord(result,t);		
//		}
//
//		return results;
//	}
	public Assignment rapidEvaluate(Poset<Description> X,Assignment initial)
	{
		Assignment results = new Assignment(initial.duration);

		for(int t = 0;t<initial.duration;t++)
		{						
			HashMap<String,Double> result = this.selectedEvaluateDescription(X,initial.getRecord(t));
			//add record to the results stream
			//System.out.println(result);
			results.setRecord(result,t);		
		}

		return results;
	}
	
}
