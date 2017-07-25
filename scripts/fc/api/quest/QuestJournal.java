package scripts.fc.api.quest;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;

import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.utils.InterfaceUtils;

public class QuestJournal
{	
	
	private static final int JOURNAL_MASTER = 275, NAME_CHILD = 2, MIN_X = 560, MAX_X = 712, MIN_Y = 235, MAX_Y = 450;
	private static final int KOUREND_MASTER = 245, QUEST_CHILD = 2;
	private static final Rectangle QUEST_LIST_SCROLLABLE_AREA = new Rectangle(MIN_X, MIN_Y, (MAX_X - MIN_X), (MAX_Y - MIN_Y));
	private static final Map<String, JournalContents> CACHE = new HashMap<>();
	
	public static JournalContents getJournalContents(String name)
	{
		return getJournalContents(name, false);
	}
	
	public static JournalContents getJournalContents(String name, boolean overrideCache)
	{
		RSInterface questJournal;
		if(overrideCache)
			CACHE.put(name, null);
		
		JournalContents cached = CACHE.get(name);
		
		//check the cache first
		if(cached != null && !cached.needsCacheUpdate())
			return cached;
		
		//if the quest journal is already opened, just parse it
		if((questJournal = getOpenQuestJournal(name)) != null)
			return parseJournal(name, questJournal);
		
		//if the kourend favor tab is open
		RSInterface kourendMain = InterfaceUtils.findContainingText("Favour Overlay");
		if(kourendMain != null && !kourendMain.isHidden())
		{
			RSInterface kourend = Interfaces.get(KOUREND_MASTER, QUEST_CHILD);
			if(kourend != null)
				Clicking.click(kourend);
		}
		
		//if we failed to open the quest tab, return
		if(!GameTab.open(TABS.QUESTS))
			return new JournalContents();
		
		//we attempt to scroll to the quest, click it, and wait for it to open. If success, parse it
		RSInterface button = getQuestButton(name);
		if(button != null && scrollToButton(button) 
				&& clickButton(button) && (questJournal = getOpenQuestJournal(name)) != null)
		{
			return parseJournal(name, questJournal);
		}
		
		//we failed to scroll to the quest and click it
		return new JournalContents();
	}
	
	private static JournalContents parseJournal(String name, RSInterface journal)
	{
		//collect all children which have text in the quest journal
		//c.getText() should not change while viewing the journal, so no real risk of NPE. Can change if necessary
		JournalContents contents = new JournalContents(
			Arrays.stream(journal.getChildren())
			.filter(c -> c != null && c.getText() != null && !c.getText().isEmpty())
			.map(RSInterface::getText)
			.map(JournalLine::new)
			.collect(Collectors.toList())
		);
		
		CACHE.put(name, contents);
		InterfaceUtils.closeQuestInterface();
		return contents;
	}
	
	private static RSInterface getOpenQuestJournal(String name)
	{
		RSInterface nameInter = Interfaces.get(JOURNAL_MASTER, NAME_CHILD);
		return nameInter == null || !nameInter.getText().contains(name) ? null : nameInter.getParent(); 
	}
	
	private static boolean clickButton(RSInterface button)
	{
		return Clicking.click(button) && Timing.waitCondition(FCConditions.interfaceUp(JOURNAL_MASTER), 1800);
	}
	
	private static RSInterface getQuestButton(String name)
	{
		return InterfaceUtils.findContainingText(name);
	}
	
	/*
	 * The following code is ripped & modified from FCInGameHopper
	 */
	private static boolean scrollToButton(RSInterface targetChild)
	{
		final long START_TIME = Timing.currentTimeMillis();
		final long TIMEOUT = 7000;
		
		Rectangle targetRectangle;
		
		while(!isQuestVisible(targetChild))
		{
			//move mouse into world list interface if necessary
			if(!QUEST_LIST_SCROLLABLE_AREA.contains(Mouse.getPos()))
				Mouse.moveBox(QUEST_LIST_SCROLLABLE_AREA);
			
			//scroll in appropriate direction
			targetRectangle = targetChild.getAbsoluteBounds();
			Mouse.scroll(targetRectangle.y < MIN_Y);			
			if(Timing.timeFromMark(START_TIME) > TIMEOUT)
				return false;                  
			General.sleep(10, 40);
		}
		
		General.sleep(70, 120);
		return true;
	}
	
	private static boolean isQuestVisible(RSInterface target)
	{	
		Rectangle rect = target.getAbsoluteBounds();
		
		return rect.y > MIN_Y && rect.y < MAX_Y;
	}
}
