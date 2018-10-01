package scripts.fc;

import org.tribot.api2007.MessageListener;
import org.tribot.api2007.Options;
import org.tribot.api2007.WorldHopper;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;
import org.tribot.script.interfaces.Starting;

import scripts.fc.api.items.FCItem;
import scripts.fc.api.settings.FCSettingsListener;
import scripts.fc.api.settings.FCSettingsObserver;
import scripts.fc.api.worldhopping.FCInGameHopper;
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
	private final FCSettingsObserver settingsObserver = new FCSettingsObserver(this);
	
	@Override
	protected int mainLogic()
	{
		Options.setRunEnabled(true);
		return 600;
	}
	
	@Override
	protected String[] scriptSpecificPaint()
	{	
		return new String[]{};
	}
	
	@Override
	public void settingChanged(final int index, final int oldValue, final int newValue)
	{
		println("SETTING INDEX: " + index + ", OLD: " + oldValue + ", NEW: " + newValue);
	}

	@Override
	public void onEnd()
	{
		super.onEnd();
	}
	
	@Override
	public void onStart()
	{
		MessageListener.addListener(this);
		//setLoginBotState(false);
		new FCFoodThread(40, 50, new FCItem(1, false, 333)).start();
		super.onStart();
	}

	@Override
	public void clanMessageReceived(final String arg0, final String arg1)
	{
	}

	@Override
	public void duelRequestReceived(final String arg0, final String arg1)
	{
	}

	@Override
	public void personalMessageReceived(final String arg0, final String arg1)
	{
	}

	@Override
	public void playerMessageReceived(final String arg0, final String arg1)
	{
	}

	@Override
	public void serverMessageReceived(final String arg0)
	{
		println("BLA:" + arg0);
	}

	@Override
	public void tradeRequestReceived(final String arg0)
	{
		
	}

	@Override
	public void varBitChanged(final int setting, final int varbitIndex, final int oldValue, final int newValue)
	{
		println("VARBIT " + varbitIndex + " FOR SETTING " + setting + " CHANGED FROM " + oldValue + " ---> " + newValue);
	}

}