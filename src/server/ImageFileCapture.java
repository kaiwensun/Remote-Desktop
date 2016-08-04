package server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Image file loader.
 * @author Kaiwen Sun
 *
 */
public class ImageFileCapture implements ImageCapture {

	private File file;
	
	/**
	 * Constructor.
	 * @param filename name of the image file.
	 */
	public ImageFileCapture(String filename){
		file = new File(filename);
	}
	
	/**
	 * Load image file.
	 * @return BufferedImage loaded from the file.
	 */
	@Override
	public BufferedImage capture() {
		try {
			return ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}