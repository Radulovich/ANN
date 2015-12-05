package neuralNets;

import javax.swing.JFrame;

import gui.NeuralNetPanel;
import core.NeuralNet;
import core.Neuron;
import core.SigmoidNeuron;

public class letterReader {

	/**
	 * This program sets up a neural net to model a 2-input XOR gate using Binary Neurons
	 */
	
	NeuralNet net;
	
	float[] inputA;
	float[] inputB;
	float[] inputC;
	float[] inputD;
	float[] inputE;
	
	float[] test1;	// imperfect test case
	
	float[] targetOutputs;
	float totalError = 0;
	float targetError = 0.1f;
	
	public static void main(String[] args) {
		letterReader LR = new letterReader();
	}
	
	public letterReader() {
		
		net = new NeuralNet(1, 10, NeuralNet.SIGMOID_NEURON);	// neural net with 1 hidden layer and 10 neurons per layer using Binary Neurons 
		net.setLearningRate(0.6f);
		
		float[] input1 = 	{0,0,1,0,0,
							 0,1,0,1,0,
							 1,0,0,0,1,
							 1,1,1,1,1,
							 1,0,0,0,1,
							 1,0,0,0,1,
							 1,0,0,0,1};
		inputA = input1;
		
		float[] input2 = 	{1,1,1,1,0,
							 1,0,0,0,1,
							 1,0,0,0,1,
							 1,1,1,1,0,
							 1,0,0,0,1,
							 1,0,0,0,1,
							 1,1,1,1,0};
		inputB = input2;
		
		float[] input3 = 	{1,1,1,1,1,
							 1,0,0,0,0,
							 1,0,0,0,0,
							 1,0,0,0,0,
							 1,0,0,0,0,
							 1,0,0,0,0,
							 1,1,1,1,1};
		inputC = input3;
		
		float[] input4 = 	{1,1,1,1,0,
							 1,0,0,0,1,
							 1,0,0,0,1,
							 1,0,0,0,1,
							 1,0,0,0,1,
							 1,0,0,0,1,
							 1,1,1,1,0};
		inputD = input4;
		
		float[] input5 = 	{1,1,1,1,1,
							 1,0,0,0,0,
							 1,0,0,0,0,
							 1,1,1,1,0,
							 1,0,0,0,0,
							 1,0,0,0,0,
							 1,1,1,1,1};
		inputE = input5;
		

		float[] test1 = 	{1,1,1,1,0,
							 1,0,0,0,1,
							 1,0,0,0,1,
							 1,0,0,0,1,
							 1,0,0,0,1,
							 1,0,0,0,1,
							 1,1,1,1,1};
		
		net.addInputLayer(inputA);	// add the inputs to the neural net model
		net.addOutputLayer(5);		// add 5 outputs in the output layer

		net.createConnections(0, 1);		// default connections with random weights between [0, 1)
		net.connectInputs(0, 1);			// connect inputs to 1st layer using random weights in the range [0, 1)
		//net.connectOutputs(0, 1);			// connect outputs in the output layer using random weights in the range [0, 1) -- [0,0] = "A", [0,1] = "B", ...

		JFrame frame = new JFrame("Neural Net");
		frame.setSize(600, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		NeuralNetPanel np = new NeuralNetPanel(net);
		frame.add(np);
		frame.setVisible(true);

		// back-propagation algorithm
		targetOutputs = new float[net.numOfOutputs()];
		
		
		net.setTargetOutputs(targetOutputs);
		np.repaint();
		System.out.println("Training...");
		int run = 1;
		do {
			totalError = 0;
			net.setInput(inputA);
			// letter A = [0, 0]
			targetOutputs[0] = 1;
			targetOutputs[1] = 0;
			targetOutputs[2] = 0;
			targetOutputs[3] = 0;
			targetOutputs[4] = 0;
			train();
			for(int i=1; i<= targetOutputs.length; i++) 
			{
				float error = targetOutputs[i-1] - net.getOutput(i);
				totalError += Math.abs(error);
			}

			
			net.setInput(inputB);
			targetOutputs[0] = 0;
			targetOutputs[1] = 1;
			targetOutputs[2] = 0;
			targetOutputs[3] = 0;
			targetOutputs[4] = 0;
			train();
			for(int i=1; i<= targetOutputs.length; i++) 
			{
				float error = targetOutputs[i-1] - net.getOutput(i);
				totalError += Math.abs(error);
			}
			
			net.setInput(inputC);
			targetOutputs[0] = 0;
			targetOutputs[1] = 0;
			targetOutputs[2] = 1;
			targetOutputs[3] = 0;
			targetOutputs[4] = 0;
			train();
			for(int i=1; i<= targetOutputs.length; i++) 
			{
				float error = targetOutputs[i-1] - net.getOutput(i);
				totalError += Math.abs(error);
			}
			
			net.setInput(inputD);
			targetOutputs[0] = 0;
			targetOutputs[1] = 0;
			targetOutputs[2] = 0;
			targetOutputs[3] = 1;
			targetOutputs[4] = 0;
			train();
			for(int i=1; i<= targetOutputs.length; i++) 
			{
				float error = targetOutputs[i-1] - net.getOutput(i);
				totalError += Math.abs(error);
			}
			
			net.setInput(inputE);
			targetOutputs[0] = 0;
			targetOutputs[1] = 0;
			targetOutputs[2] = 0;
			targetOutputs[3] = 0;
			targetOutputs[4] = 1;
			train();
			for(int i=1; i<= targetOutputs.length; i++) 
			{
				float error = targetOutputs[i-1] - net.getOutput(i);
				totalError += Math.abs(error);
			}
			
			run++;
			
			if(run%100000 == 0) 
			{
				System.out.println("Run " + run + " error: " + totalError);
				np.repaint();
			}
			
			if(totalError < 2)
				net.setLearningRate(0.3f);
			else
				net.setLearningRate(1f);
		}while (totalError > targetError);
		
		System.out.println("Run " + run + " error: " + totalError);
		System.out.println("Neural Net is trained with Weights: \n" + net.getWeights());
		
		// test trained net
		System.out.println("Testing trained net on inputA...");
		net.setInput(inputA);
		net.forwardPropagate();
		System.out.println(net.getOutputValues());
		
		System.out.println("Testing trained net on inputB...");
		net.setInput(inputB);
		net.forwardPropagate();
		System.out.println(net.getOutputValues());
		
		System.out.println("Testing trained net on inputC...");
		net.setInput(inputC);
		net.forwardPropagate();
		System.out.println(net.getOutputValues());
		
		System.out.println("Testing trained net on inputD...");
		net.setInput(inputD);
		net.forwardPropagate();
		System.out.println(net.getOutputValues());
		
		System.out.println("Testing trained net on inputE...");
		net.setInput(inputE);
		net.forwardPropagate();
		System.out.println(net.getOutputValues());
		
		System.out.println("Testing on input test1...");
		net.setInput(test1);
		net.forwardPropagate();
		System.out.println(net.getOutputValues());

		np.repaint();

	}
	
	public void train()
	{
//		int count = 1;
//		do
//		{
			//totalError = 0;
			// step 1
			net.forwardPropagate();
			// print output values
			//System.out.println(net.getOutputValues());
			// step 2
			net.calcOutputErrors();
			// step 3: calculate new weights for output layer
			net.calcOutputLayerWeights();
			// step 4: calculate (back-propagate) errors for hidden layers
			net.calcHiddenLayerErrors(1);

			//net.calcHiddenLayerWeights();

			net.calcInputLayerWeights();
			// change hidden layer weights
			//net.changeWeights(1);
			
			//System.out.println("Iteration " + count + " error = " + totalError);
//			count++;
//		}while (totalError > targetError);
//		System.out.println("Iteration " + count + " error = " + totalError);
	}

}