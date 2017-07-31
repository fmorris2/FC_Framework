package scripts.webwalker_logic.teleport_logic;

import static scripts.webwalker_logic.teleport_logic.TeleportLocation.AL_KHARID;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.ARDOUGNE_MARKET_PLACE;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.BARBARIAN_OUTPOST;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.BURTHORPE_GAMES_ROOM;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.CAMELOT;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.CASTLE_WARS;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.CHAMPIONS_GUILD;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.CLAN_WARS;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.COOKING_GUILD;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.CORPOREAL_BEAST;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.CRAFTING_GUILD;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.DRAYNOR_VILLAGE;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.DUEL_ARENA;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.ECTO;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.EDGEVILLE;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.FALADOR_CENTER;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.FALADOR_PARK;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.FISHING_GUILD;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.GRAND_EXCHANGE;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.KARAMJA_BANANA_PLANTATION;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.LUMBRIDGE_CASTLE;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.MONASTRY_EDGE;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.MOTHERLOAD_MINE;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.RANGED_GUILD;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.VARROCK_CENTER;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.WARRIORS_GUILD;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.WINTERTODT_CAMP;
import static scripts.webwalker_logic.teleport_logic.TeleportLocation.WOOD_CUTTING_GUILD;

import java.util.ArrayList;
import java.util.Arrays;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Game;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSTile;

import scripts.fc.api.banking.FCBanking;
import scripts.fc.api.banking.listening.FCBankObserver;
import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.items.FCItem;
import scripts.fc.api.items.FCItemList;
import scripts.fc.api.travel.FCTeleporting;
import scripts.fc.framework.quest.BankBool;
import scripts.webwalker_logic.local.walker_engine.WaitFor;
import scripts.webwalker_logic.local.walker_engine.interaction_handling.NPCInteraction;
import scripts.webwalker_logic.shared.helpers.RSItemHelper;
import scripts.webwalker_logic.shared.helpers.magic.Spell;

public enum TeleportMethod implements Validatable {

    VARROCK_TELEPORT_SPELL (VARROCK_CENTER),
    VARROCK_TELEPORT_TAB (VARROCK_CENTER),
    
    LUMBRIDGE_TELEPORT_SPELL  (LUMBRIDGE_CASTLE),
    LUMBRIDGE_TELEPORT_TAB (LUMBRIDGE_CASTLE),
    HOME_TELEPORT(LUMBRIDGE_CASTLE),
    
    FALADOR_TELEPORT_SPELL  (FALADOR_CENTER),
    FALADOR_TELEPORT_TAB (FALADOR_CENTER),
    
    CAMELOT_TELEPORT_SPELL  (CAMELOT),
    CAMELOT_TELEPORT_TAB (CAMELOT),
    
    ARDOUGNE_TELPORT_SPELL  (ARDOUGNE_MARKET_PLACE),
    ARDOUGNE_TELEPORT_TAB (ARDOUGNE_MARKET_PLACE),
    
    GLORY (EDGEVILLE, DRAYNOR_VILLAGE, KARAMJA_BANANA_PLANTATION, AL_KHARID),
    COMBAT_BRACE  (CHAMPIONS_GUILD, WARRIORS_GUILD, RANGED_GUILD, MONASTRY_EDGE),
    GAMES_NECKLACE  (CORPOREAL_BEAST, BURTHORPE_GAMES_ROOM, WINTERTODT_CAMP, BARBARIAN_OUTPOST),
    DUELING_RING (DUEL_ARENA, CASTLE_WARS, CLAN_WARS),
    ECTOPHIAL (ECTO),
    SKILLS_NECKLACE (FISHING_GUILD, MOTHERLOAD_MINE, CRAFTING_GUILD, COOKING_GUILD, WOOD_CUTTING_GUILD),
    RING_OF_WEALTH (GRAND_EXCHANGE, FALADOR_PARK)
    ;

    private TeleportLocation[] destinations;

    TeleportMethod(TeleportLocation... destinations){
        this.destinations = destinations;
    }

    private static final Filter<RSItem>
            GLORY_FILTER = Filters.Items.nameContains("Glory").combine(Filters.Items.nameContains("("), true),
            GAMES_FILTER = Filters.Items.nameContains("Games").combine(Filters.Items.nameContains("("), true),
            DUELING_FILTER = Filters.Items.nameContains("dueling").combine(Filters.Items.nameContains("("), true),
            COMBAT_FILTER = Filters.Items.nameContains("Combat").combine(Filters.Items.nameContains("("), true),
            SKILLS_FILTER = Filters.Items.nameContains("Skills necklace").combine(Filters.Items.nameContains("("), true),
            WEALTH_FILTER = Filters.Items.nameContains("Ring of wealth").combine(Filters.Items.nameContains("("), true)
    ;

    public TeleportLocation[] getDestinations() {
        return destinations;
    }

    @Override
    public boolean canUse()
    {
    	switch(this)
    	{
			case ARDOUGNE_TELEPORT_TAB: return Game.getSetting(165) >= 30;
			case ARDOUGNE_TELPORT_SPELL: return Game.getSetting(165) >= 30 && Spell.ARDOUGNE_TELEPORT.canUse();
			case CAMELOT_TELEPORT_SPELL: return Spell.CAMELOT_TELEPORT.canUse();
			case FALADOR_TELEPORT_SPELL: return Spell.FALADOR_TELEPORT.canUse();
			case LUMBRIDGE_TELEPORT_SPELL: return Spell.LUMBRIDGE_TELEPORT.canUse();
			case VARROCK_TELEPORT_SPELL: return Spell.VARROCK_TELEPORT.canUse();
			default: return true;
    	}
    }
    
    public boolean hasOnCharacter() {
        switch (this){
        	case HOME_TELEPORT: return FCTeleporting.canHomeTele();
            case ECTOPHIAL: return Inventory.find(Filters.Items.nameContains("Ectophial")).length > 0;
            case VARROCK_TELEPORT_SPELL: return Spell.VARROCK_TELEPORT.hasInInv();
            case VARROCK_TELEPORT_TAB: return Inventory.getCount("Varrock teleport") > 0;
            case LUMBRIDGE_TELEPORT_SPELL: return Spell.LUMBRIDGE_TELEPORT.hasInInv();
            case LUMBRIDGE_TELEPORT_TAB: return Inventory.getCount("Lumbridge teleport") > 0;
            case FALADOR_TELEPORT_SPELL: return Spell.FALADOR_TELEPORT.hasInInv();
            case FALADOR_TELEPORT_TAB: return Inventory.getCount("Falador teleport") > 0;
            case CAMELOT_TELEPORT_SPELL: return Spell.CAMELOT_TELEPORT.hasInInv();
            case CAMELOT_TELEPORT_TAB: return Inventory.getCount("Camelot teleport") > 0;
            case ARDOUGNE_TELPORT_SPELL: return Spell.ARDOUGNE_TELEPORT.hasInInv();
            case ARDOUGNE_TELEPORT_TAB: return Inventory.getCount("Ardougne teleport") > 0;
            case GLORY: return Inventory.find(GLORY_FILTER).length > 0 || Equipment.find(GLORY_FILTER).length > 0;
            case COMBAT_BRACE: return Inventory.find(COMBAT_FILTER).length > 0 || Equipment.find(COMBAT_FILTER).length > 0;
            case GAMES_NECKLACE: return Inventory.find(GAMES_FILTER).length > 0 || Equipment.find(GAMES_FILTER).length > 0;
            case DUELING_RING: return Inventory.find(DUELING_FILTER).length > 0 || Equipment.find(DUELING_FILTER).length > 0;
            case RING_OF_WEALTH: return Inventory.find(WEALTH_FILTER).length > 0 || Equipment.find(WEALTH_FILTER).length > 0;
            case SKILLS_NECKLACE: return Inventory.find(SKILLS_FILTER).length > 0 || Equipment.find(SKILLS_FILTER).length > 0;
            default:
			break;
        }
        return false;
    }
    
    public boolean hasInBank()
    {
    	FCBankObserver obs = BankBool.bankObserver;
    	switch(this)
    	{
		    case ECTOPHIAL: return obs.containsItem(Filters.Items.nameContains("Ectophial"));
		    case VARROCK_TELEPORT_SPELL: return Spell.VARROCK_TELEPORT.isInBank();
		    case VARROCK_TELEPORT_TAB: return obs.containsItem("Varrock teleport", 1);
		    case LUMBRIDGE_TELEPORT_SPELL: return Spell.LUMBRIDGE_TELEPORT.isInBank();
		    case LUMBRIDGE_TELEPORT_TAB: return obs.containsItem("Lumbridge teleport", 1);
		    case FALADOR_TELEPORT_SPELL: return Spell.FALADOR_TELEPORT.isInBank();
		    case FALADOR_TELEPORT_TAB: return obs.containsItem("Falador teleport", 1);
		    case CAMELOT_TELEPORT_SPELL: return Spell.CAMELOT_TELEPORT.isInBank();
		    case CAMELOT_TELEPORT_TAB: return obs.containsItem("Camelot teleport", 1);
		    case ARDOUGNE_TELPORT_SPELL: return Spell.ARDOUGNE_TELEPORT.isInBank();
		    case ARDOUGNE_TELEPORT_TAB: return obs.containsItem("Ardougne teleport", 1);
		    case GLORY: return obs.containsItem(GLORY_FILTER);
		    case COMBAT_BRACE: return obs.containsItem(COMBAT_FILTER);
		    case GAMES_NECKLACE: return obs.containsItem(GAMES_FILTER);
		    case DUELING_RING: return obs.containsItem(DUELING_FILTER);
		    case RING_OF_WEALTH: return obs.containsItem(WEALTH_FILTER);
		    case SKILLS_NECKLACE: return obs.containsItem(SKILLS_FILTER);
		    default: return false;
    	}
    }
    
    public boolean use(TeleportMethod method)
    {
    	return use(method, null);
    }
    
    public boolean withdraw()
    {
    	FCBankObserver obs = BankBool.bankObserver;
    	if(Banking.isBankScreenOpen() || Banking.openBank() && Timing.waitCondition(FCConditions.BANK_LOADED_CONDITION, 5000))
    	{
	    	switch(this)
	    	{
				case ARDOUGNE_TELEPORT_TAB:
					return FCBanking.withdraw(new FCItemList(new FCItem(1, false, "Ardougne teleport")));
				case ARDOUGNE_TELPORT_SPELL:
					return FCBanking.withdraw(Spell.ARDOUGNE_TELEPORT.getItemList());
				case CAMELOT_TELEPORT_SPELL:
					return FCBanking.withdraw(Spell.CAMELOT_TELEPORT.getItemList());
				case CAMELOT_TELEPORT_TAB:
					return FCBanking.withdraw(new FCItemList(new FCItem(1, false, "Camelot teleport")));
				case COMBAT_BRACE:
					return FCBanking.withdraw(new FCItemList(new FCItem(1, obs.getItem(COMBAT_FILTER).getDefinition())));
				case DUELING_RING:
					return FCBanking.withdraw(new FCItemList(new FCItem(1, obs.getItem(DUELING_FILTER).getDefinition())));
				case ECTOPHIAL:
					return FCBanking.withdraw(new FCItemList(new FCItem(1, false, "Ectophial")));
				case FALADOR_TELEPORT_SPELL:
					return FCBanking.withdraw(Spell.FALADOR_TELEPORT.getItemList());
				case FALADOR_TELEPORT_TAB:
					return FCBanking.withdraw(new FCItemList(new FCItem(1, false, "Falador teleport")));
				case GAMES_NECKLACE:
					return FCBanking.withdraw(new FCItemList(new FCItem(1, obs.getItem(GAMES_FILTER).getDefinition())));
				case GLORY:
					return FCBanking.withdraw(new FCItemList(new FCItem(1, obs.getItem(GLORY_FILTER).getDefinition())));
				case LUMBRIDGE_TELEPORT_SPELL:
					return FCBanking.withdraw(Spell.LUMBRIDGE_TELEPORT.getItemList());
				case LUMBRIDGE_TELEPORT_TAB:
					return FCBanking.withdraw(new FCItemList(new FCItem(1, false, "Lumbridge teleport")));
				case RING_OF_WEALTH:
					return FCBanking.withdraw(new FCItemList(new FCItem(1, obs.getItem(WEALTH_FILTER).getDefinition())));
				case SKILLS_NECKLACE:
					return FCBanking.withdraw(new FCItemList(new FCItem(1, obs.getItem(SKILLS_FILTER).getDefinition())));
				case VARROCK_TELEPORT_SPELL:
					return FCBanking.withdraw(Spell.VARROCK_TELEPORT.getItemList());
				case VARROCK_TELEPORT_TAB:
					return FCBanking.withdraw(new FCItemList(new FCItem(1, false, "Varrock teleport")));
				default: return false;
	    	}
    	}
    	return false;
    }
    
    public boolean use(TeleportMethod method, TeleportLocation loc)
    {
    	switch(method)
    	{
			case ARDOUGNE_TELEPORT_TAB: return RSItemHelper.click("Ardougne t.*", "Break");
			case ARDOUGNE_TELPORT_SPELL: return Spell.ARDOUGNE_TELEPORT.cast();
			case CAMELOT_TELEPORT_SPELL: return Spell.CAMELOT_TELEPORT.cast();
			case CAMELOT_TELEPORT_TAB: return RSItemHelper.click("Camelot t.*", "Break");
			case COMBAT_BRACE: return useCombatBracelet(loc);
			case DUELING_RING: return useDuelingRing(loc);
			case ECTOPHIAL: return RSItemHelper.click(Filters.Items.nameContains("Ectophial"), "Empty");
			case FALADOR_TELEPORT_SPELL: return Spell.FALADOR_TELEPORT.cast();
			case FALADOR_TELEPORT_TAB: return RSItemHelper.click("Falador t.*", "Break");
			case GAMES_NECKLACE: return useGamesNecklace(loc);
			case GLORY: return useGlory(loc);
			case HOME_TELEPORT: return FCTeleporting.homeTeleport();
			case LUMBRIDGE_TELEPORT_SPELL: return Spell.LUMBRIDGE_TELEPORT.cast();
			case LUMBRIDGE_TELEPORT_TAB: return RSItemHelper.click("Lumbridge t.*", "Break");
			case RING_OF_WEALTH: return useRingOfWealth(loc);
			case SKILLS_NECKLACE: return useSkillsNecklace(loc);
			case VARROCK_TELEPORT_SPELL: return Spell.VARROCK_TELEPORT.cast();
			case VARROCK_TELEPORT_TAB: return RSItemHelper.click("Varrock t.*", "Break");
			default: return false;
    	}
    }
    
    private boolean useSkillsNecklace(TeleportLocation loc)
    {
    	switch(loc)
    	{
	    	case FISHING_GUILD: return teleportWithItem(SKILLS_FILTER, "Fishing.*");
	        case MOTHERLOAD_MINE: return teleportWithItem(SKILLS_FILTER, "Mother.*");
	        case CRAFTING_GUILD: return teleportWithItem(SKILLS_FILTER, "Crafting.*");
	        case COOKING_GUILD: return teleportWithItem(SKILLS_FILTER, "Cooking.*");
	        case WOOD_CUTTING_GUILD: return teleportWithItem(SKILLS_FILTER, "Woodcutting.*");
	        default: return false;
    	}
    }
    
    private boolean useRingOfWealth(TeleportLocation loc)
    {
    	switch(loc)
    	{
	    	case GRAND_EXCHANGE: return teleportWithItem(WEALTH_FILTER, "Grand.*");
	        case FALADOR_PARK: return teleportWithItem(WEALTH_FILTER, "Falad.*");
	        default: return false;
    	}
    }
    
    private boolean useGlory(TeleportLocation loc)
    {
    	switch(loc)
    	{
	    	case AL_KHARID: return teleportWithItem(GLORY_FILTER, "Al .*");
	        case EDGEVILLE: return teleportWithItem(GLORY_FILTER, "Edge.*");
	        case KARAMJA_BANANA_PLANTATION: return teleportWithItem(GLORY_FILTER, "Karamja.*");
	        case DRAYNOR_VILLAGE: return teleportWithItem(GLORY_FILTER, "Draynor.*");
	        default: return false;
    	}
    }
    
    private boolean useGamesNecklace(TeleportLocation loc)
    {
    	switch(loc)
    	{
		    case BURTHORPE_GAMES_ROOM: return teleportWithItem(GAMES_FILTER, "Burthorpe.*");
		    case WINTERTODT_CAMP: return teleportWithItem(GAMES_FILTER, "Winter.*");
		    case CORPOREAL_BEAST: return teleportWithItem(GAMES_FILTER, "Corp.*");
		    case BARBARIAN_OUTPOST: return teleportWithItem(GAMES_FILTER, "Barb.*");
		    default: return false;
    	}
    }
    
    private boolean useDuelingRing(TeleportLocation loc)
    {
    	switch(loc)
    	{
    	   case DUEL_ARENA: return teleportWithItem(DUELING_FILTER, "(Duel.*|Al K.*)");
           case CASTLE_WARS: return teleportWithItem(DUELING_FILTER, "Castle War.*");
           case CLAN_WARS: return teleportWithItem(DUELING_FILTER, "Clan Wars.*");
           default: return false;
    	}
    }
    
    private boolean useCombatBracelet(TeleportLocation loc)
    {
    	switch(loc)
    	{
	        case WARRIORS_GUILD: return teleportWithItem(COMBAT_FILTER, "Warrior.*");
	        case CHAMPIONS_GUILD: return teleportWithItem(COMBAT_FILTER, "Champ.*");
	        case MONASTRY_EDGE: return teleportWithItem(COMBAT_FILTER, "Mona.*");
	        case RANGED_GUILD: return teleportWithItem(COMBAT_FILTER, "Rang.*");
	        default: return false;
    	}
    }

    private static boolean itemAction(String name, String... actions){
        RSItem[] items = Inventory.find(name);
        if (items.length == 0){
            return false;
        }
        return items[0].click(actions);
    }

    private static boolean teleportWithItem(Filter<RSItem> itemFilter, String regex){
        ArrayList<RSItem> items = new ArrayList<>();
        items.addAll(Arrays.asList(Inventory.find(itemFilter)));
        items.addAll(Arrays.asList(Equipment.find(itemFilter)));

        if (items.size() == 0){
            return false;
        }

        RSItem teleportItem = items.get(0);
        if (!RSItemHelper.clickMatch(teleportItem, "(Rub|" + regex + ")")){
            return false;
        }

        RSTile startingPosition = Player.getPosition();
        return WaitFor.condition(General.random(3800, 4600), () -> {
            NPCInteraction.handleConversationRegex(regex);
            if (startingPosition.distanceTo(Player.getPosition()) > 5){
                return WaitFor.Return.SUCCESS;
            }
            return WaitFor.Return.IGNORE;
        }) == WaitFor.Return.SUCCESS;
    }

}
