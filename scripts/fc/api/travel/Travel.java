package scripts.fc.api.travel;

import org.tribot.api.General;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.WebWalking;

import scripts.webwalker_logic.WebWalker;
import scripts.webwalker_logic.local.walker_engine.WalkingCondition;

public class Travel
{
	private static final int MAX_FAILURES = 5;
	
	public static boolean shouldFallBackOnTribotWeb = true;
	
	private static int failures = 0;
	public static boolean webWalkTo(Positionable p)
	{
		boolean daxSuccess = WebWalker.walkTo(p.getPosition());
		
		failures = !daxSuccess ? failures + 1 : 0;
		
		if(shouldFallBackOnTribotWeb && failures >= MAX_FAILURES) {
			failures = 0;
			General.println("Falling back on TRiBot web walker");
			return WebWalking.walkTo(p);
		}
		
		return daxSuccess;
	}
	
	public static boolean webWalkTo(Positionable p, Condition c)
	{
		boolean daxSuccess = WebWalker.walkTo(p.getPosition(), () -> {return c.active() ? WalkingCondition.State.EXIT_OUT_WALKER_SUCCESS : WalkingCondition.State.EXIT_OUT_WALKER_FAIL;});
		
		failures = !daxSuccess ? failures + 1 : 0;
		
		if(shouldFallBackOnTribotWeb && failures >= MAX_FAILURES) {
			failures = 0;
			General.println("Falling back on TRiBot web walker");
			return WebWalking.walkTo(p, c, 600);
		}
		
		return daxSuccess;
	}
	
	public static boolean walkToBank()
	{
		boolean daxSuccess = WebWalker.walkToBank();
		
		failures = !daxSuccess ? failures + 1 : 0;
		
		if(shouldFallBackOnTribotWeb && failures >= MAX_FAILURES) {
			failures = 0;
			General.println("Falling back on TRiBot web walker");
			return WebWalking.walkToBank();
		}
		
		return daxSuccess;
	}
}
