package client;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;

import utils.MyPic;
import utils.Postman;

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
			while(working){
				MyPic myPic = (MyPic)postman.recv();
				BufferedImage image = myPic.image;
				vf.videoShow(image);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally{
			if(postman!=null)
				postman.close();
		}
		
	}
}