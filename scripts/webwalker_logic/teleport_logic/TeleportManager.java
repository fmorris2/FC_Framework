package scripts.webwalker_logic.teleport_logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.tribot.api.General;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;

import scripts.webwalker_logic.WebPath;
import scripts.webwalker_logic.WebWalker;
import scripts.webwalker_logic.local.walker_engine.Loggable;
import scripts.webwalker_logic.local.walker_engine.WaitFor;
import scripts.webwalker_logic.shared.helpers.BankHelper;


public class TeleportManager implements Loggable {

    public static TeleportAction previousAction;

    private int offset;
    private HashSet<TeleportMethod> blacklistTeleportMethods;
    private HashSet<TeleportLocation> blacklistTeleportLocations;
    private ExecutorService executorService;

    private static TeleportManager teleportManager;
    private static TeleportManager getInstance(){
        return teleportManager != null ? teleportManager : (teleportManager = new TeleportManager());
    }

    public TeleportManager(){
        offset = 75;
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
    	/*
        if (originalPathLength < getInstance().offset){
            return null;
        }
        
        //first, get useable teleport methods
        */
    	List<TeleportMethod> useableTeleports = Arrays.stream(TeleportMethod.values())
    			.filter(method -> method.canUse() && (method.hasOnCharacter() || method.hasInBank()) && !getInstance().blacklistTeleportMethods.contains(method))
    			.collect(Collectors.toList());
    	
    	List<PathComputer> pathComputers = new ArrayList<>();
    	
    	/*
    	 	Iterate through useable teleport methods,
    		and create a PathComputer object for
    		the closest teleport location to our
    		destination for that method
		*/
    	for(TeleportMethod method : useableTeleports)
    	{
    		TeleportLocation closestLoc = 
    			Arrays.stream(method.getDestinations())
    			.filter(loc -> !getInstance().blacklistTeleportLocations.contains(loc))
    			.min((l1, l2) -> l1.getRSTile().distanceTo(destination) - l2.getRSTile().distanceTo(destination))
    			.orElse(null);
    		
    		if(closestLoc != null)
    			pathComputers.add(new PathComputer(method, closestLoc, destination));			
    	}
    	
    	/*
    	 * We now submit each pathcomputer to the executor
    	 * service, and get a list of future teleport actions
    	 * in return
    	 */
    	List<Future<TeleportAction>> futures = new ArrayList<>();
    	
    	for(PathComputer computer : pathComputers)
    		futures.add(getInstance().executorService.submit(computer));
    	
    	/*
    	 * Find best teleport action
    	 */
    	TeleportAction teleportAction = futures.stream()
    			.map(future -> { //flatten out futures
                    try {
                        return future.get();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }).filter(teleportAction1 -> teleportAction1 != null && teleportAction1.path.size() > 0)
                .min(Comparator.comparingInt(o -> o.path.size())).orElse(null);
    	
        if (teleportAction == null || teleportAction.path.size() >= originalPathLength || teleportAction.path.size() == 0){
            return null;
        }

        previousAction = teleportAction;

        if (originalPathLength - teleportAction.path.size() < getInstance().offset){
            getInstance().log("No efficient teleports!");
            return null;
        }

        getInstance().log("We will be using " + teleportAction.teleportMethod + " to " + teleportAction.teleportLocation);
        if(!teleportAction.teleportMethod.hasOnCharacter())
        {
        	General.println("We will be grabbing " + teleportAction.teleportMethod + " from the bank");
        	while(!BankHelper.isInBank())
        	{
        		General.println("Walking to bank to grab teleport supplies...");
        		WebWalker.walkToBank();
        		General.sleep(100);
        	}
        	while(!teleportAction.teleportMethod.hasOnCharacter()
        			&& teleportAction.teleportMethod.hasInBank())
        	{
        		General.println("Withdrawing teleport from bank...");
        		teleportAction.teleportMethod.withdraw();
        		General.sleep(100);
        	}
        	
        	while(Banking.isBankScreenOpen())
        	{
        		Banking.close();
        		General.sleep(100);
        	}
        }
        if (!teleportAction.teleportMethod.use(teleportAction.teleportMethod, teleportAction.teleportLocation)){
            getInstance().log("Failed to teleport");
            return null;
        }
        else
        	WaitFor.condition(General.random(3000, 54000), () -> teleportAction.teleportLocation.getRSTile().distanceTo(Player.getPosition()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
        
        return teleportAction.path;
    }

    private static class PathComputer implements Callable<TeleportAction>{

        private TeleportMethod teleportMethod;
        private TeleportLocation teleportLocation;
        private RSTile destination;
        private boolean hasOnCharacter;
        
        private PathComputer(TeleportMethod teleportMethod, TeleportLocation teleportLocation, RSTile destination){
        	General.println("Creating PathComputer with location: " + teleportLocation);
            this.teleportMethod = teleportMethod;
            this.teleportLocation = teleportLocation;
            this.destination = destination;
            this.hasOnCharacter = teleportMethod.hasOnCharacter();
            General.println("Method: " + teleportMethod + " has on character: " + hasOnCharacter);
        }

        @Override
        public TeleportAction call() throws Exception {
        	//check if we need to get from bank first
        	ArrayList<RSTile> toBank = new ArrayList<>();
        	if(!hasOnCharacter)
        	{
        		General.println("Needs to grab [" + teleportMethod + "] from bank...");
        		toBank = WebPath.getPathToBank();
        		General.println("Path to nearest bank is " + toBank.size() + " tiles long...");
        	}
        	
        	General.println("Checking path... [" + teleportMethod + "] -> [" + teleportLocation + "]");
            toBank.addAll((WebPath.getPath(teleportLocation.getRSTile(), destination)));
            General.println("Total path size for [" + teleportMethod +"] is " + toBank.size() + " tiles");
            return new TeleportAction(toBank, teleportMethod, teleportLocation);
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
