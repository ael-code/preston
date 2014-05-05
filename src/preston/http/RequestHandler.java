package preston.http;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;

import org.json.JSONObject;
import org.simpleframework.http.Query;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;

import preston.moduleTree.ModuleTreeLeaf;
import preston.moduleTree.ModuleTreeRoot;
import preston.moduleTree.exceptions.ModuleNotFoundException;
import preston.moduleTree.exceptions.ModuleNullReturnException;
import preston.moduleTree.exceptions.PathFormatException;

public class RequestHandler implements Container{
	private ModuleTreeRoot rootNode;
	
	public RequestHandler() {
		rootNode = new ModuleTreeRoot();
	}
	
	@Override
	public void handle(Request req, Response resp) {
		try {
			PrintStream body = resp.getPrintStream();
			long time = System.currentTimeMillis();
			InetSocketAddress clientAddr = req.getClientAddress();
			System.out.println("IP[ "+clientAddr+" ] "+"req[ "+req.getTarget()+" ]");

			/*general response header*/
			resp.setValue("Server", "Preston_Server");
		    resp.setDate("Date", time);
		    resp.setDate("Last-Modified", time);
		    
		      
		    String reqPath = req.getPath().getPath();
		    Query query = req.getQuery();
		    JSONObject result = new JSONObject();
	    	rootNode.getResponse(result, reqPath, query);
	    	//hide root node
	    	if(result.has("root"))
	    		result = result.getJSONObject("root");
		    
		    body.println(result.toString(3));
		    body.close();
		    
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ModuleNotFoundException e) {
			System.err.println(e.getMessage());
			handleMethodNotAllowed(resp);
			e.printStackTrace();
		} catch (ModuleNullReturnException e) {
			System.err.println(e.getMessage());
			handleInternalServerError(resp);
			e.printStackTrace();
		}
	}
	
	private void handleMethodNotAllowed(Response resp){
		resp.setStatus(Status.METHOD_NOT_ALLOWED);
		resp.setValue("Content-Type", "text/plain");
		PrintStream body;
		try {
			body = resp.getPrintStream();
			body.println("METHOD_NOT_ALLOWED");	
			body.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handleInternalServerError(Response resp){
		resp.setStatus(Status.INTERNAL_SERVER_ERROR);
		resp.setValue("Content-Type", "text/plain");
		PrintStream body;
		try {
			body = resp.getPrintStream();
			body.println("INTERNAL_SERVER_ERROR");	
			body.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void addModule(String path,ModuleTreeLeaf module) throws PathFormatException{
		rootNode.addModule(path, module);
	}
	
	public JSONObject getTreeView() {
		JSONObject result = new JSONObject();
		rootNode.getTreeView(result);
		//hide root node
		if(result.has("root"))
    		result = result.getJSONObject("root");
		return result;
	}
}