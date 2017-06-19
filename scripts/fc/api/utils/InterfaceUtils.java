package scripts.fc.api.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;

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
}
