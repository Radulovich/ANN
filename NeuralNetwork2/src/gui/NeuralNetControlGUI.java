package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class NeuralNetControlGUI extends JPanel 
{
	
	public JButton stepButton;
	public JButton trainButton;
	public JButton saveButton;
	public JButton loadButton;
	public JButton calcOutputButton;
	public JButton loadDataButton;
	
	public NeuralNetControlGUI()
	{
		loadDataButton = new JButton("Load Data");
		add(loadDataButton, BorderLayout.PAGE_END);
		
		calcOutputButton = new JButton("Calc Output");
		add(calcOutputButton,BorderLayout.PAGE_END);
		
		stepButton = new JButton("Next Iteration");
		add(stepButton,BorderLayout.PAGE_END);
		
		trainButton = new JButton("Train Network");
		add(trainButton,BorderLayout.PAGE_END);
		
		saveButton = new JButton("Save Network");
		add(saveButton, BorderLayout.PAGE_END);
		
		loadButton = new JButton("Load Network");
		add(loadButton, BorderLayout.PAGE_END);
	}
}
