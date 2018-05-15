package scripts.fc.framework.equipment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.tribot.api2007.Equipment;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;

public class EquipmentPiece {
	public final int[] IDS_ARR;
	public final Equipment.SLOTS SLOT;
	private final Set<Integer> IDS_SET;
	
	private int startingAmt = 1;
	private int minimumAmt = 1;
	
	public EquipmentPiece(Equipment.SLOTS slot, int... ids) {
		SLOT = slot;
		IDS_ARR = ids;
		IDS_SET = new HashSet<>(Arrays.stream(ids).map(i -> new Integer(i)).boxed().collect(Collectors.toList()));
	}
	
	public EquipmentPiece(EquipData data) {
		SLOT = data.SLOT;
		IDS_ARR = data.IDS;
		IDS_SET = new HashSet<>(Arrays.stream(data.IDS).map(i -> new Integer(i)).boxed().collect(Collectors.toList()));
	}
	
	public EquipmentPiece(Equipment.SLOTS slot, int minAmt, int startAmt, int... ids) {
		SLOT = slot;
		IDS_ARR = ids;
		IDS_SET = new HashSet<>(Arrays.stream(ids).map(i -> new Integer(i)).boxed().collect(Collectors.toList()));
		minimumAmt = minAmt;
		startingAmt = startAmt;
	}
	
	public boolean hasMinimumAmtOnCharacter() {
		RSItem[] inventory = Inventory.find(IDS_ARR);	
		return (inventory.length > 0 && inventory[0].getStack() >= minimumAmt) || hasMinimumAmtEquipped();
	}
	
	public boolean hasMinimumAmtEquipped() {
		RSItem equipped = Equipment.getItem(SLOT);
		return equipped != null && IDS_SET.contains(equipped.getID()) && equipped.getStack() >= minimumAmt;
	}
	
	public int getStartingAmt() {
		return startingAmt;
	}
}
