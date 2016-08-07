package postoffice;

import utils.Postman;

public class PostmanPair implements Runnable{
	protected final Postman controller;
	protected final Postman controllee;
	public final String clientName;
	private ServiceDesk serviceDesk;
	private Thread c2s;
	private Thread s2c;
	private static volatile Integer COUNT = 0;
	private int ID;
	public PostmanPair(Postman controller, Postman controllee, String clientName, ServiceDesk serviceDesk){
		synchronized (COUNT) {
			COUNT++;
			ID = COUNT;
		}
		this.controller = controller;
		this.controllee = controllee;
		this.clientName = clientName;
		this.serviceDesk = serviceDesk;
	}
	public void startService(){
		if(this.serviceDesk.getActiceClientNum()==1){
			this.serviceDesk.startImageFetcher();
		}
		Thread thread = new Thread(this);
		thread.start();
	}
	public void stopService(){
		if(c2s!=null)
			c2s.interrupt();
		if(s2c!=null)
			s2c.interrupt();
		new Thread(){
			@Override
			public void run(){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
				if(serviceDesk.getActiceClientNum()==0){
					serviceDesk.stopImageFetcher();
				}
			}
		}.start();
	}
	
	@Override
	public String toString(){
		return PostmanPair.class.getSimpleName()+" "+ID+" (controller "+controller+", controllee "+controllee;
	}

	@Override
	public void run() {
		c2s = new Thread(){
			@Override
			public void run(){
				try {
					while(!Thread.interrupted()){
						Object obj = null;
						try{
							obj = controller.recv();
						}
						catch (Exception e) {
							System.err.println("Controller "+controller+" ==> controllee "+controllee+" connection fails.");
							break;
						}
						controllee.send(obj);
					}
				} catch (Exception e) {
					System.err.println("Controller "+controller+" ==> controllee "+controllee+" connection fails.");
				}
				finally{
					System.out.println("Controller "+controller+" ==> controllee "+controllee+" connection stops.");
					try{controller.close();}catch(Exception e){}
					serviceDesk.removeClient(clientName);
				}
			}
		};
		s2c = new Thread(){
			@Override
			public void run(){
				try {
					while(!Thread.interrupted()){
						Object obj = null;
						try{
							obj = controllee.recv();
						}
						catch (Exception e) {
							System.err.println("Controllee "+controllee+" ==> controller "+controller+" connection fails.");
							break;
						}
						controller.send(obj);
					}
				} catch (Exception e) {
					System.err.println("Controllee "+controllee+" ==> controller "+controller+" connection fails.");
				}
				finally{
					System.out.println("Controllee "+controllee+" ==> controller "+controller+" connection stops.");
					try{controller.close();}catch(Exception e){}
					serviceDesk.removeClient(clientName);
				}
			}
		};
		s2c.start();
		c2s.start();
	}
}
