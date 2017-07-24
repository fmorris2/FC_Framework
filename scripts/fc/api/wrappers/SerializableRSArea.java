package scripts.fc.api.wrappers;

import java.io.Serializable;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSTile;

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
		if(area == null)
			area = new RSArea(centerTile.getPosition(), radius);
		
		return area;
	}
	
	public boolean contains(Positionable p)
	{
		int x = p.getPosition().getX(), y = p.getPosition().getY();
		int centerX = centerTile.getX(), centerY = centerTile.getY();
		int minX = centerX - radius, maxX = centerX + radius, minY = centerY - radius, maxY = centerY + radius;
		return minX <= x && maxX >= x && minY <= y && maxY >= y;
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
