package utils;

/**
 * User information including username and password. 
 * @author Kaiwen Sun
 *
 */
public abstract class UserInfo{
	private static final String SPLIT = ":";
	public final String username;
	public final String password;
	public final boolean allowaction;
	
	/**
	 * Constructor.
	 * @param username username
	 * @param password password
	 * @param allowaction whether the user is allowed to conduct mouse and keyboard action
	 */
	public UserInfo(String username, String password, boolean allowaction){
		this.username = username;
		this.password = password;
		this.allowaction = allowaction;
	}
	
	public UserInfo(String string) {
		String[] fields = string.split(SPLIT);
		this.username = fields[0];
		this.password = fields[1];
		this.allowaction = Boolean.parseBoolean(fields[2]);
	}
	@Override
	public String toString(){
		return username+SPLIT+password+SPLIT+allowaction;
	}
}
