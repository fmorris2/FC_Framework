package scripts.fc.framework.equipment.requirement;

import org.tribot.api2007.Game;

public class SettingReq implements WieldEquipmentRequirement {
	
	private final int SETTING, VALUE;
	
	public SettingReq(int setting, int value) {
		SETTING = setting;
		VALUE = value;
	}
	
	@Override
	public boolean canWield() {
		return Game.getSetting(SETTING) == VALUE;
	}

}
