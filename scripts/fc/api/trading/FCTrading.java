package scripts.fc.api.trading;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tribot.api.General;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Trading;
import org.tribot.api2007.Trading.WINDOW_STATE;
import org.tribot.api2007.types.RSInterfaceChild;

public class FCTrading 
{
	private final static int TRADING_MASTER = 335;
	private final static int SECOND_WINDOW_MASTER = 334;
	private final static int SECOND_WINDOW_OPPONENT_CHILD = 30;
	private final static int SECOND_WINDOW_STATUS_CHILD = 4;
	private final static int MY_VALUE_CHILD = 24;
	private final static int OTHER_VALUE_CHILD = 27;
	
	private static RSInterfaceChild child;
	
	public static String getTradingWith()
	{
		WINDOW_STATE window = Trading.getWindowState();
		
		if(window == null)
		{
			return "";
		}	
		else if(window.equals(WINDOW_STATE.FIRST_WINDOW))
		{
			child = Interfaces.get(TRADING_MASTER, CHILDREN.TRADING_WITH.getId());
			
			if(child != null && child.getText().length() > 14)
			{
				String name = child.getText().substring(14);
				
				return format(name);
			}
		}
		else if(window.equals(WINDOW_STATE.SECOND_WINDOW))
		{
			child = Interfaces.get(SECOND_WINDOW_MASTER, SECOND_WINDOW_OPPONENT_CHILD);
			
			if(child != null && child.getText().length() > 17)
			{
				String name = child.getText().substring(17);
				
				return format(name);
			}
		}
		
		return "";
	}
	
	public static int getTradeValue(boolean otherPlayer)
	{
		int value = 0;
		
		child = Interfaces.get(TRADING_MASTER, (otherPlayer ? OTHER_VALUE_CHILD : MY_VALUE_CHILD));
		
		if(child != null)
		{
			String patternStr = "Value: (.*?) coins";
			Pattern pattern = Pattern.compile(patternStr);
			
			String text = General.stripFormatting(child.getText()).replace(",", "");
			General.println("Stripped: " + General.stripFormatting(text));
			Matcher m = pattern.matcher(text);
			if(m.find())
				value = Integer.parseInt(m.group(1));
		}
		
		return value;
	}
	
	private static String format(String name)
	{
		String formatted = "";
		
		for(int i = 0; i < name.length(); i++)
		{
			char c = name.charAt(i);
			int numValue = Character.getNumericValue(c);
			
			if(numValue == -1)
				formatted += " ";
			else
				formatted += c;
		}
		
		return formatted;
	}
	
	public static int getOpponentFreeSlots()
	{
		child = Interfaces.get(TRADING_MASTER, CHILDREN.FREE_SLOTS.getId());
		
		if(child != null)
		{
			String[] parts = child.getText().split(" ");
			
			return Integer.parseInt(parts[parts.length - 4]);
		}
		
		return -1;
	}
	
	public static boolean hasAccepted(boolean otherPlayer)
	{
		WINDOW_STATE window = Trading.getWindowState();
		
		String search = otherPlayer ? "Other player has accepted." : "Waiting for other player...";
		
		if(window == null)
		{
			return false;
		}	
		
		child = window.equals(WINDOW_STATE.FIRST_WINDOW) ? Interfaces.get(TRADING_MASTER, CHILDREN.BOTTOM_STATUS_TEXT.getId())
				: Interfaces.get(SECOND_WINDOW_MASTER, SECOND_WINDOW_STATUS_CHILD);
		
		if(child != null)
		{	
			return child.getText().equals(search);
		}
		
		return false;
	}
	
	private enum CHILDREN
	{
		FREE_SLOTS(9),
		ACCEPT(12),
		DECLINE(13),
		YOUR_OFFER(25),
		OPPONENTS_OFFER(28),
		BOTTOM_STATUS_TEXT(30),
		TRADING_WITH(31);
					
		private int id;
		
		CHILDREN(int id)
		{
			this.id = id;
		}
		
		public int getId()
		{
			return id;
		}
	}
}
