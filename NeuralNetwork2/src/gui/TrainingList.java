package gui;

import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class TrainingList extends JList 
{
	JScrollPane listScroller;
	DefaultListModel listModel;
	
	public TrainingList(ArrayList<float[]> data)
	{
		listModel = new DefaultListModel();
		if(data != null)
		{
			String dataString = "";
			for(int i=0; i<data.size(); i++)
			{
				dataString += data.get(i) + ", ";
			}
			listModel.addElement(dataString.subSequence(0, dataString.length()-2));
		}
		
		setModel(listModel);
		
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setLayoutOrientation(JList.HORIZONTAL_WRAP);
		setVisibleRowCount(-1);
	}
	
	public void setList(ArrayList<float[]> data)
	{
		if(data != null)
		{
			String dataString = "";
			for(int i=0; i<data.size(); i++)
			{
				dataString = "";
				for(int j=0; j<data.get(i).length; j++)
				{
					dataString += data.get(i)[j] + ", ";
				}
				listModel.addElement(dataString.subSequence(0, dataString.length()-2));
			}
		}
		
		setModel(listModel);
	}

}
