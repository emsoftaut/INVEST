package _parametricmodelprops;

public enum PropertyType
{
	prob, cost;
	String lhsVariable = this.name();//e.g. prob, cost
	String rhsVariable = this.name().charAt(0)+"";//e.g. p, c
	
}
