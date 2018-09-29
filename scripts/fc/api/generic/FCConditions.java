package scripts.fc.api.generic;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Banking;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Combat;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Game;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.Player;
import org.tribot.api2007.Trading;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.types.RSVarBit;

import scripts.fc.api.trading.FCTrading;
import scripts.fc.api.utils.InterfaceUtils;
import scripts.webwalker_logic.shared.helpers.BankHelper;

public class FCConditions
{
	public static final Condition CHOOSE_OPTION_CONDITION = chooseOptionOpen();
	public static final Condition IN_BANK_CONDITION = inBankCondition();
	public static final Condition IN_DIALOGUE_CONDITION = inDialogueCondition();
	public static final Condition SPACEBAR_HOLD = spaceBarHold();
	public static final Condition DEPOSIT_BOX_OPEN_CONDITION = depositBoxOpenCondition();
	public static final Condition CLICK_CONTINUE_CONDITION = clickContinueCondition();
	public static final Condition KILL_CONDITION = killCondition();
	public static final Condition IN_GAME_CONDITION = inGameCondition();
	public static final Condition NOT_IN_GAME_CONDITION = notInGameCondition();
	public static final Condition BANK_LOADED_CONDITION = bankLoadedCondition();
	public static final Condition IN_COMBAT_CONDITION = inCombatCondition();
	public static final Condition ENTER_AMOUNT_CONDITION = enterAmountCondition();
	public static final Condition NOT_TRADING_CONDITION = notTradingCondition();
	public static final Condition NOT_MOVING_CONDITION = notMovingCondition();
	public static final Condition IN_BUILDING_CONDITION = inBuildingCondition();
	public static final Condition PLEASE_WAIT_NOT_UP_CONDITION = pleaseWaitNotUpCondition();
	
	public static Condition inBuildingCondition()
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(30);
				return BankHelper.isInBuilding(Player.getPosition().toLocalTile(), Game.getSceneFlags());
			}
		};
	}
	
	public static Condition objectOnScreenCondition(final RSObject object)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return object.isOnScreen();
			}
		};
	}
	
	public static Condition invAmountChanged(final String name, final int prevCount)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Inventory.getCount(name) > prevCount;
			}
		};
	}
	
	private static Condition chooseOptionOpen()
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return ChooseOption.isOpen();
			}
		};
	}
	
	private static Condition notMovingCondition()
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return !Player.isMoving();
			}
		};
	}
	
	public static Condition interTextNotContains(final int master, final int child, final String text)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				final RSInterface inter = Interfaces.get(master, child);
				return inter != null && !inter.getText().equals(text);
			}

		};
	}
	
	private static Condition notTradingCondition()
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Trading.getWindowState() == null;
			}
		};
	}
	
	public static Condition varbitChanged(final int varbit, final int value)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return RSVarBit.get(varbit).getValue() != value;
			}
		};
	}
	
	public static Condition tradingWithCondition(final String name)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return FCTrading.getTradingWith().equals(name);
			}
		};
	}
	
	public static Condition onWorldCondition(final int world)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return WorldHopper.getWorld() == world || WorldHopper.getWorld() % 100 == world;
			}
		};
	}
	
	public static Condition interfaceNotUp(final int id)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				final RSInterface inter = Interfaces.get(id);
				return inter == null || inter.isHidden();
			}		
		};
	}
	
	public static Condition inventoryNotContains(final int id)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Inventory.getCount(id) == 0;
			}		
		};
	}
	
	public static Condition inventoryNotContains(final String name)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Inventory.getCount(name) == 0;
			}		
		};
	}
	
	public static Condition isEquipped(final int id)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Equipment.isEquipped(id);
			}	
		};
	}
	
	public static Condition settingEqualsCondition(final int index, final int setting)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Game.getSetting(index) == setting;
			}
		};
	}
	
	public static Condition settingNotEqualsCondition(final int index, final int setting)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Game.getSetting(index) != setting;
			}
		};
	}
	
	public static Condition withinDistanceOfTile(final Positionable p, final int threshold)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Player.getPosition().distanceTo(p) < threshold;
			}
		};
	}
	
	public static Condition yCoordGreaterThan(final int y)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Player.getPosition().getY() > y;
			}
		};
	}
	
	public static Condition yCoordLessThan(final int y)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Player.getPosition().getY() < y;
			}
		};
	}
	
	private static Condition enterAmountCondition()
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				final RSInterface inter = Interfaces.get(162, 32);
				return inter != null && !inter.isHidden();
			}
		};
	}
	
	private static Condition inCombatCondition()
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				final RSCharacter interacting  = Player.getRSPlayer().getInteractingCharacter();
				final RSCharacter interactingsInteracting = interacting == null ? null : interacting.getInteractingCharacter();
				
				return Player.getRSPlayer().isInCombat() ||
							(interacting != null 
									&& ((interacting.isInteractingWithMe() && interacting.isInCombat())
										|| (interactingsInteracting != null && !interactingsInteracting.equals(Player.getRSPlayer()))));
			}
		};
	}
	
	private static Condition bankLoadedCondition()
	{
		final int BANK_AMT_MASTER = 12;
		final int BANK_AMT_CHILD = 5;
		
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				final RSInterface inter = Interfaces.get(BANK_AMT_MASTER, BANK_AMT_CHILD);
				final String text = inter == null || inter.isHidden() ? null : inter.getText();
				final int amt = text == null || text.length() == 0 ? -1 : Integer.parseInt(text);
				return amt > 0 && amt == Banking.getAll().length;
			}
		};
	}
	
	private static Condition clickContinueCondition()
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return NPCChat.getClickContinueInterface() != null;
			}
		};
	}
	
	private static Condition notInGameCondition()
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Game.getGameState() != 30 || Login.getLoginState() != STATE.INGAME;
			}
		};
	}
	
	private static Condition inGameCondition()
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Login.getLoginState() == STATE.INGAME
						&& Game.getGameState() == 30;
			}
		};
	}
	
	public static Condition interfaceUp(final int id)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Interfaces.get(id) != null;
			}

		};
	}
	
	public static Condition killCondition()
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				final RSCharacter target = Combat.getTargetEntity();
				final RSCharacter rangedTarget = Player.getRSPlayer().getInteractingCharacter();
				return target != null && target.getHealthPercent() <= 0 ||
						(rangedTarget != null && rangedTarget.isInCombat()
						&& rangedTarget.isInteractingWithMe() && rangedTarget.getHealthPercent() <= 0);
			}
	
		};
	}
	
	private static Condition inDialogueCondition()
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);		
				return NPCChat.getClickContinueInterface() != null || NPCChat.getSelectOptionInterface() != null;
			}
		};
	}
	
	private static Condition spaceBarHold()
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				Timing.waitCondition(IN_DIALOGUE_CONDITION, 1200);
				final RSInterface continueInter = InterfaceUtils.findContainingText("Click here to continue");
				
				if(Login.getLoginState() != STATE.INGAME) {
					return true;
				}
				
				return NPCChat.getSelectOptionInterface() != null 
						|| ((continueInter == null || continueInter.isHidden())
							 && Timing.waitCondition(PLEASE_WAIT_NOT_UP_CONDITION, 5400));
			}
		};
	}
	
	private static Condition pleaseWaitNotUpCondition() {
		return new Condition() {
			@Override
			public boolean active() {
				General.sleep(100);
				final RSInterface pleaseWait = InterfaceUtils.findContainingText("Please wait...");
				return pleaseWait == null || pleaseWait.isHidden();
			}
		};
	}
	
	public static Condition positionEquals(final Positionable p)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Player.getPosition().equals(p);
			}
		};
	}
	
	private static Condition inBankCondition()
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);				
				return Banking.isInBank();
			}
			
		};
	}
	
	private static Condition depositBoxOpenCondition()
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Banking.isDepositBoxOpen();
			}
			
		};
	}
	
	public static Condition planeChanged(final int startingPlane)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Player.getPosition().getPlane() != startingPlane;
			}
		};
	}
	
	public static Condition animationChanged(final int startingAnimation)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				
				return Player.getAnimation() != startingAnimation;
			}	
		};
	}
	
	public static Condition inventoryContains(final String name)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Inventory.getCount(name) > 0;
			}
		};
	}
	
	public static Condition inAreaCondition(final RSArea area)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{	
				General.sleep(100);
				return area.contains(Player.getPosition());
			}
			
		};
	}
	
	public static Condition inventoryChanged(final int startingAmt)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Inventory.getAll().length != startingAmt;
			}	
		};
	}

	public static Condition tileOnScreen(final RSTile tile)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return tile.isOnScreen();
			}	
		};
	}

	public static Condition uptextContains(final String str)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Game.isUptext(str);
			}	
		};
	}

	public static Condition tradeContains(final String name, final boolean other)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Trading.getCount(other, name) > 0;
			}	
		};
	}
	
	public static Condition tradeContains(final int id, final boolean other)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Trading.getCount(other, id) > 0;
			}	
		};
	}
	
	public static Condition tradeContains(final String name, final int amt, final boolean other)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Trading.getCount(other, name) >= amt;
			}	
		};
	}
}
