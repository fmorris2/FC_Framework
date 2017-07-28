package scripts.fc.api.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tribot.api.General;

public class PriceUtils
{
	private static final Map<Integer, Integer> PRICE_CACHE = new HashMap<>();
	
	public static int getPrice(final int itemId) 
	{
		if(PRICE_CACHE.containsKey(itemId))
			return PRICE_CACHE.get(itemId);
		
		General.println("[Pricing] Getting official GE price for item id " + itemId);
	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item=" + itemId).openStream()))) {
	        Matcher matcher = Pattern.compile(".*\"price\":\"?(\\d+\\,?\\.?\\d*)([k|m]?)\"?},\"today\".*").matcher(reader.readLine());
	        if (matcher.matches()) 
	        {
	            double price = Double.parseDouble(matcher.group(1).replace(",",""));
	            String suffix = matcher.group(2);
	            int parsed = (int) (suffix.isEmpty() ? price : price * (suffix.charAt(0) == 'k' ? 1000 : 1000000));
	            PRICE_CACHE.put(itemId, parsed);
	            return parsed;
	        }
	    } 
	    catch (IOException e) 
	    {
	        e.printStackTrace();
	    }
		
		return -1;
    }
	
	public static String getCondensedNumber(long amt) 
	{
		double convertedAmount;
		String formattedAmount;
		String tag;
		DecimalFormat decimalFormat = new DecimalFormat(".##");
		
		//IF(gold amount is under 1k)
		if(amt < 1000)
		{
			//set convertedAmount to amt
			convertedAmount = amt;
			
			//set tag to ""
			tag = "";
		}
		//ELSE IF(gold amount is under 1m)
		else if(amt < 1000000)
		{
			//set converted amount to (amt / 1000.0)
			convertedAmount = (amt / 1000.0);
			
			//set tag to "k"
			tag = "k";
		}
		else //(gold amount is 1m or over)
		{
			//set convertedAmount to (amt / 1000000.0)
			convertedAmount = (amt / 1000000.0);
			
			//set tag to "m"
			tag = "m";
			
		} //END IF
		
		//IF(gold amount is under 1k)
		if(amt < 1000)
		{
			//set formattedAmount to amt
			formattedAmount = ""+Math.round(amt);
			
		}
		else //(gold amount is 1k or over)	
		{
			//format convertedAmount so that it only goes to hundredths place
			formattedAmount = decimalFormat.format(convertedAmount);
			
		} //END IF
		
		//return formattedAmount + tag
		return formattedAmount + tag;
		
	} //END getCondensedNumber(long amt)
}
