package core;

import java.awt.Graphics;
import java.util.ArrayList;

public class Neuron extends Node
{
	private ArrayList<Node> outputs;	// list of other neurons that this neuron's output is connected to
	protected ArrayList<Node> inputs;	// list of other neurons that this neuron's inputs are connected to
	protected ArrayList<Float> weights;	// weights associated with each input
	protected float output;				// current value of output
	protected float threshold;
	protected boolean useThreshold;		// if false, you must add a bias input to each neuron whose weight will be equivalent to a negative bias
	
//	public Neuron(ArrayList<Node> in, ArrayList<Node> out)
//	{
//		outputs = out;
//		inputs = in;
//		output = 0;
//		threshold = 1;
//		label = "" + output;
//		useThreshold = false;
//		if(useThreshold)
//			weights = new ArrayList<Float>(in.size());
//		else {
//			weights = new ArrayList<Float>(in.size()+1);	// the extra weight will be the bias that incorporates the threshold 
//			weights.set(0, -threshold);						// first node will be the bias node
//			inputs.add(new InputNode(1));
//		}
//	}
	
	public Neuron()
	{
		outputs = new ArrayList<Node>();
		inputs = new ArrayList<Node>();
		output = 0;
		threshold = 1;
		weights = new ArrayList<Float>();
		//weights.add(0, -threshold);			// first node will be the bias node
		//inputs.add(new InputNode(-1));
		label = "" + output;
		useThreshold = false;
	}
	
	public int getNumInputs()
	{
		return inputs.size();
	}
	
	public ArrayList<Node> getInputs()
	{
		return inputs;
	}
	
	public int getNumOutputs()
	{
		return outputs.size();
	}
	
	public ArrayList<Node> getOutputs()
	{
		return outputs;
	}
	
	public ArrayList<Float> getWeights()
	{
		return weights;
	}
	
	/**
	 * Returns the number of weights associated with the inputs of this node
	 * @return Number of incoming weights
	 */
	public int getNumWeights()
	{
		return weights.size();
	}
	
	/**
	 * get the weight of the ith input for this neuron
	 * @param i
	 */
	public float getWeight(int i)
	{
		return weights.get(i);
	}
	
	/**
	 * sets the weight of input i of this Neuron
	 * @param w
	 * @param i - index starts at 1 because bias is at zero
	 */
	public void setWeight(float w, int i)
	{
		weights.set(i, new Float(w));
	}
	
	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
		weights.set(0, threshold);
		useThreshold = true;
		//label = "" + threshold;
	}

	/**
	 * sets the weights of all of this neuron's inputs to w
	 * @param w
	 * @param i
	 */
	public void setAllWeights(float w)
	{
		for(int i=0; i<weights.size(); i++)
			weights.set(i, new Float(w));
	}
	
	/**
	 * connect node other to this neuron, with a given weight
	 */
	public void connectInput(Node other, float weight)
	{
		inputs.add(other);
		weights.add(new Float(weight));
		//calcOutput();
	}
	
	/**
	 * connect this neuron's output to other neuron, with a given weight
	 */
	public void connectOutput(Node other, float weight)
	{
		outputs.add(other);
		other.connectInput(this, weight);
	}
	
	public float getOutput()
	{
		return output;
	}
	
	public void calcOutput()
	{
		output = 0;
		for(int i=0; i<inputs.size(); i++)
		{
			output = output + inputs.get(i).getOutput()*weights.get(i).floatValue();
		}
		label = "" + output;
	}
	
	public boolean isUseThreshold() {
		return useThreshold;
	}

	public void setUseThreshold(boolean useThreshold) {
		this.useThreshold = useThreshold;
	}
	
	// GUI related stuff
	public void setSize(int size)
	{
		this.size = size;
	}
	
	public String toString() 
	{
		String result = output+"";
		
		return result;
	}

}
