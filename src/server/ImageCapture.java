package server;

import java.awt.image.BufferedImage;

/**
 * Provide captured image.
 * @author Kaiwen Sun
 *
 */
public interface ImageCapture {
	/**
	 * Provide captured BufferedImage.
	 * @return BufferedImage
	 */
	public BufferedImage capture();
}
