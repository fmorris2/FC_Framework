package scripts.fc.api.quest;

import java.util.ArrayList;
import java.util.List;

public class JournalContents extends ArrayList<JournalLine>
{
	private static final long serialVersionUID = 2569006187074715169L;
	
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
}
