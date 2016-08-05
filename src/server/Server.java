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
	
	
	private void buildConnectionWithClient(Postman postman){
		boolean allowaction = true;
		if(Cfg.authenticate){
			UserInfo userInfo = authenticate(postman);
			if(userInfo==null){
				postman.close();
				postman = null;
				return;
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
		if(Cfg.postoffice_register){
			try {
				vst.join();
			} catch (InterruptedException e) {
			}
			try {
				rst.join();
			} catch (InterruptedException e) {
			}
			deregisterAtPostoffice();
		}
		return;
	}
	/**
	 * Run the server. Authenticate user (if required), then start two threads sending
	 * screen image and receiving commands (if allowed).
	 */
	private void run(){
		if(Cfg.postoffice_register){
			Postman postman = registerAtPostoffice();
			if(postman==null){
				return;
			}
			buildConnectionWithClient(postman);
		}
		else{
			ServerSocket listener = null;
			try {
				listener = new ServerSocket(port);
				while(true){
					Socket incoming = listener.accept();
					Postman postman = new Postman(incoming);
					
					buildConnectionWithClient(postman);
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
	
	private Postman registerAtPostoffice(){
		Postman postman = null;
		try {
			Socket socket = new Socket(Cfg.postoffice_address,Cfg.postoffice_port);
			postman = new Postman(socket);
			SecuString string = new SecuString("CTRLEE REG REQ:"+Cfg.my_name, null);	//controllee register request
			postman.send(string);
			Object obj = postman.recv();
			if(!(obj instanceof SecuString))
				return null;
			SecuString replied = (SecuString)obj;
			String repliedstr = replied.decrypt(null);
			if(!repliedstr.equals("CTRLEE REG ACK:OK")){
				System.err.println("Fail to register at postoffice ("+repliedstr+").");
				return null;
			}
		} catch (Exception e) {
			return null;
		}
		return postman;
	}
	
	private boolean deregisterAtPostoffice(){
		try {
			Socket socket = new Socket(Cfg.postoffice_address,Cfg.postoffice_port);
			Postman postman = new Postman(socket);
			SecuString string = new SecuString("CTRLEE DEREG REQ:"+Cfg.my_name, null);	//controllee deregister request
			postman.send(string);
		} catch (Exception e1) {
			return false;
		}
		return true;
	}
}


