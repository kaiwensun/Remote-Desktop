package utils;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

/**
 * A serializable image sent from server to client.
 * @author Kaiwen Sun
 *
 */
public class MyPic extends Message implements Serializable {
 	private static final long serialVersionUID = -3381884330589371280L;
	public transient BufferedImage image;

	public MyPic(BufferedImage image){
		this.image = image;
	}
	/**
	 * Serialize to write out the object
	 * @param out output stream
	 * @throws IOException fail to serialize
	 */
    private void writeObject(ObjectOutputStream out) throws IOException {
    	out.defaultWriteObject();
        ImageIO.write(image, "png", out); // png is lossless
    }

    /**
	 * Serialize to read in the object
	 * @param in input stream
	 * @throws IOException fail to serialize
	 * @throws ClassNotFoundException fail to serialize
	 */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        image = ImageIO.read(in);
    }
}