package scripts.fc.framework;

import java.io.Serializable;

public class WorldHopSettings implements Serializable
{
	private static final long serialVersionUID = -5771913543672453230L;
	
	public int playersInArea = -1;
	public int resourceStolen = -1;
	public boolean noResourceAvailable = false;
	
	public WorldHopSettings(int pia, int rs, boolean nra)
	{
		playersInArea = pia;
		resourceStolen = rs;
		noResourceAvailable = nra;
	}
}
