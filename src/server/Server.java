package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import utils.Postman;

public class Server {
	private int port = 9001;
	public static void main(String[] argv){
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
}


