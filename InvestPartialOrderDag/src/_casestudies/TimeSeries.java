package _casestudies;

import java.util.Random;

public class TimeSeries 
{
	

	public static Double[] compose(Double[] input,Double[] part, int index)
	{
		int length = input.length;
		Double [] values = new Double[length];
		
		int j = 0;
		for(int i = 0;i<length;i++)
		{
			if(i > index && j < part.length)
			{
				values[i] = part[j];
				j++;
				
			}
			else
			{
				values[i] = input[i];
			}			
		}
		return values;
	}
	
	public static Double[] generateBase(int length,Double value)
	{
		Double [] values = new Double[length];
			
		for(int i = 0;i<length;i++)
		{
			values[i] = value;
		}

		return values;
	}

		
	public static Double[] generateProbIncrease(int length, Double start, Double stop)
	{
		
		double step = (Math.abs(stop-start)/length);
		Double [] usage = new Double[length];
		usage[0] = start;

		

		for(int i = 1;i<length;i++)
		{
			usage[i] = usage[i-1]+step;
		}

		return usage;
	}

	public static  Double[] generateProbBound(int length,Double lowerBound,Double upperBound)
	{
		Double [] usage = new Double[length];

		Random rand = new Random();

		for(int i = 0;i<length;i++)
		{
			usage[i] = lowerBound + rand.nextDouble(upperBound - lowerBound);
		}

		return usage;
	}

	
	//given an input of streams, obtain the tth value from eaxh stream,
	//forming a new stream.
	public static Double[] pointWiseArray(int t,Double[][] streams) 
	{
		
		Double[] valuesAtTimet = new Double[streams.length];
		
		
		int i = 0;
		for(Double[] stream : streams)
		{
			valuesAtTimet[i] = stream[t];
			i++;
			
		}
		
		return valuesAtTimet;
	}

	//System.out.println((0.99-0.70)/24);
	public static Double[] generateProbDecrease(int length, Double start, Double stop)
	{
		
		double step = (Math.abs(stop-start)/length);
		Double [] usage = new Double[length];
		usage[0] = start;

		

		for(int i = 1;i<length;i++)
		{
			double v = (usage[i-1]-step);
			usage[i] = v<0?0:v;
		}

		return usage;
	}

	/*
	 * Sets the value of input to value, between startIndex and stopIndex inclusive
	 * */
	public static void setValuesInterval(Double[] input, int startIndex, int stopIndex, double value) 
	{
		
		for(int i=startIndex;i<=stopIndex;i++)
		{
			input[i] = value;
		}
		
	}

}
