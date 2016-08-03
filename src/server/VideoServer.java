package server;

import java.awt.image.BufferedImage;

import utils.MyPic;
import utils.Postman;

public class VideoServer implements Runnable{

	private Postman postman;
	
	
	VideoServer(Postman postman){
		this.postman = postman;
	}
	@Override
	public void run() {
		//ImageCapture capture = new ImageFileCapture();
		ImageCapture capture = new ScreenCapture();
		try {
			while(true){
				BufferedImage image = capture.capture();
				MyPic pic = new MyPic();
				pic.image = image;
				try{
					postman.send(pic);
					image = null;
					pic.image = null;
					pic = null;
				}
				catch (Exception e) {
					System.err.println("Fail to send image. Stop VideoServer thread.");
					break;
				}
				Thread.sleep(15);
			}
		} catch (InterruptedException e) {
		}
		finally{
			postman.close();
		}
		
	}	
}
