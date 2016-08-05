package utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES encrypted serializable string message sent on internet.
 * @author Kaiwen Sun
 *
 */
public class SecuString extends Message implements Serializable {
	private static final long serialVersionUID = -1400067914953447482L;
	private transient byte[] encrypted;
	private int length;
	
	/**
	 * Constructor. Create an AES encrypted string.
	 * @param str plain text
	 * @param key AES encryption key. If string is null or empty, then use str as cipher text.
	 */
	public SecuString(String str, String key){
		if(key==null || key.equals("")){
			encrypted = str.getBytes();
			return;
		}
		byte[] tmpbyte = null;
		try{
			Cipher encryptor = getEncryptor(key); 
			byte[] plain = str.getBytes("UTF-8");
			tmpbyte  = encryptor.doFinal(plain);
		}
		catch(IllegalBlockSizeException| BadPaddingException| UnsupportedEncodingException e){
			e.printStackTrace();
		}
		finally{
			encrypted = tmpbyte;
		}
	}
	
	/**
	 * Decipher the message.
	 * @param key decryption AES key. If string is null or empty, then assume message is not encrypted. 
	 * @return deciphered message
	 */
    public String decrypt(String key){
    	if(key==null || key.equals("")){
    		return new String(encrypted,StandardCharsets.UTF_8);
		}
    	try{
    		Cipher decryptor = getDecryptor(key);
	    	byte[] plain = decryptor.doFinal(encrypted);
	    	return new String(plain,StandardCharsets.UTF_8);
    	}
    	catch(IllegalBlockSizeException| BadPaddingException e){
    		return null;
    	}
    
    }
    
    /**
     * Construct an AES Cipher object to decipher message using a given key.
     * @param key AES key
     * @return Cipher object
     */
    private static Cipher getDecryptor(String key){
    	if(key==null)
    		return null;
    	Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES");
			Key aesKey = getAesKey(key);
	    	cipher.init(Cipher.DECRYPT_MODE, aesKey);
	    	return cipher;
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			e.printStackTrace();
			return null;
		}

    }
    
    /**
     * Construct an AES Cipher object to encrypt message using a given key.
     * @param key AES key
     * @return Cipher object
     */
    private static Cipher getEncryptor(String key) {
    	if(key==null)
    		return null;
    	try{
	    	Cipher cipher = Cipher.getInstance("AES");
	    	Key aesKey = getAesKey(key);
	    	cipher.init(Cipher.ENCRYPT_MODE, aesKey);
	    	return cipher;
    	}
    	catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
    		e.printStackTrace();
			return null;
		}
    }
    
    /**
     * Get AES key
     * @param key key string
     * @return AES key
     * @throws NoSuchAlgorithmException no such algorithm
     * @throws NoSuchPaddingException no such padding
     */
    private static Key getAesKey(String key) throws NoSuchAlgorithmException, NoSuchPaddingException{
    	if(key.getBytes().length!=128/8){
    		StringBuilder sb = new StringBuilder();
    		Random rg = new Random(key.hashCode());
    		while(sb.length()<16){
				sb.append((char)rg.nextInt(128));
			}
    		key = sb.toString();
    	}
    	Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
    	return aesKey;
    }
    
    /**
     * Serialize to write out this object.
     * @param out output stream
     * @throws IOException fail to serialize write
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
    	length = encrypted.length;
    	out.defaultWriteObject();
    	for(int i=0;i<length;i++){
    		out.writeByte(encrypted[i]);
    	}
        
    }

    /**
     * Serialize to read in this object.
     * @param in input stream
     * @throws IOException fail to serialize read
     * @throws ClassNotFoundException fail to serialize read
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        encrypted = new byte[length];
        for(int i=0;i<length;i++){
        	encrypted[i]=in.readByte();
        }
        	
    }
}
