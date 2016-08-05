package postoffice;

import utils.Postman;

public class PostmanPair implements Runnable{
	protected Postman controller;
	protected Postman controllee;
	private Thread thread;
	private static volatile Integer COUNT = 0;
	private int ID;
	public PostmanPair(Postman controller, Postman controllee){
		synchronized (COUNT) {
			COUNT++;
			ID = COUNT;
		}
		this.controller = controller;
		this.controllee = controllee;
	}
	
	public void stopService(){
		if(thread!=null)
			thread.interrupt();
	}
	public boolean setThread(Thread thread){
		if(thread!=null)
			return false;
		this.thread = thread;
		return true;
	}
	@Override
	public String toString(){
		return PostmanPair.class.getSimpleName()+" "+ID+" (controller "+controller+", controllee "+controllee;
	}

	@Override
	public void run() {
		Thread c2s = new Thread(){
			@Override
			public void run(){
				try {
					while(true){
						Object obj = controller.recv();
						controllee.send(obj);
					}
				} catch (Exception e) {
					System.err.println("Controller "+controller+" ==> controllee "+controllee+" connection fails.");
				}
				finally{
					System.out.println("Controller "+controller+" ==> controllee "+controllee+" connection stops.");
					try{controller.close();}catch(Exception e){}
					try{controllee.close();}catch(Exception e){}
				}
			}
		};
		Thread s2c = new Thread(){
			@Override
			public void run(){
				try {
					while(true){
						Object obj = controllee.recv();
						controller.send(obj);
					}
				} catch (Exception e) {
					System.err.println("Controllee "+controllee+" ==> controller "+controller+" connection fails.");
				}
				finally{
					System.out.println("Controllee "+controllee+" ==> controller "+controller+" connection stops.");
					try{controller.close();}catch(Exception e){}
					try{controllee.close();}catch(Exception e){}
				}
			}
		};
		s2c.start();
		c2s.start();
	}
}
