package scripts.fc.api.quest;

import java.util.ArrayList;
import java.util.List;

import org.tribot.api.Timing;

public class JournalContents extends ArrayList<JournalLine>
{
	private static final long serialVersionUID = 2569006187074715169L;
	private static final long UPDATE_THRESH = 60000 * 5; //WE STOP RELYING ON THE CACHE EVERY 5 MINUTES
	
	private long created = Timing.currentTimeMillis();
	
	public JournalContents(List<JournalLine> collect)
	{
		super(collect);
	}

	public JournalContents()
	{
		super();
	}

	public boolean hasLineThatContains(String str)
	{
		return stream().anyMatch(l -> l.contains(str));
	}
	
	public boolean hasLineThatEquals(String str)
	{
		return stream().anyMatch(l -> l.equals(str));
	}
	
	public boolean hasLineCompleted(String str, boolean contains)
	{
		return stream().anyMatch(l -> (contains ? l.contains(str) : l.equals(str) && l.isComplete()));				
	}
	
	public boolean needsCacheUpdate()
	{
		return Timing.timeFromMark(created) > UPDATE_THRESH;
	}
}
