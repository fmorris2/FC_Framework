package scripts.fc.framework.quest;

import org.tribot.api2007.Banking;
import org.tribot.api2007.GrandExchange;

import scripts.fc.api.quest.JournalContents;
import scripts.fc.api.quest.QuestJournal;

public class QuestJournalBool extends QuestBool
{
	private String quest;
	private String line;
	private boolean needsCacheReset;
	private JOURNAL_STATUS status;
	private boolean failed;
	
	public QuestJournalBool(String quest, String line, JOURNAL_STATUS status, boolean normal)
	{
		super(normal);
		this.quest = quest;
		this.line = line;
		this.status = status;
	}

	@Override
	public boolean value()
	{
		if(!QuestJournal.isCached(quest) || needsCacheReset)
		{
			if(Banking.isBankScreenOpen())
				Banking.close();
			
			if(GrandExchange.getWindowState() != null)
				GrandExchange.close();
		}
		
		JournalContents contents = QuestJournal.getJournalContents(quest, needsCacheReset);
		if(!contents.isEmpty())
		{
			needsCacheReset = false;
			failed = false;
		}
		else
			failed = true;
		
		if(status == JOURNAL_STATUS.CONTAINS_AND_ISNT_COMPLETE)
			return contents.hasLineThatContains(line) && !contents.hasLineCompleted(line, true);
		
		return status == JOURNAL_STATUS.HAS_COMPLETED ? contents.hasLineCompleted(line, true) : contents.hasLineThatContains(line);
	}
	
	@Override
	public boolean validate()
	{
		return super.validate() && !failed;
	}
	
	public void resetCache()
	{
		needsCacheReset = true;
	}
	
	public enum JOURNAL_STATUS
	{
		CONTAINS_STRING,
		CONTAINS_AND_ISNT_COMPLETE,
		HAS_COMPLETED
	}

}
