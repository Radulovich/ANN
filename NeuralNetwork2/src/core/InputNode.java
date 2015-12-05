package core;

import java.awt.Graphics;
import java.util.ArrayList;

public class InputNode extends Node {
	private ArrayList<Node> outputs;		// list of other neurons that this node's outputs are connected to
	private float output;					// current value of this node's output
	private float threshold;
	
	/**
	 * constructor
	 * @param f
	 */
	public InputNode(float f)
	{
		outputs = new ArrayList<Node>();
		output = f;
		label = ""+output;
		threshold = 0;
	}
	
	public int getNumOutputs()
	{ 
		return outputs.size(); 
	}
	
	public float getOutput()
	{
		return output;
	}
	
	public void setInput(float f)
	{
		output = f;
		label = ""+f;
	}
	
	public void setThreshold(float t)
	{
		threshold = t;
	}
	
	public ArrayList<Node> getOutputs()
	{
		return outputs;
	}
	
	public void connectOutput(Node other, float weight)
	{
		outputs.add(other);
		other.connectInput(this, weight);
	}

	@Override
	public void connectInput(Node other, float weight) {

	}
	public void calcOutput()
	{
		// there is nothing to calculate as this is an input node
	}
	
	public String toString() 
	{
		String result = "input layer: " + output;
		int i = 0;
		for(Node n : outputs)
			result += "--" + n.getWeight(i++) +"-->" + n.getOutput() + "\n";
		
		return result;
	}
	
}
