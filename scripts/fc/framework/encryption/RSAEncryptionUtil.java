package scripts.fc.framework.encryption;

import java.io.ObjectInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;

public class RSAEncryptionUtil
{
	private static final String ALGORITHM = "RSA";
	private static final String KEY_PATH = "https://www.vikingsoftware.org/downloads/encryption/public.key";
	
	/*
	 * Encrypts plain text using RSA with the public key
	 */
	public static byte[] encrypt(String text)
	{		
		byte[] cipherText = null;
		try
		{
			//first, load the public key file
			ObjectInputStream inputStream = new ObjectInputStream(new URL(KEY_PATH).openStream());
			final PublicKey PUBLIC_KEY = (PublicKey)inputStream.readObject();
			
			if(PUBLIC_KEY == null)
				return null;
			
			//get an RSA cipher object
			final Cipher CIPHER = Cipher.getInstance(ALGORITHM);
			
			//encrypt the plain text using the public key
			CIPHER.init(Cipher.ENCRYPT_MODE, PUBLIC_KEY);
			cipherText = CIPHER.doFinal(text.getBytes(StandardCharsets.UTF_8));
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return cipherText;
	}
	
	/*
	 * Converts the cipher to a base64 String so we can send it easily over the wire
	 */
	public static String convertToBase64(byte[] cipher)
	{
		return Base64.getUrlEncoder().withoutPadding().encodeToString(cipher);
	}
	
}
