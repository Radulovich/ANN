package gui;

import java.awt.*;

import javax.swing.*;

import core.InputNode;
import core.NeuralNet;
import core.Neuron;
import core.Node;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

// Frame template:  use this code as a starting point for your own program

public class NeuralNetPanel extends JPanel implements KeyListener,  MouseListener
{
	int height, width;
	public final static int H_NODE_SPACING = 140; 
	public final static int V_NODE_SPACING = 120; 
	private long elapsedTime, startTime;
	private float error;
	
	// Add your own state variables here
	private NeuralNet net;

	public NeuralNetPanel (NeuralNet n)
	{
		net = n;
		init ();
	}

	public void init () 
	{
		height = 480;
		width = 600;

		// Initialize any other objects here (such as GUI buttons, or your own class objects)

		
		// listen to keyboard events -- eg. if a key is pressed, keyPressed() method is called 
		addKeyListener(this);
		startTime = System.nanoTime();
	}
	
	public void drawInputLayer(Graphics g, int xPos, int yPos)
	{
		//int x = width/2-(H_NODE_SPACING+net.getInputLayer().get(0).getSize())*net.getInputLayer().size()/2;	// starting x-value for input nodes
		int x = net.getInputLayer().get(0).getSize()/2;
		int count = 0;
		for(InputNode n: net.getInputLayer())
		{
			n.setPos(x, yPos);
			if(count==0)
				n.draw(g, Color.CYAN);	// for bias
			else
				n.draw(g, Color.GREEN);
			
			
			x += n.getSize()+ H_NODE_SPACING;
			count++;
		}
	}
	
	public void drawLayer(Graphics g, int layer, int xPos, int yPos)
	{
		//int x = width/2-(H_NODE_SPACING+net.getLayer(layer).get(0).getSize())*net.getLayer(layer).size()/2;	
		int x = net.getInputLayer().get(0).getSize()/2;
		int count = 0;
		for(Node n: net.getLayer(layer))
		{
			n.setPos(x, yPos);

			if(count==0 && layer != net.getNumLayers())
				n.draw(g, Color.CYAN);	// for bias
			else if(count != 0)
				n.draw(g, Color.WHITE);
			
			x += n.getSize()+ H_NODE_SPACING;
			count++;
		}
	}
	
	public void drawConnections(Graphics g)
	{
		// draw connections from input layer to 1st layer
		g.setColor(Color.BLACK);
		ArrayList<InputNode> inputLayer = net.getInputLayer();
		ArrayList<Node> firstLayer = net.getLayer(1);
		for(int i=0; i<inputLayer.size(); i++){
			//System.out.println("Number of outputs for Node " + i + " of first layer = " + inputLayer.get(i).getOutputs().size());
			for(int j=0; j<inputLayer.get(i).getOutputs().size(); j++){
				g.drawLine(inputLayer.get(i).getPos()[0], inputLayer.get(i).getPos()[1], 
						   inputLayer.get(i).getOutputs().get(j).getPos()[0], inputLayer.get(i).getOutputs().get(j).getPos()[1]);
			}
		}

		// display weights into the 1st layer
		g.setColor(Color.BLUE);	// display weights in blue
		for(Node n : firstLayer)
		{
			for(int i=0; i<n.getNumInputs(); i++){
				float w = n.getWeight(i);
				int vertDisp = i%2;
				g.drawString(new Float(w).toString(), n.getPos()[0] - (n.getPos()[0]-n.getInputs().get(i).getPos()[0])/(2+vertDisp), 
													  n.getPos()[1] - (n.getPos()[1]-n.getInputs().get(i).getPos()[1])/(2+vertDisp));
			}
		}
		
		// draw connections between all layers, including output layer
		ArrayList<Node> layer;
		for(int i=1; i<net.getNumLayers(); i++){
			layer = net.getLayer(i);
			for(int j=0; j<layer.size(); j++){
				for(int k=0; k<net.getLayer(i+1).size()-1; k++){
					if(i+1 == net.getNumLayers())
						k++;
					int vertDisp = k%2;
					
						g.drawLine(layer.get(j).getPos()[0], layer.get(j).getPos()[1], net.getLayer(i+1).get(k).getPos()[0], net.getLayer(i+1).get(k).getPos()[1]);
						g.drawString(new Float(net.getLayer(i+1).get(k).getWeight(j)).toString(), 
								 layer.get(j).getPos()[0] - (layer.get(j).getPos()[0]-net.getLayer(i+1).get(k).getPos()[0])/(2+vertDisp), 
								 layer.get(j).getPos()[1] - (layer.get(j).getPos()[1]-net.getLayer(i+1).get(k).getPos()[1])/(2));
					
					
				}
			}
		}
		g.setColor(Color.BLACK);
		
		// draw outputs
		int i = net.getNumLayers();
		int count = 0;
		for(Node n : net.getLayer(i))
		{
			if(count != 0)
			{
				g.drawLine(n.getPos()[0], n.getPos()[1], n.getPos()[0], n.getPos()[1] + V_NODE_SPACING);
				g.drawString(""+n.getOutput(), n.getPos()[0]-10, n.getPos()[1] + V_NODE_SPACING+10);
			}
			count++;
		}
		
	}
	
	protected void paintComponent(Graphics g)  
    {  
        super.paintComponent(g);  
        //drawInputLayer(g, 70, 70);
        for(int i=1; i<=net.getNumLayers(); i++)
        {
        	drawLayer(g, i, 70, 70+i*V_NODE_SPACING);
        }
        drawConnections(g);
        drawInputLayer(g, 70, 70);
        for(int i=1; i<=net.getNumLayers(); i++)
        {
        	drawLayer(g, i, 70, 70+i*V_NODE_SPACING);
        }
        
        
        // provide some progress feedback
        int h = this.getHeight();
        int w = this.getWidth();
        
        g.drawString("Elapsed Time: " + (System.nanoTime() - startTime)/1000000000, w-130, h-50);
        g.drawString("Error: " + error, w-130, h-35);
        g.drawString("Output = "+ net.getOutput(1), w-130, h-20);
        g.drawString("Iteration No: " + net.getIterationNumber(), w-130, h-5);
        
    }  

	public float getError() {
		return error;
	}

	public void setError(float error) {
		this.error = error;
	}
	
	/*********************************************************************************/
	/***************************** Methods for listeners *****************************/
	/*********************************************************************************/
	
	// Keyboard Listener methods
	// This method is called when a key is pressed...if needed, place your code inside to respond to the pressed key
	public void keyPressed(KeyEvent e) {
		if(e.getKeyChar() == 'a') { System.out.println("Key A pressed.");}  // this is an example...you will change this to suit your needs

	}

	// This method is called when a key is typed...if needed, place your code inside to respond to the typed key
	// This method is the preferred way to find out about character input 
	public void keyTyped(KeyEvent e) {

	}

	// This method is called when a key is released...if needed, place your code inside to respond to the released key
	public void keyReleased(KeyEvent e) {

	}


	public void mouseClicked(MouseEvent e) {

		
	}


	public void mouseEntered(MouseEvent e) {

		
	}


	public void mouseExited(MouseEvent e) {

		
	}


	public void mousePressed(MouseEvent e) {

		
	}


	public void mouseReleased(MouseEvent e) {

		
	}
}