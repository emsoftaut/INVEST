package _casestudies;

import java.util.HashMap;

import _networkdescription.Building;
import _networkdescription.Entity;
import _networkdescription.Network;
import _networkdescription.Renewable;
import _parametricmodelprops.PrismParametricResult;
import _parametricverify.Assignment;
import _parametricverify.ParametricModelChecker;
import _parametricverify.ParametricTask;
import _patterns.Parallel;
import _patterns.Sequence;
import core.Description;
import core.Poset;

public class ScaleExperiment {



	long cvDurAverage = 0;
	long ivDurAverage = 0;
	long cevalDurAverage = 0;
	long ievalDurAverage = 0;

	int duration = 24;	
	int step = 0;
	int  numberOfVariables= 0;	
	long start=0;
	long stop = 0;
	long cvDur = 0;//compositional verify
	long ivDur = 0;//incremental verify
	long cevalDur = 0;//complete eval
	long ievalDur= 0;//incremental eval

	Network network = Figure1Network.initialNetworkConfig();
	//applying ReInvest to the network performs compositional verification
	ParametricModelChecker pmc = new ParametricModelChecker();
	ReInvestFramework reInvest = null;
	//assign value stream to all environment vars, only constant.
	Double[] probValues=TimeSeries.generateBase(duration, 0.9);
	Double[] costValues=TimeSeries.generateBase(duration, 1.0);				
	Assignment records = new Assignment(duration);		

	String console = "";
	String transcript = "";

	private int nBuildings=0;
	private int nRenewables=0;
	private int elements = 0;


	public void resetDurationMeasures()
	{


		this.start=0;
		this.stop = 0;
		this.cvDur = 0;//compositional verify
		this.ivDur = 0;//incremental verify
		this.cevalDur = 0;//complete eval
		this.ievalDur= 0;//incremental eval
	}

	public ScaleExperiment()
	{

		this.resetDurationMeasures();
		//initialise experiments
		start=System.currentTimeMillis();
		this.reInvest = new ReInvestFramework(pmc,network);
		stop = start=System.currentTimeMillis();
		cvDur = stop-start;
		System.out.println("Scale Experiments.");
		System.out.println("Starting with initial network config and CV completed");
		System.out.println(reInvest.network.config.toString());


		//assign value stream to all environment vars, only constant.
		Double[] probValues=TimeSeries.generateBase(duration, 0.9);
		Double[] costValues=TimeSeries.generateBase(duration, 1.0);				
		this.records = new Assignment(duration);		
		this.records.addStream("ppv", probValues);
		this.records.addStream("pwf", probValues);
		this.records.addStream("b3ch1", TimeSeries.generateBase(duration, 0.6));
		this.records.addStream("b3ch2", TimeSeries.generateBase(duration, 0.4));
		this.records.addStream("cpv", costValues);
		this.records.addStream("cwf", costValues);

		
		//Evaluate entire network.
//		Poset<PrismParametricResult> changedExpressions = reInvest.rho.range;
		start = System.currentTimeMillis();
		reInvest.rapidEvaluator.rapidEvaluate(reInvest.gamma.domain, records);
		stop = System.currentTimeMillis();
		cevalDur = stop-start; 

		numberOfVariables = records.numberOfVariables();
		nBuildings = reInvest.network.buildings().size();
		nRenewables = reInvest.network.renewableEnergy().size();

		System.out.println("Completed Initialisation");
		String console = "step = "+step+" vars = "+numberOfVariables+" elements = "+elements+" nBuildings = "+nBuildings+" nRenewables = "+nRenewables;
		console+=" cvDur = "+cvDur+"ms ivDur = "+ivDur+"ms cevalDur = "+cevalDur+"ms ievalDur = "+ievalDur+"ms";
		System.out.println(console);

		transcript+=console+"\n";

	}




	public void addMeasureEnergyProvider(Renewable renewable)
	{
		this.resetDurationMeasures();
		//records = new EvaluationRecords(duration);
		step++;
		Renewable ep = renewable;
		String provider = ep.getLabel();

		network.config.add(ep);

		start=System.currentTimeMillis();
		reInvest.alpha.computeMap(ep);		
		reInvest.beta.computeMap(ep);
		Poset<Description> needsVerification = reInvest.alpha.domain.upset(ep);		
		for(Description d : needsVerification)
		{
			reInvest.gamma.update(d);
		}
		reInvest.gamma.inferRangeOrdering();

		//now reverify those tasks that have been affected.
		reInvest.rho.computeMap(reInvest.gamma.image(needsVerification));
		stop = System.currentTimeMillis();
		ivDur = stop-start;

		//update record with environ vars for new provider.
		records.addStream("p"+provider, probValues);		
		records.addStream("c"+provider, costValues);

		//increment re-evaluation on changes.
		ParametricTask task = reInvest.gamma.get(ep);
		//Poset<PrismParametricResult> changedExpressions = reInvest.rho.image(task);
		start = System.currentTimeMillis();
		reInvest.rapidEvaluator.rapidEvaluate(needsVerification, records);
		stop = System.currentTimeMillis();
		ievalDur = stop-start; 

		//complete re-evaluation of the system of equations.
		//Poset<PrismParametricResult> allExpressions = reInvest.rho.range;
		start = System.currentTimeMillis();
		reInvest.rapidEvaluator.rapidEvaluate(reInvest.gamma.domain, records);
		stop = System.currentTimeMillis();
		cevalDur = stop-start; 

		//now do compositional verification
		start = System.currentTimeMillis();
		this.reInvest = new ReInvestFramework(pmc,network);
		stop = System.currentTimeMillis();
		cvDur = stop-start;

		//now output the transcript odf results.
		numberOfVariables = records.numberOfVariables();
		nBuildings = reInvest.network.buildings().size();
		nRenewables = reInvest.network.renewableEnergy().size();
		elements = nBuildings+nRenewables;
		//console = "Adding Energy Provider: "+provider;
		console = ""+step+";"+numberOfVariables+";"+elements+";"+nBuildings+";"+nRenewables;
		console+=";"+cvDur+";"+ivDur+";"+cevalDur+";"+ievalDur+"";
		System.out.println(console);transcript+=console+"\n";
	}


	public void addMeasureBuilding(Building building)
	{

		this.resetDurationMeasures();
		step++;
		Building b = building;
		String buildingName = b.getLabel();

		for(Entity x : b.pattern.getEntities())
		{
			network.config.lessThan(x, b);
		}

		network.config.add(b);
		network.config.lessThan(b, Figure1Network.network);

	
		//incremental verification
		start=System.currentTimeMillis();
		reInvest.alpha.computeMap(b);	
		reInvest.beta.computeMap(b);
		Poset<Description> needsVerification = reInvest.alpha.domain.upset(b);	
		ParametricTask[] tasks = new ParametricTask[needsVerification.toArrayList().size()];
		int i = 0;
		for(Description d : needsVerification)
		{
			reInvest.gamma.update(d);
			tasks[i]=reInvest.gamma.get(d);
			i++;
		}
		reInvest.gamma.inferRangeOrdering();

		//now reverify those tasks that have been affected.
		reInvest.rho.computeMap((reInvest.gamma.range.subPoset(tasks)));

		stop = System.currentTimeMillis();
		ivDur = stop-start;

		//update record with environ vars for new provider.
		records.addStream("prob"+buildingName, probValues);		
		records.addStream("cost"+buildingName, costValues);

		//increment re-evaluation on changes.		
		Poset<ParametricTask> dom = reInvest.gamma.range.subPoset(tasks);
			
		//Poset<PrismParametricResult> changedExpressions = reInvest.rho.image(dom);
		start = System.currentTimeMillis();
		reInvest.rapidEvaluator.rapidEvaluate(needsVerification, records);
		stop = System.currentTimeMillis();
		ievalDur = stop-start; 

		//complete re-evaluation of the system of equations.
		//Poset<PrismParametricResult> allExpressions = reInvest.rho.range;
		start = System.currentTimeMillis();
		reInvest.rapidEvaluator.rapidEvaluate(reInvest.gamma.domain, records);
		stop = System.currentTimeMillis();
		cevalDur = stop-start; 

		//now reverify those tasks that have been affected.		
		//now do compositional verification		
		start = System.currentTimeMillis();
		this.reInvest = new ReInvestFramework(pmc,network);
		stop = System.currentTimeMillis();
		cvDur = stop-start;

		//now output the transcript odf results.
		numberOfVariables = records.numberOfVariables();
		nBuildings = reInvest.network.buildings().size();		
		nRenewables = reInvest.network.renewableEnergy().size();
		elements = nBuildings+nRenewables;
		//console = "Adding a Building: "+buildingName;		
		console = ""+step+";"+numberOfVariables+";"+elements+";"+nBuildings+";"+nRenewables;
		console +=";"+cvDur+";"+ivDur+";"+cevalDur+";"+ievalDur+"";
		System.out.println(console);		
		//transcript+=console+"\n";
	}


	public static void main(String[] args)
	{

		int nTimes = 5;

		for(int t = 1;t<=nTimes;t++) 
		{
			//initialisation
			ScaleExperiment se = new ScaleExperiment();
			String console = "";
			console += "\nstep;"+"nvars;"+"elements;"+"nBuildings;"+" nRenewables;";
			console+=" cvDur;"+"ivDur;"+"cevalDur;"+"ievalDur;";
			System.out.println(console);

			//step 1
			Renewable ren1 = new Renewable("ren1");
			se.addMeasureEnergyProvider(ren1);

			//step 2
			Building bu1 = new Building("bu1");
			bu1.pattern = new Sequence(bu1.getLabel(),new Entity[] {Figure1Network.wf,ren1,Figure1Network.pv});		
			se.addMeasureBuilding(bu1);


			//step 3
			Renewable ren2 = new Renewable("ren2");
			se.addMeasureEnergyProvider(ren2);

			//step 4
			Building bu2 = new Building("bu2");
			bu2.pattern = new Parallel(bu2.getLabel(),new Entity[] {ren1,ren2,Figure1Network.wf});		
			se.addMeasureBuilding(bu2);

			//step 5
			Renewable ren3 = new Renewable("ren3");
			se.addMeasureEnergyProvider(ren3);

			//step 6
			Building bu3 = new Building("bu3");
			bu3.pattern = new Sequence(bu3.getLabel(),new Entity[] {ren1,ren2,ren3});		
			se.addMeasureBuilding(bu3);


			//step 7
			Renewable ren4 = new Renewable("ren4");
			se.addMeasureEnergyProvider(ren4);

			//step 8
			Building bu4 = new Building("bu4");
			bu4.pattern = new Sequence(bu4.getLabel(),new Entity[] {bu3,bu1,ren1});		
			se.addMeasureBuilding(bu4);


			//step 9
			Renewable ren5 = new Renewable("ren5");
			se.addMeasureEnergyProvider(ren5);

			//step 10
			Building bu5 = new Building("bu5");
			bu5.pattern = new Parallel(bu5.getLabel(),new Entity[] {bu3,bu2,ren2});		
			se.addMeasureBuilding(bu5);



			//step 9
			Renewable ren6 = new Renewable("ren6");
			se.addMeasureEnergyProvider(ren6);

			//step 10
			Building bu6 = new Building("bu6");
			bu6.pattern = new Parallel(bu6.getLabel(),new Entity[] {bu5,bu2,ren6});		
			se.addMeasureBuilding(bu6);


			//step 11
			Renewable ren7 = new Renewable("ren7");
			se.addMeasureEnergyProvider(ren7);

			//step 12
			Building bu7 = new Building("bu7");
			bu7.pattern = new Sequence(bu7.getLabel(),new Entity[] {ren1,ren2,ren3});		
			se.addMeasureBuilding(bu7);


			//step 11
			Renewable ren8 = new Renewable("ren8");
			se.addMeasureEnergyProvider(ren8);

			//step 12
			Building bu9 = new Building("bu9");
			bu9.pattern = new Sequence(bu9.getLabel(),new Entity[] {ren1,ren2,ren5});		
			se.addMeasureBuilding(bu9);


			for(int i = 9;i<=17;i++)
			{
				//	System.out.println("i = "+i);
				//step 11
				Renewable ren = new Renewable("ren"+i);
				se.addMeasureEnergyProvider(ren);

				//step 12
				Building bu = new Building("bu"+(i+1));
				bu.pattern = new Sequence(bu.getLabel(),new Entity[] {ren1,ren2,ren5});		
				se.addMeasureBuilding(bu);

			}


		}
	}

}
