package scripts.fc.framework.goal;

import java.io.Serializable;

public interface Goal extends Serializable
{		
	public abstract String getName();
	public abstract boolean hasReached();
	public abstract String getCompletionMessage();
}
