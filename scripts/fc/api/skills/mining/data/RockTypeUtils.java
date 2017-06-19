package scripts.fc.api.skills.mining.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;

import scripts.fc.api.skills.ProgressionType;

public class RockTypeUtils
{
	public static RockType getAppropriate(ProgressionType progressionType)
	{
		final int MINING_LVL = Skills.getActualLevel(SKILLS.MINING);
		List<RockType> appropriates = new ArrayList<>();
		
		for(RockType r : RockType.values())
			if(r != RockType.AUTO_SELECT && r.getLevelReq() <= MINING_LVL && r.getProgressionTypes().contains(progressionType))
				appropriates.add(r);
		
		Collections.shuffle(appropriates);
		Collections.sort(appropriates, new RockComparator());
		
		return appropriates.isEmpty() ? RockType.COPPER : appropriates.get(0);
	}
	
	private static class RockComparator implements Comparator<RockType>
	{
		@Override
		public int compare(RockType o1, RockType o2)
		{
			return o2.getLevelReq() - o1.getLevelReq();
		}
		
	}
}
