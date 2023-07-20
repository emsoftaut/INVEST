package core;

abstract public class Property {

	public String label;
	
	
	public Property(String label)
	{
		this.label  = label;
	}
	public String getLabel()
	{
		return this.label;
	}
	
	public String toString()
	{
		return "p"+this.getLabel();
	}
	
	
}
