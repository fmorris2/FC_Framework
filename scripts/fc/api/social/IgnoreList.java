package scripts.fc.api.social;

import java.util.ArrayList;
import java.util.List;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;

public class IgnoreList 
{
	private final static int MASTER = 432;
	private final static int NAMES_CHILD = 7;
	private final static int ADD_NAME_CHILD = 10;
	
	private final static int INPUT_MASTER = 162;	
	private final static int INPUT_TEXT_CHILD = 32;
	
	public static boolean isOpen()
	{
		return GameTab.getOpen() == TABS.IGNORE;
	}
	
	public static boolean openTab()
	{
		return GameTab.open(TABS.IGNORE);
	}
	
	public static List<String> getNames()
	{
		List<String> names = new ArrayList<String>();
		
		RSInterfaceChild namesChild = Interfaces.get(MASTER, NAMES_CHILD);
		
		if(namesChild != null && namesChild.getChildren() != null)
		{	
			for(RSInterfaceComponent component : namesChild.getChildren())
			{
				if(component.getText() != null)
				{
					names.add(component.getText());
				}
			}
		}
		
		return names;
	}
	
	public static boolean addName(String name)
	{
		openTab();
		
		RSInterfaceChild addNameChild = Interfaces.get(MASTER, ADD_NAME_CHILD);
		
		if(addNameChild != null && Clicking.click(addNameChild))
		{
			General.sleep(20, 40);
			
			RSInterfaceChild inputTextChild = Interfaces.get(INPUT_MASTER, INPUT_TEXT_CHILD);
			
			if(inputTextChild != null && inputTextChild.getText() != null 
					&& inputTextChild.getText().contains("Enter name"))
			{
				Keyboard.typeSend(name);
				
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean removeName(String name)
	{
		openTab();
		
		int index = indexOf(name);
		
		if(index != -1)
		{
			RSInterfaceComponent nameComponent = Interfaces.get(MASTER, NAMES_CHILD).getChild(index);
			
			return Clicking.click("Delete", nameComponent);
		}
		
		return false;
	}
	
	public static boolean removeAll()
	{
		for(String name : getNames())
		{
			removeName(name);
		}
		
		return getNames().size() == 0;
	}
	
	public static boolean contains(String name)
	{
		for(String str : getNames())
		{
			if(str.equalsIgnoreCase(name))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private static int indexOf(String name)
	{
		List<String> names = getNames();
		
		for(int i = 0; i < names.size(); i++)
		{
			if(names.get(i).equalsIgnoreCase(name))
			{
				return i;
			}
		}
		
		return -1;
	}
	
}
