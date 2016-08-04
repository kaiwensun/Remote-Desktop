package client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;

import utils.MKEvent;
import utils.Postman;

/**
 * Mouse and keyboard monitor extending VideoFrame.
 * @author Kaiwen Sun
 *
 */
public class ControlPanel extends VideoFrame{
	
	/**
	 * Constructor. Establish monitor on mouse and keyboard.
	 * @param postman postman
	 */
	public ControlPanel(Postman postman) {
		super();
		super.label.setFocusable(true);
		super.label.setFocusTraversalKeysEnabled(false);
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if(!frame.isActive())
					return;
				MKEvent event = new MKEvent(MKEvent.MOUSEMOVE, e.getX(),e.getY(),Cfg.frame_width,Cfg.frame_height);
				try {
					postman.send(event);
				} catch (IOException e1) {
					System.err.println("Fail to send MKEvent "+event);
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if(!frame.isActive())
					return;
				MKEvent event = new MKEvent(MKEvent.MOUSEMOVE, e.getX(),e.getY(),Cfg.frame_width,Cfg.frame_height);
				try {
					postman.send(event);
				} catch (IOException e1) {
					System.err.println("Fail to send MKEvent "+event);
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e){
				if(!frame.isActive())
					return;
				MKEvent event = new MKEvent(MKEvent.MOUSEPRESS,e.getButton());
				try {
					postman.send(event);
				} catch (IOException e1) {
					System.err.println("Fail to send MKEvent "+event);
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e){
				if(!frame.isActive())
					return;
				MKEvent event = new MKEvent(MKEvent.MOUSERELEASE,e.getButton());
				try {
					postman.send(event);
				} catch (IOException e1) {
					System.err.println("Fail to send MKEvent "+event);
				}
			}
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(!frame.isActive())
					return;
				MKEvent event = new MKEvent(MKEvent.MOUSEWHEEL, e.getWheelRotation());
				try {
					postman.send(event);
				} catch (IOException e1) {
					System.err.println("Fail to send MKEvent "+event);
				}
            }
			
		};
		 
		super.label.addMouseListener(mouseAdapter);
		super.label.addMouseMotionListener(mouseAdapter);
		super.label.addMouseWheelListener(mouseAdapter);
		
		super.label.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				MKEvent event = new MKEvent(MKEvent.KEYPRESS, e.getKeyCode());
				try {
					postman.send(event);
				} catch (IOException e1) {
					System.err.println("Fail to send MKEvent "+event);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				MKEvent event = new MKEvent(MKEvent.KEYRELEASE, e.getKeyCode());
				try {
					postman.send(event);
				} catch (IOException e1) {
					System.err.println("Fail to send MKEvent "+event);
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
        });
	}

}
