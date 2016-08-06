package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import utils.Challenger;
import utils.ClientUserInfo;
import utils.Postman;
import utils.SecuString;
import utils.ServerUserInfo;

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
		if(Cfg.postoffice_register){
			server.runPostofficeMode();
		}
		else
			server.runNonePostofficeMode();
	}
	
	
	private void buildConnectionWithClient(Postman postman, boolean allowaction){
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
			//don't deregister until both threads stop.
			try{
				try {
					vst.join();
				} catch (InterruptedException e) {
				}
				try {
					rst.join();
				} catch (InterruptedException e) {
				}
			}
			finally{	
				if(deregisterAtPostoffice()){
					System.out.println("Successfully deregistered at postoffic.");
				}
				else {
					System.out.println("Failed to deregister at postoffic.");
				}
			}
		}
		return;
	}
	
	private void runPostofficeMode(){
		Postman postman = registerAtPostoffice();
		if(postman==null){
			System.out.println("Failed to register at postoffic.");
			return;
		}
		else {
			System.out.println("Successfully registered at postoffic.");
		}
		buildConnectionWithClient(postman, true);
	}
	/**
	 * Run the server in non-postoffice mode. Authenticate user (if required), then start two threads sending
	 * screen image and receiving commands (if allowed).
	 */
	private void runNonePostofficeMode(){
		ServerSocket listener = null;
		try {
			listener = new ServerSocket(port);
			while(true){
				Socket incoming = listener.accept();
				Postman postman = new Postman(incoming);
				boolean allowaction = true;
				if(Cfg.authenticate){
					ClientUserInfo userInfo = authenticate(postman);
					if(userInfo==null){
						postman.close();
						postman = null;
						return;
					}
					allowaction = allowaction && userInfo.allowaction;
				}
				buildConnectionWithClient(postman,allowaction);
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
	private ClientUserInfo authenticate(Postman postman){
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
		for(ClientUserInfo userInfo : Cfg.users.values()){
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
			
			//tell postoffice that I want to register using my identity.
			SecuString string;
			if(Cfg.authenticate){
				string = new SecuString("CTRLEE REG REQ AUTH:"+Cfg.my_name, null);	//controllee register request
			}
			else{
				string = new SecuString("CTRLEE REG REQ NAUT:"+Cfg.my_name, null);	//controllee register request
			}
			postman.send(string);
			
			ServerUserInfo serverUserInfo = new ServerUserInfo(Cfg.my_name, Cfg.my_password, false);
			//receive and process identity challenge from postoffice			
			Challenger.solveChallenge(postman, serverUserInfo);
			Object obj = postman.recv();
			if(!(obj instanceof SecuString))
				return null;
			SecuString replied = (SecuString)obj;
			String repliedstr = replied.decrypt(Cfg.my_password);
			if(!repliedstr.equals("CTRLEE REG ACK:OK")){
				System.err.println("Fail to register at postoffice ("+repliedstr+").");
				return null;
			}
			for(ClientUserInfo clientUserInfo : Cfg.users.values()){
				postman.send(new SecuString(clientUserInfo.toString(), Cfg.my_password));
			}
			postman.send(new SecuString("CLIENT LIST:END", Cfg.my_password));
		} catch (Exception e) {
			return null;
		}
		return postman;
	}
	
	private boolean deregisterAtPostoffice(){
		try {
			Socket socket = new Socket(Cfg.postoffice_address,Cfg.postoffice_port);
			Postman postman = new Postman(socket);
			
			//request to deregister
			SecuString string = new SecuString("CTRLEE DEREG REQ:"+Cfg.my_name, null);	//controllee deregister request
			postman.send(string);
			
			Challenger.solveChallenge(postman, new ServerUserInfo(Cfg.my_name, Cfg.my_password, false));

			//if everything goes correct, the other side of the postman should have been closed by postoffice.
			Object obj = null;
			try{
				obj = postman.recv();
			}
			catch(Exception e){
				//expected exception due to postoffice closed postman as requested.
				return true;
			}
			if(obj instanceof SecuString){
				string = (SecuString)obj;
				System.err.println("Unecpected message from postman:"+string.decrypt("")+" ("+string.decrypt(Cfg.my_password)+")");
			}
			return false;
		} catch (Exception e1) {
			return false;
		}
	}
}


