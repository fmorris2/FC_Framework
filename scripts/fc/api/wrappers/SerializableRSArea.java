package scripts.fc.api.wrappers;

import java.io.Serializable;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.types.RSArea;

public class SerializableRSArea implements Serializable
{
	private static final long serialVersionUID = -9031856365675937395L;

	private SerializablePositionable centerTile;
	private int radius;
	private transient RSArea area;
	
	public SerializableRSArea(SerializablePositionable centerTile, int radius)
	{
		this.centerTile = centerTile;
		this.radius = radius;
		this.area = new RSArea(centerTile.getPosition(), radius);
	}
	
	public RSArea getArea()
	{
		return area;
	}
	
	public int getRadius()
	{
		return radius;
	}
	
	public Positionable getCenterTile()
	{
		return centerTile.getPosition();
	}
}
