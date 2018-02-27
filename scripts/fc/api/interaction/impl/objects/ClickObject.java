package scripts.fc.api.interaction.impl.objects;

import org.tribot.api.input.Mouse;
import org.tribot.api2007.Game;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSObject;

import scripts.fc.api.interaction.ObjectInteraction;
import scripts.fc.api.mouse.AccurateMouse;
import scripts.fc.api.viewport.FCCameraUtils;

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
		
		for(int i = 0; i < CLICK_ATTEMPT_THRESHOLD; i++) {
			if(AccurateMouse.click(object, action)) {
				return true;
			}
		}
		
		FCCameraUtils.adjustCameraRandomly();
		return false;
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
