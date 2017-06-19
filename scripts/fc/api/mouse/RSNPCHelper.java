package scripts.fc.api.mouse;

import org.tribot.api2007.types.RSNPC;

/**
 * 
 * @author DAXMAGEX
 *
 */
public class RSNPCHelper {

    public static String getName(RSNPC rsnpc){
        String name = rsnpc.getName();
        return name != null ? name : "null";
    }

    public static String[] getActions(RSNPC rsnpc){
        String[] actions = rsnpc.getActions();
        return actions != null ? actions : new String[0];
    }

}