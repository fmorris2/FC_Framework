package scripts.fc.api.wrappers;

import java.util.function.BooleanSupplier;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;

public class FCTiming
{
	private static long CYCLE_TIME = 40;
	public static boolean waitCondition(BooleanSupplier supp, long timeout)
	{
		return Timing.waitCondition(new Condition() 
		{
			@Override
			public boolean active()
			{
				General.sleep(CYCLE_TIME);
				return supp.getAsBoolean();
			}
			
		}, timeout);
	}
}
