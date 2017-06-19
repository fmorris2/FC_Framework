package scripts.fc.api.pkeravoidance;

import java.util.Arrays;
import java.util.List;

import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Players;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.script.Script;

import scripts.fc.api.utils.Utils;


public class PkerObserver extends Thread
{
	//constants
	private final long SLEEP_TIME = 10;
	
	//variables
	private Script script;
	private List<PkerListener> listeners;
	private Filter<RSPlayer> detectionFilter;
	private RSPlayer[] detectedPlayers;
	
	public PkerObserver(Script script, Filter<RSPlayer> detectionFilter, PkerListener... listeners)
	{
		this.script = script;
		this.listeners = Arrays.asList(listeners);
		this.detectionFilter = detectionFilter;
	}

	@Override
	public void run()
	{
		try
		{
			while(script != null && script.isActive())
			{
				detect();
				sleep(SLEEP_TIME);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void detect()
	{
		final int WILDY_LEVEL = Utils.getWildyLevel();
		
		if(WILDY_LEVEL > 0)
			detectedPlayers = Players.getAll(detectionFilter);
		else
			detectedPlayers = null;
		
		if(detectedPlayers != null && detectedPlayers.length > 0)
			notifyListeners();
	}
	
	private void notifyListeners()
	{
		for(PkerListener l : listeners)
			l.pkerDetected(detectedPlayers);
	}
	
	public List<PkerListener> getListeners()
	{
		return listeners;
	}
}
