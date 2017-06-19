package scripts.fc.framework.paint;

import java.awt.Color;
import java.awt.Graphics;

import org.tribot.api.Timing;
import org.tribot.script.Script;

import scripts.fc.framework.script.FCScript;

public class FCPaint
{
	private final int PAINT_X = 4; //The x coordinate for the paint text
	private final int PAINT_BOT_Y = 336; //The y coordinate for the paint string on the bottom
	private final int PAINT_SPACE = 15; //The space between paint fields		
	
	protected FCPaintable paintable; //Paintable object we're painting (we get our paint info from this)
	protected FCScript script;	//Script we're painting for --> This is so we can call getRunningTime() from this class
	protected Color color;	//The color of the paint
	protected long startTime;
	
	public FCPaint(FCPaintable paintable, Color color)
	{
		this.script = (FCScript)paintable;
		this.paintable = paintable;
		this.color = color;
		startTime = Timing.currentTimeMillis();
	}
	
	public void paint(Graphics g)
	{
		//set paint text color
		g.setColor(color);
		
		String[] info = paintable.getPaintInfo();
		
		//FOR(each paint information field in paintInfo)
		for(int index = 0; index < info.length; index++)
		{
			//draw paint field at the appropriate position on the screen, as defined by constants
			g.drawString(info[index], PAINT_X, PAINT_BOT_Y - (PAINT_SPACE * (info.length - (index + 1))));
			
		} //END FOR
	}
		
	public long getTimeRanMs()
	{
		return Timing.currentTimeMillis() - startTime;
	}

	public String getTimeRan()
	{	
		//return the properly formatted string
		return Timing.msToString(Timing.currentTimeMillis() - startTime);
		
	} //END getTimeRan()
	
	public long getPerHour(int amount)
	{	
		//return the projected amount per hour
		return Math.round(amount / ((Timing.currentTimeMillis() - startTime) / 3600000.0));
	}
	
	public void setColor(Color c)
	{
		this.color = c;
	}
	
	public Script getScript()
	{
		return script;
	}
}
