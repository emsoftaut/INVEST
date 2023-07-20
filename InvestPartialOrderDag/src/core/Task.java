package core;


/**
 * Stores a verification model, property and result to form a task.
 * When are two tasks the same?
 * */
public class Task implements Element
{
	public String label;
	public Model m;
	public Property p;
	//----------------------------------------------------
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Task)
		{
			Task t = (Task) o;
			return (t.m.equals(this.m) && t.p.equals(this.p));
		}
		
		return false;
	}
	//----------------------------------------------------
	public Task(Model m,Property p)
	{
		this.label = m.getLabel();
		this.m = m;
		this.p = p;
	}
	//----------------------------------------------------
	public Task(String label)
	{
		this.label = label;
		this.m = null;
		this.p = null;
	}
	//----------------------------------------------------
	public String toString()
	{
		return "Task:"+this.label;
	}
	//----------------------------------------------------
	/**
	 * Determines whether or not the task comprises the following
	 * model mx and property px using object reference equality.
	 * 
	 * */
	public boolean comprises(Model mx,Property px) 
	{
		
		return this.m == mx & this.p == px;
	}
}
