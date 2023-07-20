package _patterns;


import _networkdescription.Entity;
import _parametricmodelprops.PrismParametricModel;
import _parametricmodelprops.PrismParametricProperty;
import _parametricmodelprops.PropertyType;
import _parametricmodelprops.SequenceModel;

public class Sequence extends Pattern 
{

	
	public Entity[] seq;
	
	
	@Override
	public String toString()
	{
		
		String o = "";
		for(Entity e : this.seq)
		{
			o+=e.getLabel()+" ";
		}
		
		return "SEQ("+o+")";
	}
	

	
	
	public Sequence(String label, Entity[] seq) 
	{		
		super(label);
		this.seq = seq;		
	}
	





	@Override
	public PrismParametricModel generateModel() 
	{	
		return new SequenceModel(this);
	}





	public PrismParametricProperty generateProperty()
	{
		return this.getProperty();
	}
	
	public PrismParametricProperty getProperty() 
	{	
		
		SequenceModel model = (SequenceModel) this.generateModel();
		PrismParametricProperty prop =  new PrismParametricProperty("SEQProperty_"+seq.length);
		
		//P=? [ F ((state=SUCC1)|(state=SUCC2)|(state=SUCC3)) ]
		prop.pctlFormulas.put(PropertyType.prob,"P=?[F"+model.successInvokeProposition()+"]");
		
		//R=? [ F ((state=DONE)) ]
		prop.pctlFormulas.put(PropertyType.cost,"R=?[F(("+SequenceModel.stateName()+"="+SequenceModel.doneStateConstant()+"))]");
		
		return prop;
	}




	@Override
	public Entity[] getEntities() {
		// TODO Auto-generated method stub
		return this.seq;
	}
}
