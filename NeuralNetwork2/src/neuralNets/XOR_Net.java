package neuralNets;

import javax.swing.JFrame;

import gui.NeuralNetPanel;
import core.BinaryNeuron;
import core.InputNode;
import core.NeuralNet;
import core.Neuron;
import core.Node;

public class XOR_Net {

	/**
	 * This program sets up a neural net to model a 2-input XOR gate using Binary Neurons
	 */
	public static void main(String[] args) {
		
		NeuralNet net = new NeuralNet(1, 2, NeuralNet.BINARY_NEURON);	// neural net with 1 hidden layer and 2 neurons per layer using Binary Neurons 
		
		float[] inputs = {0, 0};
		net.addInputLayer(inputs);	// add the inputs to the neural net model
		net.addOutputLayer(1);		// add one output in the output layer
		
		net.createConnections(1);		// default connections with weights of 1
		net.connectInputs(1);			// connect inputs to 1st layer using weights of 1
		net.connectOutputs(1);		// connect last (hidden) layer to output layer using weights of 1   <---------- problem!! adds extra inputs to output layer

		// XOR gate thresholds
		((BinaryNeuron)(net.getLayer(1).get(1))).setThreshold(1.5f);	
		((BinaryNeuron)(net.getLayer(1).get(2))).setThreshold(0.5f);
		((BinaryNeuron)(net.getLayer(2).get(1))).setThreshold(0.5f);	
		
		// set XOR weights
		((Neuron)(net.getLayer(2).get(1))).setWeight(-1, 1);
		
		JFrame frame = new JFrame("Neural Net");
		frame.setSize(600, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		NeuralNetPanel np = new NeuralNetPanel(net);
		frame.add(np);
		frame.setVisible(true);
		
		// back-propagation algorithm
		float[] targetOutputs = new float[1];
		targetOutputs[0] = 0.5f;
		net.setTargetOutputs(targetOutputs);
		// step 1
		net.forwardPropagate();
		// step 2
		net.calcOutputErrors();
		// step 3
		
		
		np.repaint();

	}

}