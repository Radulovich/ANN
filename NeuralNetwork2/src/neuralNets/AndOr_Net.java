package neuralNets;

import javax.swing.JFrame;

import gui.NeuralNetPanel;
import core.BinaryNeuron;
import core.InputNode;
import core.NeuralNet;

public class AndOr_Net {

	/**
	 * This program sets up a neural net to model a 2-input AND or OR gate using Binary Neurons
	 */
	public static void main(String[] args) {
		
		NeuralNet n = new NeuralNet(0, NeuralNet.BINARY_NEURON);	// neural net with 0 hidden layers using Binary Neurons 
		float[] inputs = {1, 0};
		n.addInputLayer(inputs);	// add the inputs to the neural net model
		
		n.addOutputLayer(1);		// add one output in the output layer
		n.createConnections(1);		// default connections with weights of 1
		n.connectInputs(1);			// connect inputs to 1st layer using weights of 1
		n.connectOutputs(1);		// connect last (hidden) layer to output layer using weights of 1
		
		
		JFrame frame = new JFrame("Neural Net");
		frame.setSize(600, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		NeuralNetPanel np = new NeuralNetPanel(n);
		frame.add(np);
		frame.setVisible(true);

		((BinaryNeuron)(n.getLayer(1).get(1))).setThreshold(0.9f);	// OR gate
		//((BinaryNeuron)(n.getLayer(1).get(0))).setThreshold(1.5f);	// AND gate
		
		n.forwardPropagate();
		np.repaint();

	}

}
