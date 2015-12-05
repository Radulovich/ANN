package neuralNets;

import javax.swing.JFrame;

import core.NeuralNet;
import core.Neuron;
import core.SigmoidNeuron;
import gui.NeuralNetFrame;
import gui.NeuralNetPanel;

public class Diabetes 
{
	/**
	 * This program sets up a neural net to model a diabetic person
	 */
	public static void main(String[] args) {
		
		float totalError = 0;
		float targetError = 0.00001f;
		
		NeuralNet net = new NeuralNet(1, 3, NeuralNet.SIGMOID_NEURON);	// neural net with 1 hidden layer and 2 neurons per layer using Binary Neurons 
		net.setLearningRate(0.3f);
		
		// inputs are as follows: BG, carbs eaten, time, physical activity (scale of 0 to 10, 0 being not active at all)
		float[] inputs = {7.8f, 80f, 10+39/60f, 1};
		net.addInputLayer(inputs);	// add the inputs to the neural net model
		net.normalizeInputs();
		net.addOutputLayer(1);		// add one output in the output layer

		net.createConnections(0, 1);	// default connections with random weights between [0, 1)
		net.connectInputs(0, 1);	   	// connect inputs to 1st layer using random weights in the range [0, 1)
		//net.connectOutputs(1);		// connect last (hidden) layer to output layer using weights of 1   <---------- problem!! adds extra inputs to output layer - fixed!

		// thresholds
		((SigmoidNeuron)(net.getLayer(1).get(1))).setThreshold(0f);	
		((SigmoidNeuron)(net.getLayer(1).get(2))).setThreshold(0f);
		((SigmoidNeuron)(net.getLayer(2).get(1))).setThreshold(0f);	

		// set weights
		net.setInitialWeights(0.0f, 1.0f);	// random weights between 0 and 1
		// layer 2 (output layer)
//		((Neuron)(net.getLayer(2).get(1))).setWeight(0f, 1);
//		((Neuron)(net.getLayer(2).get(1))).setWeight(0f, 2);
		// layer 1	(hidden layer)
//		((Neuron)(net.getLayer(1).get(1))).setWeight(0f, 1);
//		((Neuron)(net.getLayer(1).get(1))).setWeight(0f, 2);
//		((Neuron)(net.getLayer(1).get(2))).setWeight(0f, 1);
//		((Neuron)(net.getLayer(1).get(2))).setWeight(0f, 2);
		
		// set target Outputs
		float[] targetOutputs = new float[1];
		
		net.setTargetOutputs(targetOutputs);
		// set output ranges
		net.setOutputRange(1, 0, 35);
		targetOutputs[0] = 4.4f/(net.getMaxOutputRange(0)-net.getMinOutputRange(0));

		NeuralNetPanel np = new NeuralNetPanel(net);
		NeuralNetFrame frame = new NeuralNetFrame(np);
		frame.setNet(net);
		frame.setTitle("Neural Net");
		frame.setSize(600, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//frame.add(np);
		frame.setVisible(true);

//		JFrame frame = new JFrame("Neural Net");
//		frame.setSize(600, 480);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		NeuralNetPanel np = new NeuralNetPanel(net);
//		frame.add(np);
//		frame.setVisible(true);

		// back-propagation algorithm
//		float[] targetOutputs = new float[1];
//		targetOutputs = new float[net.numOfOutputs()];
//		targetOutputs[0] = 4.4f;	// ideal glucose level
//		System.out.println("Number of Outputs = " + net.numOfOutputs());
//		net.setTargetOutputs(targetOutputs);
//		int run = 1;
//		do
//		{
//			totalError = 0;
//			// step 1
//			net.forwardPropagate();
//			// print output values
//			//System.out.println(net.getOutputValues());
//			// step 2
//			net.calcOutputErrors();
//			// step 3: calculate new weights for output layer
//			net.calcOutputLayerWeights();
//			// step 4: calculate (back-propagate) errors for hidden layers
//			net.calcHiddenLayerErrors(1);
//
//			//net.calcHiddenLayerWeights();
//
//			net.calcInputLayerWeights();
//			// change hidden layer weights
//			//net.changeWeights(1);
//			
//			run++;
//			
//			for(int i=0; i< targetOutputs.length; i++) 
//			{
//				float error = targetOutputs[i] - net.getOutput(i+1);
//				totalError += Math.abs(error);
//			}
//			np.setError(totalError);
//			np.repaint();
//		}while(totalError > targetError);
//		
//		System.out.println("Run " + run + " error: " + totalError);
//		System.out.println("Neural Net is trained with Weights: \n" + net.getWeights());

		net.train();
		System.out.println(net.getInputs());
		System.out.println(net.getWeights());
		np.repaint();
	}
}
