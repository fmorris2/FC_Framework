package scripts.fc.api.utils;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSMenuNode;
import org.tribot.api2007.types.RSTile;
import org.tribot.util.Util;

import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.generic.FCFilters;
import scripts.webwalker_logic.shared.helpers.BankHelper;

public class Utils
{
	private final static int[] FREE_WORLDS = { 301, 308, 316, 326, 335, 381,
		382, 383, 384, 393, 394 }; // Free worlds
	
	private static final int OPTIONS_TAB_MASTER = 261;
	private static final int ADVANCED_CHILD = 21;
	private static final int SELECTION_MASTER = 60;
	private static final int REMOVE_ROOFS_CHILD = 8;
	private static final int EXIT_CHILD = 2;
	private static final int EXIT_COMP = 11;
	private static final int REMOVE_BUTTON_TEXTURE = 762;
	
	private static Image paintImage;
	
	public static String getTribotDir() {
		final File WORKING_DIR = Util.getWorkingDirectory();
		final File HOME_DIR = Util.getHomeDirectory();
		final File APP_DATA_DIR = Util.getAppDataDirectory();
		
		if(WORKING_DIR.getName().equals(".tribot")) {
			return WORKING_DIR.getAbsolutePath();
		}
		
		if(APP_DATA_DIR.getName().equals(".tribot")) {
			return APP_DATA_DIR.getAbsolutePath();
		}
		
		if(Arrays.stream(HOME_DIR.list()).noneMatch(n -> n.equals(".tribot"))) {
			return HOME_DIR.getAbsolutePath() + "\\AppData\\.tribot";
		}
		
		return HOME_DIR.getAbsolutePath() + "\\.tribot";
	}
	
	public static void handleGui(final JFrame gui)
	{	
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{	
					gui.setLocationRelativeTo(null);
					gui.setAlwaysOnTop(true);
					
					//Position GUI in center of screen
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();		
					gui.setLocation((int)(screenSize.getWidth() / 2), (int)(screenSize.getHeight() / 2));
					
					//Make GUI visible
					gui.setVisible(true);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	public static Image loadImage(String url)
	{	
		paintImage = null;
		
		new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try 
                {
                	paintImage = ImageIO.read(new URL(url));
					this.finalize();
				} 
                catch (Throwable e) 
                {
					e.printStackTrace();
				}
            }
        }).run();
		
		return paintImage;
	}
	
	public static boolean isPlayerMember()
	{
		return WorldHopper.isMembers(WorldHopper.getWorld());
	}
	
	public static boolean isInBuilding(Positionable p)
	{
		RSTile t = p.getPosition().toLocalTile();
		return BankHelper.isInBuilding(t, Game.getSceneFlags());
	}
	
	public static RSMenuNode getOption(String str)
	{
		for(RSMenuNode node : ChooseOption.getMenuNodes())
		{
			if(node.getAction().equalsIgnoreCase(str))
			{
				return node;
			}
		}
		
		return null;
	}
	
	public static int getWildyLevel()
	{
		RSInterface inter = Interfaces.get(90, 26);
		if (inter != null)
		{
			String text = inter.getText();
			
			return text == null || text.length() < 8 ? 0 : Integer.parseInt(text.substring(7));
		} else
		{
			return 0;
		}
	}

	public static boolean isMember()
	{
		for(int w : FREE_WORLDS)
		{
			if(Game.getCurrentWorld() == w)
			{
				return false;		
			}			
		}
		
		return true;		
	}
	
	public static void turnSoundDown()
	{
		General.println("Turn sound down");
		Object[][] audioSettings = {{168, "Adjust Music Volume"}, {169, "Adjust Sound Effect Volume"}, {872, "Adjust Area Sound Effect Volume"}};
		
		Object[][] needToChange = Arrays.stream(audioSettings)
			.filter(o -> Game.getSetting((int)o[0]) < 4)
			.toArray(Object[][]::new);
		
		if(needToChange.length == 0 || !GameTab.open(TABS.OPTIONS))
			return;
				
		RSInterface[] soundButton = InterfaceUtils.find(FCFilters.containsAction("Audio"));
		if(soundButton.length == 0 || !Clicking.click(soundButton[0]))
			return;
		
		General.sleep(600, 1200);
		
		for(Object[] o : needToChange)
		{
			RSInterface[] button = InterfaceUtils.find(FCFilters.containsAction((String)o[1]));
			if(button.length > 0 && Clicking.click(button[0]))
				continue;
		}
	}
	
	public static void removeRoofs()
	{
		General.println("Remove roofs");
		if(!GameTab.open(TABS.OPTIONS))
			return;
		
		RSInterface advancedOptions = Interfaces.get(OPTIONS_TAB_MASTER, ADVANCED_CHILD);
		if(advancedOptions != null && Clicking.click(advancedOptions) && Timing.waitCondition(FCConditions.interfaceUp(SELECTION_MASTER), 3000))
		{
			RSInterface master = Interfaces.get(SELECTION_MASTER);
			RSInterface removeRoofs = master == null ? null : master.getChild(REMOVE_ROOFS_CHILD);
			
			if(removeRoofs != null)
			{
				RSInterface exitButton = master.getChild(EXIT_CHILD);
				RSInterface exitComp = exitButton == null ? null : exitButton.getChild(EXIT_COMP);
				
				//check if remove roofs is toggled
				if(isRemoveRoofsOn(removeRoofs))
				{
					General.println("Remove roofs already on!");
					if(exitComp != null)
						Clicking.click(exitComp);
					
					return;
				}
				else if(Clicking.click(removeRoofs))
				{
					if(exitComp != null && Clicking.click(exitComp))
						Timing.waitCondition(FCConditions.interfaceNotUp(SELECTION_MASTER), 3000);
				}
			}
		}
	}
	
	public static Thread getMainScriptThread()
	{
		java.util.Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		return Arrays.stream(threadArray)
				.filter(t -> t != null && t.getName().contains("Script"))
				.findFirst().orElse(null);
	}
	
	public static Thread getMouseMovementThread()
	{
		java.util.Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		return Arrays.stream(threadArray)
				.filter(t -> t != null && t.getName().contains("Mouse Movement"))
				.findFirst().orElse(null);
	}
	
	private static boolean isRemoveRoofsOn(RSInterface removeButton)
	{
		return removeButton.getTextureID() == REMOVE_BUTTON_TEXTURE;
	}
}
