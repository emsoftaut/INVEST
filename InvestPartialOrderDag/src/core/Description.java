package core;

abstract public class Description implements Element 
{
	
	private String label;
	
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public String getLabel()
	{
		return this.label;
	}
	
	public Description(String label)
	{
		this.label = label;
	}
	
	public String toString()
	{
		return this.label;
	}

}
