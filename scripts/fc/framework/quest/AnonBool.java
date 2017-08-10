package scripts.fc.framework.quest;

import java.util.function.BooleanSupplier;

public class AnonBool extends QuestBool
{
	public final BooleanSupplier SUPPLIER;
	
	public AnonBool(BooleanSupplier supp, boolean normal)
	{
		super(normal);
		SUPPLIER = supp;
	}

	@Override
	public boolean value()
	{
		return SUPPLIER.getAsBoolean();
	}
	
}
