package _casestudies;

import _networkdescription.Building;
import _networkdescription.Entity;
import _networkdescription.Network;
import _networkdescription.Renewable;
import _patterns.Parallel;
import _patterns.ProbChoice;
import _patterns.Sequence;
import core.Description;
import core.Poset;

public class Figure1Network 
{
	//network description object
	static public Network network;

	//builings that comprise the super block
	static public Building b1 = new Building("b1");
	static public Building b2 = new Building("b2");
	static public Building b3 = new Building("b3");

	//reusable energy producers supplying the superblock
	static public Renewable wf = new Renewable("wf");
	static public Renewable pv = new Renewable("pv");	

	static public Network initialNetworkConfig()
	{

		
		Poset<Description> config = new Poset<Description>();
		b1.pattern = new Sequence(b1.getLabel(),new Entity[] {wf,pv});
		b2.pattern = new Parallel(b2.getLabel(),new Entity[] {b3,pv});
		b3.pattern = new ProbChoice(b3.getLabel(),new Entity[] {b1,wf},new Double[] {0.6,0.4});
		//b3.pattern = probChoice;
		
		network = new Network("net",config);
		config.add(b1,b2,b3,wf,pv,network);

		//set up dependencies in the network.
		network.addEnergyFlowFromToDependency(wf, b1);
		network.addEnergyFlowFromToDependency(pv, b1);
		
		network.addEnergyFlowFromToDependency(b3, b2);
		network.addEnergyFlowFromToDependency(pv, b2);
		
		network.addEnergyFlowFromToDependency(b1, b3);
		network.addEnergyFlowFromToDependency(wf, b3);
		
		config.lessThan(wf, network);
		config.lessThan(pv, network);
		config.lessThan(b1, network);
		config.lessThan(b2, network);
		config.lessThan(b3, network);
				
		
		return network;
		
	}

}
