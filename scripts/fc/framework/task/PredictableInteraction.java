package scripts.fc.framework.task;

import java.io.Serializable;

import scripts.fc.api.interaction.EntityInteraction;

/**
 * Represents an interaction with an entity that can be
 * prepared for. Example use case: Talking with an NPC
 * 
 * @author Final Calibur
 *
 */
public interface PredictableInteraction extends Serializable
{
	/**
	 * Returns EntityInteraction that we can prepare
	 * the interaction with
	
	 * @return the EntityInteraction that can be prepared
	 */
	public EntityInteraction getInteractable();
}
