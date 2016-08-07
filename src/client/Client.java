package client;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;

import utils.Challenger;
import utils.ClientUserInfo;
import utils.MyPic;
import utils.Postman;
import utils.SecuString;

/**
 * Client (controller) side main class.
 * @author Kaiwen Sun
 *
 */
public class Client implements Runnable{
	private volatile boolean working = true;
	
	/**
	 * Client (controller) side main entry to run the client.
	 * @param argv not used
	 */
	public static void main(String[] argv) {
		Cfg.init("client.json");
		Client client = new Client();
		client.run();
	}

	/**
	 * Run client.
	 * Establish socket connection with server via Postman.
	 * Encrypted username and password authentication.
	 * Create a JLabel monitoring user's mouse and keyboard actions on it, and pass actions to server.
	 * Receive live video from server and display it on the JLabel.
	 */
	@Override
	public void run() {
		Postman postman = null;
		try{
			Socket socket=new Socket(Cfg.ip,Cfg.port);
			postman = new Postman(socket);
			
			if(Cfg.postoffice_register){
				if(!registerAtPostoffice(postman)){
					return;
				}
			}
			
			else if(Cfg.authenticate && !authenticate(postman)){
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
					try{
						myPic = (MyPic)postman.recv();
					}
					catch(Exception e){
						break;
					}
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
	
	/**
	 * Login to server according to the Cfg.username and Cfg.password via postman.
	 * @param postman postman
	 * @return true if successfully authenticated; false otherwise.
	 */
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
	

	private boolean registerAtPostoffice(Postman postman){
		try {
			SecuString string = new SecuString("CTRLER REG REQ:"+Cfg.controllee_name+":"+Cfg.username, null);	//controllee register request
			postman.send(string);
			if(!Challenger.solveChallenge(postman, new ClientUserInfo(Cfg.username,Cfg.password,Cfg.authenticate)))
				return false;
			Object obj = postman.recv();
			if(!(obj instanceof SecuString))
				return false;
			SecuString replied = (SecuString)obj;
			String repliedstr = replied.decrypt(null);
			if(!repliedstr.equals("CTRLER REG ACK:OK")){
				System.err.println("Fail to register at postoffice("+repliedstr+").");
				return false;
			}
		} catch (Exception e1) {
			return false;
		}
		return true;
	}
}