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

public class SecuString extends Message implements Serializable {
	private static final long serialVersionUID = -1400067914953447482L;
	private transient byte[] encrypted;
	private int length;
	
	public SecuString(String str, String key){
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
    public String decrypt(String key){
    	try{
    		Cipher decryptor = getDecryptor(key);
	    	byte[] plain = decryptor.doFinal(encrypted);
	    	return new String(plain,StandardCharsets.UTF_8);
    	}
    	catch(IllegalBlockSizeException| BadPaddingException e){
    		return null;
    	}
    
    }
    
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
    
    
    private void writeObject(ObjectOutputStream out) throws IOException {
    	length = encrypted.length;
    	out.defaultWriteObject();
    	for(int i=0;i<length;i++){
    		out.writeByte(encrypted[i]);
    	}
        
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        encrypted = new byte[length];
        for(int i=0;i<length;i++){
        	encrypted[i]=in.readByte();
        }
        	
    }
}
