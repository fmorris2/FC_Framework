package scripts.fc.api.interaction;

import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Objects;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSObject;

import scripts.fc.api.abc.ABC2Helper;

public abstract class ObjectInteraction extends EntityInteraction
{
	protected RSObject object;
	protected RSArea area;
	
	public ObjectInteraction(String action, String name, int searchDistance)
	{
		super(action, name, searchDistance);
	}
	
	public ObjectInteraction(String action, int id, int searchDistance)
	{
		super(action, id, searchDistance);
	}

	public ObjectInteraction(String action, RSObject object)
	{
		super(action, object);
		this.object = object;
	}
	
	public ObjectInteraction(String action, String name, RSArea area)
	{
		super(action, name);
		this.area = area;
	}

	public ObjectInteraction(String action, String name, int searchDistance, boolean checkPath)
	{
		super(action, name, searchDistance, checkPath);
	}

	@Override
	protected void findEntity()
	{
		RSObject[] objects = null;
		Filter<RSObject> filter = null;
		
		if(area != null)
			filter = Filters.Objects.inArea(area);
		
		if(id <= 0)
			objects = Objects.findNearest(searchDistance, (filter == null ? Filters.Objects.nameEquals(name) : filter.combine(Filters.Objects.nameEquals(name), false)));
		else
			objects = Objects.findNearest(searchDistance, (filter == null ? Filters.Objects.idEquals(id) : filter.combine(Filters.Objects.idEquals(id), false)));
		
		if(objects.length > 0)
		{
			object = ABC2Helper.shouldUseClosest(abcOne, objects) ? objects[0] : objects[1];
			entity = object;
			position = object;
		}
	}

}
