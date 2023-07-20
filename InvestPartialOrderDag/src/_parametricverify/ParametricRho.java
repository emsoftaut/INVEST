package _parametricverify;

import core.Poset;
import core.PosetMorphism;
import core.Result;
import core.Verifier;
import java.util.HashMap;
import java.util.Iterator;

import _parametricmodelprops.PrismParametricResult;


public class ParametricRho extends PosetMorphism<ParametricTask,PrismParametricResult>
{

	private Verifier verifier;
	public ParametricGamma gamma;



	public ParametricRho(ParametricGamma gamma,Verifier verifier)
	{
		super();
		this.gamma = gamma;
		this.domain = gamma.range;
		this.verifier = verifier;
		//this.verifier.setRepo(this);

		this.computeMap();
	}

	@Override
	protected void computeMap() 
	{

		this.map = new HashMap<ParametricTask,PrismParametricResult>();
		this.computeMap(this.domain);
		this.inferRangeOrdering();
	}

	public void computeMap(Iterator<ParametricTask> it)
	{
		while(it.hasNext())
		{
			ParametricTask task = it.next();
			Result result = verifier.modelCheck(task);
			PrismParametricResult pResult = (PrismParametricResult) result;
			this.map.put(task, (PrismParametricResult) pResult);
		}
		this.inferRangeOrdering();
	}
	
	public void computeMap(Poset<ParametricTask> subsetOfDomain) 
	{
		Iterator<ParametricTask> it = subsetOfDomain.iterator();

		while(it.hasNext())
		{
			ParametricTask task = it.next();
			Result result = verifier.modelCheck(task);
			PrismParametricResult pResult = (PrismParametricResult) result;
			this.map.put(task, (PrismParametricResult) pResult);
		}
		this.inferRangeOrdering();
	}

	@Override
	public void update(ParametricTask x) {
		// TODO Auto-generated method stub

	}

}
