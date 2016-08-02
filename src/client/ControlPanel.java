package client;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;

import utils.Event;
import utils.Postman;

public class ControlPanel extends VideoFrame{
	public ControlPanel(Postman postman) {
		super();
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				System.out.println("MOUSE at "+e.getPoint());
				Event event = new Event(Event.MOUSEMOVE, e.getX(),e.getY(),Cfg.frame_width,Cfg.frame_height);
				try {
					postman.send(event);
				} catch (IOException e1) {
					System.err.println("Fail to send Event "+event);
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e){
				Event event = new Event(Event.MOUSEPRESS,e.getButton());
				try {
					postman.send(event);
				} catch (IOException e1) {
					System.err.println("Fail to send Event "+event);
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e){
				Event event = new Event(Event.MOUSERELEASE,e.getButton());
				try {
					postman.send(event);
				} catch (IOException e1) {
					System.err.println("Fail to send Event "+event);
				}
			}
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				Event event = new Event(Event.MOUSEWHEEL, e.getWheelRotation());
				try {
					postman.send(event);
				} catch (IOException e1) {
					System.err.println("Fail to send Event "+event);
				}
            }
			
		};
		 
		super.label.addMouseListener(mouseAdapter);
		super.label.addMouseMotionListener(mouseAdapter);
		super.label.addMouseWheelListener(mouseAdapter);
	}

}
