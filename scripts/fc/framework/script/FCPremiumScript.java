package scripts.fc.framework.script;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.stream.Stream;

import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.EventBlockingOverride;
import org.tribot.script.interfaces.Painting;
import org.tribot.script.interfaces.Starting;

import scripts.fc.framework.paint.FCDetailedPaint;
import scripts.fc.framework.paint.FCPaintable;


public abstract class FCPremiumScript extends FCMissionScript implements FCPaintable, Painting, Starting, Ending, EventBlockingOverride
{	
	public FCDetailedPaint paint = getPaint();
	
	public abstract FCDetailedPaint getPaint();
	
	public void onPaint(Graphics g)
	{
		if(currentMission != null || this.compilingPreReqs)
			paint.paint(g);
	}
	
	public String[] getPaintInfo()
	{
		String[] currentMissionPaint = currentMission == null ? new String[0] : currentMission.getMissionSpecificPaint();
		
		return Stream.concat(Arrays.stream(scriptSpecificPaint()), Arrays.stream(currentMissionPaint)).toArray(String[]::new);
	}
	
	@Override
	public OVERRIDE_RETURN overrideKeyEvent(KeyEvent e) 
	{
		return EventBlockingOverride.OVERRIDE_RETURN.PROCESS;
	}
	
	@Override
	public OVERRIDE_RETURN overrideMouseEvent(MouseEvent e) 
	{
		return paint.handleMouseEvent(e);
	}
}
