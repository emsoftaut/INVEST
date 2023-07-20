package _parametricmodelprops;


import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import _parametricmodelprops.PropertyType;
import core.Result;
import param.ParamResult;


public class PrismParametricResult extends Result  
{

	//probability equation for building b1Prob = expression from prob PMC
	//cost equation for building b1Cost = expression from R=? PMC

//	public PrismParametricModel model;

		
	
	public HashMap<PropertyType,String> equations;

	public String label;

	static final public String probEquationVariable = "prob";
	static final public String costEquationVariable = "cost";


	public String getLabel()
	{
		return this.label;
	}

	@Override
	public String toString()
	{
		String out = "";

		for(PropertyType pt : PropertyType.values())
		{
			out+= pt+" --> "+equations.get(pt)+"\n";
		}
		return out;
	}


//	private String translate(String expressionFromPrism)
//	{
//
//		StringBuilder convertedExpression = new StringBuilder();
//		expressionFromPrism = expressionFromPrism.stripLeading();
//		expressionFromPrism = expressionFromPrism.trim();
//
//		String[] terms = expressionFromPrism.split(" ");
//
//		for (String term : terms) 
//		{
//
//			if (term.matches("\\d+"))
//			{
//				if (term.matches("\\d+\\s[a-zA-Z0-9]+")) 
//				{ // Check if term is a number followed by a variable
//					String[] parts = term.split("\\s+");
//					convertedExpression.append(parts[0]).append(" * ").append(parts[1]).append(" ");
//				}
//			}
//			else
//				convertedExpression.append(term);
//		}
//
//		return convertedExpression.toString();
//
//
//	}

	//------------------------------------------------------------------------------------
	public String createEquation(PropertyType pt,String rightHandSide) 
	{

		String labelForLeftHandSide = pt.name()+this.getLabel();
		int startIndex = rightHandSide.indexOf('{');
		int endIndex = rightHandSide.indexOf('}');

		if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) 
		{			
			String regex = "\\d+\\s+\\w+";			

			String rhs = rightHandSide.substring(startIndex + 1, endIndex);
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(rhs);

			while (matcher.find()) 
			{
				String substring = matcher.group();
				rhs = rhs.replaceAll(substring, substring.replace(" ", "*"));			
			}					
			return labelForLeftHandSide+" = "+ rhs;
		} 	        
		return rightHandSide;
	}
	//------------------------------------------------------------------------------------
	public String getExpression(PropertyType pt)
	{
		String b = this.equations.get(pt).toString();
		return (b);
	}
	//------------------------------------------------------------------------------------
	public PrismParametricResult(String label)
	{
		this.label = label;
		this.equations = new HashMap<PropertyType,String>();
		for(PropertyType t : PropertyType.values())
		{
			this.equations.put(t,"");
		}
	}
}