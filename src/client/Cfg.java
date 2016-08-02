package client;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
	
	public static String ip = "127.0.0.1";
	public static int port = 9001;
	public static int frame_height = 480;
	public static int frame_width = 720;
	
	
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
		try {ip = json.getString("ip");} catch (Exception e) {}
		try {port = json.getInt("port");} catch (Exception e) {}
		try {frame_height = json.getInt("frame height");} catch (Exception e) {}
		try {frame_width = json.getInt("frame width");} catch (Exception e) {}
		
	}
	
	@Override
	public String toString(){
		return 
			"ip: "			+	ip				+	NL
		+	"port: "		+	port			+	NL
		+	"frame_height: "+	frame_height	+	NL
		+	"frame_width: "	+	frame_width		+	NL
		;
	}
	public static void main(String[] argv){
		init("conf.json");
		System.out.println(new Cfg());
	}
}
