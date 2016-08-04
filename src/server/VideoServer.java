package server;

import java.awt.image.BufferedImage;

import utils.MyPic;
import utils.Postman;

/**
 * Video server sending images to user.
 * @author Kaiwen Sun
 *
 */
public class VideoServer implements Runnable{

	private Postman postman;
	private static volatile Integer instanceCount = 0;
	private static Thread captureThread;
	private static BufferedImage image = new BufferedImage(1,1,BufferedImage.TYPE_BYTE_BINARY);;
	/**
	 * Constructor.
	 * @param postman postman used to communicate with user
	 */
	VideoServer(Postman postman){
		this.postman = postman;
	}
	
	/**
	 * Run video server. Continuously capture screen image and send to client.
	 */
	@Override
	public void run() {
		synchronized (instanceCount) {
			instanceCount++;
			if(instanceCount==1){
				captureThread = new Thread(){
					@Override
					public void run(){
						try{
							ImageCapture capture = new ScreenCapture();	
							while(instanceCount>0){
								image = capture.capture();
								Thread.sleep(50);
							}
						} catch (InterruptedException e) {
						}
					}
				};
				System.out.println("Start capturing screen.");
				captureThread.start();
			}
		}
		try {
			while(true){
				if(image==null){
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						break;
					}
					continue;
				}
				MyPic pic = new MyPic();
				pic.image = image;
				try{
					postman.send(pic);
				}
				catch (Exception e) {
					System.err.println("Fail to send image. Stop VideoServer thread.");
					break;
				}
			}
		}
		finally{
			synchronized (instanceCount) {
				instanceCount--;
				if(instanceCount==0){
					captureThread.interrupt();
					System.out.println("No client conntcts to this server. Stop capturing screen.");
				}
			}
			postman.close();
		}
	}	
}
