package core;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public abstract class Node 
{
	//GUI state
	protected int[] pos = {0, 0};					// (x, y) position of neuron
	protected int size = 50;						// diameter of neuron
	protected String label = "";						// label of neuron, typically its threshold
	protected float error;							// error for this node
	
	public float getOutput() {return 1;}			// default output is 1
	public void connectOutput(Node other, float weight) {}
	public void connectInput(Node other, float weight) {}
	public void calcOutput() {}
	public void setAllWeights(float w) {}
	public int getNumInputs() { return 0; }
	public int getNumOutputs() { return 0; }
	public ArrayList<Node> getOutputs() { return null; }
	public float getWeight(int i) { return 0; }
	public void setWeight(float w, int i) {}
	public ArrayList<Node> getInputs() { return null; }
	
	public float getError() 
	{ 
		return error;
	}
	
	public void setError(float e)
	{
		error = e;
	}
	
	
	public void setPos(int x, int y) {
		pos[0] = x;
		pos[1] = y;
	}
	
	public int[] getPos()
	{
		return pos;
	}
	
	public int getSize()
	{
		return size;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public void setLabel(float value)
	{
		label = "" + value;
	}
	
	public void draw(Graphics g, Color c)
	{
		g.setColor(c);
		g.fillOval(pos[0]-size/2, pos[1]-size/2, size, size);
		g.setColor(Color.BLACK);
		g.drawOval(pos[0]-size/2, pos[1]-size/2, size, size);
		g.drawString(label, pos[0]-size/4, pos[1]+size/8);
	}
}
