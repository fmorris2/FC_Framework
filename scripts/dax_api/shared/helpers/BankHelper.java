package scripts.dax_api.shared.helpers;

import java.util.HashSet;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Game;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;

import scripts.dax_api.walker_engine.WaitFor;
import scripts.dax_api.walker_engine.interaction_handling.InteractionHelper;

public class BankHelper {

    private static final Filter<RSObject> BANK_OBJECT_FILTER = Filters.Objects.nameContains("bank", "Bank", "Exchange booth", "Open chest")
            .combine(Filters.Objects.actionsContains("Collect"), true)
            .combine(Filters.Objects.actionsContains("Bank"), true);

    public static boolean isInBank(){
        return isInBank(Player.getPosition());
    }

    public static boolean isInBank(final Positionable positionable){
        final RSObject[] bankObjects = Objects.findNearest(15, BANK_OBJECT_FILTER);
        if (bankObjects.length == 0){
            return false;
        }
        final RSObject bankObject = bankObjects[0];
        final HashSet<RSTile> building = getBuilding(bankObject);
        return building.contains(positionable.getPosition()) || (building.size() == 0 && positionable.getPosition().distanceTo(bankObject) < 5);
    }

    /**
     *
     * @return whether if the action succeeded
     */
    public static boolean openBank() {
        return Banking.isBankScreenOpen() || InteractionHelper.click(InteractionHelper.getRSObject(BANK_OBJECT_FILTER), "Bank");
    }

    /**
     *
     * @return bank screen is open
     */
    public static boolean openBankAndWait(){
        if (Banking.isBankScreenOpen()){
            return true;
        }
        final RSObject object = InteractionHelper.getRSObject(BANK_OBJECT_FILTER);
        return InteractionHelper.click(object, "Bank") && waitForBankScreen(object);
    }

    public static HashSet<RSTile> getBuilding(final Positionable positionable){
        return computeBuilding(positionable, Game.getSceneFlags(), new HashSet<>());
    }

    private static HashSet<RSTile> computeBuilding(final Positionable positionable, final byte[][][] sceneFlags, final HashSet<RSTile> tiles){
        final RSTile local = positionable.getPosition().toLocalTile();
        final int localX = local.getX(), localY = local.getY(), localZ = local.getPlane();
        if (localX == -1 || localY == -1 || localZ == -1){
            return tiles;
        }
        if (sceneFlags.length <= localZ || sceneFlags[localZ].length <= localX || sceneFlags[localZ][localX].length <= localY){ //Not within bounds
            return tiles;
        }
        if (sceneFlags[localZ][localX][localY] < 4){ //Not a building
            return tiles;
        }
        if (!tiles.add(local.toWorldTile())){ //Already computed
            return tiles;
        }
        computeBuilding(new RSTile(localX, localY + 1, localZ, RSTile.TYPES.LOCAL).toWorldTile(), sceneFlags, tiles);
        computeBuilding(new RSTile(localX + 1, localY, localZ, RSTile.TYPES.LOCAL).toWorldTile(), sceneFlags, tiles);
        computeBuilding(new RSTile(localX, localY - 1, localZ, RSTile.TYPES.LOCAL).toWorldTile(), sceneFlags, tiles);
        computeBuilding(new RSTile(localX - 1, localY, localZ, RSTile.TYPES.LOCAL).toWorldTile(), sceneFlags, tiles);
        return tiles;
    }

    public static boolean isInBuilding(final RSTile localRSTile, final byte[][][] sceneFlags) {
        return !(sceneFlags.length <= localRSTile.getPlane()
                    || sceneFlags[localRSTile.getPlane()].length <= localRSTile.getX()
                    || sceneFlags[localRSTile.getPlane()][localRSTile.getX()].length <= localRSTile.getY())
                && sceneFlags[localRSTile.getPlane()][localRSTile.getX()][localRSTile.getY()] >= 4;
    }

    private static boolean waitForBankScreen(final RSObject object){
        return WaitFor.condition(WaitFor.getMovementRandomSleep(object), ((WaitFor.Condition) () -> Banking.isBankScreenOpen() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE).combine(WaitFor.getNotMovingCondition())) == WaitFor.Return.SUCCESS;
    }

}
