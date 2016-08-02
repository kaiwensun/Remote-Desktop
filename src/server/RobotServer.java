package server;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.io.IOException;

import utils.Event;
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
			Event event;
			while(true){
				try{
					event = (Event)postman.recv();
				}
				catch (IOException e) {
					System.err.println("Fail to send image. Stop thread.");
					break;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					break;
				}
				doEvent(event);
			}
		}
		finally{
			postman.close();
		}
	}
	
	private void doEvent(Event event){
		if(!event.isValid())
			return;
		switch (event.eventType) {
		case Event.KEYPRESS:
			robot.keyPress(event.param1);
			break;
		case Event.KEYRELEASE:
			robot.keyRelease(event.param1);
			break;
		case Event.MOUSEMOVE:
			//System.out.println(event);
			robot.mouseMove(event.param1*sz.width/event.param3, event.param2*sz.height/event.param4);
			break;
		case Event.MOUSEPRESS:
			//System.out.println(event);
			robot.mousePress(InputEvent.getMaskForButton(event.param1));
			break;
		case Event.MOUSERELEASE:
			//System.out.println(event);
			robot.mouseRelease(InputEvent.getMaskForButton(event.param1));
			break;
		case Event.MOUSEWHEEL:
			//System.out.println(event);
			robot.mouseWheel(event.param1);
			break;
		default:
		}
	}
}
