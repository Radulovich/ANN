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

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import core.NeuralNet;

public class NeuralNetFrame extends JFrame implements ActionListener
{
	private NeuralNetControlGUI GUI_Control;
	private NeuralNet net;
	private NeuralNetPanel np;
	
	public NeuralNetFrame(NeuralNetPanel np)
	{
		this.np = np;
		add(np);
		GUI_Control = new NeuralNetControlGUI();
		add(GUI_Control, BorderLayout.PAGE_END);
		GUI_Control.stepButton.addActionListener(this);
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
					net.getInputs();
					writer.write(net.getInputs() + "\n");
		            writer.write(net.getWeights()); 
		            writer.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} //you don't need to create a File object, FileWriter takes a string for the filepath as well

	         
	        } 
		}else if(e.getSource() == GUI_Control.loadButton)
		{
			System.out.println("Load Network Button pressed...");
			float inputs[];
			float hiddenLayers[];
			float outputs[];
			final JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            //This is where a real application would open the file.
	            System.out.println("Loading: " + file.getName() + ".");	//TODO open a new file and read in weights, etc
	            Scanner sc;
				try {
					sc = new Scanner(file);
					if (sc.hasNextLine()) {	// each line from the file is a separate layer (1st line = input, followed by hidden layers, 
												// followed by output layer
		                String line = sc.nextLine();
		                String tokens[] = line.split(",");
		                // first, get the inputs
		                inputs = new float[tokens.length];
		                for(int i=0; i<inputs.length; i++)
		                {
		                	inputs[i] = Float.parseFloat(tokens[i]);
		                }
		            }
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
					// Finally, get the output
//					if (sc.hasNextLine()) {	// each line from the file is a separate layer (1st line = input, followed by hidden layers, 
//						// followed by output layer
//						String line = sc.nextLine();
//						String tokens[] = line.split(",");
//						// first, get the outputs
//						outputs = new float[tokens.length];
//						for(int i=0; i<outputs.length; i++)
//						{
//							outputs[i] = Float.parseFloat(tokens[i]);
//						}
//					}

					sc.close();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(this, "Error loading ANN file.");
					e1.printStackTrace();
				}
	        } 
		}
	}

	public NeuralNet getNet() {
		return net;
	}

	public void setNet(NeuralNet net) {
		this.net = net;
	}
}
