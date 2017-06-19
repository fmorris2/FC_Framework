package scripts.fc.api.interaction.impl.npcs.dialogue;

import org.tribot.api.General;

import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.interaction.NpcInteraction;
import scripts.fc.api.utils.NpcUtils;
import scripts.fc.api.utils.PlayerUtils;
import scripts.fc.api.wrappers.FCTiming;

public class NpcDialogue extends NpcInteraction
{	
	private static final long SLEEP_TIME = 60;
	protected static Thread currentExecutingThread = null;
	
	private DialogueThread dialogueThread;
	private boolean waitMainThread = true;
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
		dialogueThread = new DialogueThread(npc, action, options);
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
	
	private boolean isInteractingWithCorrectNpc()
	{
		//if name was used for this interaction, check that
		if(name != null)
		{
			String interactingName = PlayerUtils.getInteractingCharacterName();
			return interactingName != null && name.equals(interactingName);
		}
		
		//if id was used, check that
		int interactingId = NpcUtils.getInteractingNpcId();
		return id == interactingId;
	}
}
