package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Configuration. Most fields of this class are public and static in order
 * to be used and add new field easily. Assigning values from out of this
 * class is depreciated. Please be sure you do understand what you are doing
 * if you want to assign values. 
 * @author Kaiwen Sun
 *
 */
public class Cfg {
	private static File file;
	private static JSONObject json;
	private static final String NL = System.getProperty("line.separator");
	
	public static int port = 9001;
	public static List<UserInfo> users = new LinkedList<>();
	public static boolean authenticate = true;
	
	
	
	/**
	 * Initialize Cfg by loading the configuration json file.
	 * @param filename configuration file path and name
	 * @return successfully load configuration file or not
	 */
	public static boolean init(String filename){
		file = new File(filename);
		if(!file.isFile() || !file.canRead()){
			return false;
		}
		else{
			String content;
			Scanner sc = null;
			try {
				sc = new Scanner(file);
				content = sc.useDelimiter("\\Z").next();
				json = new JSONObject(content);
				parse(json);
				return true;
			} catch (FileNotFoundException e) {
				System.err.println("Configuration file "+filename+" not loaded.");
				file=null;
				return false;
			} catch (JSONException e) {
				System.err.println("Configuration file "+filename+" has bad format.");
				json = null;
				return false;
			}
			finally {
				if(sc!=null)
					sc.close();
			}
		}
	}
	

	/**
	 * Parse json object to assign vales to each fields.
	 * @param json json object
	 */
	private static void parse(JSONObject json){
		try {port = json.getInt("port");} catch (Exception e) {}
		JSONArray array = null;
		try {
			array = json.getJSONArray("users");
			for(int i=0;i<array.length();i++){
				try{
					JSONObject user = array.getJSONObject(i);
					String username = user.getString("username");
					String password = user.getString("password");
					UserInfo userInfo = new UserInfo(username, password);
					users.add(userInfo);
				}
				catch(Exception e){}
			}
		}catch (Exception e) {}
		try {authenticate = json.getBoolean("authenticate");} catch (Exception e) {}
	}
	
	@Override
	public String toString(){
		return 
			"port: "					+	port			+	NL
		+	"Number of user records: "	+	users.size()	+	NL
		+	"authenticate: "			+	authenticate	+	NL
		;
	}
	public static void main(String[] argv){
		init("server.json");
		System.out.println(new Cfg());
	}
}

class UserInfo{
	public final String username;
	public final String password;
	public UserInfo(String username, String password){
		this.username = username;
		this.password = password;
	}
}
