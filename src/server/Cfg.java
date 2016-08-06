package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.ClientUserInfo;

/**
 * Configuration of server (controllee). Most fields of this class are public and static in order
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
	
	protected static int port = 9001;				//server socket listener port
	protected static HashMap<String,ClientUserInfo> users = new HashMap<>();	//a list of client user information
	protected static boolean authenticate = true;	//whether authenticate user. should be identical to client side Cfg.
	protected static boolean postoffice_register = true;		//whether register at post office.
	protected static String postoffice_address = "127.0.0.1";	//postoffice address
	protected static int postoffice_port = 9000;	//postoffice socket listener port
	protected static String my_name = ""+((new Random()).nextInt());	//
	protected static String my_password = "";							//used when register at postoffice.
	
	
	
	/**
	 * Initialize Cfg by loading the configuration json file.
	 * @param filename configuration file path and name
	 * @return successfully load configuration file or not
	 */
	protected static boolean init(String filename){
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
					boolean allowaction = false;
					try{allowaction = user.getBoolean("allow action");}
					catch(Exception e){}
					ClientUserInfo userInfo = new ClientUserInfo(username, password, allowaction);
					users.put(username,userInfo);
				}
				catch(Exception e){}
			}
		}catch (Exception e) {}
		try {authenticate = json.getBoolean("authenticate");} catch (Exception e) {}
		try {postoffice_register = json.getBoolean("postoffice register");} catch (Exception e) {}
		try {postoffice_address = json.getString("postoffice address");} catch (Exception e) {}
		try {postoffice_port = json.getInt("postoffice port");} catch (Exception e) {}
		try {my_name = json.getString("my name");} catch (Exception e) {}
		try {my_password = json.getString("my password");} catch (Exception e) {}
	}
	
	/**
	 * Convert non-confidential configuration fields to string. 
	 */
	@Override
	public String toString(){
		return 
			"port: "					+	port			+	NL
		+	"Number of user records: "	+	users.size()	+	NL
		+	"authenticate: "			+	authenticate	+	NL
		+	"postoffice address: "		+	postoffice_address		+	NL
		+	"postoffice port: "			+	postoffice_port	+	NL
		+	"my name: "					+	my_name			+	NL
		+	"my password's hashcode: "	+	my_password.hashCode()	+	NL
		;
	}
}
