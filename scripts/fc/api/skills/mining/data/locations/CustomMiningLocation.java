package scripts.fc.api.skills.mining.data.locations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;

import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.skills.mining.data.RockType;
import scripts.fc.api.wrappers.SerializablePositionable;

public class CustomMiningLocation extends MiningLocation implements Serializable
{
	private static final long serialVersionUID = 1425327960897508793L;
	
	private SerializablePositionable[] bankPath;
	private SerializablePositionable serializableCenter;
	private String name;
	private int radius;
	private boolean isDepositBox;
	private transient Positionable[] path;
	
	public CustomMiningLocation(String name, boolean isDepositBox, SerializablePositionable center, int radius, SerializablePositionable[] bankPath, RockType... rocks)
	{
		this.name = name;
		this.isDepositBox = isDepositBox;
		this.serializableCenter = center;
		this.radius = radius;
		this.bankPath = bankPath;
		this.supported = Arrays.asList(rocks);
		this.serializableSupported = new ArrayList<>(Arrays.asList(rocks));
		convertSerializableObjects();
	}
	
	private void convertSerializableObjects()
	{
		path = new Positionable[bankPath.length];
		for(int i = 0; i < bankPath.length; i++)
			path[i] = bankPath[i].getPosition();	
	}
	
	@Override
	public Positionable centerTile()
	{	
		if(centerTile == null && serializableCenter != null)
			centerTile = serializableCenter.getPosition();
		
		return centerTile;
	}
	
	@Override
	public Positionable getCenterTile()
	{
		return centerTile == null ? centerTile() : centerTile;
	}

	@Override
	public String getName()
	{
		return name;
	}
	
	public Positionable[] getBankPath()
	{	
		if(path == null)
			convertSerializableObjects();
		
		return path;
	}
	
	@Override
	public boolean goTo()
	{	
		if(path != null && Walking.walkPath(Walking.invertPath(path), FCConditions.inAreaCondition(getArea()), 600))
			return true;
		
		return WebWalking.walkTo(centerTile.getPosition(), FCConditions.inAreaCondition(getArea()), 600);	
	}

	@Override
	public boolean goToBank()
	{
		if(getBankPath() != null && Walking.walkPath(getBankPath()))
			return true;
		
		return WebWalking.walkToBank();	
	}

	@Override
	public boolean hasRequirements()
	{
		return true;
	}

	@Override
	public int getRadius()
	{
		return radius;
	}
	
	public SerializablePositionable getSerializableCenter()
	{
		return serializableCenter;
	}

	@Override
	public boolean isDepositBox()
	{
		return isDepositBox;
	}

	@Override
	public List<RockType> getSupported()
	{
		return serializableSupported;
	}
}
