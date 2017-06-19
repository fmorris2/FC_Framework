package scripts.fc.api.skills.fishing.locations.impl;

import java.util.Arrays;
import java.util.List;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.ext.Ships;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSTile;

import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.interaction.impl.npcs.dialogue.NpcDialogue;
import scripts.fc.api.skills.GatheringLocation;
import scripts.fc.api.skills.fishing.FishType;

public class Karamja extends GatheringLocation<FishType>
{
	public static final RSArea KARAMJA = new RSArea(new RSTile(2901, 3160, 0), 60);
	private static final RSArea BOARD_SHIP = new RSArea(new RSTile(3027, 3217, 0), 10);
	private static final RSArea CUSTOMS_OFFICER = new RSArea(new RSTile(2954, 3147, 0), 10);
	private static final Condition BOAT_CONDITION = boatCondition();
	private static final Positionable BANK_TILE = new RSTile(3043, 3236, 0);
	private static final String[] SEAMEN = {"Seaman Lorris", "Captain Tobias", "Seaman Thresnor"};
	
	@Override
	public String getName()
	{
		return "Karamja";
	}

	@Override
	public List<FishType> getSupported()
	{
		return Arrays.asList(FishType.LOBSTER, FishType.SWORDFISH);
	}

	@Override
	public Positionable centerTile()
	{
		return new RSTile(2925, 3179, 0);
	}

	@Override
	public boolean goTo()
	{
		if(KARAMJA.contains(Player.getPosition())) //ON KARAMJA
			return WebWalking.walkTo(centerTile.getPosition(), FCConditions.withinDistanceOfTile(centerTile, 8), 600);
		else if(Ships.isOnShip()) //ON SHIP
			handleShip();
		else //ON MAINLAND
			goToShip();
		
		return false;
	}

	@Override
	public boolean isDepositBox()
	{
		return true;
	}

	@Override
	public boolean goToBank()
	{
		final boolean ON_SHIP = Ships.isOnShip();
		final boolean ON_KARAMJA = KARAMJA.contains(Player.getPosition());
		final boolean ON_MAINLAND = !ON_SHIP && !ON_KARAMJA;
		
		if(ON_MAINLAND)
			return WebWalking.walkTo(BANK_TILE);
		else if(ON_SHIP && !Ships.isSailing())
		{
			if(Ships.crossGangplank())
				General.sleep(600, 1200);
		}
		else if(ON_KARAMJA)
			boardShipFromKaramja();
		
		return false;
	}
	
	private void boardShipFromKaramja()
	{
		General.println("Board ship from karamja");
		if(!CUSTOMS_OFFICER.contains(Player.getPosition()))
			WebWalking.walkTo(CUSTOMS_OFFICER.getRandomTile(), FCConditions.inAreaCondition(CUSTOMS_OFFICER), 2500);
		else
		{
			General.println("Talk to customs officer");
			if(new NpcDialogue("Talk-to", "Customs officer", 25, 0, 1, 0).execute())
				Timing.waitCondition(BOAT_CONDITION, 7500);
		}
	}

	@Override
	public boolean hasRequirements()
	{
		return true;
	}

	@Override
	public int getRadius()
	{
		return 15;
	}
	
	private void goToShip()
	{	
		General.println("Go to ship...");
		if(!BOARD_SHIP.contains(Player.getPosition()))
			WebWalking.walkTo(BOARD_SHIP.getRandomTile(), FCConditions.inAreaCondition(BOARD_SHIP), 600);
		else
		{
			General.println("Talk to seamen...");
			RSNPC[] seamen = NPCs.findNearest(SEAMEN);
			if(seamen.length > 0 && new NpcDialogue("Talk-to", seamen[0].getName(), 15, 0).execute())
				Timing.waitCondition(BOAT_CONDITION, 7500);
					
		}
	}
	
	private static Condition boatCondition()
	{
		return new Condition()
		{
			public boolean active()
			{
				General.sleep(100);
				return Ships.isOnShip() && !Ships.isSailing();
			}
		};
	}
	
	private void handleShip()
	{
		if(Ships.isSailing())
			General.println("sailing...");
		else
		{
			General.println("cross gangplank...");
			if(Ships.crossGangplank())
				General.sleep(600, 1200);
		}
	}

}
