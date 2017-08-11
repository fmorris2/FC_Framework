package scripts.fc.framework.task;

import java.util.function.BooleanSupplier;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Player;

import scripts.fc.api.interaction.EntityInteraction;
import scripts.fc.api.travel.Travel;
import scripts.fc.api.wrappers.FCTiming;

public abstract class BasicInteractionTask extends Task
{
	private static final long serialVersionUID = 1L;
	
	protected abstract Positionable getPosition();
	protected abstract int getRadius();
	protected abstract EntityInteraction getInteraction();
	protected abstract BooleanSupplier getWaitCondition();
	protected abstract int getWaitTimeout();
	
	@Override
	public boolean execute()
	{
		if(Player.getPosition().distanceTo(getPosition()) > getRadius())
			return Travel.webWalkTo(getPosition());
		
		BooleanSupplier waitCond = getWaitCondition();
		
		return getInteraction().execute() && waitCond != null ? FCTiming.waitCondition(waitCond, getWaitTimeout()) : true;
	}
}
