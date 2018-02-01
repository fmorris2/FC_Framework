package scripts.fc.api.utils;

import java.util.HashMap;
import java.util.Map;

import org.tribot.api.General;
import org.tribot.api.Timing;

public class DebugUtils {
	private static final Map<String, Long> MSG_MAP = new HashMap<>();
	
	public static void debugOnInterval(String msg, long interval) {
		if(Timing.timeFromMark(MSG_MAP.getOrDefault(msg, 0L)) > interval) {
			General.println(msg);
			MSG_MAP.put(msg, Timing.currentTimeMillis());
		}
		
	}
}
