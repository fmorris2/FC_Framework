package scripts.fc.api.viewport;

import org.tribot.api.General;
import org.tribot.api2007.Camera;

public class FCCameraUtils
{
	public static void adjustCameraRandomly()
	{
		Camera.setCameraRotation(General.random(0, 360));
		Camera.setCameraAngle(General.random(0, 100));
	}
}
