package postoffice;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import utils.ClientUserInfo;
import utils.MyPic;
import utils.Postman;
import utils.SecuString;
import utils.ServerUserInfo;

public class ServiceDesk {
	protected final Postman server;
	private final ServerUserInfo serverUserInfo;
	private MyPic myPic = new MyPic(new BufferedImage(1,1,BufferedImage.TYPE_BYTE_BINARY)); 
	Thread imageFetcher = new Thread(){
		@Override
		public void run(){
			while(!Thread.interrupted()){
				Object obj = null;
				try {
					obj = server.recv();
				} catch (ClassNotFoundException | IOException e) {
					System.out.println("Fail to recv from server. Close ServiceDesk.");
					closeServiceDesk();
				}
				if(!(obj instanceof MyPic)){
					System.err.println("Unexpected object eceived from server ("+obj.getClass().getName()+")");
				}
				else{
					myPic = (MyPic)obj;
				}
			}
		}
	};
	private final HashMap<String, PostmanPair> clients = new HashMap<>();	//client name ==&gt; (server postman, client postman) pair
	private HashMap<String, ClientUserInfo> clientUserInfos = new HashMap<>();	//client name ==&gt; client user info allowed by the server
	protected final boolean authentication;
	public ServiceDesk(Postman server, boolean authentication, ServerUserInfo serverUserInfo){
		this.server = server;
		this.authentication = authentication;
		this.serverUserInfo = serverUserInfo;
	}
	public int getActiceClientNum(){
		return clients.size();
	}
	public synchronized boolean addClient(ClientUserInfo clientUserInfo, Postman client){
		PostmanPair pair = new PostmanPair(this.server, client, clientUserInfo.username, this);
		synchronized (clients) {
			if(null!=clients.putIfAbsent(clientUserInfo.username,pair))
				return false;
			else{
				if(clients.size()==1){
					startImageFetcher();
				}
				pair.startService();
				return true;
			}
		}
	}
	public MyPic getMyPic(){
		return myPic;
	}
	protected  void startImageFetcher(){
		synchronized (imageFetcher) {
			if(imageFetcher.isAlive())
				return;
			SecuString secuString = new SecuString("IMAGE FETCHER:START", serverUserInfo.password);
			try {
				server.send(secuString);
			} catch (IOException e1) {
			}
			try {
				Object obj = server.recv();
				while(!(obj instanceof SecuString)){
					if(obj instanceof MyPic){
						this.myPic = (MyPic)obj;
					}
					obj = server.recv();
				}
				secuString = (SecuString)obj;
				String string = secuString.decrypt(serverUserInfo.password);
				if(!string.equals("IMAGE FETCHER:OK"))
					return;
			} catch (ClassNotFoundException | IOException e) {
				return;
			}
			imageFetcher.start();
		}
	}
	protected void stopImageFetcher(){
		SecuString secuString = new SecuString("IMAGE FETCHER:STOP", serverUserInfo.password);
		try {
			server.send(secuString);
		} catch (IOException e1) {
		}
		synchronized (imageFetcher) {
			if(imageFetcher.isAlive())
				imageFetcher.interrupt();
		}
	}

	public boolean removeClient(String clientname){
		synchronized (clients) {
			PostmanPair postmanPair = clients.remove(clientname);
			if(postmanPair==null)
				return false;
			if(clients.size()==0){
				stopImageFetcher();
			}
			return true;
		}
	}
	
	public synchronized void closeServiceDesk(){
		for(PostmanPair pair:clients.values()){
			pair.controller.close();
		}
		server.close();
		stopImageFetcher();
	}
	public void addClientUserInfo(ClientUserInfo clientUserInfo){
		clientUserInfos.putIfAbsent(clientUserInfo.username, clientUserInfo);
	}
	public ClientUserInfo getClientUserInfo(String username){
		return clientUserInfos.get(username);
	}
}
