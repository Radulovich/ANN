package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class NeuralNet 
{
	private int numLayers;	// number of hidden layers + 1 output layer
	private int neuronType;
	private ArrayList<InputNode> input;
	private ArrayList<float[]> trainingData;	// raw data sets
	private ArrayList<ArrayList<Node>> net;
	private float[] outputErrors;
	private float[] targetOutputs;  // normalized
	private float learningRate;
	
	private float targetError = 0.00001f;
	
	// range of the expected output (used to scale the output)
	private float[] minOutput;
	private float[] maxOutput;
	
	private int iterationNumber;
	
	public static final int BINARY_NEURON = 1;
	public static final int SIGMOID_NEURON = 2;
			
	
	public NeuralNet(int numLayers)
	{
		this(numLayers, 0, SIGMOID_NEURON);
	}
	
	public NeuralNet(int numLayers, int neuronType)
	{
		this(numLayers, 0, neuronType);
	}
	
	/**
	 * Creates a new neural net made of a given neuron type with a given number of hidden layers and a given number of neurons per layer.
	 * Note that the number of layers does NOT include the last output layer
	 * @param numHiddenLayers - number of hidden layers to create (ie. does not include input or output layers)
	 * @param numNeurons - number of neurons per layer.
	 * @param neuronType - type of neuron to use in the neural net model
	 */
	public NeuralNet(int numHiddenLayers, int numNeurons, int neuronType)
	{
		init(numHiddenLayers, numNeurons, neuronType);
	}
	
	public void init(int numHiddenLayers, int numNeurons, int neuronType)
	{
		this.numLayers = numHiddenLayers;
		this.neuronType = neuronType;
		input = new ArrayList<InputNode>();
		net = new ArrayList<ArrayList<Node>>();
		learningRate = 0.2f;
		iterationNumber = 0;
		for(int i=0; i< numLayers; i++)
		{
			ArrayList<Node> layer = new ArrayList<Node>();
			layer.add(new InputNode(-1));	// extra node for bias -- should be -1
			net.add(layer);
		}
		
		switch (neuronType) {
			case BINARY_NEURON:
				for(int i=1; i<=numLayers; i++){
					for(int j=0; j<numNeurons; j++){
						addNeuron(new BinaryNeuron(), i);
					}
				}
				break;
			default:	// defaults to Sigmoid neuron
				for(int i=1; i<=numLayers; i++){
					for(int j=0; j<numNeurons; j++){
						addNeuron(new SigmoidNeuron(), i);
					}
				}
				break;
			}
		outputErrors = new float[numOfOutputs()];
		targetOutputs = new float[numOfOutputs()];
		minOutput = new float[1];
		maxOutput = new float[1];
		// set default range
		for(int i=0; i< minOutput.length; i++)
		{
			minOutput[i] = 0;
			maxOutput[i] = 1;
		}
	}
	
	/**
	 * clears the current network
	 */
	public void clear()
	{
		input.clear();
		for(int i=0; i< numLayers; i++)
		{
			net.get(i).clear();
		}
		net.clear();
	}
	
	public boolean loadInputData(File f)
	{
		boolean result = true;
		Scanner sc;
		ArrayList<float[]> inputs = new ArrayList<float[]>();
		
		try {
			sc = new Scanner(f);
			String tokens[];
			String line;
			float[] inputLine;	// array to hold 1 line of input data
			
			while (sc.hasNextLine()) {	// each line from the file is a separate input set
				line = sc.nextLine();
				if(line.trim().startsWith("#"))
					continue;
				tokens = line.split(",");
				inputLine = new float[tokens.length+1];	// leave one more for the bias
				inputLine[0] = -1; // bias
				for(int i=1; i<=tokens.length; i++)
				{
					inputLine[i] = Float.parseFloat(tokens[i-1]);		
				}
				inputs.add(inputLine);
			}
			trainingData = inputs;
			System.out.println("Loaded " + trainingData.size() + " sets of training data.");
		}	catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(null, "Error loading Input Data file.");
				result = false;
		}
			
		return result;
	}
	
	public boolean loadNetwork(File f)
	{
		boolean result = true;
		float inputsWithBias[] = null;
		float inputs[] = null;
		Scanner sc;
		
		try {
			sc = new Scanner(f);
			if (sc.hasNextLine()) {	// each line from the file is a separate layer (1st line = input, followed by hidden layers, 
				String tokens[];
				// followed by output layer
				String line = sc.nextLine();
				targetError = Float.parseFloat(line);
				line = sc.nextLine();
				tokens = line.split(",");
				minOutput = new float[tokens.length];
				for(int i=0; i<tokens.length; i++)
				{
					minOutput[i] = Float.parseFloat(tokens[i]);		
				}
				line = sc.nextLine();
				tokens = line.split(",");
				maxOutput = new float[tokens.length];
				for(int i=0; i<tokens.length; i++)
				{
					maxOutput[i] = Float.parseFloat(tokens[i]);		
				}
				line = sc.nextLine();
				learningRate = Float.parseFloat(line);
				line = sc.nextLine();
				neuronType = Integer.parseInt(line);
				line = sc.nextLine();
				numLayers = Integer.parseInt(line);
				
				line = sc.nextLine();
				tokens = line.split(",");
				// first, get the inputs
				inputsWithBias = new float[tokens.length];
				for(int i=0; i<inputsWithBias.length; i++)
				{
					inputsWithBias[i] = Float.parseFloat(tokens[i]);
				}
				// create second set of inputs without bias
				inputs = new float[inputsWithBias.length - 1];
				for(int i=0; i<inputs.length; i++)
				{
					inputs[i] = inputsWithBias[i+1];
				}
			}
			//addInputLayer(inputs);	// add the inputs to the neural net model
			//normalizeInputs();
			// Now get the hidden layers
			ArrayList<float[]> layers = new ArrayList<float[]>();
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String tokens[] = line.split(",");
				layers.add(new float[tokens.length]);
				for(int i=0; i<layers.get(layers.size()-1).length; i++)
				{
					layers.get(layers.size()-1)[i] = Float.parseFloat(tokens[i]);
				}
			}
			sc.close();
			// Now complete creating the new network
			clear();	// clear the current network
			init(layers.size()-2, 3, SIGMOID_NEURON);  // last 2 arguments could be read from file in future versions
			setLearningRate(learningRate);
			
			addInputLayerWithBias(inputsWithBias);	// add the inputs to the neural net model
			//normalizeInputs();
			addOutputLayer(1);		// add one output in the output layer

			createConnections(0, 1);	// default connections with random weights between [0, 1)
			connectInputs(0, 1);	   	// connect inputs to 1st layer using random weights in the range [0, 1)
			// set thresholds
			for(int i=1; i<=this.numLayers; i++)
			{
				for(int j=0; j<getLayer(i).size()-1; j++)
				{
					((SigmoidNeuron)(getLayer(i).get(j+1))).setThreshold(0f);	
				}
			}
				
			
			/**
			 * Sets the weight of a connection of a node within a certain layer.  Useful to set weights of a pre-trained network.
			 * @param w	- the weight to set
			 * @param layerNum - the layer where the node resides (first layer = 1)
			 * @param nodeNum - the node whose weight we want to set on one of its connections
			 * @param connectionNum - the particular connection of the node whose weight is to be set (index starts at 1 because bias is at zero)
			 */
			
			// set weights
			int n = 0;
			for(int i=0; i<numLayers; i++)
			{
				for(int j=0; j<net.get(i).size(); j++)	// for each node
				{
					n = 0;
					for(int k=0; k<net.get(i).get(j).getNumWeights(); k++)
						setWeight(layers.get(i)[n++], i+1, j, k);
				}
			}
			
			for(int i=0; i<targetOutputs.length; i++)
			{
				targetOutputs[i] = layers.get(layers.size()-1)[i];
			}
			setTargetOutputs(targetOutputs);
			
		}catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(null, "Error loading ANN file.");
				result = false;
		}
		
		return result;
	}
	
	/**
	 * Sets the possible range of outputs.
	 * @param output	The output to set (starts at 1)
	 * @param min		The min value of this output
	 * @param max		The max value of this output
	 */
	public void setOutputRange(int output, float min, float max)
	{
		if(output <= minOutput.length)
		{
			minOutput[output-1] = min;
			maxOutput[output-1] = max;
		}
	}
	
	/**
	 * Sets all inputs
	 * @param min
	 * @param max
	 */
	public void setOutputRange(float min, float max)
	{
		for(int i=1; i<=minOutput.length; i++)
		{
			setOutputRange(i, min, max);
		}
	}
	
	public float getMaxOutputRange(int i)
	{
		return maxOutput[i];
	}
	
	public float getMinOutputRange(int i)
	{
		return minOutput[i];
	}
	
	// normalizes the input data using the formula x_normnalized = (x - mean) / standard deviation
	// note: the method destroys (overwrites) the original inputs
	public void normalizeInputs()
	{
		float mean;
		float stdDeviation;
		
		// calc mean of input
		float sum = 0;
		for(InputNode node : input)
		{
			sum += node.getOutput();
		}
		mean = sum / input.size();
		
		// calc standard deviation
		sum = 0;
		for(InputNode node : input)
		{
			sum += Math.pow(node.getOutput() - mean, 2);
		}
		stdDeviation = (float)Math.sqrt(sum);
		
		// now finally compute the normalized inputs
		for(InputNode node : input)
		{
			node.setInput((node.getOutput() - mean)/stdDeviation);
		}
	}

	
	public void setTargetOutputs(float[] t)
	{
		assert(t.length == getLayer(getNumLayers()).size());
		targetOutputs = t;
	}
	
	public void addNeuron(Neuron n, int layer)		// add a single neuron to a certain layer, layers start at 1
	{
		net.get(layer-1).add(n);
	}
	
	public void addInputLayer(float[] in )
	{
		input.add(new InputNode(-1));	// extra node for bias - should be -1
		for(int i=0; i<in.length; i++)	
		{
			input.add(new InputNode(in[i]));
		}
	}
	
	public void addInputLayerWithBias(float[] in )
	{
		for(int i=0; i<in.length; i++)	
		{
			input.add(new InputNode(in[i]));
		}
	}
	
	/**
	 * Sets the input to be the ith training data
	 * @param i
	 */
	public void setInput(int i)
	{
		if(trainingData == null)
			return;
		if(i <= trainingData.size())
			setInput(trainingData.get(i-1));
	}
	
	/**
	 * Sets inputs from array in.  Note that the size of the array in must be equal to the size of this.input
	 * @param in
	 */
	public void setInput(float[] in)
	{
		if(in.length != input.size())
		{
			System.out.println("Failed to set input: Training input data size does not match ANN input size.");
			return;
		}
		for(int i=0; i<in.length; i++)	
		{
			InputNode inode = input.get(i);
			inode.setInput(in[i]);
		}
	}
	
	public ArrayList<InputNode> getInputLayer()
	{
		return input;
	}
	
	/**
	 * adds an output layer to the network with a given amount of output nodes
	 * @param numberOfOutputs
	 */
	public void addOutputLayer(int numberOfOutputs )
	{
		ArrayList<Node> newLayer = new ArrayList<Node>();
		newLayer.add(new InputNode(-1));	// should be -1
		net.add(newLayer);
		numLayers++;
		
		for(int i=0; i<numberOfOutputs; i++)
		{
			switch (neuronType) {
				case BINARY_NEURON:
					newLayer.add(new BinaryNeuron());
					break;
				default:
					newLayer.add(new SigmoidNeuron());
					break;
			}	
		}
		outputErrors = new float[numberOfOutputs];
		targetOutputs = new float[numOfOutputs()];
	}
	
	public int numOfOutputs() 
	{
		if(numLayers==0)
			return 0;
		return net.get(numLayers-1).size()-1;	// exclude bias
	}
	
	/**
	 * calculates errors of output neurons and returns the total error for the output layer
	 * @return the total error for the output layer
	 */
	public float calcOutputErrors()
	{
		float totalError = 0;
		ArrayList<Node> outputLayer = getLayer(numLayers);
		for(int i=1; i<=outputErrors.length; i++)	{	// skip bias
			outputErrors[i-1] = outputLayer.get(i).getOutput() * (1 -  outputLayer.get(i).getOutput()) * (targetOutputs[i-1] -  outputLayer.get(i).getOutput());
			//System.out.println("Error for Node[" + numLayers + ", " + i + "] = " + outputErrors[i-1]);
			totalError += Math.abs(outputErrors[i-1]);
			outputLayer.get(i).setError(outputErrors[i-1]);
		}
		return totalError;
	}
	
	/**
	 * calculate and change the output layer weights based on the previously calculated errors of the output neurons
	 */
	public void calcOutputLayerWeights()
	{
		ArrayList<Node> outputLayer = getLayer(numLayers);
		for(int i=1; i<=outputErrors.length; i++)		// skip bias
		{
			for(int j=0; j<outputLayer.get(i).getInputs().size(); j++ )
			{
				float w1 = outputLayer.get(i).getWeight(j);
				float input = outputLayer.get(i).getInputs().get(j).getOutput();
				float newWeight = w1 + learningRate * outputErrors[i-1] * input;
				outputLayer.get(i).setWeight(newWeight, j);
				//System.out.println("New weight between Nodes [" + i + ", " + j + "] = " + newWeight);
			}
		}
	}
	
	/**
	 * calculate the hidden layer errors for layer n and returns the total error produced
	 */
	public float calcHiddenLayerErrors(int n)
	{
		float totalError = 0;
		for(int i=0; i<net.get(n-1).size(); i++)
		{
			Node node = net.get(n-1).get(i);	// this is the node whose error is to be calculated
			float error = 0;
			for(int j=1; j<net.get(n).size(); j++)	// skip bias node
			{
				Node nextLayerNode = net.get(n).get(j);
				error +=  (nextLayerNode.getError() * nextLayerNode.getWeight(i));
			}
			error = node.getOutput() * (1 - node.getOutput()) * error;
			node.setError(error);
			totalError += Math.abs(error);
			
			//System.out.println("Error on Node [" + n + ", " + i + "] = " + error);
		}
		return totalError;
	}
	
	public void calcHiddenLayerWeights()	// needs to be rewritten
	{
		for(int i=numLayers-1; i>0; i--)	// for each hidden layer
		{
			float error = 0;
			for(int j=0; j<net.get(i).size(); j++)		// for each node j in ith layer
			{
				for(int k=0; k<net.get(i).get(j).getNumOutputs(); k++)		// for each output k in node j
				{
					Node n = net.get(i).get(j).getOutputs().get(k);
					error += n.getWeight(k) * n.getError();
				}
				float output = net.get(i).get(j).getOutput();
				error = output * (1 - output) * error;	// this is error for node j on layer i
				//System.out.println("Error for Node[" + i + ", " + j + "] = " + error);
				net.get(i).get(j).setError(error);
			}
			changeWeights(i);
		}
	}
	
	/**
	 * calculate the input to 1st layer weights
	 */
	public void calcInputLayerWeights()
	{
		for(int i=0; i<getInputLayer().size(); i++)
		{
			Node n_input = getInputNode(i);
			for(int j=0; j<n_input.getNumOutputs(); j++)
			{
				Node n_output = n_input.getOutputs().get(j);
				float newWeight = n_output.getWeight(i) + learningRate * n_output.getError() * n_input.getOutput();
				n_output.setWeight(newWeight, i);
				//System.out.println("New weight between Input Node [" + i + "], and Layer 1 Node [" + j + "] = " + newWeight);
			}
		}
	}
	
	/**
	 * Sets the weight of a connection of a node within a certain layer.  Useful to set weights of a pre-trained network.
	 * @param w	- the weight to set
	 * @param layerNum - the layer where the node resides (first layer = 1)
	 * @param nodeNum - the node whose weight we want to set on one of its connections
	 * @param connectionNum - the particular connection of the node whose weight is to be set (index starts at 1 because bias is at zero)
	 */
	public void setWeight(float w, int layerNum, int nodeNum, int connectionNum)
	{
		ArrayList<Node> layer = getLayer(layerNum);
		layer.get(nodeNum).setWeight(w, connectionNum);
	}
	
	/**
	 * change weights of layer layerNum using precalculated errors
	 * @param layerNum - layer to be altered (layers begin at 1)
	 */
	public void changeWeights(int layerNum)
	{
		ArrayList<Node> layer = getLayer(layerNum);
		for(int i=0; i<layer.size(); i++)		// for all nodes on this layer
		{
			float newWeight = layer.get(i).getWeight(i) + learningRate*layer.get(i).getError()*layer.get(i).getOutput();
			layer.get(i).setWeight(newWeight, i);
		}
	}
	
	public void changeOutputWeights()
	{
		ArrayList<Node> outputLayer = getLayer(getNumLayers());
		ArrayList<Node> previousLayer = getLayer(getNumLayers()-1);
		
		for(int i=0; i<outputLayer.size(); i++)		// for all output nodes
		{
			float newWeight = outputLayer.get(i).getWeight(i) + learningRate*outputErrors[i]*previousLayer.get(i).getOutput();
			outputLayer.get(i).setWeight(newWeight, i);
		}
	}
	
	/**
	 * adds neurons to a given layer
	 * @param num - number of neurons to add
	 * @param layer - layer to add the neurons to (layers are numbered 1, 2, 3, ...)
	 */
	public void addNeurons(int num, int layer)
	{
		for(int i=0; i<num; i++)
		{
			addNeuron(new Neuron(), layer);
		}
		
		outputErrors = new float[numOfOutputs()];
	}
	
	public int getNumLayers()
	{
		return numLayers;
	} 
	
	/**
	 * returns array of neurons at given layer
	 * @param layer - first layer = 1
	 * @return
	 */
	public ArrayList<Node> getLayer(int layer)
	{
		return net.get(layer-1);
	}
	
	/**
	 * connects input in to neuron on a given layer
	 * @param in
	 * @param layer
	 * @param neuron
	 */
	public void connectInput(int in, int layer, int neuron, float weight)
	{
		input.get(in).connectOutput(net.get(layer-1).get(neuron), weight);
	}
	
	/**
	 * connects neuron on given layer to output out
	 * @param layer - layer on which neuron lives (layers start at 1)
	 * @param out - output node to be connected to
	 * @param neuron - neuron from given layer
	 */
	public void connectOutput(int layer, int out, int neuron, float weight)
	{
		net.get(layer-1).get(neuron).connectOutput(net.get(numLayers-1).get(out), weight);
	}
	
	/**
	 * connect all outputs of second-last layer to each neuron of the last layer with the given weight
	 * @param weight
	 */
	public void connectOutputs(float weight)		
	{
		if(numLayers<=1)
			return;
		for(int i=0; i<net.get(numLayers-2).size(); i++) 
		{
			for(int j=1; j<net.get(numLayers-1).size(); j++)
				connectOutput(numLayers-1, j, i, weight);
		}
	}
	
	/**
	 * connect all outputs of second-last layer to each neuron of the last layer with random weights in the range [randStart, randEnd)
	 * @param randStart
	 * @param randEnd
	 */
	public void connectOutputs(float randStart, float randEnd)		
	{
		Random rand = new Random();
		if(numLayers<=1)
			return;
		for(int i=0; i<net.get(numLayers-2).size(); i++) 
		{
			for(int j=1; j<net.get(numLayers-1).size(); j++)
			{
				float weight = rand.nextFloat() * (randEnd-randStart) + randStart;
				connectOutput(numLayers-1, j, i, weight);
			}
		}
	}
	
	public InputNode getInputNode(int i)
	{
		return input.get(i);
	}
	
	/**
	 *  connect output of one neuron to input of a second neuron with a given weight
	 * @param layer1 - layer where 1st neuron lives (first layer = 1)
	 * @param index1 - location on layer where 1st neuron lives
	 * @param layer2 - layer where 2nd neuron lives
	 * @param index2 - location on layer where 2nd neuron lives
	 * @param weight - 
	 */
	public void connect(int layer1, int index1, int layer2, int index2, float weight)		
	{
		Node n1 = net.get(layer1-1).get(index1);
		Node n2 = net.get(layer2-1).get(index2);
		n1.connectOutput(n2, weight);
	}
	
	/**
	 * connect all inputs using the given weight to each neuron of the first layer
	 * @param weight
	 */
	public void connectInputs(float weight)		
	{
		if(getNumLayers() == 1)
		{
			for(int i=0; i<input.size(); i++) 
			{
				for(int j=1; j<net.get(0).size(); j++) {
					input.get(i).connectOutput(net.get(0).get(j), weight);
				}
			}
		}else {
			for(int i=0; i<input.size(); i++) 
			{
				for(int j=1; j<net.get(0).size(); j++) {	// j=1 to avoid connecting input to bias of first layer
					input.get(i).connectOutput(net.get(0).get(j), weight);
				}
			}
		}
	}
	
	/**
	 * connect all inputs using random weights to each neuron of the first layer
	 * @param weight
	 */
	public void connectInputs(float randStart, float randEnd)		
	{
		Random rand = new Random();
		if(getNumLayers() == 1)
		{
			for(int i=0; i<input.size(); i++) 
			{
				for(int j=1; j<net.get(0).size(); j++) {
					float weight = rand.nextFloat() * (randEnd-randStart) + randStart;
					input.get(i).connectOutput(net.get(0).get(j), weight);
				}
			}
		}else {
			for(int i=0; i<input.size(); i++) 
			{
				for(int j=1; j<net.get(0).size(); j++) {	// j=1 to avoid connecting input to bias of first layer
					float weight = rand.nextFloat() * (randEnd-randStart) + randStart;
					input.get(i).connectOutput(net.get(0).get(j), weight);
				}
			}
		}
	}
	
	/**
	 * creates a network with a given number of layers and a given number of neurons per layer
	 * @param numLayers - number of layers, excluding input layer and output layer
	 * @param numNeurons
	 */
	public void createNet(int numLayers, int numNeurons)
	{
		net = new ArrayList<ArrayList<Node>>();
		for(int i=0; i< numLayers; i++)
		{
			ArrayList<Node> layer = new ArrayList<Node>();
			net.add(layer);
		}
		for(int i=0; i<numLayers; i++){
			for(int j=0; j<numNeurons; j++){
				addNeuron(new Neuron(), i);
			}
		}
	}
	
	/**
	 * creates default connections with given weight between all nodes where every layer's neurons are connected to every neuron in the layer above it
	 */
	public void createConnections(float weight)
	{
		for(int i=1; i<numLayers; i++){
			for(int j=0; j<net.get(i-1).size(); j++){
				for(int k=1; k<net.get(i).size(); k++){	// start at k=1 to avoid connecting to bias node
					connect(i, j, i+1, k, weight);
				}
			}
		}
	}
	
	/**
	 * creates default connections with random weights between all nodes where every layer's neurons are connected to every neuron in the layer above it
	 * @param randStart - beginning of random number range, inclusive
	 * @param randEnd - end of random number range, exclusive
	 */
	public void createConnections(float randStart, float randEnd)
	{
		Random rand = new Random();
		for(int i=1; i<numLayers; i++){
			for(int j=0; j<net.get(i-1).size(); j++){
				for(int k=1; k<net.get(i).size(); k++){	// start at k=1 to avoid connecting to bias node
					float weight = rand.nextFloat() * (randEnd-randStart) + randStart;
					connect(i, j, i+1, k, weight);
				}
			}
		}
	}
	
	/**
	 * Set all weights to w
	 * @param w - default weight
	 */
	public void setInitialWeights(float w)
	{
		for(int i=0; i<numLayers; i++){
			for(int j=0; j<net.get(i).size(); j++){
				net.get(i).get(j).setAllWeights(w);
			}
		}
	}
	
	/**
	 * Set all weights randomly within a range
	 * @param randStart - smallest random value
	 * @param randEnd - largest random value
	 */
	public void setInitialWeights(float randStart, float randEnd)
	{
		Random rand = new Random();
		float weight;
		for(int i=0; i<numLayers; i++){
			for(int j=0; j<net.get(i).size(); j++){
				for(int k=0; k<net.get(i).get(j).getNumWeights(); k++)
				{
					weight = rand.nextFloat() * (randEnd-randStart) + randStart;
					net.get(i).get(j).setWeight(weight, k);
				}
				
				//net.get(i).get(j).setAllWeights(weight);
			}
		}
	}
	
	/**
	 * calculates new outputs based on current inputs and current weights
	 */
	public void forwardPropagate()
	{
		for(int i=0; i<numLayers; i++){
			for(int j=0; j<net.get(i).size(); j++){
				net.get(i).get(j).calcOutput();
			}
		}
	}
	
	public String toString() 
	{
		String result = "";
		
		result = "Neural Net Topology Info:";
		result += "\nNumber of layers = " + net.size();
		result += "\nNumber of inputs = " + input.size();
		result += "\nNumber of outputs = " + net.get(net.size()-1).size();
		
		for(InputNode n : input)
			result += "\n" + n.toString();
		
		result += "\nOutputs: ";
		for(Node n : net.get(numLayers-1))
			result += "\n" + n.toString();
		
		return result;
	}

	public float getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(float learningRate) {
		this.learningRate = learningRate;
	}
	
	/**
	 * returns the ith output of the neural net 
	 * @param i - index of output (starts at 1)
	 * @return the ith output of the neural net without any adjustments or normalization
	 */
	public float getRawOutput(int i)
	{
		return getLayer(getNumLayers()).get(i).getOutput();
	}
	
	/**
	 * returns the ith output of the neural net 
	 * @param i - index of output (starts at 1)
	 * @return the ith output of the neural net adjusted by the pre-defined range
	 */
	public float getOutput(int i)
	{
		float out = getLayer(getNumLayers()).get(i).getOutput();	// raw output between 0 and 1
		float factor = maxOutput[i-1] - minOutput[i-1];
		return out*factor + minOutput[i-1];
	}
	
	// print output values
	public String getOutputValues() 
	{
		String str = "";
		for(int i=1; i<getLayer(getNumLayers()).size(); i++)	// skip bias
			str += "Output " + i + " = " + getLayer(getNumLayers()).get(i).getOutput() + "\n";
		
		return str;
	}
	
	public String getInputs()
	{
		String str = "";
		for(InputNode i: input)
		{
			str+= i.getOutput() + ", ";
		}
		return str.substring(0, str.length()-2);
	}
	
	public String getWeights()
	{
		String str = "";
		for(int i=0; i<numLayers; i++){
			for(int j=1; j<net.get(i).size(); j++){
				for(int k=0; k<net.get(i).get(j).getNumInputs(); k++) {
					str += net.get(i).get(j).getWeight(k) + ", ";
				}
				//str += "\t";
			}
			str = str.substring(0, str.length()-2) + "\n";	// remove trailing comma and add a newline
		}
		str = str.substring(0, str.length()-1);
		return str;
	}
	
	public void updateNetwork()
	{
		// step 1
		forwardPropagate();
		// print output values
		//System.out.println(net.getOutputValues());
		// step 2
		calcOutputErrors();
		// step 3: calculate new weights for output layer
		calcOutputLayerWeights();
		// step 4: calculate (back-propagate) errors for hidden layers
		calcHiddenLayerErrors(1);

		//calcHiddenLayerWeights();

		calcInputLayerWeights();
		// change hidden layer weights
		//changeWeights(1);
	}
	
	public int getIterationNumber()
	{
		return iterationNumber;
	}
	
	/**
	 * 
	 * @param normalizedTargetOutputs The target output(s) of the NN
	 * @param iterations Number of times to run the upateNetwork algorithm
	 * @return error
	 */

	public float train(int iterations)	// target outputs should be defined before this method is called
	{
		float totalError = 0;
		//float[] targetOutputs = new float[1];
		//targetOutputs = new float[numOfOutputs()];
		//targetOutputs[0] = 4.4f;
		System.out.println("Number of Outputs = " + numOfOutputs());
		setTargetOutputs(targetOutputs);
		int run = 0;
		
		do
		{
			totalError = 0.0f;
			updateNetwork();
			
			run++;
			
			for(int i=0; i< targetOutputs.length; i++) 
			{
				float error = targetOutputs[i] - getRawOutput(i+1);
				totalError += Math.abs(error);
			}
			iterationNumber++;
			//totalError = 0.0f;
		}while(run < iterations);
		return totalError;
	}
	
	/**
	 *  Runs the updateNetwork algorithm until the target output(s) are within acceptable range
	 */
	public void train()	// should pass in array of input training data?
	{
		float totalError = 0;
		System.out.println("Number of Outputs = " + numOfOutputs());
		setTargetOutputs(targetOutputs);

			do
			{
				totalError = 0.0f;
				updateNetwork();

				for(int i=1; i<= targetOutputs.length; i++) 
				{
					float error = targetOutputs[i-1] - getRawOutput(i);
					totalError += Math.abs(error);
				}
				System.out.println("error = " + totalError);

				iterationNumber++;

			}while(totalError > targetError);

	}
	
	/**
	 *  Runs the updateNetwork algorithm on entire set of training data until the target output(s) are within acceptable range
	 */
	public void train(ArrayList<float[]> data)	// should pass in array of input training data?
	{
		float totalError = 0;
		System.out.println("Number of Outputs = " + numOfOutputs());
		setTargetOutputs(targetOutputs);
		
		for(int j=0; j<data.size(); j++)	// go through all training data
		{
			float[] inputData = data.get(j);
			setInput(inputData);

			do
			{
				totalError = 0.0f;
				updateNetwork();

				for(int i=1; i<= targetOutputs.length; i++) 
				{
					float error = targetOutputs[i-1] - getRawOutput(i);
					totalError += Math.abs(error);
				}
				System.out.println("error = " + totalError);

				iterationNumber++;

			}while(totalError > targetError);
		}

	}

	public String getTargets() 
	{
		String result = ""; 
		for(float f : targetOutputs)
		{
			result += f + ", ";
		}
		return result.substring(0, result.length()-2);
	}

	public float getTargetError() {
		return targetError;
	}

	public void setTargetError(float targetError) {
		this.targetError = targetError;
	}
	
	public String getMinOutputs()
	{
		String result = ""; 
		for(float m : minOutput)
		{
			result += m + ", ";
		}
		return result.substring(0, result.length()-2);
	}
	
	public String getMaxOutputs()
	{
		String result = ""; 
		for(float m : maxOutput)
		{
			result += m + ", ";
		}
		return result.substring(0, result.length()-2);
	}

	public int getNeuronType() {
		return neuronType;
	}

	public void setNeuronType(int neuronType) {
		this.neuronType = neuronType;
	}

	public void setNumLayers(int numLayers) {
		this.numLayers = numLayers;
	}

	public ArrayList<float[]> getTrainingData() {
		return trainingData;
	}
}
