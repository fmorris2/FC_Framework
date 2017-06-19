package scripts.fc.api.settings;

import org.tribot.api2007.Game;

public class FCSettingsObserver extends Thread
{
	private final int CYCLE_TIME = 500;
	
	private int[] settingsArray;
	private FCSettingsListener listener;
	
	public FCSettingsObserver(FCSettingsListener listener)
	{
		this.listener = listener;
		this.settingsArray = Game.getSettingsArray().clone();
		
		start();
	}
	
	public void run()
	{
		while(true)
		{
			try
			{
				int[] newSettings = Game.getSettingsArray().clone();
				
				for(int i = 0; i < newSettings.length; i++)
				{
					if(newSettings[i] != settingsArray[i])
						listener.settingChanged(i, settingsArray[i], newSettings[i]);
				}
				
				settingsArray = newSettings;
				
				Thread.sleep(CYCLE_TIME);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
