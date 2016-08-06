package postoffice;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import utils.Challenger;
import utils.ClientUserInfo;
import utils.Postman;
import utils.SecuString;
import utils.ServerUserInfo;

public class PostOffice{
	
	private HashMap<String,ServiceDesk> serviceDesks = new HashMap<>();
	
	public static void main(String[] argv){
		Cfg.init("postoffice.json");
		PostOffice postoffice = new PostOffice();
		postoffice.run();
	}
	
	private boolean addServiceDesk(String serverName, ServiceDesk server){
		if(serviceDesks.putIfAbsent(serverName, server)==null)
			return true;
		else
			return false;
	}
	
	private boolean addClient(String serverName, Postman client){
		ServiceDesk serviceDesk = serviceDesks.get(serverName);
		if(serviceDesk==null)
			return false;
		/**
		 * TODO: should postoffice authenticate the client? Check serviceDesk.authentication .
		 */
		serviceDesk.addClient(client);
		return true;
	}
	private boolean closeServiceDesk(String serverName){
		ServiceDesk serviceDesk = serviceDesks.get(serverName);
		if(serviceDesk==null)
			return false;
		serviceDesk.closeServiceDesk();
		return true;
	}
	private boolean processRequest(Postman postman){
		try{
			//get non-encrypted request.
			Object obj = postman.recv();
			if(!(obj instanceof SecuString)){
				System.err.println("Unexpected object of type "+obj.getClass().getName());
				return false;
			}
			SecuString secuString = (SecuString)obj;
			String str = secuString.decrypt(null);
			
			
			if(str.startsWith("CTRLEE REG REQ AUTH:") || str.startsWith("CTRLEE REG REQ NAUT:")){
				//if server requests to register at postoffice
				boolean authentication = str.startsWith("CTRLEE REG REQ AUTH:");	//if server requires client authentication
				String CtrleeName = str.substring(20);
				//get server's name
				ServerUserInfo serverUserInfo = Cfg.users.get(CtrleeName);
				if(serverUserInfo==null){
					//the server is not listed in postoffice config file
					postman.send(new SecuString("CTRLEE REG ACK:You are not allowed to connect to me", null));
					return false;
				}
				
				//challenge server using its password.
				if(!Challenger.challenge(postman, serverUserInfo)){
					postman.send(new SecuString("CTRLEE REG ACK:You failed tp prove you are whom you claim to be", null));
					return false;
				}
				//authentication succeeded. try to create service desk for the server.
				ServiceDesk serviceDesk = new ServiceDesk(postman, authentication);
				if(addServiceDesk(CtrleeName, serviceDesk)){
					postman.send(new SecuString("CTRLEE REG ACK:OK", serverUserInfo.password));
				}
				else{
					postman.send(new SecuString("CTRLEE REG ACK:You've already registered.", serverUserInfo.password));
				}
				
				//if server requires authentication, add client user info from server to postman.
				if(authentication){
					while(true){
						obj = postman.recv();
						if(!(obj instanceof SecuString))
							return false;
						secuString = (SecuString)obj;
						str = secuString.decrypt(serverUserInfo.password);
						if(str.equals("CLIENT LIST:END"))
							break;
						serviceDesk.addClientUserInfo(new ClientUserInfo(str));
					}
				}
				System.out.println("Server "+serverUserInfo.username+" successfully registered at this postoffice.");
			}
			else if(str.startsWith("CTRLER REG REQ:")){
				String CtrleeName = str.substring(15);
				if(addClient(CtrleeName, postman)){
					postman.send(new SecuString("CTRLER REG ACK:OK", null));
				}
				else{
					postman.send(new SecuString("CTRLEFR REG ACK:Your server is not registered", null));
				}
			}
			else if(str.startsWith("CTRLEE DEREG REQ:")){
				String CtrleeName = str.substring(17);
				ServerUserInfo serverUserInfo = Cfg.users.get(CtrleeName);
				if(serverUserInfo==null){
					postman.send(new SecuString("CTRLEE DEREG ACK:You are not allowed to connect to me",null));
					return false;
				}
				if(!Challenger.challenge(postman, serverUserInfo)){
					return false;
				}
				if(closeServiceDesk(CtrleeName)){
					System.out.println("Server "+serverUserInfo.username+" sussefully deregister at this postoffice.");
					//server postman already closed
				}
				else{
					postman.send(new SecuString("CTRLEE DEREG ACK:You are not registed", null));
				}
			}
			else{
				postman.send(new SecuString("ERR: I can't understand you", null));
			}
		}
		catch(Exception e){
			return false;
		}
		return true;
	}
	
	private void run(){
		ServerSocket listener = null;
		try {
			listener = new ServerSocket(Cfg.port);
			while(true){
				Socket incoming = listener.accept();
				Postman postman = new Postman(incoming);
				Thread thread = new Thread(){
					@Override
					public void run(){
						if(!processRequest(postman)){
							postman.close();
						}
					}
				};
				thread.start();
			}
		} catch (IOException e1) {
			System.err.println("Can't open port "+Cfg.port);
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


