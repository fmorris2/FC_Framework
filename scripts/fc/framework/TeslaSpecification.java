package scripts.fc.framework;

public abstract class TeslaSpecification
{
	protected String args;
	
	public TeslaSpecification(String args)
	{
		this.args = args;
		parseArgs();
	}
	
	public abstract void parseArgs();
}
