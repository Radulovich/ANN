package core;

import java.util.ArrayList;

/**
 * Base class for all inputs to a neural net
 * @author radulov
 *
 */
public class Input<T>
{
	private ArrayList<T> data;
	
	public Input(T t)
	{
		data = new ArrayList<T>();
	}
	
	public T[] getArray()
	
	{
		return (T[]) data.toArray();
	}
}
