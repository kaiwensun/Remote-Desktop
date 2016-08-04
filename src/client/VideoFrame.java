package client;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Video displayer.
 * @author Kaiwen Sun
 *
 */
public class VideoFrame{
	protected JFrame frame;
	protected JLabel label;
	
	/**
	 * Constructor. create a JFrame and JLabel to display video.
	 */
	public VideoFrame() {
		frame = new JFrame();
		label = new JLabel();
		frame.add(label,BorderLayout.CENTER);
		
		frame.setVisible(false);
	}
	
	/**
	 * Show video based on given images. video size is defined in Cfg class.
	 * @param img a BufferedImage frame of video.
	 */
	public void videoShow(BufferedImage img){
		try{
			BufferedImage bufferedImage = resize(img, Cfg.frame_width, Cfg.frame_height);
			label.setIcon(new ImageIcon(bufferedImage));
			if(!frame.isVisible()){
				frame.pack();
				frame.setVisible(true);
			}
		}
		catch(Exception e){
			System.err.println("Unable to video img.");
		}
	}
	
	/** 
	 * Resize image. 
	 * @param img image
	 * @param newW new image width
	 * @param newH new image height
	 * @return resized image
	 */
	private static BufferedImage resize(BufferedImage img, int newW, int newH) {  
	    int w = img.getWidth();  
	    int h = img.getHeight();  
	    BufferedImage dimg = new BufferedImage(newW, newH, img.getType());  
	    Graphics2D g = dimg.createGraphics();  
	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	    RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
	    g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);  
	    g.dispose();
	    return dimg;  
	}
}
