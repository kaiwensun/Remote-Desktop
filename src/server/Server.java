package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import utils.Postman;
import utils.SecuString;

/**
 * Server (controllee) main class.
 * @author Kaiwen Sun
 *
 */
public class Server {
	private int port = Cfg.port;
	
	/**
	 * Server (controllee) main entry.
	 * @param argv not used.
	 */
	public static void main(String[] argv){
		Cfg.init("server.json");
		Server server = new Server();
		server.run();
	}
	
	/**
	 * Run the server. Authenticate user (if required), then start two threads sending
	 * screen image and receiving commands (if allowed).
	 */
	private void run(){
		ServerSocket listener = null;
		try {
			listener = new ServerSocket(port);
			while(true){
				Socket incoming = listener.accept();
				Postman postman = new Postman(incoming);
				
				boolean allowaction = true;
				if(Cfg.authenticate){
					UserInfo userInfo = authenticate(postman);
					if(userInfo==null){
						postman.close();
						postman = null;
						continue;
					}
					allowaction = allowaction && userInfo.allowaction;
				}
				Runnable vsr = new VideoServer(postman);
				Thread vst = new Thread(vsr);
				
				Thread rst = null;
				if(allowaction){
					Runnable rsr = new RobotServer(postman);
					rst = new Thread(rsr);
				}
				vst.start();
				if(allowaction)
					rst.start();
				
				Thread.sleep(15);
			}
		} catch (IOException e1) {
			System.err.println("Can't open port "+port);
			return;
		}
		catch(Throwable throwable){}
		finally{
			if(listener!=null)
				try {
					listener.close();
				} catch (IOException e) {
				}
		}
	}
	
	/**
	 * Authenticate user according to users record in configuration.
	 * @param postman postman used to communicate with user
	 * @return userInfo if user is authenticated. null if fail to authenticate.
	 */
	private UserInfo authenticate(Postman postman){
		Object obj = null;
		try {
			obj = postman.recv();
		} catch (ClassNotFoundException | IOException e) {
			System.err.println(postman+" fails to receive authentication message.");
			e.printStackTrace();
		}
		if(!(obj instanceof SecuString))
			return null;
		SecuString secuStr = (SecuString)obj;
		for(UserInfo userInfo : Cfg.users){
			if(userInfo.username.equals(secuStr.decrypt(userInfo.password))){
				try {
					postman.send(new SecuString("OK", userInfo.password));
					return userInfo;
				} catch (IOException e) {
					System.err.println(postman+" fails to reply true user's authentication message.");
					return null;
				}
			}
		}
		Random rg = new Random();
		try {
			postman.send(new SecuString("FAIL", Integer.toHexString(rg.nextInt())));
		} catch (IOException e) {
			System.err.println(postman+" fails to reply false user's authentication message.");
			return null;
		}
		return null;
	}
}


