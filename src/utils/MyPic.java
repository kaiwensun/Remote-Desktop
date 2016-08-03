package utils;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

public class MyPic extends Message implements Serializable {
 	private static final long serialVersionUID = -3381884330589371280L;
	public transient BufferedImage image;

    private void writeObject(ObjectOutputStream out) throws IOException {
    	out.defaultWriteObject();
        ImageIO.write(image, "png", out); // png is lossless
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        image = ImageIO.read(in);
    }
}