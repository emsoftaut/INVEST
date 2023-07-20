package _patterns;


import _networkdescription.Entity;
import _parametricmodelprops.ParallelModel;
import _parametricmodelprops.PrismParametricModel;
import _parametricmodelprops.PrismParametricProperty;
import _parametricmodelprops.ProbFallBackModel;
import _parametricmodelprops.PropertyType;
import _parametricmodelprops.SequenceModel;

public class ProbFallBack extends Pattern
{

	

	
	public Entity[] choice;
	public Double[] chProb;	
	public Entity fallback;
	
	
	
	public ProbFallBack(String label) 
	{
		super(label);
		// TODO Auto-generated constructor stub
	}

	public ProbFallBack(String label, Entity[] entities,Double[] chProb) 
	{
		super(label);
		this.chProb = chProb;
		this.choice = new Entity[entities.length-1];
		for(int i = 0;i<entities.length-1;i++)
		{
			this.choice[i] = entities[i];
		}
		
		this.fallback = entities[entities.length-1];
	}

	@Override
	public PrismParametricModel generateModel() 
	{
		// TODO Auto-generated method stub
		return new ProbFallBackModel(this);
	}

	@Override
	public PrismParametricProperty generateProperty() 
	{
	PrismParametricProperty prop =  new PrismParametricProperty("PROBFALLBACKProperty_"+this.choice.length);
		
		//P=? [ F (succ=true) ]
		prop.pctlFormulas.put(PropertyType.prob,"P=? [ F ("+ParallelModel.successVariable()+"=true) ]");
		
		//R=? [ F ((state=DONE)) ]
		prop.pctlFormulas.put(PropertyType.cost,"R=?[F(("+SequenceModel.stateName()+"="+SequenceModel.doneStateConstant()+"))]");
		
		return prop;	
	}

	@Override
	public Entity[] getEntities() 
	{
		Entity[] entities = new Entity[this.choice.length+1];
		for(int i = 0;i<this.choice.length;i++)
		{
			entities[i] = this.choice[i];
		}
		
		entities[this.choice.length] = this.fallback;
		return entities;
	}

}
