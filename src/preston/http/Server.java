package preston.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.json.JSONObject;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import preston.moduleTree.ModuleTreeLeaf;
import preston.moduleTree.exceptions.PathFormatException;

public class Server {
	public static final int DEFAULT_PORT = 4445;
	
	private int port;
	private RequestHandler reqHandler;
	
	public Server(){
		this(DEFAULT_PORT);
	}
	public Server(int port){
		this.port = port;
		this.reqHandler = new RequestHandler();
	}
	
	public void start(){
		org.simpleframework.transport.Server server;
		try {
			server = (org.simpleframework.transport.Server) new ContainerServer(reqHandler);
			@SuppressWarnings("resource")
			Connection connection = new SocketConnection(server);
		    SocketAddress address = new InetSocketAddress(port);
		    connection.connect(address);
		    System.out.println("Server runnung on port: "+port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addModule(String path, ModuleTreeLeaf module) throws PathFormatException{
		reqHandler.addModule(path, module);
	}
	public JSONObject getTreeView(){
		return reqHandler.getTreeView();
	}
}	
