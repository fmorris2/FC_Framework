package scripts.fc.api.inventory;

import java.util.HashMap;
import java.util.HashSet;

import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;

/**
 * 	This class will observe a player's inventory, and notify the inventory
 * 		listener of any changes
 * 
 * 	@author Freddy
 *
 */
public class FCInventoryObserver extends Thread 
{
	private final int CYCLE_TIME = 400; //How long our run() method will sleep for every cycle
	
	//The listeners that have to be notified - No duplicates.
	private HashSet<FCInventoryListener> listeners = new HashSet<>();
	
	//The inventory maps, so we can compare and see if the inventory changed
	private HashMap<Integer, Integer> oldInventory = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> newInventory;	
	public boolean isRunning = true;
	
	public FCInventoryObserver(FCInventoryListener listener)
	{
		fillInventoryMap(oldInventory);
		listeners.add(listener);
	}
	
	public void run()
	{
		try
		{
			while(isRunning)
			{
				newInventory = new HashMap<Integer, Integer>();
				fillInventoryMap(newInventory);
				
				for(int i : newInventory.keySet())
				{
					if(!oldInventory.containsKey(i))
						notifyListenersOfAddition(i, newInventory.get(i));
					else if(newInventory.get(i) > oldInventory.get(i))
						notifyListenersOfAddition(i, newInventory.get(i) - oldInventory.get(i));
				}
				
				for(int x : oldInventory.keySet())
				{
					if(!newInventory.containsKey(x))
						notifyListenersOfRemoval(x, oldInventory.get(x));
					else if(oldInventory.get(x) > newInventory.get(x))
						notifyListenersOfRemoval(x, oldInventory.get(x) - newInventory.get(x));
				}
			
				oldInventory = newInventory;
				sleep(CYCLE_TIME);
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void fillInventoryMap(HashMap<Integer, Integer> map)
	{
		map.clear();
		
		for(RSItem i : Inventory.getAll())
			map.put(i.getID(), Inventory.getCount(i.getID()));	
	}

	public void notifyListenersOfAddition(int id, int amt)
	{
		for(FCInventoryListener listener : listeners)
			listener.inventoryAdded(id, amt);
	}

	public void notifyListenersOfRemoval(int id, int amt)
	{
		for(FCInventoryListener listener : listeners)
			listener.inventoryRemoved(id, amt);
	}
	
	public void addListener(FCInventoryListener listener)
	{
		if(listener != null)
			listeners.add(listener);
		
	}
	
}
