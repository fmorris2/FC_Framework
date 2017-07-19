package scripts.fc.api.interaction.impl.npcs.dialogue;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api2007.types.RSInterface;

import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.interaction.NpcInteraction;
import scripts.fc.api.utils.InterfaceUtils;
import scripts.fc.api.wrappers.FCTiming;

public class NpcDialogue extends NpcInteraction
{	
	private static final long SLEEP_TIME = 60;
	protected static Thread currentExecutingThread = null;
	
	private DialogueThread dialogueThread;
	private boolean waitMainThread = true, ignoreChatName;
	private int[] options;
	
	public NpcDialogue(String action, String name, int searchDistance, int... options)
	{
		super(action, name, searchDistance);
		this.options = options;
	}
	
	public NpcDialogue(String action, String name, int searchDistance, boolean waitMainThread, int... options)
	{
		this(action, name, searchDistance, options);
		this.waitMainThread = waitMainThread;
	}

	@Override
	protected boolean interact()
	{	
		if(currentExecutingThread != null) //Avoid multiple threads from being started when one is still executing
			return false;
		
		if(npc == null) //Can't interact with a null NPC
			return false;
		
		General.println("Starting dialogueThread...");
		
		//check for abnormal click to continue interface
		RSInterface abnormalClickToContinue = InterfaceUtils.findContainingText("Click to continue");
		if(abnormalClickToContinue != null && !abnormalClickToContinue.isHidden())
			Clicking.click(abnormalClickToContinue);
		
		dialogueThread = new DialogueThread(npc, action, options).ignoreChatName(ignoreChatName);
		dialogueThread.start();
		currentExecutingThread = dialogueThread;
		
		//wait for dialogue thread to finish if necessary
		if(waitMainThread)
		{
			while(dialogueThread.isRunning())
				General.sleep(SLEEP_TIME);
			
			return dialogueThread.isSuccessful();
		}
		
		return FCTiming.waitCondition(() -> FCConditions.IN_DIALOGUE_CONDITION.active() || dialogueThread.isSuccessful(), 4000);
	}
	
	public void setIgnoreChatName(boolean b)
	{
		ignoreChatName = b;
	}
	
	public boolean wentThroughDialogue()
	{
		return dialogueThread.wentThroughDialogue();
	}
}
