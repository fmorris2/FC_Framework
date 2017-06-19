package scripts.fc.api.skills.fishing.locations;

import java.util.ArrayList;
import java.util.List;

import org.tribot.api.General;

import scripts.fc.api.skills.GatheringLocation;
import scripts.fc.api.skills.fishing.FishType;
import scripts.fc.api.skills.fishing.locations.impl.BarbarianVillage;
import scripts.fc.api.skills.fishing.locations.impl.DraynorVillage;
import scripts.fc.api.skills.fishing.locations.impl.Karamja;
import scripts.fc.api.skills.fishing.locations.impl.LumbridgeSwamp;

public enum FishLocation
{
	LUMBRIDGE_SWAMP(new LumbridgeSwamp(), 5),
	DRAYNOR_VILLAGE(new DraynorVillage(), 5),
	BARBARIAN_VILLAGE(new BarbarianVillage(), 5),
	KARAMJA(new Karamja(), 5);
	
	public GatheringLocation<FishType> loc;
	public int weight;
	
	FishLocation(GatheringLocation<FishType> loc, int weight)
	{
		this.loc = loc;
		this.weight = weight;
	}
	
	@SuppressWarnings("unchecked")
	public static GatheringLocation<FishType> getAppropriate(FishType fish)
	{
		List<FishLocation> appropriateLocs = new ArrayList<>();
		
		General.println("Getting appropriate location for fishType " + fish);
		int sumOfWeights = 0;
		
		for(FishLocation loc : values())
		{
			if(loc.loc.getSupported().contains(fish) && loc.loc.hasRequirements())
			{
				appropriateLocs.add(loc);
				sumOfWeights += loc.weight;
			}
		}
		
		//This next piece of code is the suggestion to force shrimp / anchovy fishers to fish at draynor
		if(appropriateLocs.contains(DRAYNOR_VILLAGE))
		{
			for(FishLocation loc : appropriateLocs)
			{
				if(loc != DRAYNOR_VILLAGE)
				{
					//REMOVE OTHER LOCS
					appropriateLocs.remove(loc);
					sumOfWeights -= loc.weight;
				}
			}
		}
		
		GatheringLocation<FishType>[] locs = new GatheringLocation[sumOfWeights];
		int currentIndex = 0;
		for(FishLocation l : appropriateLocs)
		{
			for(int i = currentIndex; i < currentIndex + l.weight; i++)
				locs[i] = l.loc;
			
			currentIndex += l.weight;
		}
		
		return locs[General.random(0, locs.length - 1)];
	}
}
