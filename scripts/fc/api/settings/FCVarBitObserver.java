package scripts.fc.api.settings;

import java.util.stream.IntStream;

import org.tribot.api.General;
import org.tribot.api2007.types.RSVarBit;

public class FCVarBitObserver extends Thread
{
	private static final int CYCLE_TIME = 200;
	private static final int VARBITS_CHECKED = 10000;
	
	public boolean isRunning = true;
	
	private RSVarBit[] varbits;
	private FCVarBitListener listener;
	
	public FCVarBitObserver(FCVarBitListener listener)
	{
		this.listener = listener;
		varbits = getVarBits();
		start();
	}
	
	public void run()
	{
		while(isRunning)
		{
			try
			{
				RSVarBit[] temp = getVarBits();
				for(int index = 0; index < temp.length; index++)
				{
					if(temp[index] == null || varbits[index] == null)
						continue;
					
					if(temp[index].getValue() != varbits[index].getValue())
					{
						listener.varbitChanged(index, varbits[index].getValue(), temp[index].getValue());
						General.println("CHANGE DETECTED");
					}
				}
				
				varbits = temp;
				sleep(CYCLE_TIME);
			}
			catch(InterruptedException e)
			{
				General.println("VarbitObserver has been interrupted");
			}
		}
	}
	
	private RSVarBit[] getVarBits()
	{
		return IntStream.range(0, VARBITS_CHECKED)
							.mapToObj(i -> RSVarBit.get(i))
							.toArray(RSVarBit[]::new);
	}
}
