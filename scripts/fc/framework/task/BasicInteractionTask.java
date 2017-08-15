package scripts.fc.framework.task;

import java.util.function.BooleanSupplier;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;

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
		if(!isWithinRadius())
			return Travel.webWalkTo(getPosition());
		
		BooleanSupplier waitCond = getWaitCondition();
		
		return getInteraction().execute() && waitCond != null ? FCTiming.waitCondition(waitCond, getWaitTimeout()) : true;
	}
	
	public boolean isWithinRadius()
	{
		final RSTile POS = Player.getPosition();
		final RSTile TARG = getPosition().getPosition();
		return POS.getPlane() == TARG.getPlane() && POS.distanceTo(TARG) <= getRadius();
	}
}
