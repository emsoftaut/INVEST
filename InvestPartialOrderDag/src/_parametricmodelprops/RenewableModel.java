package _parametricmodelprops;

public class RenewableModel extends PrismParametricModel
{
	
	private String prob = PrismParametricModel.rhsProbParameter+this.getLabel();
	private String cost = PrismParametricModel.rhsCostParameter+this.getLabel();

	public RenewableModel(String label) 
	{
		super(label);
	
	}

	
	
	
	public String toString()
	{
		return this.getLabel();
	}
	
	@Override
	public String[] probabilityParameters() 
	{
		return new String[] {prob};
	}

	@Override
	public String[] costParameters() 
	{
		return new String[] {cost};
	}

	@Override
	public String[] paramNames()
	{
		int n = 1;
		String[] paramNames = new String[n*2];

		String[] probs = probabilityParameters();
		String[] costs = costParameters();

		for(int i = 0;i<n;i++)
		{
			paramNames[i] = probs[i];
			paramNames[i+n] = costs[i];
		}

		return paramNames;
	}

	@Override
	public String[] paramLowerBounds() 
	{
	
		return new String[] {"0.0","0.0"};
	}

	@Override
	public String[] paramUpperBounds() 
	{
	
		return new String[] {"1.0","1.0"};
	}

	@Override
	public PrismParametricResult getResult(int n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateDTMC() 
	{
	
		return "dtmc\n"
				+ "\n"
				+ "\n"
				+ "const double "+prob+";\n"
				+ "const double "+cost+";\n"
				+ "\n"
				+ "const int SUCC=1;\n"
				+ "const int FAIL = 2;\n"
				+ "const int DONE= 3;\n"
				+ "\n"
				+ "module renewableEnergy\n"
				+ "state : [0..3] init 0;\n"
				+ "[] (state=0) -> "+prob+":(state'=SUCC) + (1-"+prob+"):(state'=FAIL);\n"
				+ "[] (state=SUCC) -> (state'=DONE);\n"
				+ "[] (state=FAIL) -> (state'=DONE);\n"
				+ "endmodule\n"
				+ "\n"
				+ "rewards\n"
				+ "(state=SUCC) : "+cost+";\n"
				+ "endrewards";
	}


	@Override
	int length() 
	{		
		return 1;
	}




	@Override
	public String getReliabilityParameter() {
		// TODO Auto-generated method stub
		return this.prob;
	}

}
