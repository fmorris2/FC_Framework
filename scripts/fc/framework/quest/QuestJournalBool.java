package scripts.fc.framework.quest;

import scripts.fc.api.quest.JournalContents;
import scripts.fc.api.quest.QuestJournal;

public class QuestJournalBool extends QuestBool
{
	private String quest;
	private String line;
	private boolean isComplete, needsCacheReset;
	
	public QuestJournalBool(String quest, String line, boolean isComplete, boolean normal)
	{
		super(normal);
		this.quest = quest;
		this.line = line;
		this.isComplete = isComplete;
	}

	@Override
	public boolean value()
	{
		JournalContents contents = QuestJournal.getJournalContents(quest, needsCacheReset);
		needsCacheReset = false;
		return isComplete ? contents.hasLineCompleted(line, true) : contents.hasLineThatContains(line);
	}
	
	public void resetCache()
	{
		needsCacheReset = true;
	}

}
