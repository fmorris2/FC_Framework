package scripts.fc.api.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tribot.api.Clicking;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;

import scripts.fc.api.wrappers.FCTiming;

public class InterfaceUtils
{
	public static RSInterface findContainingText(String text)
	{
		RSInterface[] inters = find(new Filter<RSInterface>()
		{
			@Override
			public boolean accept(RSInterface i)
			{
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
				return i.getWidth() == 26 && i.getHeight() == 23;
			}
			
		};
	}
	
	public static boolean isQuestInterfaceUp()
	{
		return findContainingText("Congratulations!") != null && findContainingText("Quest Points:") != null;
	}
}
