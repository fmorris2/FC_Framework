package scripts.fc.api.viewport;

import org.tribot.api.General;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Camera.ROTATION_METHOD;
import org.tribot.api2007.Player;
import org.tribot.script.Script;

import scripts.fc.api.utils.DebugUtils;
/**
 * 
 * @author Final Calibur & WastedBro (no specific order)
 *
 */
public class ACamera 
{
	//Local constants
	private final int ANGLE_MODIFIER = 12;
	private final int ROTATION_MODIFIER = 30;
	private final int STOP_DISTANCE_MIN = 1;
	private final int STOP_DISTANCE_MAX = 5;
	
	
    //Private instance fields
    private RotationThread rotationThread; //The thread that will handle camera rotation
    private AngleThread angleThread; //The thread that will handle camera angle 
    private Script script; //The script we are handling the camera for. This is so we can know when to shut off the threads.
    private boolean runsWithoutScript;
    private Positionable lastTile;
    private int rotationModifier, angleModifier, stopDistance;
    
    /*
     * Normal, widely used constructor
     * If you use this constructor, the threads
     * will terminate automatically when you end the script
     * associated with this object.
     */
    public ACamera(Script s)
    {
        instantiateVars(s);
    }
    
    /*
     * Default constructor.
     * 
     * Threads will keep running even after you end the script.
     * This can be useful for "background" scripts such as a bot farm manager
     * for example.
     */
    public ACamera()
    {
    	runsWithoutScript = true; 	
    	instantiateVars(null);
    }
    
    /*
     *	This method instantiates all of our variables for us.
     *	I decided to make this method because it makes instantiating the variables in the constructors less redundant.
     */
    private void instantiateVars(Script s)
    {
    	script = s;
        rotationThread = new RotationThread();
        rotationThread.setName("ACamera Rotation Thread");
        angleThread = new AngleThread();
        angleThread.setName("ACamera Angle Thread");
        Camera.setRotationMethod(ROTATION_METHOD.ONLY_KEYS); //DON'T CHANGE THIS
    }
    
    private void setAngle(Positionable tile, int angle)
    {
    	synchronized(angleThread)
    	{
	    	if(!angleThread.isAlive())
	    		angleThread.start();
	    	
	    	lastTile = angleThread.tile;
	    	angleThread.tile = tile;
	    	angleThread.angle = angle;
	    	angleThread.notify();
    	}
    }
    
    private void setRotation(Positionable tile, int rotation)
    {
    	synchronized(rotationThread)
    	{
	    	if(!rotationThread.isAlive())
	    		rotationThread.start();
	    	
	    	lastTile = rotationThread.tile;
	    	rotationThread.tile = tile;
	    	rotationThread.rotation = rotation;
	    	rotationThread.notify();
    	}
    }
    
    public void setAngle(Positionable tile)
    {
    	setAngle(tile, -1);
    }
    
    public void setRotation(Positionable tile)
    {
    	setRotation(tile, -1);
    }
    
    public void setRotation(int rotation)
    {
    	setRotation(null, rotation);
    }
    
    public void setAngle(int angle)
    {
    	setAngle(null, angle);
    }
    
    private int getOptimalAngle(Positionable tile)
    {
    	angleModifier = 0;
    	
    	if(lastTile == null || tile == null)
    		return Camera.getCameraAngle();
    	
    	if(!tile.equals(lastTile))
    		angleModifier = General.random(ANGLE_MODIFIER * -1, ANGLE_MODIFIER);
    	
    	return adjustAngleToTile(tile) + angleModifier;
    }
    
    private int getOptimalRotation(Positionable tile)
    {
    	rotationModifier = 0;
    	
    	if(lastTile == null || tile == null)
    		return Camera.getCameraRotation();
    	
    	if(!tile.equals(lastTile))
    		rotationModifier = General.random(ROTATION_MODIFIER * -1, ROTATION_MODIFIER);
    	
    	return Camera.getTileAngle(tile) + rotationModifier;
    }
    
    public void turnToTile(Positionable tile)
    {  
    	stopDistance = General.random(STOP_DISTANCE_MIN, STOP_DISTANCE_MAX);
    	setAngle(tile);
    	setRotation(tile);
    }   
    
    public static int adjustAngleToTile(Positionable tile)
    {
    	//Distance from player to object - Used in calculating the optimal angle.
    	//Objects that are farther away require the camera to be turned to a lower angle to increase viewing distance.
    	int distance = Player.getPosition().distanceTo(tile);
    	
    	//The angle is calculated by taking the max height (100, optimal for very close objects),
    	//and subtracting an arbitrary number (I chose 6 degrees) for every tile that it is away.
    	int angle = 100 - (distance * 6);
    	
    	return angle;
    }
    
    private boolean shouldStopCamera(Positionable tile)
    {
    	int dist = Player.getPosition().distanceTo(tile);
    	return !Player.isMoving() || dist < stopDistance || dist > STOP_DISTANCE_MAX;
    }
    
    private class RotationThread extends Thread
    {	
        protected Positionable tile;
        protected int rotation = Camera.getCameraRotation();
        
        @Override
        public synchronized void run() 
        {
        	try
        	{  
        		while((script != null && script.isActive()) || runsWithoutScript)
        		{   
        			if(tile != null)
        			{
        				DebugUtils.debugOnInterval("[ACamera] Setting Camera Rotation", 3500);
        				Camera.setCameraRotation(getOptimalRotation(tile));
        				if(!shouldStopCamera(tile))
        				{
        					sleep(General.random(10, 20));
        					continue;
        				}
        			}
        			else if(rotation != -1)
        				Camera.setCameraRotation(rotation);
		               
        			wait();
        		}
        	}
        	catch(Exception e)
        	{
        		e.printStackTrace();
        		General.println("Error initiating wait on angle thread.");
        	}
        }
        
    }
       
    private class AngleThread extends Thread
    {
    	protected Positionable tile;
    	protected int angle = Camera.getCameraAngle();
    	
    	@Override
    	public synchronized void run() 
    	{
    		try
    		{   
    			while((script != null && script.isActive()) || runsWithoutScript)
    			{	
    				if(tile != null)
    				{
    					DebugUtils.debugOnInterval("[ACamera] Setting Camera Angle", 3500);
    					Camera.setCameraAngle(getOptimalAngle(tile));
    					if(!shouldStopCamera(tile))
        				{
        					sleep(General.random(10, 20));
        					continue;
        				}
    				}
    				else if(angle != -1)
    					Camera.setCameraAngle(angle);
    				
    				wait();
    			}
    		}
    		catch(Exception e)
    		{
    			General.println("Error initiating wait on angle thread.");
    		}
    	}
    	
    }

    public RotationThread getRotationThread() 
    {
        return rotationThread;
    }

    public AngleThread getAngleThread() 
    {
        return angleThread;
    }
  
    public Script getScript() 
    {
    	return script;
    }
    
}