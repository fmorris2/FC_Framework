package scripts.fc.api.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tribot.api.Clicking;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;

import scripts.fc.api.quest.QuestJournal;
import scripts.fc.api.wrappers.FCTiming;

public class InterfaceUtils
{
	public static RSInterface findContainingText(String text)
	{
		return findContainingText(text, null);
	}
	
	public static RSInterface findContainingText(String text, Filter<RSInterface> exceptions) {
		RSInterface[] inters = find(new Filter<RSInterface>() {
			@Override
			public boolean accept(RSInterface i) {
				if (exceptions != null && exceptions.accept(i)) {
					return false;
				}

				String interText = i.getText();
				return interText != null && interText.contains(text);
			}
		});

		return inters.length == 0 ? null : inters[0];
	}
	
	public static RSInterface[] find(Filter<RSInterface> filter) 
	{
		return matches(filter, Interfaces.getAll());
	}

	private static RSInterface[] matches(Filter<RSInterface> filter, RSInterface[] interfaces)
	{
		List<RSInterface> matches = new ArrayList<>();

		for (RSInterface i : interfaces)
		{
			if (i != null)
			{
				if (filter.accept(i))
					matches.add(i);

				RSInterface[] children = i.getChildren();

				if (children != null)
					matches.addAll(Arrays.asList(matches(filter, children)));
			}
		}

		return matches.toArray(new RSInterface[matches.size()]);
	}
	
	public static boolean closeQuestInterface()
	{
		if(GameTab.getOpen() == TABS.QUESTS)
			GameTab.open(TABS.INVENTORY);
		
		RSInterface[] closeQuestButton = find(getCloseQuestButton());
		if(closeQuestButton.length > 0)
			return Clicking.click(closeQuestButton[0]) && FCTiming.waitCondition(() -> !isQuestInterfaceUp(), 1800);
		
		return false;
	}
	
	private static Filter<RSInterface> getCloseQuestButton()
	{
		return new Filter<RSInterface>()
		{
			@Override
			public boolean accept(RSInterface i)
			{
				return i.isBeingDrawn() && i.getWidth() == 26 && i.getHeight() == 23;
			}
			
		};
	}
	
	private static boolean isInterfaceSubstantiated(RSInterface i) {
		return Interfaces.isInterfaceValid(i.getIndex()) && i.isBeingDrawn() && !i.isHidden();
	}
	
	public static boolean isQuestInterfaceUp()
	{		
		return Interfaces.get(QuestJournal.JOURNAL_MASTER) != null
				|| (isInterfaceSubstantiated(findContainingText("You are awarded:")) 
						&& isInterfaceSubstantiated(findContainingText("Quest Points:")));
	}
}
