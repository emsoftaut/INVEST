package core;


abstract public class Verifier
{

	//model checker
	public abstract Result modelCheck(Task task);
	
	
	protected PosetMorphism<Task,Result> rho;//repository

	public void setRepo(PosetMorphism<Task,Result> rho) 
	{
		this.rho = rho;
	}	
}
