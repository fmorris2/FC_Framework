package scripts.fc.framework.quest;

import org.tribot.api2007.types.RSVarBit;

public class VarBitBool extends QuestBool
{

	private int index;
	private int value;
	private Order order;
	
	public VarBitBool(int index, int value, boolean normal, Order order)
	{
		super(normal);
		this.index = index;
		this.value = value;
		this.order = order;
	}
	

	@Override
	public boolean value()
	{
		RSVarBit vb = RSVarBit.get(index);	
		return vb != null && order.evaluate(vb.getValue(), value);
	}

	public String toString()
	{
		return "VarBitBool for ("+index+","+value+"): " + validate();
	}

}
