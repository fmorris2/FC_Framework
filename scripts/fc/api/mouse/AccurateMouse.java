package scripts.fc.api.mouse;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Clickable;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Game;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSMenuNode;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSTile;

/**
 * This class does NOT examine objects.
 *
 * clickAction should never include the target entity's name. Just the action.
 * 
 * @author DAXMAGEX
 */
public class AccurateMouse {

    public static void move(int x, int y){
        move(new Point(x, y));
    }

    public static void move(Point point){
        Mouse.move(point.x, point.y);
    }

    public static void click(int button){
        click(Mouse.getPos(), button);
    }

    public static void click(int x, int y){
        click(x, y, 1);
    }

    public static void click(int x, int y, int button){
        click(new Point(x, y), button);
    }

    public static void click(Point point){
        click(point.x, point.y, 1);
    }

    public static void click(Point point, int button){
        if (!Mouse.getPos().equals(point)) {
            Mouse.move(point.x, point.y);
        }
        Mouse.sendPress(point, button);
        General.sleep(General.randomSD(5, 90, 30, 20));
        Mouse.sendRelease(point, button);
        Mouse.sendClickEvent(point, button);
    }


    public static boolean click(Clickable clickable, String... clickActions){
        return action(clickable, false, clickActions);
    }

    public static boolean hover(Clickable clickable, String... clickActions){
        return action(clickable, true, clickActions);
    }

    private static boolean action(Clickable clickable, boolean hover, String... clickActions){
        if (clickable == null){
            return false;
        }
        String name = null;
        RSModel model = null;
        if (clickable instanceof RSCharacter){
            RSCharacter rsCharacter = ((RSCharacter) clickable);
            name = rsCharacter.getName();
            model = rsCharacter.getModel();
        } else if (clickable instanceof RSGroundItem){
            RSGroundItem rsGroundItem = ((RSGroundItem) clickable);
            RSItemDefinition rsItemDefinition = rsGroundItem.getDefinition();
            name = rsItemDefinition != null ? rsItemDefinition.getName() : null;
            model = rsGroundItem.getModel();
        } else if (clickable instanceof RSObject){
            RSObject rsObject = ((RSObject) clickable);
            RSObjectDefinition rsObjectDefinition = rsObject.getDefinition();
            name = rsObjectDefinition != null ? rsObjectDefinition.getName() : null;
            model = rsObject.getModel();
        } else if (clickable instanceof RSItem){
            name = RSItemHelper.getItemName((RSItem) clickable);
        }
        return action(model, clickable, name, hover, clickActions);
    }

    /**
     *
     * @param model model of {@code clickable}
     * @param clickable target entity
     * @param clickActions actions to click or hover. Do not include {@code targetName}
     * @param targetName name of the {@code clickable} entity
     * @param hover True to hover the OPTION, not the entity model. It will right click {@code clickable} and hover over option {@code clickAction}
     * @return whether action was successful.
     */
    private static boolean action(RSModel model, Clickable clickable, String targetName, boolean hover, String... clickActions){
        for (int i = 0; i < General.random(4, 7); i++) {
            if (attemptAction(model, clickable, targetName, hover, clickActions)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Clicks or hovers desired action of entity.
     *
     * @param model target entity model
     * @param clickable target entity
     * @param clickActions actions
     * @param targetName name of target
     * @param hover hover option or not
     * @return result of action
     */
    private static boolean attemptAction(RSModel model, Clickable clickable, String targetName, boolean hover, String... clickActions){
//        System.out.println((hover ? "Hovering over" : "Clicking on") + " " + targetName + " with [" + Arrays.stream(clickActions).reduce("", String::concat) + "]");

        if (handleRSItemRSInterface(clickable, hover, clickActions)){
            return true;
        }

        Point point = null;
        if (clickable instanceof RSTile && Arrays.stream(clickActions).anyMatch(s -> s.matches("Walk here"))){
            point = ((RSTile) clickable).getHumanHoverPoint();
        } else if (model == null){
            return false;
        }

        if (ChooseOption.isOpen()){
            RSMenuNode menuNode = getValidMenuNode(clickable, targetName, ChooseOption.getMenuNodes(), clickActions);
            if (handleMenuNode(menuNode, hover)){
                return true;
            } else {
                ChooseOption.close();
            }
        }

        if (point == null) {
            point = model.getHumanHoverPoint();
        }

        if (point == null || point.getX() == -1){
            return false;
        }

        if (point.distance(Mouse.getPos()) < Mouse.getSpeed()/20){
            Mouse.hop(point);
        } else {
            Mouse.move(point);
        }

        if (!model.getEnclosedArea().contains(point)){
            return false;
        }

        if (hover && clickActions.length == 0){
            return true;
        }

        String regex = "(" + String.join("|", Arrays.stream(clickActions).map(Pattern::quote).collect(Collectors.toList())) + ")" + " (.* )*?(-> )?" + (targetName != null ? targetName : "") + "(.*)";
        General.println("regex: " + regex);
        if (WaitFor.condition(100, () -> Arrays.stream(ChooseOption.getOptions()).anyMatch(s -> s.matches(regex)) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
        	boolean multipleMatches = false;

            String[] options = ChooseOption.getOptions();
            if (Arrays.stream(options).filter(s -> s.matches(regex)).count() > 1){
                multipleMatches = true;
            }

            String uptext = Game.getUptext();
            if (uptext == null){ //double check
                return false;
            }

            if (uptext.matches(regex) && !hover && !multipleMatches){
                click(1);
                return waitResponse() == State.RED;
            }

            click(3);
            RSMenuNode menuNode = getValidMenuNode(clickable, targetName, ChooseOption.getMenuNodes(), clickActions);
            
            if (handleMenuNode(menuNode, hover)){
                return true;
            }

        }
        return false;
    }

    private static boolean handleRSItemRSInterface(Clickable clickable, boolean hover, String... clickActions){
        if (!(clickable instanceof RSItem || clickable instanceof RSInterface)){
            return false;
        }

        Rectangle area = clickable instanceof  RSItem ? ((RSItem) clickable).getArea() : ((RSInterface) clickable).getAbsoluteBounds();
        String uptext = Game.getUptext();
        if (area.contains(Mouse.getPos())){
            if (uptext != null && (clickActions.length == 0 || Arrays.stream(clickActions).anyMatch(uptext::contains))) {
                if (hover) {
                    return true;
                }
                click(1);
                return true;
            } else {
                Mouse.click(3);
                return ChooseOption.select(clickActions);
            }
        } else {
            Mouse.moveBox(area);
            if (!hover){
                return clickable.click(clickActions);
            }
            //TODO: handle hovering of interfaces for secondary actions such as right click hover
            return true;
        }
    }

    private static boolean handleMenuNode(RSMenuNode rsMenuNode, boolean hover){
        if (rsMenuNode == null){
            return false;
        }
        Rectangle rectangle = rsMenuNode.getArea();
        if (rectangle == null){
            ChooseOption.close();
            return false;
        }
        Point currentMousePosition = Mouse.getPos();
        if (hover){
            if (!rectangle.contains(currentMousePosition)){
                Mouse.moveBox(rectangle);
            }
        } else {
            if (rectangle.contains(currentMousePosition)){
                click(1);
            } else {
                Mouse.clickBox(rectangle, 1);
            }
        }
        return true;
    }

    private static RSMenuNode getValidMenuNode(Clickable clickable, String targetName, RSMenuNode[] menuNodes, String... clickActions){
        if (clickable == null || targetName == null || menuNodes == null){
            return null;
        }
        List<RSMenuNode> list = Arrays.stream(menuNodes).filter(rsMenuNode -> {
            String target = rsMenuNode.getTarget(), action = rsMenuNode.getAction();
            //General.println("target: " + target + ", action: " + action + ", targetName: " + targetName);
            return target != null && action != null && Arrays.stream(clickActions).anyMatch(s -> s.equals(action));
        }).collect(Collectors.toList());
        
        return list.stream().filter(rsMenuNode -> rsMenuNode.correlatesTo(clickable) || rsMenuNode.getTarget().equals(targetName)).findFirst().orElse(list.size() > 0 ? list.get(0) : null);
    }

    public static State waitResponse(){
        State response = WaitFor.getValue(250, () -> {
            switch (getState()){
                case YELLOW: return State.YELLOW;
                case RED: return State.RED;
				default:
					break;
            }
            return null;
        });
        return response != null ? response : State.NONE;
    }

    public static State getState(){
        int crosshairState = Game.getCrosshairState();
        for (State state : State.values()){
            if (state.id == crosshairState){
                return state;
            }
        }
        return State.NONE;
    }

    public enum State {
        NONE (0),
        YELLOW (1),
        RED (2);
        private int id;
        State (int id){
            this.id = id;
        }
    }

}