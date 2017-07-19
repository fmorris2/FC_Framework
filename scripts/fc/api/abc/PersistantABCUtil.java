package scripts.fc.api.abc;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Clickable07;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;

/**
 * An extension of ABCUtil, allowing for persistence with some of the
 * conditions, in replica of ABCv1.
 * 
 * @author TRiLeZ
 */
public class PersistantABCUtil extends ABCUtil {

	private transient int run_at = -1;
	private transient int eat_at = -1;
	private transient Boolean should_hover = null;
	private transient Boolean should_open_menu = null;

	private transient Positionable next_target = null;
	private transient Positionable next_target_closest = null;
	private transient String playerName;

	/**
	 * Gets the persistent value for the energy at which we should activate run,
	 * or generates one if there is no persistent value already set.
	 * 
	 * @return int
	 */
	@Override
	public int generateRunActivation() {
		if (this.run_at != -1)
			return this.run_at;

		return this.run_at = super.generateRunActivation();
	}

	/**
	 * Resets the run activation persistence.
	 */
	public void resetRunActivation() {
		this.run_at = -1;
	}
	
	public int generateEatAtHP()
	{
		if(this.eat_at != -1)
			return this.eat_at;
		
		return this.eat_at = super.generateEatAtHP();
	}
	
	public void resetEatAtHP()
	{
		this.eat_at = -1;
	}

	/**
	 * Determines whether or not we should hover the mouse over the next target,
	 * using persistence.
	 * 
	 * @return boolean
	 */
	@Override
	public boolean shouldHover() {
		
		if(!Mouse.isInBounds())
			return false;
		
		if (this.should_hover != null)
			return this.should_hover;

		return this.should_hover = super.shouldHover();
	}

	/**
	 * Resets the should hover persistence.
	 */
	public void resetShouldHover() {
		this.should_hover = null;
	}

	/**
	 * Determines whether or not we should open the menu for the next target,
	 * using persistence.
	 * 
	 * @return boolean
	 */
	@Override
	public boolean shouldOpenMenu() {
		if(!Mouse.isInBounds())
			return false;
		
		if (this.should_open_menu != null)
			return this.should_open_menu;

		return this.should_open_menu = super.shouldOpenMenu();
	}

	/**
	 * Resets the should open menu persistence.
	 */
	public void resetShouldOpenMenu() {
		this.should_open_menu = null;
	}

	/**
	 * Determines if our current next target is still valid.
	 * 
	 * @param possible_targets
	 *            The possible next targets. This is used just in-case a new
	 *            closest target is available. If that is the case, then we can
	 *            say he next target is invalid and let the script determine a
	 *            new next target.
	 * 
	 * @return boolean
	 */
	private boolean isNextTargetValid(final Positionable[] possible_targets) {
		final RSTile pos = this.next_target.getPosition();
		if (pos == null)
			return false;

		if (!Projection.isInViewport(Projection.tileToScreen(pos, 0)))
			return false;

		if (this.next_target instanceof Clickable07 && !((Clickable07) this.next_target).isClickable())
			return false;

		if (this.next_target instanceof RSNPC && !((RSNPC) this.next_target).isValid())
			return false;

		if (this.next_target instanceof RSCharacter) {
			final String name = ((RSCharacter) this.next_target).getName();
			if (name == null || name.trim().equalsIgnoreCase("null"))
				return false;
		}

		if (this.next_target instanceof RSObject) {
			if (!Objects.isAt(this.next_target, new Filter<RSObject>() {

				@Override
				public boolean accept(final RSObject o) {
					return o.obj.equals(((RSObject) next_target).obj);
				}

			}))
				return false;
		}

		// Finally, we check if there is a new closest possible target.
		if (possible_targets != null && possible_targets.length > 0 && this.next_target_closest != null) {
			final RSTile new_closest_tile = possible_targets[0].getPosition();
			final RSTile orig_closest_tile = this.next_target_closest.getPosition();
			final RSTile player_pos = Player.getPosition();

			if (new_closest_tile != null && orig_closest_tile != null && player_pos != null) {
				final double new_closest_dist = new_closest_tile.distanceToDouble(player_pos);
				final double orig_closest_dist = orig_closest_tile.distanceToDouble(player_pos);

				// If there is a new closest object
				if (new_closest_dist < orig_closest_dist)
					return false;
			}
		}

		return true;
	}

	/**
	 * Nullifies the next target.
	 */
	public void resetNextTarget() {
		this.next_target = null;
		this.next_target_closest = null;
	}

	/**
	 * Gets the next target.
	 * 
	 * @return {@link Positionable}, or null if we do not currently have a next
	 *         target.
	 */
	public Positionable getNextTarget() {
		if (this.next_target == null || !isNextTargetValid(null))
			return null;

		return this.next_target;
	}

	/**
	 * Selects the next target, using persistence.
	 * 
	 * @return {@link Positionable}
	 */
	@Override
	public Positionable selectNextTarget(final Positionable[] possible_targets) {
		try {
			if (this.next_target != null && isNextTargetValid(possible_targets))
				return this.next_target;

			return this.next_target = super.selectNextTarget(possible_targets);
		} finally {
			if (this.next_target != null && possible_targets.length > 0)
				this.next_target_closest = possible_targets[0];
		}
	}
	
	public int generateReactionTime(int waitingTime)
	{
		// If the condition is met, we specify the relevant flag, otherwise we set the variable to 0
		// When we pass 0 into generateReactionTime as a bit flag option, it will not change anything
		final long hover_option = shouldHover() ? ABCUtil.OPTION_HOVERING : 0;
		final long menu_open_option = shouldOpenMenu() ? ABCUtil.OPTION_MENU_OPEN : 0;
		
		// Generate the reaction time
		return generateReactionTime(generateBitFlags(waitingTime, hover_option, menu_open_option));
	}
	
	public void generateTrackers(int waitingTime)
	{
		generateTrackers(generateBitFlags(waitingTime));
	}
	
	/*
	 * Convenient method to perform all timed actions
	 */
	public boolean performTimedActions()
	{
		if (shouldCheckTabs())
			checkTabs();
		else if (shouldCheckXP())
			checkXP();
		else if (shouldExamineEntity())
			examineEntity();
		else if (shouldMoveMouse())
			moveMouse();
		else if (shouldPickupMouse())
			pickupMouse();
		else if (shouldRightClick())
			rightClick();
		else if (shouldRotateCamera())
			rotateCamera();
		else if (shouldLeaveGame())
			leaveGame();
		else
			return false;
		
		return true;
	}
	
	/*
	 * Checks if this util needs to be reset due
	 * to a new Runescape account being logged in
	 */
	public boolean needsReset()
	{
		RSPlayer player = Player.getRSPlayer();
		String newName = player != null ? player.getName() : null;
		
		if(newName != null)
		{
			if(playerName == null)
				playerName = newName;
			else if(!playerName.equals(newName))
			{
				playerName = player.getName();
				return true;
			}
		}
		
		return false;
	}
	
	public void generateAndPerformReaction(long waitTime)
	{
		Long boxed = new Long(waitTime);
		long reactionTime = (generateReactionTime(boxed.intValue()));
		General.println("Performing wait of " + reactionTime + "ms for ABC2 reaction time");
		generateTrackers(reactionTime);
		sleep(reactionTime);
		generateTrackers();
	}
	
	public void sleep(long ms)
	{
		try
		{
			super.sleep(ms);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}

}
