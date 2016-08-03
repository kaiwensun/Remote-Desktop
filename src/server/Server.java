package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import utils.Postman;
import utils.SecuString;

public class Server {
	private int port = Cfg.port;
	public static void main(String[] argv){
		Cfg.init("server.json");
		Server server = new Server();
		server.run();
	}
	private void run(){
		ServerSocket listener = null;
		try {
			listener = new ServerSocket(port);
			while(true){
				Socket incoming = listener.accept();
				Postman postman = new Postman(incoming);
				if(Cfg.authenticate){
					if(!authenticate(postman)){
						postman.close();
						postman = null;
						continue;
					}
				}
				Runnable vsr = new VideoServer(postman);
				Thread vst = new Thread(vsr);
				
				Runnable rsr = new RobotServer(postman);
				Thread rst = new Thread(rsr);
				
				vst.start();
				rst.start();
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
	
	private boolean authenticate(Postman postman){
		Object obj = null;
		try {
			obj = postman.recv();
		} catch (ClassNotFoundException | IOException e) {
			System.err.println(postman+" fails to receive authentication message.");
			e.printStackTrace();
		}
		if(!(obj instanceof SecuString))
			return false;
		SecuString secuStr = (SecuString)obj;
		for(UserInfo userInfo : Cfg.users){
			if(userInfo.username.equals(secuStr.decrypt(userInfo.password))){
				try {
					postman.send(new SecuString("OK", userInfo.password));
					return true;
				} catch (IOException e) {
					System.err.println(postman+" fails to reply true user's authentication message.");
					return false;
				}
			}
		}
		Random rg = new Random();
		try {
			postman.send(new SecuString("FAIL", Integer.toHexString(rg.nextInt())));
		} catch (IOException e) {
			System.err.println(postman+" fails to reply false user's authentication message.");
			return false;
		}
		return false;
		
	}
}


