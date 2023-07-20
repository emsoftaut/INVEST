package _parametricverify;

import core.Description;
import core.Poset;
import core.PosetMorphism;

public class ParametricGamma  extends PosetMorphism<Description,ParametricTask> {


	ParametricAlpha alpha; 
	ParametricBeta beta;


	public ParametricGamma(Poset<Description> config, ParametricAlpha alpha, ParametricBeta beta)
	{
		this.domain = config;
		this.alpha = alpha;
		this.beta = beta;
		this.computeMap();
		this.inferRangeOrdering();
	}

	@Override
	protected void computeMap() {
		for(Description d : this.domain)
		{						
			ParametricTask task = new ParametricTask(this.alpha.get(d), this.beta.get(d));
			this.map.put(d, task);		
		}
	}

	@Override
	public void update(Description d) 
	{		
		ParametricTask task = new ParametricTask(this.alpha.get(d), this.beta.get(d));
		this.map.put(d, task);
	}

}
