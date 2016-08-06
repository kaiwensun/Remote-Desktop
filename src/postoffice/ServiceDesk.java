package postoffice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import utils.ClientUserInfo;
import utils.Postman;

public class ServiceDesk {
	protected final Postman server;
	private final List<PostmanPair> clients = new LinkedList<>();
	private HashMap<String, ClientUserInfo> clientUserInfos = new HashMap<>();
	protected final boolean authentication;
	public ServiceDesk(Postman server, boolean authentication){
		this.server = server;
		this.authentication = authentication;
	}
	public synchronized void addClient(Postman client){
		PostmanPair pair = new PostmanPair(this.server, client);
		clients.add(pair);
		Thread thread = new Thread(pair);
		thread.start();
	}
	public synchronized boolean removeClient(Postman client){
		Iterator<PostmanPair> iterator = clients.iterator();
		while(iterator.hasNext()){
			PostmanPair pair = iterator.next();
			if(pair.controllee==client){
				client.close();
				iterator.remove();
				return true;
			}
		}
		return false;
	}
	public void closeServiceDesk(){
		for(PostmanPair pair:clients){
			pair.controller.close();
		}
		server.close();
	}
	public void addClientUserInfo(ClientUserInfo clientUserInfo){
		clientUserInfos.putIfAbsent(clientUserInfo.username, clientUserInfo);
	}
}
