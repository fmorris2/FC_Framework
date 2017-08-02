package scripts.fc.framework.script;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.stream.Stream;

import org.tribot.api.General;
import org.tribot.api.util.ABCUtil;
import org.tribot.api2007.util.ThreadSettings;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.Painting;
import org.tribot.script.interfaces.Starting;

import scripts.fc.api.abc.PersistantABCUtil;
import scripts.fc.api.banking.listening.FCBankObserver;
import scripts.fc.api.utils.Utils;
import scripts.fc.framework.data.Vars;
import scripts.fc.framework.paint.FCPaint;
import scripts.fc.framework.paint.FCPaintable;
import scripts.fc.framework.quest.BankBool;
import scripts.fc.framework.statistic_tracking.StatTracker;
import scripts.fc.framework.statistic_tracking.StatTracking;

public abstract class FCScript extends Script implements FCPaintable, Painting, Starting, Ending
{	
	public final ScriptManifest MANIFEST = (ScriptManifest)this.getClass().getAnnotation(ScriptManifest.class);
	public final FCBankObserver BANK_OBSERVER = new FCBankObserver();
	
	public FCPaint paint = new FCPaint(this, Color.WHITE);
	
	protected abstract int mainLogic();
	protected abstract String[] scriptSpecificPaint();
	protected boolean isRunning = true;
	private StatTracker statTracker;
	
	public void run()
	{
		if(statTracker != null && !statTracker.reportOnline(getScriptName()))
		{
			if(statTracker.getStatus() == StatTracker.STATUS.NOT_ALLOWED_FIREWALL)
			{
				println("You must enable the connection to " + StatTracker.SOCKET_URL + " for statistic tracking purposes.");
				return;
			}
			else
				println("VikingScripts statistic tracking server is down.");
		}
		
		while(isRunning)
		{
			try
			{
				handleAbc2Reset();
				
				int sleep = mainLogic();
				
				if(sleep == -1)
					return;
				
				sleep(sleep);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				sleep(600);
			}
		}
	}
	
	public void onStart()
	{
		if(this instanceof StatTracking)
			statTracker = new StatTracker();
		
		Vars.get().add("abc", new ABCUtil());
		Vars.get().add("abc2", new PersistantABCUtil());
		Vars.get().addOrUpdate("isRunning", true);
		General.useAntiBanCompliance(true);
		ThreadSettings.get().setClickingAPIUseDynamic(true);
		BankBool.bankObserver = BANK_OBSERVER;
		println("Started " + MANIFEST.name() + " v" + MANIFEST.version() + " by " + MANIFEST.authors()[0]);
	}
	
	public void onEnd()
	{
		((PersistantABCUtil)Vars.get().get("abc2")).close();
		Vars.get().addOrUpdate("isRunning", false);
		BANK_OBSERVER.isRunning = false;

		//stat tracking
		if(statTracker != null && statTracker.isOnline())
		{
			println("Reporting statistics to FCScripting database...");
			String statsArgs = ((StatTracking)(this)).getStatsArgs();
			statTracker.report(statsArgs);
		}
		
		println("Thank you for running " + MANIFEST.name() + " v" + MANIFEST.version() + " by " + MANIFEST.authors()[0]);
	}
	
	protected String[] basicPaint()
	{
		return new String[]{MANIFEST.name() + " v" + MANIFEST.version() + " by " + MANIFEST.authors()[0],
				"Time ran: " + paint.getTimeRan()};
	}
	
	public String[] getPaintInfo()
	{
		return Stream.concat(Arrays.stream(basicPaint()), Arrays.stream(scriptSpecificPaint())).toArray(String[]::new);
	}
	
	public void onPaint(Graphics g)
	{
		paint.paint(g);
	}
	
	public FCPaint getPaint()
	{
		return paint;
	}
	
	public void setIsRunning(boolean b)
	{
		isRunning = b;
	}
	
	protected void handleAbc2Reset()
	{
		PersistantABCUtil abc2 = Vars.get().get("abc2");
		if(abc2.needsReset()) //new RS account logs in
		{
			abc2.close();
			Vars.get().addOrUpdate("abc2", new PersistantABCUtil());
		}
	}
	
}
