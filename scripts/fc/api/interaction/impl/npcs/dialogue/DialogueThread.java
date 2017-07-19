package scripts.fc.api.interaction.impl.npcs.dialogue;

import java.awt.Rectangle;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.util.abc.ABCProperties;
import org.tribot.api2007.Game;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSNPC;

import scripts.fc.api.abc.PersistantABCUtil;
import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.generic.FCFilters;
import scripts.fc.api.mouse.AccurateMouse;
import scripts.fc.api.utils.ChooseOptionUtils;
import scripts.fc.api.utils.InterfaceUtils;
import scripts.fc.api.utils.PlayerUtils;
import scripts.fc.api.wrappers.FCTiming;
import scripts.fc.framework.data.Vars;

public class DialogueThread extends Thread
{	
	private static final int DIALOGUE_MASTER = 231;
	private static final int PLAYER_DIALOGUE_MASTER = 217;
	private static final int CUTSCENE_SETTING = 1021, CUTSCENE_VALUE = 192;
	private static final int QUEST_REWARD_MASTER = 277, QUEST_REWARD_CLOSE = 15;
	private static final String WAIT_START_VAR = "startCutscene";
	private static final long EST_WAIT_TIME = 3000, CUTSCENE_WAIT_THRESH = 10000;
	
	private int[] options;
	private int optionIndex;
	private long lastCutsceneWait;
	
	private boolean isSuccessful, isRunning = true, ignoreChatName, wentThroughDialogue;
	
	private RSNPC npc;
	private String npcName, action;
	
	public DialogueThread(RSNPC npc, String action, int... options)
	{
		this.npc = npc;
		this.npcName = npc.getName();
		this.action = action;
		this.options = options;
	}
	
	public DialogueThread ignoreChatName(boolean t)
	{
		ignoreChatName = t;
		return this;
	}
	
	public void run()
	{
		log("Thread started");
		
		//first, check if we need to click the npc
		if(needsToClickNpc())
		{
			log("Needs to click npc");
			if(!clickNpc())
			{
				stopThread(false);
				return;
			}
		}
		
		//second, wait for dialogue to start
		if(waitForDialogue())
		{
			log("Dialogue successfully started");
			if(handleDialogue())
			{
				log("Dialogue successfully handled");
				stopThread(true);
			}
		}
		
		//dialogue failed to start
		stopThread(false);
	}
	
	private boolean handleDialogue()
	{
		//while any of the dialogue screens are up and we're in game
		while((areDialogueInterfacesUp() || areCutsceneInterfacesUp() || isInCutscene()) && Login.getLoginState() == STATE.INGAME)
		{
			wentThroughDialogue = true;
			handleAbc2Reaction();
			
			//check for option selection first
			String[] dialogueOptions = NPCChat.getOptions();
			if(dialogueOptions != null && dialogueOptions.length > 0)
			{
				//if we don't know how to handle the option that is currently up
				//either we've ran out of supplied options, or the option interface doesn't contain the option that was supplied
				if(optionIndex >= options.length || dialogueOptions.length <= options[optionIndex])
					return false;
				
				//send the appropriate option
				General.println("Sending option: " + (options[optionIndex] + 1));
				Keyboard.sendType(Integer.toString(options[optionIndex] + 1).charAt(0));
				sleep(600, 1200);
				optionIndex++;
			}
			else if(areDialogueInterfacesUp()) //click continue interface
				doClickToContinue();
			else if(isInCutscene())
			{
				log("In cutscene...");	
				startAbc2Timing();
				
				//for quest rewards when you're in a cutscene
				RSInterface[] inter = InterfaceUtils.find(FCFilters.containsAction("Close"));
				RSInterface questReward = Interfaces.get(QUEST_REWARD_MASTER, QUEST_REWARD_CLOSE);
				if(inter.length > 0)
					Clicking.click(inter[0]);
				else if(questReward != null)
					Clicking.click(questReward);
				
				sleep(600, 1200);
			}
			
			FCTiming.waitCondition(() -> areDialogueInterfacesUp() || areCutsceneInterfacesUp() || isInCutscene(), General.random(600, 1200));
		}
		
		//assume we've gone through the dialogue successfully
		return true;
	}
	
	private void startAbc2Timing()
	{
		PersistantABCUtil abc2 = Vars.get().get("abc2");
		if(Timing.timeFromMark(lastCutsceneWait) < CUTSCENE_WAIT_THRESH)
			return;
		
		if(Vars.get().get(WAIT_START_VAR, new Long(-1)) == -1)
		{
			General.println("Started waiting for cutscene...");
			Vars.get().addOrUpdate(WAIT_START_VAR, Timing.currentTimeMillis());
			abc2.generateTrackers(EST_WAIT_TIME);
		}
		else //we've already began the process of waiting...
			abc2.performTimedActions();
	}
	
	private void handleAbc2Reaction()
	{
		PersistantABCUtil abc2 = Vars.get().get("abc2");
		long cutsceneStartTime = Vars.get().get(WAIT_START_VAR, new Long(-1));
		if(cutsceneStartTime != -1) //we need to wait a reaction time
		{
			ABCProperties props = Vars.get().get("abc2Props");
			
			props.setWaitingTime(((Long)(Timing.timeFromMark(cutsceneStartTime))).intValue());
			props.setWaitingFixed(true);
			
			abc2.generateAndPerformReaction(props);
			Vars.get().addOrUpdate(WAIT_START_VAR, new Long(-1));
			lastCutsceneWait = Timing.currentTimeMillis();
		}
	}
	
	public static void doClickToContinue()
	{
		//first, check for abnormal click to continue interface
		RSInterface abnormalClickToContinue = InterfaceUtils.findContainingText("Click to continue");
		
		if(abnormalClickToContinue != null && !abnormalClickToContinue.isHidden())
		{
			log("Abnormal click continue interface up");
			if(Clicking.click(abnormalClickToContinue)) //click abnormal interface and wait up to 2 gameticks for it to disappear
				FCTiming.waitCondition(() -> InterfaceUtils.findContainingText("Click to continue") == null, 1200);
		}
		else //normal click to continue interface is up
		{
			log("Holding spacebar...");
			Keyboard.holdKey(' ', Keyboard.getKeyCode(' '), FCConditions.SPACEBAR_HOLD);
			log("Releasing spacebar...");
		}
	}
	
	public static boolean isInCutscene()
	{
		RSInterface continueInter = InterfaceUtils.findContainingText("to continue");
		
		return Game.getSetting(CUTSCENE_SETTING) == CUTSCENE_VALUE
					|| (continueInter == null && NPCChat.getSelectOptionInterface() == null
							&& (NPCChat.getMessage() != null || NPCChat.getName() != null));
	}
	
	private boolean needsToClickNpc()
	{
		//first, check if the choose option menu is up for this NPC
		Rectangle menuOption = ChooseOptionUtils.getArea(action, npc);
		if(menuOption != null)
		{
			Mouse.clickBox(menuOption, 1);
			return false;
		}
		
		//second, check if we're already walking toward the NPC due to a previous interaction
		if(isInteractingWithNpc())
			return false;
		
		//lastly, check if we're already in dialogue with the NPC
		if(isInDialogueWithNpc())
			return false;
		
		//all of our other checks failed, so it is safe to assume we need to click the NPC
		return true;
	}
	
	private boolean clickNpc()
	{
		log("Attempting to click npc");
		if(AccurateMouse.click(npc, action))
			return FCTiming.waitCondition(() -> isInteractingWithNpc(), 1200); //wait up to 2 game ticks for interacting val to be set
			
		return false;
	}
	
	private boolean waitForDialogue()
	{
		//while our character is moving toward the appropriate NPC, we must wait
		while(isInteractingWithNpc() && Login.getLoginState() == STATE.INGAME)
		{
			log("Waiting for dialogue to start....");
			sleep(20, 30);
		}
		
		return isInDialogueWithNpc();
	}
	
	public static boolean areDialogueInterfacesUp()
	{
		RSInterface continueInter = InterfaceUtils.findContainingText("to continue");
		
		return (continueInter != null && !continueInter.isHidden())
				|| NPCChat.getName() != null
				|| NPCChat.getSelectOptionInterface() != null;
	}
	
	private boolean areCutsceneInterfacesUp()
	{
		RSInterface dialogueMaster = Interfaces.get(DIALOGUE_MASTER);
		RSInterface playerDialogueMaster = Interfaces.get(PLAYER_DIALOGUE_MASTER);
		
		return (dialogueMaster != null && !dialogueMaster.isHidden())
				|| (playerDialogueMaster != null && !playerDialogueMaster.isHidden());
	}
	
	private boolean isInDialogueWithNpc()
	{
		String npcChatName = NPCChat.getName();
		
		return (ignoreChatName && npcChatName != null) 
					|| npcChatName != null && (npcChatName.equals(npcName) || npcChatName.equals(Player.getRSPlayer().getName()));
	}
	
	private boolean isInteractingWithNpc()
	{
		String interactingCharName = PlayerUtils.getInteractingCharacterName();
		return interactingCharName != null && interactingCharName.equals(npcName);
	}
	
	private void stopThread(boolean success)
	{
		log("Finished with success value of " + success);
		isRunning = false;
		isSuccessful = success;
		NpcDialogue.currentExecutingThread = null;
	}
	
	private static void log(String str)
	{
		General.println("[DialogueThread] " + str);
	}
	
	
	/*
	public void run()
	{
		General.println("Running dialogueThread");
		String chatName = NPCChat.getName();
		
		if((chatName == null || npc == null || !chatName.equals(npc.getName())) 
				&& !DynamicClicking.clickRSNPC(npc, action) || !Timing.waitCondition(FCConditions.IN_DIALOGUE_CONDITION, 4000))
		{
			General.println("dialogueThread not successful");
			isSuccessful = false;
			isRunning = false;
			NpcDialogue.currentExecutingThread = null;
			return;
		}
		
		General.println("dialogueThread: in dialogue...");
		
		handleDialogue();
		
		General.println("Out of loop");
		
		isRunning = false;
		isSuccessful = success && NPCChat.getClickContinueInterface() == null && NPCChat.getSelectOptionInterface() == null;
		
		if(!isSuccessful)
			checkForOtherClickContinue();
		
		NpcDialogue.currentExecutingThread = null;
	}
	*/
	
	private void sleep(int min, int max)
	{
		try
		{
			sleep(General.random(min, max));
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean isSuccessful()
	{
		return isSuccessful;
	}
	
	public boolean wentThroughDialogue()
	{
		return wentThroughDialogue;
	}
	
	public boolean isRunning()
	{
		return isRunning;
	}
}
