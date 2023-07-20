package _patterns;

import _networkdescription.Entity;
import _parametricmodelprops.ParallelModel;
import _parametricmodelprops.PrismParametricModel;
import _parametricmodelprops.PrismParametricProperty;
import _parametricmodelprops.PropertyType;
import _parametricmodelprops.SequenceModel;

public class Parallel extends Pattern
{

	
public Entity[] par;
	
	
	@Override
	public String toString()
	{
		
		String o = "";
		for(Entity e : this.par)
		{
			o+=e.getLabel()+" ";
		}
		
		return "PAR("+o+")";
	}
	
	
	
	public Parallel(String label,Entity[] par) 
	{
		super(label);
		this.par = par;		
	}

	@Override
	public PrismParametricModel generateModel() {
		
		return new ParallelModel(this);
	}

	public PrismParametricProperty getProperty() 
	{				
		PrismParametricProperty prop =  new PrismParametricProperty("PARProperty_"+par.length);
		
		//P=? [ F (succ=true) ]
		prop.pctlFormulas.put(PropertyType.prob,"P=? [ F ("+ParallelModel.successVariable()+"=true) ]");
		
		//R=? [ F ((state=DONE)) ]
		prop.pctlFormulas.put(PropertyType.cost,"R=?[F(("+SequenceModel.stateName()+"="+SequenceModel.doneStateConstant()+"))]");
		
		return prop;
	}

//-------------

	@Override
	public PrismParametricProperty generateProperty()
	{
		return this.getProperty();
	}



	@Override
	public Entity[] getEntities() 
	{		
		return this.par;
	}
}
