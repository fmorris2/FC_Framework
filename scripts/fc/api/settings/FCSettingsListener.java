package scripts.fc.api.settings;

public interface FCSettingsListener
{
	public void settingChanged(int index, int oldValue, int newValue);
	public void varBitChanged(int setting, int varbitIndex, int oldValue, int newValue);
}
