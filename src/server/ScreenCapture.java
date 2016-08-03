package server;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class ScreenCapture implements ImageCapture {

	private Robot robot;
	private Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
	public ScreenCapture() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	@Override
	public BufferedImage capture() {
		if(robot!=null)
			return robot.createScreenCapture(screenRect);
		else {
			return null;
		}
	}
}
