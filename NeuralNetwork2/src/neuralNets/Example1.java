package neuralNets;

import javax.swing.JFrame;

import gui.NeuralNetFrame;
import gui.NeuralNetPanel;
import core.NeuralNet;
import core.Neuron;
import core.SigmoidNeuron;

public class Example1 {

	/**
	 * This program sets up a neural net to model a 2-input 
	 */
	public static void main(String[] args) {
		
		float totalError = 0;
		float targetError = 0.00001f;
		
		NeuralNet net = new NeuralNet(1, 2, NeuralNet.SIGMOID_NEURON);	// neural net with 1 hidden layer and 2 neurons per layer using Binary Neurons 
		net.setLearningRate(1f);
		
		float[] inputs = {0.9f, 0.35f};
		net.addInputLayer(inputs);	// add the inputs to the neural net model
		//net.normalizeInputs();
		net.addOutputLayer(1);		// add one output in the output layer

		net.createConnections(1);		// default connections with weights of 1
		net.connectInputs(1);			// connect inputs to 1st layer using weights of 1
		//net.connectOutputs(1);		// connect last (hidden) layer to output layer using weights of 1   <---------- problem!! adds extra inputs to output layer - fixed!

		// thresholds
		((SigmoidNeuron)(net.getLayer(1).get(1))).setThreshold(0f);	
		((SigmoidNeuron)(net.getLayer(1).get(2))).setThreshold(0f);
		((SigmoidNeuron)(net.getLayer(2).get(1))).setThreshold(0f);	

		// set weights
		// layer 2 (output layer)
		((Neuron)(net.getLayer(2).get(1))).setWeight(0.9f, 1);
		((Neuron)(net.getLayer(2).get(1))).setWeight(0.3f, 2);
		// layer 1	(hidden layer)
		((Neuron)(net.getLayer(1).get(1))).setWeight(0.6f, 1);
		((Neuron)(net.getLayer(1).get(1))).setWeight(0.4f, 2);
		((Neuron)(net.getLayer(1).get(2))).setWeight(0.8f, 1);
		((Neuron)(net.getLayer(1).get(2))).setWeight(0.1f, 2);
		
		// set target Outputs
		float[] targetOutputs = new float[1];
		targetOutputs[0] = 4.4f;
		net.setTargetOutputs(targetOutputs);
		// set output ranges
		net.setOutputRange(1, 0, 35);

		NeuralNetPanel np = new NeuralNetPanel(net);
		NeuralNetFrame frame = new NeuralNetFrame(np);
		frame.setNet(net);
		frame.setTitle("Neural Net");
		frame.setSize(600, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//frame.add(np);
		frame.setVisible(true);

		// back-propagation algorithm
		//net.train(1);
//		float[] targetOutputs = new float[1];
//		targetOutputs = new float[net.numOfOutputs()];
//		targetOutputs[0] = 0.5f;
//		System.out.println("Number of Outputs = " + net.numOfOutputs());
//		net.setTargetOutputs(targetOutputs);
//		int run = 1;
//		do
//		{
//			net.train();
//			
//			run++;
//			
//			for(int i=0; i< targetOutputs.length; i++) 
//			{
//				float error = targetOutputs[i] - net.getOutput(i+1);
//				totalError += Math.abs(error);
//			}
//		}while(totalError > targetError);
		
		System.out.println(" error: " + totalError);
		System.out.println("Neural Net is trained with Weights: \n" + net.getWeights());

		np.repaint();
	}
}