package scripts.fc.framework.equipment;

import org.tribot.api2007.Equipment.SLOTS;
import org.tribot.api2007.Skills.SKILLS;

import scripts.fc.framework.equipment.requirement.EquipLvlReq;
import scripts.fc.framework.equipment.requirement.WieldEquipmentRequirement;

public enum EquipData {
	
	//MELEE HELMETS
	BRONZE_MED_HELM(1139, CombatType.MELEE, SLOTS.HELMET, new EquipLvlReq(SKILLS.DEFENCE, 1)),
	BRONZE_FULL_HELM(1155, CombatType.MELEE, SLOTS.HELMET, new EquipLvlReq(SKILLS.DEFENCE, 1)),
	IRON_MED_HELM(1137, CombatType.MELEE, SLOTS.HELMET, new EquipLvlReq(SKILLS.DEFENCE, 1)),
	IRON_FULL_HELM(1153, CombatType.MELEE, SLOTS.HELMET, new EquipLvlReq(SKILLS.DEFENCE, 1)),
	STEEL_MED_HELM(1141, CombatType.MELEE, SLOTS.HELMET, new EquipLvlReq(SKILLS.DEFENCE, 5)),
	STEEL_FULL_HELM(1157, CombatType.MELEE, SLOTS.HELMET, new EquipLvlReq(SKILLS.DEFENCE, 5)),
	BLACK_MED_HELM(1151, CombatType.MELEE, SLOTS.HELMET, new EquipLvlReq(SKILLS.DEFENCE, 10)),
	BLACK_FULL_HELM(1165, CombatType.MELEE, SLOTS.HELMET, new EquipLvlReq(SKILLS.DEFENCE, 10)),
	MITHRIL_MED_HELM(1143, CombatType.MELEE, SLOTS.HELMET, new EquipLvlReq(SKILLS.DEFENCE, 20)),
	MITHRIL_FULL_HELM(1159, CombatType.MELEE, SLOTS.HELMET, new EquipLvlReq(SKILLS.DEFENCE, 20)),
	ADAMANT_MED_HELM(1145, CombatType.MELEE, SLOTS.HELMET, new EquipLvlReq(SKILLS.DEFENCE, 30)),
	ADAMANT_FULL_HELM(1161, CombatType.MELEE, SLOTS.HELMET, new EquipLvlReq(SKILLS.DEFENCE, 30)),
	RUNE_MED_HELM(1147, CombatType.MELEE, SLOTS.HELMET, new EquipLvlReq(SKILLS.DEFENCE, 40)),
	RUNE_FULL_HELM(1163, CombatType.MELEE, SLOTS.HELMET, new EquipLvlReq(SKILLS.DEFENCE, 40)),
	
	//MELEE BODIES
	BRONZE_CHAINBODY(1103, CombatType.MELEE, SLOTS.BODY, new EquipLvlReq(SKILLS.DEFENCE, 1)),
	IRON_CHAINBODY(1101, CombatType.MELEE, SLOTS.BODY, new EquipLvlReq(SKILLS.DEFENCE, 1)),
	STEEL_CHAINBODY(1105, CombatType.MELEE, SLOTS.BODY, new EquipLvlReq(SKILLS.DEFENCE, 5)),
	BLACK_CHAINBODY(1107, CombatType.MELEE, SLOTS.BODY, new EquipLvlReq(SKILLS.DEFENCE, 10)),
	MITHRIL_CHAINBODY(1109, CombatType.MELEE, SLOTS.BODY, new EquipLvlReq(SKILLS.DEFENCE, 20)),
	ADAMANT_CHAINBODY(1111, CombatType.MELEE, SLOTS.BODY, new EquipLvlReq(SKILLS.DEFENCE, 30)),
	RUNE_CHAINBODY(1113, CombatType.MELEE, SLOTS.BODY, new EquipLvlReq(SKILLS.DEFENCE, 40)),
	;
	
	final int[] IDS;
	final CombatType TYPE;
	final SLOTS SLOT;
	final WieldEquipmentRequirement[] REQUIREMENTS;
	
	EquipData(int id, CombatType type, SLOTS slot, WieldEquipmentRequirement... reqs) {
		IDS = new int[]{id};
		TYPE = type;
		SLOT = slot;
		REQUIREMENTS = reqs;
	}
}
