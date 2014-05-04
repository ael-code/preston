package preston.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

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
		port = 4445;
	}
	public Server(int port){
		this.port = port;
		this.reqHandler = new RequestHandler();
	}
	
	public void addModule(String path, ModuleTreeLeaf module) throws PathFormatException{
		reqHandler.addModule(path, module);
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
	
	
	
	
}	
