package postoffice;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import utils.Postman;
import utils.SecuString;

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
			Object obj = postman.recv();
			if(!(obj instanceof SecuString)){
				System.err.println("Unexpected object of type "+obj.getClass().getName());
				return false;
			}
			SecuString secuString = (SecuString)obj;
			String str = secuString.decrypt(null);
			if(str.startsWith("CTRLEE REG REQ:")){
				String CtrleeName = str.substring(15);
				ServiceDesk serviceDesk = new ServiceDesk(postman);
				if(addServiceDesk(CtrleeName, serviceDesk)){
					postman.send(new SecuString("CTRLEE REG ACK:OK", null));
				}
				else{
					postman.send(new SecuString("CTRLEE REG ACK:You've already registered", null));
				}
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
				if(closeServiceDesk(CtrleeName)){
					//server postman already closed
				}
				else{
					postman.send(new SecuString("CTRLEE DEREG ACK:Your service name is not found", null));
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
						processRequest(postman);
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


