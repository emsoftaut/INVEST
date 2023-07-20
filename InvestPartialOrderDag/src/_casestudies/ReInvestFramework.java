package _casestudies;


import java.util.HashMap;
import java.util.Iterator;
import _networkdescription.*;
import _parametricmodelprops.PrismParametricResult;
import _parametricmodelprops.PropertyType;
import _parametricverify.*;
import _patterns.ProbFallBack;
import _patterns.Sequence;
import core.Description;
import core.Poset;



public class ReInvestFramework 
{

	public static final int duration = 24;

	public ParametricModelChecker pmc;	
	public RapidEvaluator rapidEvaluator;	
	public ParametricAlpha alpha;
	public ParametricBeta beta;
	public ParametricGamma gamma;
	public ParametricRho rho;

	//network description object
	public Network network;


	//--------------------------------------------------------------------------------------------------------------
	public void compositionalVerify(Iterator<ParametricTask> it)
	{
		this.rho.computeMap(it);
	}
	//--------------------------------------------------------------------------------------------------------------
	public ReInvestFramework(ParametricModelChecker pmc, Network network)
	{
		long start = System.currentTimeMillis();
		this.network = network;		
		this.pmc = pmc;

		//MAPS Entities (e.g. Building descriptions) to their models. 
		this.alpha = new ParametricAlpha(this.network.config);

		//MAPS Entities to their requirements (properties)
		this.beta = new ParametricBeta(this.network.config);

		//maps Entities to a verification task (Model and Property)
		this.gamma = new ParametricGamma(this.network.config,alpha,beta);

		//maps Verification Tasks to it's verification results (e.g. algebraic expressions)
		this.rho = new ParametricRho(gamma,pmc);

		//initialise the rapid eval
		this.rapidEvaluator = new RapidEvaluator(gamma,rho);

		long stop = System.currentTimeMillis();

		//	System.out.println("Initialisation of ReInvest took: "+(stop-start)+"ms");

	}
	//--------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * 
	 * Given some initial values for ground parameters, do a complete system evaluation
	 * 
	 * */
	public Assignment evaluate(Assignment initial)
	{
		System.out.println("Complete System Evaluation given initial environment values");
		long start = System.currentTimeMillis();
		Assignment results = new Assignment(initial.duration);

		for(int t = 0;t<initial.duration;t++)
		{			
			HashMap<String,Double> result = rapidEvaluator.completeEvaluate(initial.getRecord(t));
			results.setRecord(result,t);		
		}
		long stop = System.currentTimeMillis();

		System.out.println("Complete Evaluation took "+(stop-start)+" ms");

		return results;

	}	

	//--------------------------------------------------------------------------------------------------------------
	/**
	 * Scenario: Building bPrime is added to the network.
	 * Must reverify and determine impact to reliability and cost of the network
	 * and also return bPrime evaluation results.
	 * */
	private Poset<Description> changesAfterAddingBuildingToNetwork(String primeName,Network network)
	{

		//create the prime building entity to add to the network
		Building bPrime = new Building(primeName);

		//add the prime building to the network
		network.addBuilding(bPrime);

		//create a new pattern for the building, using existing entities within the network
		//from which to draw power.
		ProbFallBack pattern = new ProbFallBack(primeName,
				new Entity[] {Figure1Network.b1,Figure1Network.pv,Figure1Network.wf},
				new Double[] {0.3,0.7});


		System.out.println("Created building "+bPrime+" with pattern "+pattern);

		//set the building's pattern, updating the network dependencies 
		network.setPattern(bPrime, pattern);

		System.out.println("Network dependencies have been updated");
		System.out.println(network.config);

		//update the domain range of alpha+beta compute dependencies between domain (description) and range (models)
		alpha.computeMap(bPrime);		
		beta.computeMap(bPrime);

		//compute the upset of bPrime; those affected by the change.
		Poset<Description> needsVerification = this.alpha.domain.upset(bPrime);
		System.out.println("Incremental Verification to be performed on this poset "+needsVerification);

		//compute the new verification tasks.
		for(Description d : needsVerification)
		{
			this.gamma.update(d);
		}
		this.gamma.inferRangeOrdering();

		//now reverify those tasks that have been affected.
		Iterator<ParametricTask> it = this.gamma.image(needsVerification).iterator();
		this.compositionalVerify(it);
		//this.rho.computeMap(this.gamma.image(needsVerification));
		
		
		
		Poset<PrismParametricResult> X = this.rho.image(this.gamma.image(needsVerification));
		System.out.println("Resulting parametric model checking expressions for upset:");
		for(PrismParametricResult r : X)
		{
			System.out.println(r);
		}
		//System.out.println("Results of Incremental Verification "+X);

		//return the results, to input into rapid evaluation
		return 	needsVerification;

	}
	//--------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Compositional verification and evaluation of the building is completed. 
	 * 
	 * Given the networkResults, 
	 * simulation adds a new building to the network and then reverifies as needed (changesAfterAddingBuildingToNetwork)
	 * Then identifies the changes
	 * 
	 * 
	 * */
	public void addBuilding(Assignment networkResults)
	{
		System.out.println("-".repeat(10)+" added building bprime re-verification + re-evaluation "+"-".repeat(10));
		System.out.println(networkResults.projectValueStreams("probnet","costnet"));
		//first change scenario, adding the prime building
		long start = System.currentTimeMillis();
		Poset<Description> upsetChanges = this.changesAfterAddingBuildingToNetwork("prime",network);
		
		System.out.println("Upset of changes for new building"+upsetChanges);
		
		
		long stop = System.currentTimeMillis();
		System.out.println("Time for incremental reverification after a building is added: "+(stop-start)+"ms");
		System.out.println("Number of variables:"+networkResults.numberOfVariables());

		//add new streams for the prob choice fallback 
		//environment variables primech1 and primech2.
		//for the new building prime. 
		Double[] primech1=TimeSeries.generateBase(duration, 0.3);
		Double[] primech2=TimeSeries.generateBase(duration, 0.7);
		networkResults.addStream("primech1", primech1);
		networkResults.addStream("primech2", primech2);

		start = System.currentTimeMillis();
		Assignment updatedResults = this.rapidEvaluator.rapidEvaluate(upsetChanges, networkResults);
		stop = System.currentTimeMillis();
		System.out.println("Time for rapid evaluation after a building is added: "+(stop-start)+"ms");
		System.out.println(updatedResults.projectValueStreams("probprime","costprime","probnet","costnet"));
		System.out.println("Completed Rapid Evaluation of Incremental Verification Results");
		System.out.println("Total number of variables:"+updatedResults.numberOfVariables());

	}
	//--------------------------------------------------------------------------------------------------------------

	public void changeBuildingPatternScenario(Assignment networkResults) 
	{
		System.out.println("-".repeat(10)+" changed building pattern re-verification + re-evaluation "+"-".repeat(10));
		System.out.println(networkResults.projectValueStreams("probb3","costb3","probnet","costnet"));

				
		Building b3 = Figure1Network.b3;
		
		ParametricTask b3t = gamma.get(b3);
		System.out.println("b3 expressions: "+rho.get(b3t));
		b3.pattern = new Sequence(b3.getLabel(),new Entity[] {Figure1Network.b1,Figure1Network.wf});
		

		long start = System.currentTimeMillis();
		//update the domain range of alpha+beta compute dependencies between domain (description) and range (models)
		alpha.computeMap(b3);		
		beta.computeMap(b3);

		//compute the upset of bPrime; those affected by the change.
		Poset<Description> needsVerification = this.alpha.domain.upset(b3);
		
		System.out.println("Incremental Verification to be performed on this poset "+needsVerification);
		//compute the new verification tasks.
		for(Description d : needsVerification)
		{
			this.gamma.update(d);
		}
		this.gamma.inferRangeOrdering();
		
				
		Iterator<ParametricTask> it = this.gamma.image(needsVerification).iterator();
		this.compositionalVerify(it);		
		//this.rho.computeMap(this.gamma.image(needsVerification));
		
		b3t = gamma.get(b3);		
		System.out.println("b3 expressions: "+rho.get(b3t));
		long stop = System.currentTimeMillis();
		System.out.println("Incremental Verification of the upset "+needsVerification+" took: "+(stop-start)+"ms");
		
		start = System.currentTimeMillis();
		Assignment updatedResults = this.rapidEvaluator.rapidEvaluate(needsVerification, networkResults);
		stop = System.currentTimeMillis();
		System.out.println("Selective Evaluation of the upset "+needsVerification+" took: "+(stop-start)+"ms");
		
		System.out.println(updatedResults.projectValueStreams("probb3","costb3","probnet","costnet"));


	}
	//--------------------------------------------------------------------------------------------------------------	
	public void changeBuildingProvidersScenario(Assignment networkResults) 
	{
		System.out.println("-".repeat(10)+" changed building providers re-verification + re-evaluation "+"-".repeat(10));
		System.out.println(networkResults.projectValueStreams("probb3","costb3","probnet","costnet"));

		Building b3 = Figure1Network.b3;
		ParametricTask b3t = gamma.get(b3);
		System.out.println("b3 expressions: "+rho.get(b3t));
		//removing dependency on b1, but keeping sequence pattern.
		
		this.network.setPattern(b3, new Sequence(b3.getLabel(),new Entity[] {Figure1Network.pv,Figure1Network.wf}));
		//b3.pattern = 
		
		
		long start = System.currentTimeMillis();
		//update the domain range of alpha+beta compute dependencies between domain (description) and range (models)
		alpha.computeMap(b3);
		beta.computeMap(b3);

		//compute the upset of bPrime; those affected by the change.
		Poset<Description> needsVerification = this.alpha.domain.upset(b3);
		
		System.out.println("Incremental Verification to be performed on this poset "+needsVerification);
		//compute the new verification tasks.
		for(Description d : needsVerification)
		{
			this.gamma.update(d);
		}
		this.gamma.inferRangeOrdering();
		
		
		
		Iterator<ParametricTask> it = this.gamma.image(needsVerification).iterator();
		this.compositionalVerify(it);
		//this.rho.computeMap(this.gamma.image(needsVerification));
		
		b3t = gamma.get(b3);		
		System.out.println("b3 expressions: "+rho.get(b3t));
		long stop = System.currentTimeMillis();
		System.out.println("Incremental Verification of the upset "+needsVerification+" took: "+(stop-start)+"ms");
		
		start = System.currentTimeMillis();
		Assignment updatedResults = this.rapidEvaluator.rapidEvaluate(needsVerification, networkResults);
		stop = System.currentTimeMillis();
		System.out.println("Selective Evaluation of the upset "+needsVerification+" took: "+(stop-start)+"ms");
		
		System.out.println(updatedResults.projectValueStreams("probb3","costb3","probnet","costnet"));
		
	}
	//--------------------------------------------------------------------------------------------------------------
	public static void main(String[] args)
	{
		changeScenarioExperiments();
	}
	//--------------------------------------------------------------------------------------------------------------
	public static void changeScenarioExperiments()
	{

		Network network = Figure1Network.initialNetworkConfig();

		//applying ReInvest to the network performs compositional verification
		ParametricModelChecker pmc = new ParametricModelChecker();
		ReInvestFramework reInvest = new ReInvestFramework(pmc,network);

		System.out.println("Network config poset/adjacancy list");
		System.out.println(reInvest.network.config.toString());
		System.out.println("Repository after compositional verification of the entire network");
		
		Iterator<ParametricTask> it = reInvest.rho.domain.iterator();

		while(it.hasNext())
		{
			ParametricTask t = it.next();
			System.out.println(t.label);
			PrismParametricResult r = reInvest.rho.map.get(t);			
			System.out.println(r.getExpression(PropertyType.prob));
			System.out.println(r.getExpression(PropertyType.cost));
		}

		//assign value streams for the environment variables.
		Double[] ch1=TimeSeries.generateBase(duration, 0.6);
		Double[] ch2=TimeSeries.generateBase(duration, 0.4);

		//vary values of the wf prob
		Double[] pwf=TimeSeries.generateBase(duration, 0.85);
		TimeSeries.setValuesInterval(pwf,6,13,0.50);
		TimeSeries.setValuesInterval(pwf,19,23,0.92);
		Double[] cwf = TimeSeries.generateBase(duration, 1.5);
		//vary values of the wf cost		
		TimeSeries.setValuesInterval(cwf,6,13,1.1);
		TimeSeries.setValuesInterval(cwf,19,23,1.6);

		//vary valuyes of the pv prob
		Double[] ppv=TimeSeries.generateBase(duration, 0.9);
		//TimeSeries.setValuesInterval(ppv,6,13,0.9);
		//TimeSeries.setValuesInterval(ppv,19,23,0.9);
		Double[] cpv=TimeSeries.generateBase(duration, 1.1);
		TimeSeries.setValuesInterval(cpv,6,13,1.1);
		TimeSeries.setValuesInterval(cpv,19,23,1.2);

		//create a new evaluation record repository and add the streams to the variables		
		Assignment initial = new Assignment(duration);
		//set initial streams of values to each ground variable.
		initial.addStream("ppv", ppv);
		initial.addStream("pwf", pwf);
		initial.addStream("b3ch1", ch1);
		initial.addStream("b3ch2", ch2);

		initial.addStream("cpv", cpv);
		initial.addStream("cwf", cwf);

		//initial evaluation of the expressions
		Assignment networkResults = reInvest.evaluate(initial);		
		System.out.println(networkResults.projectValueStreams("pwf","cwf","probnet","costnet"));
		System.out.println("-".repeat(60));
		System.out.println("-".repeat(10)+" initial verification + evaluation of network "+"-".repeat(10));

		
		
		//re-verification and rapid evaluation when updating a pattern
		System.out.println("-".repeat(60));
		System.out.println("Change Building Pattern Scenario");
		reInvest.changeBuildingPatternScenario(networkResults);
		System.out.println("-".repeat(60));
		
		
		System.out.println("-".repeat(60));
		System.out.println("Add Building  Scenario");
		//re-verification and rapid evaluation when adding a building
		reInvest.addBuilding(networkResults);
		System.out.println("-".repeat(60));

		
		System.out.println("-".repeat(60));
		System.out.println("Change Building Providers Scenario");
		reInvest.changeBuildingProvidersScenario(networkResults);


	}

}
//rapid evaluation over an different value stream.
		//we can change ch1 and ch2 in resulting in rapid evaluation of prob choice and dependents
		//	reInvest.changeScenario0RapidEvaluation(networkResults);






