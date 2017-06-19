package scripts.fc.framework.paint;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.util.Screenshots;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.script.interfaces.EventBlockingOverride.OVERRIDE_RETURN;

import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.script.FCMissionScript;

public abstract class FCDetailedPaint extends FCPaint
{
	private final int PERIOD_UPDATE_TIME = 1000;
	private final int MAX_STATUS_PERIODS = 3; //The max number of status periods that will be displayed
	private final int PROGRESS_MAX_WIDTH = 512; //The maximum width of the progress bar
	private final int PROGRESS_MAX_HEIGHT = 11;	//The maximum height of the progress bar
	private final Font PAINT_FONT = new Font("Open Sans", Font.PLAIN, 10); //The text font we will use on our paint
	private final Font VERSION_AND_STATUS_FONT = new Font("Open Sans", Font.PLAIN, 9); //The text font we will use for the version and status fields on our paint
	private final int PROGRESS_START_X = 4; //The start x of the progress bar
	private final int PROGRESS_START_Y = 328; //The start y of the progress bar
	private final int COLUMN_ONE_X = 16; //The x position of all paint fields in column one
	private final int COLUMN_SEPARATION = 200; //The number of pixels between columns
	private final int ROW_ONE_Y = 390; //The y position of all the paint fields
	private final int ROW_SEPARATION = 16; //The number of pixels between rows
	private final long ONE_HOUR_MS = 3600000; //How long an hour is in ms
	private final String[] TOOLS = {"Take screenshot", "Toggle paint", "Toggle GUI", "Toggle XP progress", "Reset statistics"};
	private final RenderingHints RENDERING_HINTS = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	private final Rectangle PAINT_RECTANGLE = new Rectangle(0, 338, 519, 142);
	
	//PROGRESS BAR COLORS
	private final Color PROGRESS_COLOR = new Color(20,20,20,127);
	private final Color PROGRESS_BORDER = new Color(50, 42, 26, 255);
	private final Color PROGRESS_SHADOW = new Color(255, 255, 255, 26);
	
	private SKILLS[] skills;
	private int[] startingXp;
	private Color rectColor = getRectColor();
	private Color hoverColor = getHoverColor();
	private Rectangle currentHover;
	private Image paintImage;
	private long lastPeriodUpdate;
	private int periods;
	private String periodString;
	private boolean isVisible = true;
	private boolean progressVisible = true;
	private boolean addedTools;
	public JFrame gui;
	private List<Rectangle> tools = new ArrayList<Rectangle>();

	public FCDetailedPaint(FCPaintable paintable, JFrame gui, Color color, SKILLS... skills)
	{
		super(paintable, color);
		this.gui = gui;
		this.skills = skills;
		this.startingXp = new int[skills == null ? 0 : skills.length];
		setStartingXp();
		loadImage();
	}
	
	//general paint colors
	public abstract Color getRectColor();
	public abstract Color getHoverColor();
	
	//image
	public abstract String getImageUrl();
	
	//reset statistics
	public abstract void resetStatistics();
		
	public void paint(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;	
		g2.setRenderingHints(RENDERING_HINTS);
		
		if(isVisible)
		{
			if(paintImage != null)
				g2.drawImage(paintImage, 0, 338, null);
			
			drawStatusBar(g2);
			drawScriptPaint(g2);
		}
		
		g2.setColor(color);
		g2.setFont(PAINT_FONT);
		
		drawTools(g2);	
		drawProgressBars(g2);
	}
	
	private void drawStatusBar(Graphics2D g2)
	{
		g2.setColor(color);
		g2.setFont(VERSION_AND_STATUS_FONT);
		FCMissionScript missionScript = (FCMissionScript)script;
		Mission m = missionScript.getCurrentMission();
		String missionName = missionScript.compilingPreReqs ? "Mission Prerequisites" : m == null ? "null" : m.getMissionName();
		String taskName = missionScript.compilingPreReqs ? "Checking" : m == null ? "null" : m.getCurrentTaskName();
		if(m != null)
			g2.drawString("[Current Mission]: " + missionName + " [Current Task]: " + taskName + getPeriods(), 9, 472);
		
		g2.drawString("v"+script.MANIFEST.version(), 490, 472);
	}
	
	private void loadImage()
	{
		SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                try 
                {
                	paintImage = ImageIO.read(new URL(getImageUrl()));
					this.finalize();
				} catch (Throwable e) 
                {
					e.printStackTrace();
				}
            }
        });
	}
	
	//This method handles the basic drawing of the progress bar
	private void drawProgressBars(Graphics2D paintGraphics)
	{
		if(!progressVisible || skills == null)
			return;
		
		for(int i = 0; i < skills.length; i++)
		{
			//red of progress bar
			paintGraphics.setColor(PROGRESS_COLOR);
			paintGraphics.fillRect(getProgressBarWidth(skills[i]) + PROGRESS_START_X, PROGRESS_START_Y - (i * PROGRESS_MAX_HEIGHT), PROGRESS_MAX_WIDTH - getProgressBarWidth(skills[i]), PROGRESS_MAX_HEIGHT);
			
			//current progress
			paintGraphics.setColor(getSkillColor(skills[i]));
			paintGraphics.fillRect(PROGRESS_START_X, PROGRESS_START_Y - (i * PROGRESS_MAX_HEIGHT), getProgressBarWidth(skills[i]), PROGRESS_MAX_HEIGHT);
			
			//progress bar border line
			paintGraphics.setColor(PROGRESS_BORDER);
			paintGraphics.drawLine(PROGRESS_START_X, PROGRESS_START_Y - (i * PROGRESS_MAX_HEIGHT), (PROGRESS_START_X + PROGRESS_MAX_WIDTH - 1), PROGRESS_START_Y - (i * PROGRESS_MAX_HEIGHT));
			
			//progress bar shadow
			paintGraphics.setColor(PROGRESS_SHADOW);
			paintGraphics.drawLine(PROGRESS_START_X, PROGRESS_START_Y - (i * PROGRESS_MAX_HEIGHT) + 1, (PROGRESS_START_X + PROGRESS_MAX_WIDTH - 1), PROGRESS_START_Y - (i * PROGRESS_MAX_HEIGHT) + 1);
			
			//set font color
			paintGraphics.setColor(Color.WHITE);
			
			//current percent text
			String timeToNextLevel = "Time to next " + skills[i].name().toLowerCase() + " level: " + (getGainedXp(skills[i]) <= 0 ? "Undetermined" : Timing.msToString(timeToLevel(skills[i])));
			String percentToNextLevel = "(" + Skills.getPercentToNextLevel(skills[i]) + "% to " + (Skills.getActualLevel(skills[i]) + 1) + ")";
			String progressBarString = timeToNextLevel + " " + percentToNextLevel;
			
			paintGraphics.drawString(progressBarString, (PROGRESS_MAX_WIDTH / 2) - (paintGraphics.getFontMetrics().stringWidth(progressBarString) / 2), 
					326 + PROGRESS_MAX_HEIGHT - (i * PROGRESS_MAX_HEIGHT));
		}
	}
	
	public int getProgressBarWidth(SKILLS skill)
	{
		double percent = Skills.getPercentToNextLevel(skill);
		
		return (int)Math.floor(PROGRESS_MAX_WIDTH * (percent / 100));
	}
	
	private void setStartingXp()
	{
		if(skills == null)
			return;
		
		for(int i = 0; i < skills.length; i++)
			startingXp[i] = Skills.getXP(skills[i]);
	}
	
	private int getGainedXp(SKILLS skill)
	{
		for(int i = 0; i < skills.length; i++)
			if(skills[i] == skill)
				return Skills.getXP(skills[i]) - startingXp[i];
		
		return 0;
	}
	private String getPeriods()
	{	
		if(Timing.timeFromMark(lastPeriodUpdate) < PERIOD_UPDATE_TIME)
			return periodString;
		
		lastPeriodUpdate = Timing.currentTimeMillis();
		
		periodString = "";
		
		for(int i = 0; i < periods; i++)
			periodString += ".";
		
		periods = periods > MAX_STATUS_PERIODS ? 0 : periods++;
		
		return periodString;
	}
	
	private void drawScriptPaint(Graphics2D g2)
	{
		g2.setColor(color);
		g2.setFont(PAINT_FONT);
		
		String[] info = paintable.getPaintInfo();
		
		for(int i = 0; i < info.length; i++)
		{
			if(i < 5) //COLUMN 1
				drawStringWithRect(info[i], COLUMN_ONE_X, ROW_ONE_Y + (i * ROW_SEPARATION), g2, rectColor,
						color, hoverColor, currentHover);
			else if(i < 10) //COLUMN 2
				drawStringWithRect(info[i], COLUMN_ONE_X + COLUMN_SEPARATION, ROW_ONE_Y + ((i - 5) * ROW_SEPARATION),
						g2, rectColor, color, hoverColor, currentHover);
		}
	}
	
	private void drawTools(Graphics2D g2)
	{
		for(int i = 0; i < TOOLS.length; i++)
		{
			if(!isVisible && i != 1)
				continue;
		
			if(addedTools)
				drawStringWithRect(TOOLS[i], COLUMN_ONE_X + COLUMN_SEPARATION * 2, ROW_ONE_Y + (i * ROW_SEPARATION), g2, rectColor,
						color, hoverColor, currentHover);
			else
				tools.add(drawStringWithRect(TOOLS[i], COLUMN_ONE_X + COLUMN_SEPARATION * 2, ROW_ONE_Y + (i * ROW_SEPARATION), g2, rectColor,
						color, hoverColor, currentHover));
		}
		
		addedTools = true;
	}
	
	public static Rectangle drawStringWithRect(String str, int x, int y, Graphics2D paintGraphics, 
			Color RECTANGLE_COLOR, Color TEXT_COLOR, Color hoverColor, Rectangle hoveringOn)
	{
		try 
		{
			FontMetrics fm = paintGraphics.getFontMetrics();
			Color rectColor = new Color(RECTANGLE_COLOR.getRed(), RECTANGLE_COLOR.getGreen(), RECTANGLE_COLOR.getBlue(), RECTANGLE_COLOR.getAlpha());
			
			paintGraphics.setColor(rectColor);
			Rectangle rect = new Rectangle(x - 2, (y - fm.getHeight() + 4), fm.stringWidth(str) + 4, fm.getHeight());
			
			if(rect.equals(hoveringOn)) 
				paintGraphics.setColor(hoverColor);
			
			paintGraphics.fill(rect);
			
			paintGraphics.setColor(TEXT_COLOR);
			
			paintGraphics.drawString(str, x, y);
			
			return rect;	
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;	
	}

	private long timeToLevel(SKILLS skill) 
	{	
		double xpLeft = Skills.getXPToNextLevel(skill);
		double hours = xpLeft / getPerHour(getGainedXp(skill));
		long hoursInMs = Math.round(ONE_HOUR_MS * hours);
		
		return hoursInMs;
	}

	public OVERRIDE_RETURN handleMouseEvent(MouseEvent e)
	{
		if(!PAINT_RECTANGLE.contains(e.getPoint()))
			return OVERRIDE_RETURN.PROCESS;
		
		for(int i = 0; i < tools.size(); i++)
		{
			if(!isVisible && i != 1)
				continue;
			
			if(tools.get(i).contains(e.getPoint()))
			{
				if(e.getID() == MouseEvent.MOUSE_PRESSED)
				{
					handleTool(i);
					return OVERRIDE_RETURN.DISMISS;
				}
				else if(e.getID() == MouseEvent.MOUSE_MOVED)
				{
					currentHover = tools.get(i);
					return OVERRIDE_RETURN.PROCESS;
				}
			}
		}
		
		currentHover = null;
			
		return OVERRIDE_RETURN.PROCESS;
	}
	
	private void handleTool(int i)
	{
		switch(i)
		{
			case 0: //TAKE SCREENSHOT
				if(Screenshots.take())
					General.println("Screenshot successfully taken! Saved in your .tribot folder.");
			break;
			case 1: //TOGGLE PAINT
				isVisible = !isVisible;
			break;
			case 2: //TOGGLE GUI
				if(gui != null)
					gui.setVisible(!gui.isVisible());
			break;
			case 3: //TOGGLE XP PROGRESS
				progressVisible = !progressVisible;
			break;
			case 4: //RESET STATISTICS
				resetStatistics();
			break;
		}
	}

	public static Color getSkillColor(SKILLS skill)
	{
		final int OPACITY = 160;
		
		switch(skill)
		{
		case ATTACK:
			return new Color(217, 0, 36, OPACITY);
		case STRENGTH:
			return new Color(53, 245, 47, OPACITY);
		case DEFENCE:
			return new Color(130, 201, 224, OPACITY);
		case RANGED:
			return new Color(63, 194, 58, OPACITY);
		case PRAYER:
		case MAGIC:
		case RUNECRAFTING:
		case CONSTRUCTION:
		case HITPOINTS:
			return new Color(255, 140, 161, OPACITY);
		case AGILITY:
		case HERBLORE:
		case THIEVING:
		case CRAFTING:
		case FLETCHING:
		case SLAYER:
		case HUNTER:
		case MINING:
			return new Color(50,169,214,OPACITY);
		case SMITHING:
		case FISHING:
		case COOKING:
		case FIREMAKING:
		case WOODCUTTING:
		case FARMING:
		}
		
		return new Color(255, 0, 0, OPACITY);
	}
}

