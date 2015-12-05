package core;

public class SigmoidNeuron extends Neuron 
{
	public SigmoidNeuron()
	{
		super();
		//this.setThreshold(0);
	}
	
	public void calcOutput()
	{
		float sum = 0;
		for(int i=0; i<inputs.size(); i++)
		{
			sum = sum + inputs.get(i).getOutput()*weights.get(i).floatValue();
		}
		//label = "" + sum;
		if(useThreshold)
			output = -threshold + sum;
		else
			output = sum;
		
		output = (float)(1 / (1+Math.exp(-output)));
		label = "" + output;
	}
}