package _parametricverify;

import core.Result;
import core.Task;
import core.Verifier;
import param.ParamResult;
import parser.ast.ModulesFile;
import parser.ast.PropertiesFile;
import parser.ast.Property;
import prism.Prism;
import prism.PrismException;
import prism.PrismFileLog;
import prism.PrismLog;
import java.util.Collection;

import _parametricmodelprops.NetworkModel;
import _parametricmodelprops.ParallelModel;
import _parametricmodelprops.PrismParametricModel;
import _parametricmodelprops.PrismParametricProperty;
import _parametricmodelprops.PrismParametricResult;
import _parametricmodelprops.ProbChoiceModel;
import _parametricmodelprops.ProbFallBackModel;
import _parametricmodelprops.SequenceModel;
import _parametricmodelprops.PropertyType;

public class ParametricModelChecker extends Verifier
{
	//------------------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Uses pattern-aware Probabilistic Model Checking to short cut
	 * verification of prob and cost for known energy usage patterns 
	 * 
	 * */
	private PrismParametricResult patternAwareVerification(PrismParametricModel model,PrismParametricProperty property)
	{
		PrismParametricResult r = new PrismParametricResult(model.getLabel());


		if(model instanceof SequenceModel)
		{					
			r = model.getResult(((SequenceModel)model).length());	
			return r;
		}

		if(model instanceof ParallelModel)
		{
			r = model.getResult(((ParallelModel)model).length());
			//r = invokePrism(model,property);
			return r;
		}

		if(model instanceof ProbChoiceModel)
		{
			r = model.getResult(((ProbChoiceModel)model).length());			
			return r;	
		}

		if(model instanceof ProbFallBackModel)
		{
			r = model.getResult(((ProbFallBackModel)model).length());			
			return r;
		}
		
		if(model instanceof NetworkModel)
		{
		
			r = model.getResult(((NetworkModel)model).length());
			//r = invokePrism(model,property);
			//System.out.println(r);
			return r;
		}

		//not a recognised pattern -- perform model checking
		r = invokePrism(model,property);

		return r;
	}

	//------------------------------------------------------------------------------------------------------------------------
	private PrismParametricResult invokePrism(PrismParametricModel model,PrismParametricProperty property)
	{
		String modeltext = model.generateDTMC();

		String[] paramNames = model.paramNames();
		String[] paramLowerBounds = model.paramLowerBounds();
		String[] paramUpperBounds = model.paramUpperBounds();

		PrismParametricResult result = new PrismParametricResult(model.getLabel());

		try {

			//we cannot have prism as instance vars since this leads to funny crashes.
			Prism prism;
			PrismLog prismMainLog;

			prismMainLog = new PrismFileLog("prismLogFile.txt");		
			prism = new Prism(prismMainLog);	

			prism.initialise();

			ModulesFile modulesFile = prism.parseModelString(modeltext);
			prism.loadPRISMModel(modulesFile);
			prism.setPRISMModelConstants(null, true);
			prism.setDoProbChecks(false);

			Collection<PropertyType> propertyTypeKeys = property.pctlFormulas.keySet();

			for(PropertyType key : propertyTypeKeys)
			{	
				String pctlFormula =  property.pctlFormulas.get(key);
				PropertiesFile propertiesFile = prism.parsePropertiesString(modulesFile,pctlFormula);	
				Property pp = propertiesFile.getPropertyObject(0);
				prism.Result fromPrismResult = prism.modelCheckParametric(propertiesFile,
						pp, paramNames, paramLowerBounds, paramUpperBounds);
				ParamResult r =  (ParamResult) fromPrismResult.getResult();



				result.equations.put(key, result.createEquation(key,r.toString()));			
			}
			prism.closeDown();
		} catch (PrismException e) 
		{				
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Result modelCheck(Task task) 
	{
		PrismParametricModel model = (PrismParametricModel) task.m;
		PrismParametricProperty property = (PrismParametricProperty) task.p;
		return patternAwareVerification(model,property);

	}

}