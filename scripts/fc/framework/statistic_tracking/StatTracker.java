package scripts.fc.framework.statistic_tracking;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.tribot.api.General;

import scripts.fc.framework.encryption.RSAEncryptionUtil;

public class StatTracker
{
	public static final String SOCKET_URL = "127.0.0.1";//"vikingsoftware.org";
	
	private static final int SESSION_ID_LENGTH = 12;
	private static final String CANDIDATE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	private STATUS status;
	private boolean isOnline, hasReported;
	private Socket socket;
	private PrintWriter out;
	
	public boolean reportOnline(String scriptName)
	{
		try
		{
			socket = new Socket(SOCKET_URL, 4566);  
	        out = new PrintWriter(socket.getOutputStream(), true);
	        out.println("username="+General.getTRiBotUsername());
	        out.println("scriptName="+scriptName);
	        isOnline = true;
	        status = STATUS.CONNECTED;
	        return true;
		}
		catch(SecurityException e)
		{
			General.println("VikingScripts connection not allowed");
			status = STATUS.NOT_ALLOWED_FIREWALL;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			status = STATUS.SERVER_DOWN;
		}
  
		return false;
	}
	
	public void report(String statsArgs)
	{
		//add sessionID to statsArgs
		statsArgs += ",sessionId="+generateSessionId();
		
		//encrypt to RSA
		byte[] encrypted = RSAEncryptionUtil.encrypt(statsArgs);
		
		//convert to base64 string (in case we're sending over the wire)
		String base64 = RSAEncryptionUtil.convertToBase64(encrypted);
		
		//send post request (or in this case just send it through the socket)
		out.println("data="+base64);
		
		try
		{
			socket.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		hasReported = true;
	}
	
	private String generateSessionId()
	{
		String sessionId = "";
		for(int i = 0; i < SESSION_ID_LENGTH; i++)
			sessionId += CANDIDATE_CHARS.charAt(General.random(0, CANDIDATE_CHARS.length() - 1));
		
		return sessionId;
	}
	
	public enum STATUS
	{
		NOT_ALLOWED_FIREWALL,
		SERVER_DOWN,
		CONNECTED;
	}
	
	//Getters
	public boolean isOnline()
	{
		return isOnline;
	}
	
	public STATUS getStatus()
	{
		return status;
	}
	
	public boolean hasReported()
	{
		return hasReported;
	}
}
