package scripts.fc.framework.equipment.requirement;

import org.tribot.api2007.WorldHopper;

public class MembersReq implements WieldEquipmentRequirement {

	@Override
	public boolean canWield() {
		return WorldHopper.isMembers(WorldHopper.getWorld());
	}

}
