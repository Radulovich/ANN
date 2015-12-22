package neuralNets;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import core.NeuralNet;
import core.Neuron;
import core.SigmoidNeuron;
import gui.NeuralNetFrame;
import gui.NeuralNetPanel;

public class DiabetesANN 
{
	/**
	 * This program sets up a neural net to model a diabetic person
	 */
	
	// inputs are as follows: 
	// BG
	// carbs eaten
	// time of BG reading(in hours)
	// next time to test BG
	// physical activity (scale of 0 to 10, 0 being not active at all)
	
	NeuralNet net;
	NeuralNetPanel np;
	NeuralNetFrame frame;
	int FRAME_WIDTH = 1200;
	int FRAME_HEIGHT = 600;
	
	float[] inputs = {7.8f, 80f, 10+39/60f, 12+30/60, 1};
	// set target Outputs
	float[] rawTargetOutputs = new float[1];  // only one target: BG
	float[] normalizedTargetOutputs = new float[1];  // only one target: BG
	float targetError = 0.00001f;
	
	public static void main(String[] args) 
	{
		//Custom button text
		Object[] options = {"Yes, please",
		                    "No, thanks"};
		int n = JOptionPane.showOptionDialog(new JFrame(),
		    "Do you want to load an existing network? ",
		    "Diabetes ANN",
		    JOptionPane.YES_NO_CANCEL_OPTION,
		    JOptionPane.QUESTION_MESSAGE,
		    null,
		    options,
		    options[1]);

		if(n==1)
			new DiabetesANN();
		else
		{
			DiabetesANN diabetesNet = new DiabetesANN();
			diabetesNet.frame.loadNetwork();
			diabetesNet.net.train();
			System.out.println(diabetesNet.net.getInputs());
			System.out.println(diabetesNet.net.getWeights());
			diabetesNet.np.repaint();
		}
		
	}
	
	public DiabetesANN(float[] inputs, float[] targets, int numHiddenLayers, int numNeurons)
	{
		float totalError = 0;
		this.inputs = inputs;
		rawTargetOutputs = targets;
		
		net = new NeuralNet(numHiddenLayers, numNeurons, NeuralNet.SIGMOID_NEURON);	// neural net with 1 hidden layer and 2 neurons per layer using Binary Neurons 
		net.setLearningRate(0.3f);
		
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
		
		net.setTargetOutputs(targets);
		// set output ranges
		net.setOutputRange(1, 0, 35);
		// normalize targets
		normalizedTargetOutputs = new float[rawTargetOutputs.length];
		for(int i=0; i<normalizedTargetOutputs.length; i++)
			normalizedTargetOutputs[i] = rawTargetOutputs[i]/(net.getMaxOutputRange(0)-net.getMinOutputRange(0));

		np = new NeuralNetPanel(net);
		frame = new NeuralNetFrame(np);
		frame.setNet(net);
		frame.setTitle("Neural Net");
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setVisible(true);
		np.repaint();

		net.train();
		System.out.println(net.getInputs());
		System.out.println(net.getWeights());
		np.repaint();
	}
	
	public DiabetesANN()
	{
		float totalError = 0;
		
		net = new NeuralNet(1, 3, NeuralNet.SIGMOID_NEURON);	// neural net with 1 hidden layer and 2 neurons per layer using Binary Neurons 
		net.setLearningRate(0.3f);
		
		net.addInputLayer(inputs);	// add the inputs to the neural net model
		//net.normalizeInputs();
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
		
		net.setTargetOutputs(normalizedTargetOutputs);
		// set output ranges
		net.setOutputRange(1, 0, 35);
		normalizedTargetOutputs[0] = 4.4f/(net.getMaxOutputRange(0)-net.getMinOutputRange(0));

		np = new NeuralNetPanel(net);
		frame = new NeuralNetFrame(np);
		frame.setNet(net);
		frame.setTitle("Neural Net");
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.pack();
		frame.setVisible(true);
		
		//net.train();
		System.out.println(net.getInputs());
		System.out.println(net.getWeights());
		np.repaint();
		//frame.repaint();
	}
}
