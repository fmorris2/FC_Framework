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
	
	public static Condition invAmountChanged(String name, int prevCount)
	{
		return new Condition()
		{
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
			public boolean active()
			{
				General.sleep(100);
				return !Player.isMoving();
			}
		};
	}
	
	public static Condition interTextNotContains(int master, int child, String text)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				RSInterface inter = Interfaces.get(master, child);
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
	
	public static Condition varbitChanged(int varbit, int value)
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
	
	public static Condition tradingWithCondition(String name)
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
	
	public static Condition onWorldCondition(int world)
	{
		return new Condition()
		{
			public boolean active()
			{
				General.sleep(100);
				return WorldHopper.getWorld() == world;
			}
		};
	}
	
	public static Condition interfaceNotUp(int id)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				RSInterface inter = Interfaces.get(id);
				return inter == null || inter.isHidden();
			}		
		};
	}
	
	public static Condition inventoryNotContains(int id)
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
	
	public static Condition inventoryNotContains(String name)
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
	
	public static Condition isEquipped(int id)
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
	
	public static Condition settingEqualsCondition(int index, int setting)
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
	
	public static Condition settingNotEqualsCondition(int index, int setting)
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
	
	public static Condition withinDistanceOfTile(Positionable p, int threshold)
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
	
	public static Condition yCoordGreaterThan(int y)
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
	
	public static Condition yCoordLessThan(int y)
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
				RSInterface inter = Interfaces.get(162, 32);
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
				RSCharacter interacting  = Player.getRSPlayer().getInteractingCharacter();
				RSCharacter interactingsInteracting = interacting == null ? null : interacting.getInteractingCharacter();
				
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
				RSInterface inter = Interfaces.get(BANK_AMT_MASTER, BANK_AMT_CHILD);
				String text = inter == null || inter.isHidden() ? null : inter.getText();
				int amt = text == null || text.length() == 0 ? -1 : Integer.parseInt(text);
				
				return amt != -1 && amt == Banking.getAll().length;
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
			public boolean active()
			{
				General.sleep(100);
				return Login.getLoginState() == STATE.INGAME
						&& Game.getGameState() == 30;
			}
		};
	}
	
	public static Condition interfaceUp(int id)
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
				RSCharacter target = Combat.getTargetEntity();
				RSCharacter rangedTarget = Player.getRSPlayer().getInteractingCharacter();
				return target != null && target.getHealth() <= 0 ||
						(rangedTarget != null && rangedTarget.isInCombat()
						&& rangedTarget.isInteractingWithMe() && rangedTarget.getHealth() <= 0);
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
				Timing.waitCondition(IN_DIALOGUE_CONDITION, 600);	
				return NPCChat.getSelectOptionInterface() != null 
						|| InterfaceUtils.findContainingText("Click here to continue") == null;
			}
		};
	}
	
	public static Condition positionEquals(Positionable p)
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

	public static Condition tileOnScreen(RSTile tile)
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

	public static Condition uptextContains(String str)
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

	public static Condition tradeContains(String name, boolean other)
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
	
	public static Condition tradeContains(int id, boolean other)
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
	
	public static Condition tradeContains(String name, int amt, boolean other)
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
