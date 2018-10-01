package scripts.fc.framework.script;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
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

import scripts.dax_api.api_lib.DaxWalker;
import scripts.dax_api.api_lib.models.DaxCredentials;
import scripts.dax_api.api_lib.models.DaxCredentialsProvider;
import scripts.fc.api.abc.PersistantABCUtil;
import scripts.fc.api.banking.listening.FCBankObserver;
import scripts.fc.framework.data.Vars;
import scripts.fc.framework.paint.FCPaint;
import scripts.fc.framework.paint.FCPaintable;
import scripts.fc.framework.quest.BankBool;
import scripts.fc.framework.statistic_tracking.StatTracker;
import scripts.fc.framework.statistic_tracking.StatTracking;

public abstract class FCScript extends Script implements FCPaintable, Painting, Starting, Ending, Serializable
{	
	private static final long serialVersionUID = 1L;
	
	public final ScriptManifest MANIFEST = this.getClass().getAnnotation(ScriptManifest.class);
	public final transient FCBankObserver BANK_OBSERVER = new FCBankObserver();
	
	public transient FCPaint paint = new FCPaint(this, Color.WHITE);
	
	protected abstract int mainLogic();
	protected abstract String[] scriptSpecificPaint();
	protected boolean isRunning = true;
	private transient StatTracker statTracker;
	
	@Override
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
				
				final int sleep = mainLogic();
				
				if(sleep == -1)
					return;
				
				sleep(sleep);
			}
			catch(final Exception e)
			{
				e.printStackTrace();
				sleep(600);
			}
		}
	}
	
	@Override
	public void onStart()
	{
		//if(this instanceof StatTracking)
			//statTracker = new StatTracker();
		
		Vars.get().add("abc", new ABCUtil());
		Vars.get().add("abc2", new PersistantABCUtil());
		Vars.get().addOrUpdate("isRunning", true);
		General.useAntiBanCompliance(true);
		ThreadSettings.get().setClickingAPIUseDynamic(true);
		BankBool.bankObserver = BANK_OBSERVER;
		println("Started " + MANIFEST.name() + " v" + MANIFEST.version() + " by " + MANIFEST.authors()[0]);
		
		DaxWalker.setCredentials(new DaxCredentialsProvider() {
            @Override
            public DaxCredentials getDaxCredentials() {
                return new DaxCredentials("sub_DheKsxfAiVHZMy", "1634dbb7-818e-454e-810a-74f840e5c2bd");
            }
        });
	}
	
	@Override
	public void onEnd()
	{
		((PersistantABCUtil)Vars.get().get("abc2")).close();
		Vars.get().addOrUpdate("isRunning", false);
		BANK_OBSERVER.isRunning = false;

		//stat tracking
		if(statTracker != null && statTracker.isOnline())
		{
			println("Reporting statistics to FCScripting database...");
			final String statsArgs = ((StatTracking)(this)).getStatsArgs();
			statTracker.report(statsArgs);
		}
		
		println("Thank you for running " + MANIFEST.name() + " v" + MANIFEST.version() + " by " + MANIFEST.authors()[0]);
	}
	
	protected String[] basicPaint()
	{
		return new String[]{MANIFEST.name() + " v" + MANIFEST.version() + " by " + MANIFEST.authors()[0],
				"Time ran: " + paint.getTimeRan()};
	}
	
	@Override
	public String[] getPaintInfo()
	{
		return Stream.concat(Arrays.stream(basicPaint()), Arrays.stream(scriptSpecificPaint())).toArray(String[]::new);
	}
	
	@Override
	public void onPaint(final Graphics g)
	{
		paint.paint(g);
	}
	
	public FCPaint getPaint()
	{
		return paint;
	}
	
	public void setIsRunning(final boolean b)
	{
		isRunning = b;
	}
	
	protected void handleAbc2Reset()
	{
		final PersistantABCUtil abc2 = Vars.get().get("abc2");
		if(abc2.needsReset()) //new RS account logs in
		{
			abc2.close();
			Vars.get().addOrUpdate("abc2", new PersistantABCUtil());
		}
	}
	
}
