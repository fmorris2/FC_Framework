package scripts.fc.api.quest;

public class JournalLine
{
	private static final String TAG_REGEX = "<[^>]+>";
	private static final String STRIKETHROUGH = "<str>";
	
	private String text;
	private boolean isComplete;
	
	public JournalLine(String unparsed)
	{
		text = parse(unparsed);
		isComplete = isComplete(unparsed);
	}
	
	private String parse(String unparsed)
	{
		return unparsed.replaceAll(TAG_REGEX, "");
	}
	
	private boolean isComplete(String str)
	{
		return str.startsWith(STRIKETHROUGH);
	}
	
	
	//START PUBLIC INTERFACE
	
	public boolean equals(String str)
	{
		return text.equals(str);
	}
	
	public boolean contains(String str)
	{
		return text.contains(str);
	}
	
	public boolean isComplete()
	{
		return isComplete;
	}
	
	public String toString()
	{
		return "Text: " + text + ", Complete: " + isComplete;
	}
	
}
