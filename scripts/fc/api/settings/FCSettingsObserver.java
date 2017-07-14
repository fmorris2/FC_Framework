package scripts.fc.api.settings;

import java.util.ArrayList;
import java.util.List;

import org.tribot.api2007.Game;
import org.tribot.api2007.types.RSVarBit;

public class FCSettingsObserver extends Thread
{
	private final int CYCLE_TIME = 500;
	
	private int[] settingsArray;
	private List<VarBitWrapper> varBits;
	private FCSettingsListener listener;
	
	public FCSettingsObserver(FCSettingsListener listener)
	{
		this.listener = listener;
		this.settingsArray = Game.getSettingsArray().clone();
		varBits = getVarBits();
		
		start();
	}
	
	public void run()
	{
		while(true)
		{
			try
			{
				int[] newSettings = Game.getSettingsArray().clone();
				List<VarBitWrapper> newVarBits = getVarBits();
				
				for(int i = 0; i < newSettings.length; i++)
				{
					if(newSettings[i] != settingsArray[i])
						listener.settingChanged(i, settingsArray[i], newSettings[i]);
				}
				
				for(int i = 0; i < newVarBits.size(); i++)
				{
					if(varBits.get(i).VALUE != newVarBits.get(i).VALUE)
						listener.varBitChanged(varBits.get(i).SETTING, i, varBits.get(i).VALUE, newVarBits.get(i).VALUE);
				}
				
				varBits = newVarBits;
				settingsArray = newSettings;
				
				Thread.sleep(CYCLE_TIME);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private List<VarBitWrapper> getVarBits()
	{
		List<VarBitWrapper> varBits = new ArrayList<>();
		int i = 0;
		RSVarBit bit;
		
		while((bit = RSVarBit.get(i)) != null)
		{
			varBits.add(new VarBitWrapper(bit.getConfigID(), bit.getValue()));
			i++;
		}
		return varBits;
	}
	
	private class VarBitWrapper 
	{
		public final int SETTING, VALUE;
		public VarBitWrapper(int setting, int value)
		{
			SETTING = setting;
			VALUE = value;
		}
	}
}
