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
	public JButton saveButton;
	public JButton loadButton;
	
	public NeuralNetControlGUI()
	{
		stepButton = new JButton("Next Iteration");
		add(stepButton,BorderLayout.PAGE_END);
		
		saveButton = new JButton("Save Network");
		add(saveButton, BorderLayout.PAGE_END);
		loadButton = new JButton("Load Network");
		add(loadButton, BorderLayout.PAGE_END);
	}
}
