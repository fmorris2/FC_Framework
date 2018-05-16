package scripts.fc;

import org.tribot.api2007.MessageListener;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;
import org.tribot.script.interfaces.Starting;

import scripts.fc.api.items.FCItem;
import scripts.fc.api.settings.FCSettingsListener;
import scripts.fc.api.settings.FCSettingsObserver;
import scripts.fc.framework.paint.FCPaintable;
import scripts.fc.framework.script.FCScript;
import scripts.fc.framework.threads.FCFoodThread;

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
	private FCSettingsObserver settingsObserver = new FCSettingsObserver(this);
	
	protected int mainLogic()
	{
		//Travel.webWalkTo(new RSTile(3016, 3514, 0));
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
		println("SETTING INDEX: " + index + ", OLD: " + oldValue + ", NEW: " + newValue);
	}

	public void onEnd()
	{
		super.onEnd();
	}
	
	public void onStart()
	{
		MessageListener.addListener(this);
		//setLoginBotState(false);
		new FCFoodThread(40, 50, new FCItem(1, false, 333)).start();
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

	@Override
	public void varBitChanged(int setting, int varbitIndex, int oldValue, int newValue)
	{
		println("VARBIT " + varbitIndex + " FOR SETTING " + setting + " CHANGED FROM " + oldValue + " ---> " + newValue);
	}

}