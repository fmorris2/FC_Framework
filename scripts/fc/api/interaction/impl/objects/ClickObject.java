package scripts.fc.api.interaction.impl.objects;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Game;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSObject;

import scripts.fc.api.interaction.ObjectInteraction;
import scripts.fc.api.mouse.AccurateMouse;

public class ClickObject extends ObjectInteraction
{
	private boolean rightClick;
	
	public ClickObject(String action, String name, int searchDistance)
	{
		super(action, name, searchDistance);
	}
	
	public ClickObject(String action, int id, int searchDistance)
	{
		super(action, id, searchDistance);
	}
	
	public ClickObject(String action, String name, int searchDistance, boolean checkPath, boolean rightClick)
	{
		super(action, name, searchDistance, checkPath);
		this.rightClick = rightClick;
	}
	
	public ClickObject(String action, RSObject object)
	{
		super(action, object);
	}
	
	public ClickObject(String action, String name, RSArea area)
	{
		super(action, name, area);
	}

	@Override
	protected boolean interact()
	{
		if(Game.isUptext("Use"))
			Mouse.click(1);
	
		/*
		ThreadSettings.get().setClickingAPIUseDynamic(true);
		
		if(rightClick)
			ThreadSettings.get().setAlwaysRightClick(true);
	
		boolean result = Clicking.click(FCFilters.correlatesTo(action, object), object);
		
		ThreadSettings.get().setClickingAPIUseDynamic(false);
		ThreadSettings.get().setAlwaysRightClick(false);
		*/
		
		boolean result = AccurateMouse.click(object, action);
		
		General.println("ClickObject interact() result: " + result);
		return result;
	}
	
	public void setRightClick(boolean b)
	{
		rightClick = b;
	}

	public boolean isRightClicking()
	{
		return rightClick;
	}
}
