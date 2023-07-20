package _networkdescription;

import java.util.ArrayList;
import java.util.Iterator;

import _parametricmodelprops.NetworkModel;
import _parametricmodelprops.PrismParametricModel;
import _parametricmodelprops.PrismParametricProperty;
import _parametricmodelprops.PropertyType;
import _patterns.Pattern;
import core.Description;
import core.Poset;

public class Network  extends Entity
{

	//network class handles the updating of the network configuration, a partial order
	public Poset<Description> config;

	//--------------------------------------------------------------------------------------------------------------
	public void addEntity(Entity x)
	{
		if(!this.config.contains(x))
		{
			this.config.lessThan(x, this);
		}
	}
	//--------------------------------------------------------------------------------------------------------------
	public void addEnergyFlowFromToDependency(Entity x,Entity y)
	{	
		this.config.lessThan(x, y);
	}
	//--------------------------------------------------------------------------------------------------------------
	public void removeEnergyFlowFromToDependency(Entity x,Entity y)
	{
		this.config.removeLessThan(x, y);
	}
	//--------------------------------------------------------------------------------------------------------------
	public void setPattern(Building b,Pattern pattern)
	{


		//first, remove all existing building pattern dependencies in the network, 
		//in the case of replacing a building pattern
		if(b.pattern != null)
		{
			Entity[] entities = b.pattern.getEntities();
			for(Entity e : entities)
			{
				this.removeEnergyFlowFromToDependency(e, b);
			}
		}

		b.pattern = pattern;

		Entity[] entities = pattern.getEntities();
		for(Entity e : entities)
		{
			this.addEntity(e);
			this.addEnergyFlowFromToDependency(e, b);

		}

	}
	//--------------------------------------------------------------------------------------------------------------
	public ArrayList<Description> renewableEnergy()
	{
		ArrayList<Description> renewableEnergy = new ArrayList<Description>();


		Iterator<Description> it = this.config.iterator();

		while(it.hasNext())
		{
			Description d = it.next();

			if(d instanceof Renewable)
			{
				renewableEnergy.add(d);
			}						
		}

		return renewableEnergy;
	}


	//--------------------------------------------------------------------------------------------------------------
	public ArrayList<Description> buildings()
	{
		ArrayList<Description> buildings = new ArrayList<Description>();


		Iterator<Description> it = this.config.iterator();

		while(it.hasNext())
		{
			Description d = it.next();

			if(d instanceof Building)
			{
				buildings.add(d);
			}						
		}

		return buildings;
	}
	//--------------------------------------------------------------------------------------------------------------
	public Network(String label,Poset<Description> config) 
	{
		super(label);
		this.config = config;	
	}

	@Override
	public PrismParametricModel generateModel() 
	{		
		return new NetworkModel(this.getLabel(),this);
	}

	@Override
	public PrismParametricProperty generateProperty() {
		// TODO Auto-generated method stub
		PrismParametricProperty prop = new PrismParametricProperty(this.getLabel());		
		prop.pctlFormulas.put(PropertyType.prob, "P=?[F(succ)&(done)]");
		prop.pctlFormulas.put(PropertyType.cost, "R=?[F(done)]");

		return prop;
	}
	public void addBuilding(Building bPrime) 
	{

		this.addEntity(bPrime);
		// TODO Auto-generated method stub

	}





}
