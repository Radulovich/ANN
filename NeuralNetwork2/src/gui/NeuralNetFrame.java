package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import core.NeuralNet;
import core.SigmoidNeuron;

public class NeuralNetFrame extends JFrame implements ActionListener
{
	private NeuralNetControlGUI GUI_Control;
	private NeuralNet net;
	private NeuralNetPanel np;
	private JPanel mainPanel;
	private JPanel inputPanel;
	
	public NeuralNetFrame()
	{
		this(null);
		
	}
	
	public NeuralNetFrame(NeuralNetPanel np)
	{
		this.np = np;
		this.setSize(1000, 500);
		setLayout(new BorderLayout(10, 10));
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		add(mainPanel, BorderLayout.CENTER);
		
		inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		ImageIcon checkeredGrid = new ImageIcon("res/images/checkeredGrid2.png");
		ImageIcon arrayGrid = new ImageIcon("res/images/checkeredArray.png");
		inputPanel.add(new JButton(new ImageIcon(arrayGrid.getImage().getScaledInstance(30, 30, 0))));
		inputPanel.add(new JButton(new ImageIcon(checkeredGrid.getImage().getScaledInstance(30, 30, 0))));
		mainPanel.add(inputPanel);
		
		if(np != null)
			mainPanel.add(np);
		else
			mainPanel.add(new JPanel());
		GUI_Control = new NeuralNetControlGUI();
		
		add(GUI_Control,  BorderLayout.PAGE_END);
		GUI_Control.stepButton.addActionListener(this);
		GUI_Control.trainButton.addActionListener(this);
		GUI_Control.saveButton.addActionListener(this);
		GUI_Control.loadButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == GUI_Control.stepButton)
		{
			System.out.println("Next Iteration Button pressed...");
			np.setError(net.train(1));
			np.repaint();
		}else if(e.getSource() == GUI_Control.saveButton)
		{
			System.out.println("Save Network Button pressed...");
			saveNetwork();
		}else if(e.getSource() == GUI_Control.loadButton)
		{
			System.out.println("Load Network Button pressed...");
			loadNetwork();
			np.repaint();
		}else if(e.getSource() == GUI_Control.trainButton)
		{
			System.out.println("Train Network Button pressed...");
			net.train();
			np.repaint();
		} 

	}

	public NeuralNet getNet() {
		return net;
	}

	public void setNet(NeuralNet net) {
		this.net = net;
	}
	
	/**
	 * File format is as follows:
	 * Line 1: target error
	 * Line 2: minimum expected output, maximum expected output
	 * Line 3: learning rate
	 * Line 4: neuron type (binary = 1, sigmoid = 2)
	 * Line 5: number of hidden layers + 1 output layer
	 * Line 6: inputs
	 * Line 7: hidden layer weights
	 * Line n: output layer weights
	 * 
	 */
	public void saveNetwork()
	{
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Specify a file to save");
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			//This is where a real application would open the file.
			System.out.println("Saving: " + file.getName() + ".");	//TODO open a new file and read in weights, etc
			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
				//net.getInputs();
				writer.write(net.getTargetError() + "\n");
				writer.write(net.getMinOutputs() + "\n");
				writer.write(net.getMaxOutputs() + "\n");
				writer.write(net.getLearningRate() + "\n");
				writer.write(net.getNeuronType() + "\n");
				writer.write(net.getNumLayers()-1 + "\n");
				writer.write(net.getInputs() + "\n");
				writer.write(net.getWeights() + "\n"); 
				writer.write(net.getTargets() + "\n");
				writer.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} //you don't need to create a File object, FileWriter takes a string for the filepath as well
		} 
	}
	
	public void loadNetwork()	
	{
		float inputs[] = null;
		float hiddenLayers[];
		float outputs[];
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			//This is where a real application would open the file.
			System.out.println("Loading: " + file.getName() + ".");	//TODO open a new file and read in weights, etc
			Scanner sc;
			if(!net.loadNetwork(file))
				JOptionPane.showMessageDialog(null, "Error loading ANN file.");
			else
			{
				np.init();
			}
		}
	}
}
