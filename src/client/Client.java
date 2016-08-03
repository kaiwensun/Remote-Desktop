package client;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;

import utils.MyPic;
import utils.Postman;
import utils.SecuString;

public class Client implements Runnable{
	private volatile boolean working = true;
	public static void main(String[] argv) throws IOException{
		Cfg.init("client.json");
		Client client = new Client();
		client.run();
	}

	@Override
	public void run() {
		Postman postman = null;
		try{
			Socket socket=new Socket(Cfg.ip,Cfg.port);
			postman = new Postman(socket);
			if(Cfg.authenticate && !authenticate(postman)){
				System.err.println("Login failed. Program exit.");
				postman.close();
				return;
			}
			VideoFrame vf = new ControlPanel(postman);
			vf.frame.addWindowListener(new WindowAdapter(){
				@Override
				public void windowClosing(WindowEvent e){
					working = false;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					System.exit(0);
				}
			});
			MyPic myPic = null;
			BufferedImage image = null;
			while(working){
				Object obj = postman.recv();
				if(obj instanceof MyPic){
					myPic = (MyPic)postman.recv();
					image = myPic.image;
					vf.videoShow(image);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally{
			if(postman!=null)
				postman.close();
			System.exit(0);
		}
	}
	
	private boolean authenticate(Postman postman){
		SecuString secuStr = new SecuString(Cfg.username,Cfg.password);
		try {
			postman.send(secuStr);
		} catch (IOException e) {
			System.err.println(postman+" fails to send authentication message.");
			return false;
		}
		Object obj = null;
		try {
			obj = postman.recv();
			if(obj!=null && !(obj instanceof SecuString)){
				System.err.println(postman+" fails to receive "+SecuString.class.getSimpleName()+" type reply ("+obj.getClass().getName()+" received).");
				return false;
			}
		} catch (IOException | ClassNotFoundException e) {
			System.err.println(postman+" fails to receive authentication reply.");
			return false;
		}
		if(obj==null){
			System.err.println(postman+" fails to receive authentication reply (null received).");
			return false;
		}
		secuStr = (SecuString)obj;
		String plain = secuStr.decrypt(Cfg.password);
		if(plain.equals("OK"))
			return true;
		else
			return false;
	}
}