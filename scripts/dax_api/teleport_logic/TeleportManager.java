package scripts.dax_api.teleport_logic;

import static scripts.dax_api.api_lib.models.PathStatus.RATE_LIMIT_EXCEEDED;
import static scripts.dax_api.api_lib.models.PathStatus.SUCCESS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.tribot.api.General;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;

import scripts.dax_api.WebPath;
import scripts.dax_api.api_lib.WebWalkerServerApi;
import scripts.dax_api.api_lib.models.PathResult;
import scripts.dax_api.api_lib.models.PlayerDetails;
import scripts.dax_api.api_lib.models.Point3D;
import scripts.dax_api.api_lib.models.RunescapeBank;
import scripts.dax_api.walker_engine.Loggable;
import scripts.dax_api.walker_engine.WaitFor;


public class TeleportManager implements Loggable {

    public static TeleportAction previousAction;

    private int offset;
    private final HashSet<TeleportMethod> blacklistTeleportMethods;
    private final HashSet<TeleportLocation> blacklistTeleportLocations;
    private final ExecutorService executorService;
    private final ExecutorService customThreadPool = new ForkJoinPool(10);


    private static TeleportManager teleportManager;

    private static TeleportManager getInstance() {
        return teleportManager != null ? teleportManager : (teleportManager = new TeleportManager());
    }

    public TeleportManager() {
        offset = 25;
        blacklistTeleportMethods = new HashSet<>();
        blacklistTeleportLocations = new HashSet<>();
        executorService = Executors.newFixedThreadPool(15);
    }

    /**
     * Blacklist teleport methods. This method is NOT threadsafe.
     *
     * @param teleportMethods
     */
    public static void ignoreTeleportMethods(final TeleportMethod... teleportMethods) {
        getInstance().blacklistTeleportMethods.addAll(Arrays.asList(teleportMethods));
    }

    public static void ignoreTeleportLocations(final TeleportLocation... teleportLocations) {
        getInstance().blacklistTeleportLocations.addAll(Arrays.asList(teleportLocations));
    }

    public static void clearTeleportMethodBlackList() {
        getInstance().blacklistTeleportMethods.clear();
    }

    public static void clearTeleportLocationBlackList() {
        getInstance().blacklistTeleportLocations.clear();
    }

    /**
     * Sets the threshold of tile difference that will trigger teleport action.
     *
     * @param offset distance in tiles
     */
    public static void setOffset(final int offset) {
        getInstance().offset = offset;
    }

    public static ArrayList<RSTile> getClosestBankPath(final RunescapeBank bank, final int originalMoveCost) {
        final int cost = originalMoveCost - 25;
        final List<TeleportWrapper> teleport = new CopyOnWriteArrayList<>();

        try {
            getInstance().customThreadPool.submit(() -> Arrays.stream(TeleportMethod.values()).parallel().forEach(teleportMethod -> {
                if (getInstance().blacklistTeleportMethods.contains(teleportMethod)) {
                    return;
                }
                if (!teleportMethod.canUse()) {
                    return;
                }
                for (final TeleportLocation teleportLocation : teleportMethod.getDestinations()) {
                    if (getInstance().blacklistTeleportLocations.contains(teleportLocation)) {
                        continue;
                    }
                    final PathResult pathResult = WebWalkerServerApi.getInstance().getBankPath(
                            Point3D.fromPositionable(teleportLocation.getRSTile()),
                            bank,
                            PlayerDetails.generate()
                    );
                    teleport.add(new TeleportWrapper(pathResult, teleportMethod, teleportLocation));
                }
            })).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        final boolean[] rateLimit = new boolean[]{false};

        final TeleportWrapper closest = teleport.stream()
                .filter(pathResult -> {
                    if (pathResult.getPathResult().getPathStatus() == RATE_LIMIT_EXCEEDED) {
                        rateLimit[0] = true;
                    }
                    return pathResult.getPathResult().getPathStatus() == SUCCESS && pathResult.getPathResult().getCost() < cost;
                })
                .min(Comparator.comparingInt(value -> value.getPathResult().getCost()))
                .orElse(null);

        if (rateLimit[0]) {
            return null;
        }

        if (closest == null) {
            return null;
        }

        getInstance().log("Found shorter path with teleport: " + closest.getTeleportMethod() + " > " + closest.getTeleportLocation() + " (" + originalMoveCost);

        if (!closest.getTeleportMethod().use(closest.getTeleportLocation())) {
            getInstance().log("Failed to teleport");
            return null;
        }

        return closest.getPathResult().toRSTilePath();
    }

    public static ArrayList<RSTile> getClosestPath(final int originalMoveCost, final RSTile destination) {
        final int cost = originalMoveCost - 25;
        final List<TeleportWrapper> teleport = new CopyOnWriteArrayList<>();

        try {
            getInstance().customThreadPool.submit(() -> Arrays.stream(TeleportMethod.values()).parallel().forEach(teleportMethod -> {
                if (getInstance().blacklistTeleportMethods.contains(teleportMethod)) {
                    return;
                }
                if (!teleportMethod.canUse()) {
                    return;
                }
                System.out.println("We can use " + teleportMethod);
                for (final TeleportLocation teleportLocation : teleportMethod.getDestinations()) {
                    if (getInstance().blacklistTeleportLocations.contains(teleportLocation)) {
                        continue;
                    }

                    final PathResult pathResult = WebWalkerServerApi.getInstance().getPath(
                            Point3D.fromPositionable(teleportLocation.getRSTile()),
                            Point3D.fromPositionable(destination),
                            PlayerDetails.generate()
                    );
                    teleport.add(new TeleportWrapper(pathResult, teleportMethod, teleportLocation));
                }
            })).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


        final boolean[] rateLimit = new boolean[]{false};

        final TeleportWrapper closest = teleport.stream()
                .filter(pathResult -> {
                    if (pathResult.getPathResult().getPathStatus() == RATE_LIMIT_EXCEEDED) {
                        rateLimit[0] = true;
                    }
                    return pathResult.getPathResult().getPathStatus() == SUCCESS && pathResult.getPathResult().getCost() < cost;
                })
                .min(Comparator.comparingInt(value -> value.getPathResult().getCost()))
                .orElse(null);

        if (rateLimit[0]) {
            return null;
        }

        if (closest == null) {
            return null;
        }

        getInstance().log("Found shorter path with teleport: " + closest.getTeleportMethod() + " > " + closest.getTeleportLocation() + " (" + originalMoveCost);

        if (!closest.getTeleportMethod().use(closest.getTeleportLocation())) {
            getInstance().log("Failed to teleport");
            return null;
        }

        return closest.getPathResult().toRSTilePath();
    }


    public static ArrayList<RSTile> teleport(final int originalPathLength, final RSTile destination) {
        if (originalPathLength < getInstance().offset) {
            return null;
        }


        final TeleportAction teleportAction = Arrays.stream(TeleportMethod.values())
                .filter(TeleportMethod::canUse)
                .filter(teleportMethod -> !getInstance().blacklistTeleportMethods.contains(teleportMethod))
                .map(teleportMethod -> Arrays.stream(teleportMethod.getDestinations())
                        .filter(teleportLocation -> !getInstance().blacklistTeleportLocations.contains(teleportLocation)) //map to destinations
                        .map(teleportLocation -> getInstance().executorService.submit(new PathComputer(teleportMethod, teleportLocation, destination)))) //map to future
                .flatMap(futureStream -> futureStream).collect(Collectors.toList()).stream().map(teleportActionFuture -> { //flatten out futures
                    try {
                        return ((Future<TeleportAction>)teleportActionFuture).get();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).filter(teleportAction1 -> teleportAction1 != null && teleportAction1.path.size() > 0).min(Comparator.comparingInt(o -> o.path.size())).orElse(null);

        if (teleportAction == null || teleportAction.path.size() >= originalPathLength || teleportAction.path.size() == 0) {
            return null;
        }

        previousAction = teleportAction;

        if (originalPathLength - teleportAction.path.size() < getInstance().offset) {
            getInstance().log("No efficient teleports!");
            return null;
        }

        getInstance().log("We will be using " + teleportAction.teleportMethod + " to " + teleportAction.teleportLocation);
        if (!teleportAction.teleportMethod.use(teleportAction.teleportLocation)) {
            getInstance().log("Failed to teleport");
        }
        WaitFor.condition(General.random(3000, 54000), () -> teleportAction.teleportLocation.getRSTile().distanceTo(Player.getPosition()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
        return teleportAction.path;

    }

    private static class PathComputer implements Callable<TeleportAction> {

        private final TeleportMethod teleportMethod;
        private final TeleportLocation teleportLocation;
        private final RSTile destination;

        private PathComputer(final TeleportMethod teleportMethod, final TeleportLocation teleportLocation, final RSTile destination) {
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

    public static class TeleportWrapper {
        private final PathResult pathResult;
        private final TeleportMethod teleportMethod;
        private final TeleportLocation teleportLocation;

        public TeleportWrapper(final PathResult pathResult, final TeleportMethod teleportMethod, final TeleportLocation teleportLocation) {
            this.pathResult = pathResult;
            this.teleportMethod = teleportMethod;
            this.teleportLocation = teleportLocation;
        }

        public PathResult getPathResult() {
            return pathResult;
        }

        public TeleportMethod getTeleportMethod() {
            return teleportMethod;
        }

        public TeleportLocation getTeleportLocation() {
            return teleportLocation;
        }
    }

    public static class TeleportAction {
        private final ArrayList<RSTile> path;
        private final TeleportMethod teleportMethod;
        private final TeleportLocation teleportLocation;

        TeleportAction(final ArrayList<RSTile> path, final TeleportMethod teleportMethod, final TeleportLocation teleportLocation) {
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
