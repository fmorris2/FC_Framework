package scripts.fc.framework.equipment;

import java.util.Arrays;

import scripts.fc.api.items.FCItem;
import scripts.fc.api.items.FCItemList;

public class EquipmentSet {
	private EquipmentPiece[] pieces;
	
	public EquipmentSet(EquipmentPiece... pieces) {
		this.pieces = pieces;
	}
	
	public boolean hasSetEquipped() {
		return Arrays.stream(pieces).allMatch(piece -> piece.hasMinimumAmtEquipped());
	}
	
	public FCItemList generateNeededEquipment() {
		FCItem[] items = Arrays.stream(pieces)
				.filter(piece -> !piece.hasMinimumAmtOnCharacter())
				.map(piece -> new FCItem(piece.getStartingAmt(), piece.getStartingAmt() > 1, piece.IDS_ARR))
				.toArray(FCItem[]::new);
		
		return new FCItemList(items);
	}
	
	public FCItemList generatePiecesToEquipInInventory() {
		FCItem[] items = Arrays.stream(pieces)
				.filter(piece -> piece.hasMinimumAmtOnCharacter() && !piece.hasMinimumAmtEquipped())
				.map(piece -> new FCItem(piece.getStartingAmt(), piece.getStartingAmt() > 1, piece.IDS_ARR))
				.toArray(FCItem[]::new);
		
		return new FCItemList(items);
	}
}
