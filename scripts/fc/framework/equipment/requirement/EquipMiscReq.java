package scripts.fc.framework.equipment.requirement;

import java.util.function.BooleanSupplier;

public class EquipMiscReq implements WieldEquipmentRequirement {
	private final BooleanSupplier CONDITION;
	
	public EquipMiscReq(BooleanSupplier supp) {
		CONDITION = supp;
	}
	
	@Override
	public boolean canWield() {
		return CONDITION.getAsBoolean();
	}

}
