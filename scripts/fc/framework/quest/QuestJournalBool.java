package scripts.fc.framework.quest;

import scripts.fc.api.quest.JournalContents;
import scripts.fc.api.quest.QuestJournal;

public class QuestJournalBool extends QuestBool
{
	private String quest;
	private String line;
	private boolean needsCacheReset;
	private JOURNAL_STATUS status;
	
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
		JournalContents contents = QuestJournal.getJournalContents(quest, needsCacheReset);
		needsCacheReset = false;
		return status == JOURNAL_STATUS.HAS_COMPLETED ? contents.hasLineCompleted(line, true) : contents.hasLineThatContains(line);
	}
	
	public void resetCache()
	{
		needsCacheReset = true;
	}
	
	public enum JOURNAL_STATUS
	{
		CONTAINS_STRING,
		HAS_COMPLETED
	}

}
