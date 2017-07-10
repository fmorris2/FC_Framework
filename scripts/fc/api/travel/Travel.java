package scripts.fc.api.travel;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;

import scripts.webwalker_logic.WebWalker;
import scripts.webwalker_logic.local.walker_engine.WalkingCondition;

public class Travel
{
	public static boolean webWalkTo(Positionable p)
	{
		return /*!*/WebWalker.walkTo(p.getPosition());// ? WebWalking.walkTo(p) : true;
	}
	
	public static boolean webWalkTo(Positionable p, Condition c)
	{
		return WebWalker.walkTo(p.getPosition(), () -> {return c.active() ? WalkingCondition.State.EXIT_OUT_WALKER_SUCCESS : WalkingCondition.State.EXIT_OUT_WALKER_FAIL;});
		/*
		return !WebWalker.walkTo(p.getPosition(), () -> {return c.active() ? WalkingCondition.State.EXIT_OUT_WALKER_SUCCESS : WalkingCondition.State.EXIT_OUT_WALKER_FAIL;})
				? WebWalking.walkTo(p, c, 600) : true;
		*/
	}
	
	public static boolean walkToBank()
	{
		return WebWalker.walkToBank();//!WebWalker.walkToBank() ? WebWalking.walkToBank() : true;
	}
}
