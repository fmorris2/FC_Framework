package scripts.fc;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.MessageListener;
import org.tribot.api2007.Player;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;
import org.tribot.script.interfaces.Starting;

import scripts.fc.api.settings.FCSettingsListener;
import scripts.fc.api.settings.FCSettingsObserver;
import scripts.fc.framework.paint.FCPaintable;
import scripts.fc.framework.script.FCScript;

@ScriptManifest(
		authors     = { 
		    "Final Calibur",
		}, 
		category    = "No Category", 
		name        = "Test", 
		version     = 0.1, 
		description = "", 
		gameMode    = 1)
	

public class Test extends FCScript implements FCPaintable, Painting, Starting, Ending, FCSettingsListener, MessageListening07
{	
	private static final int DIALOGUE_MASTER = 231;
	private static final int PLAYER_DIALOGUE_MASTER = 217;
	
	private FCSettingsObserver settingsObserver = new FCSettingsObserver(this);
	
	protected int mainLogic()
	{
		Positionable pos = Player.getPosition();
		return 600;
	}
	
	@Override
	protected String[] scriptSpecificPaint()
	{
		return new String[]{};
	}
	
	@Override
	public void settingChanged(int index, int oldValue, int newValue)
	{
		println("INDEX: " + index + ", OLD: " + oldValue + ", NEW: " + newValue);
	}

	public void onEnd()
	{
		super.onEnd();
	}
	
	public void onStart()
	{
		MessageListener.addListener(this);
		//setLoginBotState(false);
		super.onStart();
	}

	@Override
	public void clanMessageReceived(String arg0, String arg1)
	{
	}

	@Override
	public void duelRequestReceived(String arg0, String arg1)
	{
	}

	@Override
	public void personalMessageReceived(String arg0, String arg1)
	{
	}

	@Override
	public void playerMessageReceived(String arg0, String arg1)
	{
	}

	@Override
	public void serverMessageReceived(String arg0)
	{
		println("BLA:" + arg0);
	}

	@Override
	public void tradeRequestReceived(String arg0)
	{
		
	}

}