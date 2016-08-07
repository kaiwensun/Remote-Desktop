package utils;

import java.util.Random;

public class Challenger {
	public static final String PREFIX = "CHALLENGE:";
	/**
	 * Challenge the connector on the other side of postman. Send a randomly generated string prefixed with
	 * "CHALLENGE:" and encrypt it with the password of whom the connector claims to be, see if the 
	 * connector can successfully reply the postoffice generated string without the prefix.
	 * @param postman postman
	 * @param userInfo userInfo of the solver who is being challenged
	 * @return true if the other side passed challenge. false otherwise.
	 */
	public static boolean challenge(Postman postman, UserInfo userInfo){
		Random rg = new Random();
		String chlng = ""+rg.nextLong();
		SecuString secuString = new SecuString(PREFIX+chlng, userInfo.password);
		try{
			postman.send(secuString);
			Object obj = postman.recv();
			if(!(obj instanceof SecuString)){
				return false;
			}
			SecuString replied = (SecuString)obj;
			if(!chlng.equals(replied.decrypt(userInfo.password))){
				return false;
			}
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
	
	/**
	 * Solve authentication challenge.
	 * <em>Solve the challenge only if you trust the other party. Otherwise, the other party 
	 * may play a MAN IN THE MIDDLE ATTACK and asks you to help him to solve the challenge.</em> 
	 * @param postman postman
	 * @param userInfo the userInfo of the solver who is being challenged.
	 * @return true if finished solving challenge (may not correctly solved). false otherwise.
	 */
	public static boolean solveChallenge(Postman postman, UserInfo userInfo){
		try{
			Object obj = postman.recv();
			if(!(obj instanceof SecuString))
				return false;
			SecuString secuString = (SecuString)obj;
			String string = secuString.decrypt(userInfo.password);
			if(string==null){
				string = secuString.decrypt(null);
			}
			if(!string.startsWith(PREFIX)){
				System.err.println("Unexpected message from postoffice ("+string+")");
				postman.send(new SecuString("Msg:I can't understand you.",null));
				return false;
			}
			SecuString reply = new SecuString(string.substring(PREFIX.length()), userInfo.password);
			postman.send(reply);
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
}
