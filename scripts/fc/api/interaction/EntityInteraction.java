package scripts.fc.api.interaction;

import java.awt.Rectangle;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Clickable;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.util.ABCUtil;
import org.tribot.api2007.Camera;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Game;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.util.DPathNavigator;

import scripts.fc.api.interaction.impl.npcs.dialogue.NpcDialogue;
import scripts.fc.api.mouse.AccurateMouse;
import scripts.fc.api.travel.Travel;
import scripts.fc.api.utils.ChooseOptionUtils;
import scripts.fc.api.viewport.ACamera;
import scripts.fc.framework.data.Vars;

public abstract class EntityInteraction
{	
	protected static final int DISTANCE_THRESHOLD = 20;

	public static ACamera aCamera = new ACamera();
	
	public ABCUtil abcOne = Vars.get().get("abc");
	protected String name;
	protected int id;
	protected String action;
	protected int searchDistance;
	protected Positionable position;
	protected Clickable entity;
	private boolean canReach;
	protected boolean checkPath;
	private boolean walkToPos = true;
	
	public EntityInteraction(String action, String name, int searchDistance)
	{
		this(action, name);
		this.searchDistance = searchDistance;
	}
	
	public EntityInteraction(String action, String name, int searchDistance, boolean checkPath)
	{
		this(action, name, searchDistance);
		this.checkPath = checkPath;
	}
	
	public EntityInteraction(String action, int id, int searchDistance)
	{
		this.action = action;
		this.id = id;
		this.searchDistance = searchDistance;
	}
	
	public EntityInteraction(String action, Positionable position)
	{
		this.action = action;
		this.position = position;
	}
	
	public EntityInteraction(String action, String name)
	{
		this.action = action;
		this.name = name;
	}
	
	public EntityInteraction checkPath()
	{
		checkPath = true;
		return this;
	}
	
	public EntityInteraction dontCheckPath()
	{
		checkPath = false;
		return this;
	}
	
	public boolean execute()
	{
		if(Login.getLoginState() != STATE.INGAME)
			return false;
		
		//The following code is for if we prepared for this interaction beforehand and had the menu open
		if(ChooseOption.isOpen() && ChooseOption.isOptionValid(action))
			return handlePreemptiveSetup();
		
		if(position == null)
			findEntity();
		if(position != null || (entity != null && this instanceof ItemInteraction))
		{
			prepareInteraction();
			return interact();
		}
		
		return false;
	}
	
	private boolean handlePreemptiveSetup()
	{
		General.println("ChooseOption is open in EntityInteraction!! Interaction was prepared for us before....");
		Rectangle option = ChooseOptionUtils.getArea(action);
		if(option != null && option.contains(Mouse.getPos()))
			Mouse.click(1);
		else
		{
			Mouse.moveBox(option);
			Mouse.click(1);
		}
		
		if(this instanceof NpcDialogue)
			return interact();
		
		return true;
	}
	
	protected abstract boolean interact();
	
	protected abstract void findEntity();
	
	
	public boolean hoverEntity()
	{			
		findEntity();
		if(position != null)
		{
			if(!PathFinding.canReach(position, this instanceof ObjectInteraction))
				return false;
			
			if(Player.getPosition().distanceTo(position) < DISTANCE_THRESHOLD)
			{
				General.println("hoverEntity: isPlayerMoving(): " + Player.isMoving());
				if(!position.getPosition().isOnScreen())
					Camera.turnToTile(position);
				
				General.println("AcrruateMouse.hover for action: " + action);
				AccurateMouse.hover(entity, action);
				
				return Game.isUptext(name);
			}
		}
		
		return false;
	}
	
	public boolean openMenu()
	{
		//some basic checks first
		if(entity == null || action == null || ChooseOption.isOpen())
			return false;
		
		if(!PathFinding.canReach(position, this instanceof ObjectInteraction))
			return false;
		
		//check if we're hovering the entity, and if not if we try to hover it and succeed	
		if(Game.isUptext(name) || hoverEntity())
		{
			Mouse.click(3); //right click
			
			//get the appropriate rectangle area for the action
			Rectangle optionRect = ChooseOptionUtils.getArea(action);
			
			if(optionRect == null)
				return false;
			
			Mouse.moveBox(optionRect);
			return optionRect.contains(Mouse.getPos());
		}
	
		return false;
	}
	
	protected void prepareInteraction()
	{	
		canReach = checkPath ? PathFinding.canReach(position, this instanceof ObjectInteraction) : true;
		
		if(!canReach || (Player.getPosition().distanceTo(position) >= DISTANCE_THRESHOLD && !Player.isMoving()))
		{
			aCamera.turnToTile(position);
			walkToPosition();
		}
			
		if(!position.getPosition().isOnScreen())
		{
			aCamera.turnToTile(position);
			
			if(!position.getPosition().isOnScreen())
				walkToPosition();
		}
	}
	
	private void walkToPosition()
	{
		if(!walkToPos)
			return;
		
		if(!canReach)
		{
			if(!Travel.webWalkTo(position))
			{
				DPathNavigator dPath = new DPathNavigator();
				dPath.setMaxDistance(75);
				dPath.traverse(position);
			}
		}
		else
			Walking.blindWalkTo(position);		
	}
	
	public Clickable findClickable()
	{
		findEntity();
		return entity;
	}
	
	public void setWalkToPos(boolean bool)
	{
		walkToPos = bool;
	}
	
	public void setCheckPath(boolean bool)
	{
		checkPath = bool;
	}
	
}
