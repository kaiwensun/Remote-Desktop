package utils;

public class ClientUserInfo extends UserInfo{

	public ClientUserInfo(String username, String password, boolean allowaction) {
		super(username, password, allowaction);
	}
	
	public ClientUserInfo(String string){
		super(string);
	}
	
}