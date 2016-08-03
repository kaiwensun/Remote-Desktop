package server;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.io.IOException;

import utils.MKEvent;
import utils.Postman;

public class RobotServer implements Runnable {

	private Postman postman;
	private Robot robot;
	private Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();
	public RobotServer(Postman postman){
		this.postman = postman;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			return;
		}
	}

	@Override
	public void run() {
		try {
			while(true){
				try{
					Object obj = postman.recv();
					if(obj instanceof MKEvent){
						doEvent((MKEvent)obj); 
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
