package _patterns;


import _networkdescription.Entity;
import _parametricmodelprops.ParallelModel;
import _parametricmodelprops.PrismParametricModel;
import _parametricmodelprops.PrismParametricProperty;
import _parametricmodelprops.ProbChoiceModel;
import _parametricmodelprops.PropertyType;
import _parametricmodelprops.SequenceModel;

public class ProbChoice extends Pattern
{

	public Entity[] choice;
	public Double[] chProb;
	
	
	public ProbChoice(String label,Entity[] choice,Double[] chProb) 
	{
		super(label);
		this.choice = choice;
		this.chProb = chProb;
	}

	@Override
	public PrismParametricModel generateModel() 
	{	
		return new ProbChoiceModel(this);
	}

	@Override
	public PrismParametricProperty generateProperty() 
	{
	PrismParametricProperty prop =  new PrismParametricProperty("PROBProperty_"+this.choice.length);
		
		//P=? [ F (succ=true) ]
		prop.pctlFormulas.put(PropertyType.prob,"P=? [ F ("+ParallelModel.successVariable()+"=true) ]");
		
		//R=? [ F ((state=DONE)) ]
		prop.pctlFormulas.put(PropertyType.cost,"R=?[F(("+SequenceModel.stateName()+"="+SequenceModel.doneStateConstant()+"))]");
		
		return prop;	
	}
	
	

	@Override
	public String toString()
	{
		
		String o = "";
		for(int i =0;i< choice.length;i++)
		{
			
			o+= (this.chProb[i]+": "+this.choice[i]+" ");
			
		}
		
		return "PROB("+o+")";
	}

	@Override
	public Entity[] getEntities() {
		// TODO Auto-generated method stub
		return this.choice;
	}
	
	

}
