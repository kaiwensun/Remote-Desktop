package utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Postman {
	private Socket socket;
	private ObjectInputStream inStream;
	private ObjectOutputStream outStream;
	
	public Postman(Socket socket) throws IOException{
		this.socket = socket;
		init();
	}
	private void init() throws IOException{
		 outStream = new ObjectOutputStream(socket.getOutputStream());
		 outStream.flush();
		 InputStream inputStream = socket.getInputStream();
		 inStream = new ObjectInputStream(inputStream);
	}

	public Object recv() throws ClassNotFoundException, IOException{
		return inStream.readObject();
		
	}
	public  void send(Object obj) throws IOException{
		outStream.writeObject(obj);
		outStream.flush();
		outStream.reset();
	}
	public void close(){
		if(inStream!=null)
			try {
				inStream.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		if(outStream!=null)
			try {
				outStream.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		if(socket!=null)
			try {
				System.out.println("Postman at "+socket+" closed.");
				socket.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
	}
}
