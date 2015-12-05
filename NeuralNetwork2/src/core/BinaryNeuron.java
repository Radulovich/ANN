package core;

public class BinaryNeuron extends Neuron {
	
	public void calcOutput()
	{
		float sum = 0;
		for(int i=0; i<inputs.size(); i++)
		{
			sum = sum + inputs.get(i).getOutput()*weights.get(i).floatValue();
		}
		label = "" + sum;
		if(useThreshold)
			output = -threshold + sum;
		else
			output = sum;
		
		if (output >= 0)
			output = 1;
		else
			output = 0;
	}

}
