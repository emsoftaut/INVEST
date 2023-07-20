package _casestudies;

import _networkdescription.*;
import _parametricverify.*;
import _patterns.*;

public class PatternTestClient {




	public static void main(String[] args) 
	{

		ParametricModelChecker pmc = new ParametricModelChecker();		
		ParametricTask task;// = new ParametricTask(null, null);		

		System.out.println("---------------------------------------------------------------------------------------------------");
		System.out.println("Sequence Pattern");


		//test sequence pattern 
		for(int i = 2;i<=2;i++) 
		{
			System.out.println("i = "+i); 
			Entity[] entities = new Entity[i]; 
			for(int j =0;j<i;j++) 
			{ 				
				entities[j] = new Building("b"+(1+j)); 
			} 
			Sequence sequence = new Sequence("myBuilding"+i,entities); 
			task = new ParametricTask(sequence.getParametricModel(), sequence.generateProperty());
			System.out.println(sequence.getParametricModel());
			System.out.println(pmc.modelCheck(task)); 
			System.out.println("---------------------------------------------------------------------------------------------------"); 
		}


		//test parallel pattern
		System.out.println("---------------------------------------------------------------------------------------------------");
		System.out.println("Parallel Pattern");
		for(int i = 2;i<=2;i++)
		{
			System.out.println("i = "+i);
			Entity[] entities = new Entity[i];
			for(int j = 0;j<i;j++)			
			{
				entities[j] = new Building("b"+(j+1));			
			}  
			Parallel parallel = new Parallel("myBuilding"+i,entities);
			System.out.println(parallel.getParametricModel().generateDTMC());
			task = new ParametricTask(parallel.getParametricModel(), parallel.generateProperty());
			System.out.println(pmc.modelCheck(task));			
			System.out.println("---------------------------------------------------------------------------------------------------");
		}


		//		//test ProbChoice pattern 
		System.out.println("//---------------------------------------------------------------------------------------------------");
		System.out.println("ProbChoice Pattern"); 
		for(int i = 2;i<=2;i++) 
		{
			System.out.println("//i = "+i); 

			Entity[] entities = new Entity[i]; 
			Double[] probs = new Double[i]; 
			Double probValue = 1.0/i;

			for(int j = 0;j<i;j++) 
			{
				entities[j] = new Building("b"+1); 
				probs[j] = probValue; 
			} 
			ProbChoice probChoice = new ProbChoice("myBuilding"+i,entities,probs);
			System.out.println(probChoice.getParametricModel().generateDTMC());
			task = new ParametricTask(probChoice.getParametricModel(),probChoice.generateProperty()); 
			System.out.println(pmc.modelCheck(task));
			System.out.println("//---------------------------------------------------------------------------------------------------"); 
		}


		//test ProbChoice pattern 
		System.out.println("//---------------------------------------------------------------------------------------------------");
		System.out.println("//ProbChoiceFallBack Pattern"); 
		for(int i = 2;i<=2;i++) 
		{
			//System.out.println("//i = "+i); 

			Entity[] entities = new Entity[i+1];			

			Double[] probs = new Double[i]; 
			Double probValue = 1.0/i;

			for(int j = 0;j<i;j++) 
			{
				entities[j] = new Building("b"+1); 
				probs[j] = probValue; 
			} 
			entities[entities.length-1]= new Building("bFall");

			ProbFallBack probFallBack = new ProbFallBack("myBuilding"+i,entities,probs);
			//System.out.println("//verifying: "+probFallBack.generateProperty().pctlFormulas);
			System.out.println(probFallBack.getParametricModel().generateDTMC());

			task = new ParametricTask(probFallBack.getParametricModel(),probFallBack.generateProperty()); 
			System.out.println(pmc.modelCheck(task));
			 
		}	

	}

}
