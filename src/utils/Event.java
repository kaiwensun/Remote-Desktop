package utils;

import java.io.Serializable;

public class Event implements Serializable {
	
	private static final long serialVersionUID = 1579499634959300347L;
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
	public Event(int eventType, int param){
		this.eventType = eventType;
		this.param1 = param;
		this.param2 = -1;
		this.param3 = -1;
		this.param4 = -1;
	}
	public Event (int evenType, int param1, int param2){
		this.eventType = evenType;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = -1;
		this.param4 = -1;
	}
	public Event (int evenType, int param1, int param2, int param3, int param4){
		this.eventType = evenType;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
		this.param4 = param4;
	}
	public boolean isValid(){
		switch (eventType) {
		case Event.KEYPRESS:
			return param1>=0 && param2==-1 && param3==-1 && param4==-1;
		case Event.KEYRELEASE:
			return param1>=0 && param2==-1 && param3==-1 && param4==-1;
		case Event.MOUSEMOVE:
			return param1>=0 && param2>=0 && param3>=0 && param4>=0;
		case Event.MOUSEPRESS:
			return param1>=0 && param2==-1 && param3==-1 && param4==-1;
		case Event.MOUSERELEASE:
			return param1>=0 && param2==-1 && param3==-1 && param4==-1;
		case Event.MOUSEWHEEL:
			return param1>=0 && param2==-1 && param3==-1 && param4==-1;
		default:
			return false;
		}
	}
	
	@Override
	public String toString(){
		String type = null;
		switch (eventType) {
		case Event.KEYPRESS:
			type = "KEYPRESS";break;
		case Event.KEYRELEASE:
			type = "KEYRELEASE";break;
		case Event.MOUSEMOVE:
			type = "MOUSEMOVE";break;
		case Event.MOUSEPRESS:
			type = "MOUSEPRESS";break;
		case Event.MOUSERELEASE:
			type = "MOUSERELEASE";break;
		case Event.MOUSEWHEEL:
			type = "MOUSEWHEEL";break;
		default:
			type = "UNKNOWN";
		}
		return type+","+param1+","+param2+","+param3+","+param4;
	}
}
