package core;


abstract public class Model 
{
	private String label;	
	
	public Model(String label)
	{
		this.setLabel(label);
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public String getLabel()
	{
		return label;
	}
}