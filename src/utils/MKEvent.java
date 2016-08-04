package utils;

import java.io.Serializable;

/**
 * Mouse and Keyboard event message. Sent from client to server.
 * @author Kaiwen Sun (Ëï¿¬ÎÄ)
 *
 */
public class MKEvent extends Message implements Serializable {
	
	private static final long serialVersionUID = 1579499634959300347L;
	private static final String TAG = MKEvent.class.getSimpleName();
	
	public transient static final int MOUSEPRESS = 0;
	public transient static final int MOUSERELEASE = 1;
	public transient static final int MOUSEMOVE = 2;
	public transient static final int MOUSEWHEEL = 3;
	public transient static final int KEYPRESS = 4;
	public transient static final int KEYRELEASE = 5;
	
	public final int eventType;
	public final int param1;
	public final int param2;
	public final int param3;		//width of controller's screen in pixels
	public final int param4;		//height of controller's screen in pixels
	
	/**
	 * Constructor.
	 * @param eventType type of mouse and keyboard event. Should be one of 
	 * MOUSEPRESS = 0, MOUSERELEASE = 1, MOUSEWHEEL = 3, KEYPRESS = 4, KEYRELEASE = 5.
	 * @param param parameter of event.
	 * @see #isValid()
	 */
	public MKEvent(int eventType, int param){
		this.eventType = eventType;
		this.param1 = param;
		this.param2 = -1;
		this.param3 = -1;
		this.param4 = -1;
	}
	
	/**
	 * Construcor.
	 * @param evenType type of mouse and keyboard event. Should be MOUSEMOVE = 2.
	 * @param param1 parameter 1 of event.
	 * @param param2 parameter 2 of event.
	 * @param param3 parameter 3 of event.
	 * @param param4 parameter 4 of event.
	 * @see #isValid()
	 */
	public MKEvent (int evenType, int param1, int param2, int param3, int param4){
		this.eventType = evenType;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
		this.param4 = param4;
	}
	
	/**
	 * Determine if the MKEvent is understandable.
	 * @return true if valid. false otherwise.
	 */
	public boolean isValid(){
		switch (eventType) {
		case MKEvent.KEYPRESS:
			return param1>=0 && param2==-1 && param3==-1 && param4==-1;
		case MKEvent.KEYRELEASE:
			return param1>=0 && param2==-1 && param3==-1 && param4==-1;
		case MKEvent.MOUSEMOVE:
			return param1>=0 && param2>=0 && param3>=0 && param4>=0;
		case MKEvent.MOUSEPRESS:
			return param1>=0 && param2==-1 && param3==-1 && param4==-1;
		case MKEvent.MOUSERELEASE:
			return param1>=0 && param2==-1 && param3==-1 && param4==-1;
		case MKEvent.MOUSEWHEEL:
			return param1>=0 && param2==-1 && param3==-1 && param4==-1;
		default:
			return false;
		}
	}
	
	/**
	 * Convert the type and parameters of MKEvent to a string.
	 */
	@Override
	public String toString(){
		String type = null;
		switch (eventType) {
		case MKEvent.KEYPRESS:
			type = "KEYPRESS";break;
		case MKEvent.KEYRELEASE:
			type = "KEYRELEASE";break;
		case MKEvent.MOUSEMOVE:
			type = "MOUSEMOVE";break;
		case MKEvent.MOUSEPRESS:
			type = "MOUSEPRESS";break;
		case MKEvent.MOUSERELEASE:
			type = "MOUSERELEASE";break;
		case MKEvent.MOUSEWHEEL:
			type = "MOUSEWHEEL";break;
		default:
			type = "UNKNOWN";
		}
		return TAG+" "+type+","+param1+","+param2+","+param3+","+param4;
	}
}
