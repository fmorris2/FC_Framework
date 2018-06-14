package scripts.fc.api.utils;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
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
import org.tribot.api2007.types.RSVarBit;
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
		/*
		final File WORKING_DIR = Util.getWorkingDirectory();
		final File HOME_DIR = Util.getHomeDirectory();
		final File APP_DATA_DIR = Util.getAppDataDirectory();
		
		General.println("WORKING DIR: " + WORKING_DIR);
		General.println("HOME DIR: " + HOME_DIR);
		General.println("APP_DATA_DIR: " + APP_DATA_DIR);
		
		if(WORKING_DIR.toString().contains("tribot")) {
			General.println("USING WORKING DIR");
			return WORKING_DIR.getAbsolutePath();
		}
		
		if(APP_DATA_DIR.toString().contains("tribot")) {
			General.println("USING APP DATA DIR");
			return APP_DATA_DIR.getAbsolutePath();
		}
		
		if(Arrays.stream(HOME_DIR.list()).noneMatch(n -> n.contains("tribot"))) {
			General.println("USING HOME DIR");
			return HOME_DIR.getAbsolutePath() + "\\AppData\\.tribot";
		}
		
		return HOME_DIR.getAbsolutePath() + "\\.tribot";
		*/
		return Util.getWorkingDirectory().getAbsolutePath();
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
					final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();		
					gui.setLocation((int)(screenSize.getWidth() / 2), (int)(screenSize.getHeight() / 2));
					
					//Make GUI visible
					gui.setVisible(true);
				} 
				catch (final Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	public static Image loadImage(final String url)
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
                catch (final Throwable e) 
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
	
	public static boolean isInBuilding(final Positionable p)
	{
		final RSTile t = p.getPosition().toLocalTile();
		return BankHelper.isInBuilding(t, Game.getSceneFlags());
	}
	
	public static RSMenuNode getOption(final String str)
	{
		for(final RSMenuNode node : ChooseOption.getMenuNodes())
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
		final RSInterface inter = Interfaces.get(90, 26);
		if (inter != null)
		{
			final String text = inter.getText();
			
			return text == null || text.length() < 8 ? 0 : Integer.parseInt(text.substring(7));
		} else
		{
			return 0;
		}
	}

	public static boolean isMember()
	{
		for(final int w : FREE_WORLDS)
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
		final Object[][] audioSettings = {{168, "Adjust Music Volume"}, {169, "Adjust Sound Effect Volume"}, {872, "Adjust Area Sound Effect Volume"}};
		
		final Object[][] needToChange = Arrays.stream(audioSettings)
			.filter(o -> Game.getSetting((int)o[0]) < 4)
			.toArray(Object[][]::new);
		
		if(needToChange.length == 0 || !GameTab.open(TABS.OPTIONS))
			return;
				
		final RSInterface[] soundButton = InterfaceUtils.find(FCFilters.containsAction("Audio"));
		if(soundButton.length == 0 || !Clicking.click(soundButton[0]))
			return;
		
		General.sleep(600, 1200);
		
		for(final Object[] o : needToChange)
		{
			final RSInterface[] button = InterfaceUtils.find(FCFilters.containsAction((String)o[1]));
			if(button.length > 0 && Clicking.click(button[0]))
				continue;
		}
	}
	
	public static void removeRoofs()
	{
		General.println("Remove roofs");
		if(!GameTab.open(TABS.OPTIONS))
			return;
		
		final RSInterface advancedOptions = Interfaces.get(OPTIONS_TAB_MASTER, ADVANCED_CHILD);
		if(advancedOptions != null && Clicking.click(advancedOptions) && Timing.waitCondition(FCConditions.interfaceUp(SELECTION_MASTER), 3000))
		{
			final RSInterface master = Interfaces.get(SELECTION_MASTER);
			final RSInterface removeRoofs = master == null ? null : master.getChild(REMOVE_ROOFS_CHILD);
			
			if(removeRoofs != null)
			{
				final RSInterface exitButton = master.getChild(EXIT_CHILD);
				final RSInterface exitComp = exitButton == null ? null : exitButton.getChild(EXIT_COMP);
				
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
		final java.util.Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		final Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		return Arrays.stream(threadArray)
				.filter(t -> t != null && t.getName().contains("Script"))
				.findFirst().orElse(null);
	}
	
	public static Thread getMouseMovementThread()
	{
		final java.util.Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		final Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		return Arrays.stream(threadArray)
				.filter(t -> t != null && t.getName().contains("Mouse Movement"))
				.findFirst().orElse(null);
	}
	
	public static int getVarBit(final int index) {
		final RSVarBit varBit = RSVarBit.get(index);
		return varBit == null ? -1 : varBit.getValue();
	}
	
	private static boolean isRemoveRoofsOn(final RSInterface removeButton)
	{
		return removeButton.getTextureID() == REMOVE_BUTTON_TEXTURE;
	}
}
