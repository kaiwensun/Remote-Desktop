package server;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/**
 * Capture screen image.
 * @author Kaiwen Sun
 *
 */
public class ScreenCapture implements ImageCapture {

	private Robot robot;
	private Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
	
	/**
	 * Constructor.
	 */
	public ScreenCapture() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Capture screen image.
	 * @return captured screen image
	 */
	@Override
	public BufferedImage capture() {
		if(robot!=null)
			return robot.createScreenCapture(screenRect);
		else {
			return null;
		}
	}
}
