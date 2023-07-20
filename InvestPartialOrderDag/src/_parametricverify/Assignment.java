package _parametricverify;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Assignment 
{

	public int duration;	
	private DecimalFormat df = new DecimalFormat("#.###");	
	private ArrayList<HashMap<String,Double>> records = new ArrayList<HashMap<String,Double>>();



	//---------------------------------------------------------------------------------
	public void setRecord(HashMap<String,Double> record,int t)
	{
		this.records.set(t, record);
	}
	//---------------------------------------------------------------------------------
	public void clear()
	{
		this.records = new ArrayList<HashMap<String,Double>>(); 
	}
	//---------------------------------------------------------------------------------
	public String projectValueStreams(String...variables)
	{
		String out = "time;";
		for(String v : variables)
		{
			out+=v+";";
		}
		out+="\n";

		for(int t = 0;t<this.records.size();t++)
		{
			out+= t+";";
			for(String v : variables)
			{				
				Double x = this.records.get(t).get(v);
				out+= df.format(x)+";";
			}
			out+="\n";

		}
		return out;
	}
	//---------------------------------------------------------------------------------
	public String projectValueStream(String variable)
	{

		String out = variable+"\n";
		Double[] valueStream = new Double[this.duration];

		for(int t = 0;t<this.records.size();t++)
		{
			valueStream[t] = this.records.get(t).get(variable);


			out+= df.format(valueStream[t])+"\n";

		}

		return out;

	}
	//---------------------------------------------------------------------------------
	public void addStream(String variable,Double[] valueStream)
	{
		for(int t = 0;t<duration;t++)
		{
			HashMap<String,Double> record = this.records.get(t);
			record.put(variable, valueStream[t]);					
		}
	}
	//---------------------------------------------------------------------------------
	/**
	 * 
	 * Return a hash map of the variables -> values, at time t.
	 * 
	 * */
	public HashMap<String,Double> getRecord(int t)
	{		
		return this.records.get(t);
	}
	//---------------------------------------------------------------------------------
	public Assignment(int duration)
	{

		this.duration = duration;

		this.records = new ArrayList<HashMap<String,Double>>();

		//create enough entries for the duration of the simulation
		for(int i =0 ; i < this.duration;i++)
		{
			this.records.add(new HashMap<String,Double>());
		}

	}

	public String toString()
	{		


		String[] keys = new String[this.records.get(0).keySet().size()];

		if(keys.length==0)
		{
			return "empty";
		}

		int i = 0;
		for(String k : this.records.get(0).keySet())
		{
			keys[i++] = k;
		}


		String out = "";
		for(String k : keys)
		{
			out+=k+",";
		}
		out = "hour,"+out.substring(0, out.length()-1)+"\n";


		for(int t = 0;t < this.records.size();t++)
		{

			HashMap<String,Double> record = this.records.get(t);
			String line = t+",";
			for(String k : keys)
			{
				line+= record.get(k)+",";				
			}
			line = line.substring(0, line.length()-1);

			out += line+"\n";	
		}
		return out;

	}
	public int numberOfVariables() 
	{
	
		if(this.records.isEmpty()) return 0;
		return this.records.get(0).size();
	}








}
