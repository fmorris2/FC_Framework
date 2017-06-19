package scripts.fc.api.wrappers;

import java.io.Serializable;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.types.RSTile;

public class SerializablePositionable implements Serializable
{
	private static final long serialVersionUID = -4576125473185019278L;
	
	private int x;
	private int y;
	private int z;
	private transient Positionable position;
	
	public SerializablePositionable(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.position = new RSTile(x, y, z);
	}
	
	public SerializablePositionable(Positionable p)
	{
		this.x = p.getPosition().getX();
		this.y = p.getPosition().getY();
		this.z = p.getPosition().getPlane();
		this.position = new RSTile(x, y, z);
	}
	
	public Positionable getPosition()
	{
		return position == null ? new RSTile(x, y, z) : position;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getZ()
	{
		return z;
	}
	
	public String toString()
	{
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
