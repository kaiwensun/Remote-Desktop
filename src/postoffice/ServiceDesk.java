package postoffice;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import utils.Postman;

public class ServiceDesk {
	private final Postman server;
	private final List<PostmanPair> clients = new LinkedList<>();
	public ServiceDesk(Postman server){
		this.server = server;
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
}
