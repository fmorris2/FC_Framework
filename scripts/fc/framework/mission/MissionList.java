package scripts.fc.framework.mission;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MissionList implements Serializable
{
	private static final long serialVersionUID = 5711067302701942794L;
	
	private String name;
	private Queue<Mission> missions;
	
	public MissionList(String name, List<Mission> missions)
	{
		this.name = name;
		this.missions = new LinkedList<>(missions);
	}
	
	public String getName()
	{
		return name;
	}
	
	public Queue<Mission> getMissions()
	{
		return missions;
	}
	
	public String toString()
	{
		return name;
	}
}
