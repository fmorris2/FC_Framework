package scripts.webwalker_logic.teleport_logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.tribot.api.General;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;

import scripts.webwalker_logic.WebPath;
import scripts.webwalker_logic.local.walker_engine.Loggable;
import scripts.webwalker_logic.local.walker_engine.WaitFor;


public class TeleportManager implements Loggable {

    public static TeleportAction previousAction;

    private int offset;
    private HashSet<TeleportMethod> blacklistTeleportMethods;
    private HashSet<TeleportLocation> blacklistTeleportLocations;
    private ExecutorService executorService;

    private static TeleportManager teleportManager;
    private static TeleportMethod teleportMethod;
    private static TeleportManager getInstance(){
        return teleportManager != null ? teleportManager : (teleportManager = new TeleportManager());
    }

    public TeleportManager(){
        offset = 25;
        blacklistTeleportMethods = new HashSet<>();
        blacklistTeleportLocations = new HashSet<>();
        executorService = Executors.newFixedThreadPool(15);;

    }

    /**
     * Blacklist teleport methods. This method is NOT threadsafe.
     * @param teleportMethods
     */
    public static void ignoreTeleportMethods(TeleportMethod... teleportMethods){
        getInstance().blacklistTeleportMethods.addAll(Arrays.asList(teleportMethods));
    }

    public static void ignoreTeleportLocations(TeleportLocation... teleportLocations){
        getInstance().blacklistTeleportLocations.addAll(Arrays.asList(teleportLocations));
    }

    public static void clearTeleportMethodBlackList(){
        getInstance().blacklistTeleportMethods.clear();
    }

    public static void clearTeleportLocationBlackList(){
        getInstance().blacklistTeleportLocations.clear();
    }

    /**
     * Sets the threshold of tile difference that will trigger teleport action.
     * @param offset distance in tiles
     */
    public static void setOffset(int offset){
        getInstance().offset = offset;
    }

    public static ArrayList<RSTile> teleport(int originalPathLength, RSTile destination){
    	return null;
    	/*
        if (originalPathLength < getInstance().offset){
            return null;
        }
        
        Arrays.stream(TeleportMethod.values())
                .filter(TeleportMethod::canUse)
                .filter(teleportMethod -> !getInstance().blacklistTeleportMethods.contains(teleportMethod))
                .forEach(t -> General.println(t + ", canUse: " + t.canUse()));
        
        TeleportAction teleportAction = Arrays.stream(TeleportMethod.values())
                .filter(TeleportMethod::canUse)
                .filter(teleportMethod -> !getInstance().blacklistTeleportMethods.contains(teleportMethod))
                        .map(teleportMethod -> Arrays.stream(teleportMethod.getDestinations()))
                                .filter(teleportLocation -> !getInstance().blacklistTeleportLocations.contains(teleportLocation)) //map to destinations
                                .map(loc -> getInstance().executorService.submit(new PathComputer(teleportMethod, loc.min((l1, l2) -> l1.getRSTile().distanceTo(destination) - l2.getRSTile().distanceTo(destination)).orElse(null), destination)))
                .map(teleportActionFuture -> { //flatten out futures
                    try {
                        return teleportActionFuture.get();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).filter(teleportAction1 -> teleportAction1 != null && teleportAction1.path.size() > 0).min(Comparator.comparingInt(o -> o.path.size())).orElse(null);
		
        General.println("teleportAction == null: " + (teleportAction == null));
        if (teleportAction == null || teleportAction.path.size() >= originalPathLength || teleportAction.path.size() == 0){
            return null;
        }

        previousAction = teleportAction;

        if (originalPathLength - teleportAction.path.size() < getInstance().offset){
            getInstance().log("No efficient teleports!");
            return null;
        }

        getInstance().log("We will be using " + teleportAction.teleportMethod + " to " + teleportAction.teleportLocation);
        if (!teleportAction.teleportMethod.use(teleportAction.teleportLocation)){
            getInstance().log("Failed to teleport");
        }
        WaitFor.condition(General.random(3000, 54000), () -> teleportAction.teleportLocation.getRSTile().distanceTo(Player.getPosition()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
        return teleportAction.path;
        */

    }

    private static class PathComputer implements Callable<TeleportAction>{

        private TeleportMethod teleportMethod;
        private TeleportLocation teleportLocation;
        private RSTile destination;

        private PathComputer(TeleportMethod teleportMethod, TeleportLocation teleportLocation, RSTile destination){
        	General.println("Creating PathComputer with location: " + teleportLocation);
            this.teleportMethod = teleportMethod;
            this.teleportLocation = teleportLocation;
            this.destination = destination;
        }

        @Override
        public TeleportAction call() throws Exception {
            getInstance().log("Checking path... [" + teleportMethod + "] -> [" + teleportLocation + "]");
            return new TeleportAction(WebPath.getPath(teleportLocation.getRSTile(), destination), teleportMethod, teleportLocation);
        }

    }

    public static class TeleportAction {
        private ArrayList<RSTile> path;
        private TeleportMethod teleportMethod;
        private TeleportLocation teleportLocation;
        TeleportAction(ArrayList<RSTile> path, TeleportMethod teleportMethod, TeleportLocation teleportLocation){
            this.path = path;
            this.teleportMethod = teleportMethod;
            this.teleportLocation = teleportLocation;
        }

        public ArrayList<RSTile> getPath() {
            return path;
        }

        public TeleportMethod getTeleportMethod() {
            return teleportMethod;
        }

        public TeleportLocation getTeleportLocation() {
            return teleportLocation;
        }
    }

    @Override
    public String getName() {
        return "Teleport Manager";
    }
}
