package scripts.fc.api.skills.mining.data.locations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import scripts.fc.api.skills.GatheringLocation;
import scripts.fc.api.skills.mining.data.RockType;

public abstract class MiningLocation extends GatheringLocation<RockType> implements Serializable
{	
	protected static final long serialVersionUID = 7175276070017336643L;
	
	protected ArrayList<RockType> serializableSupported;
	
	public abstract List<RockType> getSupported();
	
	public boolean contains(RockType... rocks)
	{
		for(RockType r : rocks)
			if(supported.contains(r))
				return true;
		
		return false;
	}
}
