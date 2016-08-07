package server;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.io.IOException;

import utils.MKEvent;
import utils.Postman;
import utils.SecuString;

/**
 * Server receiving mouse and keyboard controlling message.
 * @author Kaiwen Sun
 *
 */
public class RobotServer implements Runnable {

	private Postman postman;
	private Robot robot;
	private Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();
	private Server server;
	
	/**
	 * Constructor.
	 * @param postman postman
	 * @param server 
	 */
	public RobotServer(Postman postman, Server server){
		this.postman = postman;
		this.server = server;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Run the server. Continuously receive mouse and keyboard commands (MKEvent). 
	 */
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()){
				try{
					Object obj = postman.recv();
					if(obj instanceof MKEvent){
						doEvent((MKEvent)obj); 
					}
					else if(Cfg.postoffice_register && (obj instanceof SecuString)){
						String str = ((SecuString)obj).decrypt(Cfg.my_password);
						if (str.startsWith("IMAGE FETCHER:")){
							String action = str.substring(14);
							if(action.equals("START")){
								//start video streaming
								server.startVideoServer();
								SecuString secuString = new SecuString("IMAGE FETCHER:OK", Cfg.my_password);
								postman.send(secuString);
							}
							else if(action.equals("STOP")){
								//stop video streaming
								server.stopVideoServer();
							}
							else{
								System.err.println("Message from "+postman+" is not understood ("+str+")");
							}
						}
						else{
							System.err.println("Message from "+postman+" is not understood ("+str+")");
						}
					}
					else{
						System.err.println("Unexpected object received from "+postman+"("+obj.getClass().getName()+")");
					}
				}
				catch (IOException e) {
					System.err.println("Fail to receive MKEvent. Stop RobotServer thread.");
					break;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					break;
				}
			}
		}
		finally{
			postman.close();
		}
	}
	
	/**
	 * Execute mouse and keyboard command.
	 * @param event mouse or keyboard command.
	 */
	private void doEvent(MKEvent event){
		if(!event.isValid())
			return;
		System.out.println(event);
		switch (event.eventType) {
		case MKEvent.KEYPRESS:
			robot.keyPress(event.param1);
			break;
		case MKEvent.KEYRELEASE:
			robot.keyRelease(event.param1);
			break;
		case MKEvent.MOUSEMOVE:
			robot.mouseMove(event.param1*sz.width/event.param3, event.param2*sz.height/event.param4);
			break;
		case MKEvent.MOUSEPRESS:
			robot.mousePress(InputEvent.getMaskForButton(event.param1));
			break;
		case MKEvent.MOUSERELEASE:
			robot.mouseRelease(InputEvent.getMaskForButton(event.param1));
			break;
		case MKEvent.MOUSEWHEEL:
			robot.mouseWheel(event.param1);
			break;
		default:
		}
	}
}
