package _patterns;

import _parametricmodelprops.RenewableModel;
import _parametricmodelprops.PropertyType;
import _networkdescription.Entity;
import _parametricmodelprops.PrismParametricModel;
import _parametricmodelprops.PrismParametricProperty;

public class EnergyProducer extends Pattern {

	
	
	public String toString()
	{
		return this.label+"(EnergyProductionPattern)";
	}
	
	
	public EnergyProducer(String label) {
		super(label);
		
	}

	public PrismParametricProperty generateProperty()
	{
		return this.getProperty();
	}
	
	public PrismParametricProperty getProperty() 
	{
		PrismParametricProperty prop =  new PrismParametricProperty("PCTL"+this.label);
		
		//P=? [ F ((state=SUCC1)|(state=SUCC2)|(state=SUCC3)) ]
		prop.pctlFormulas.put(PropertyType.prob,"P=?[F(state=SUCC)]");
		
		//R=? [ F ((state=DONE)) ]
		prop.pctlFormulas.put(PropertyType.cost,"R=?[F(state=DONE)]");
		
		return prop;
	}
	
	@Override
	public PrismParametricModel generateModel() {
		// TODO Auto-generated method stub
		return new RenewableModel(this.label);
	}


	@Override
	public Entity[] getEntities() 
	{		
		return new Entity[0];
	}


}
