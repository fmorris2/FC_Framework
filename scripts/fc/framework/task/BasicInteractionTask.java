package scripts.fc.framework.task;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Player;

import scripts.fc.api.interaction.EntityInteraction;
import scripts.fc.api.travel.Travel;

public abstract class BasicInteractionTask extends Task
{
	private static final long serialVersionUID = 1L;
	
	protected abstract Positionable getPosition();
	protected abstract int getRadius();
	protected abstract EntityInteraction getInteraction();
	
	@Override
	public boolean execute()
	{
		if(Player.getPosition().distanceTo(getPosition()) > getRadius())
			return Travel.webWalkTo(getPosition());
		
		return getInteraction().execute();
	}
}
