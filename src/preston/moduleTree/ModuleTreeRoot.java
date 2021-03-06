package preston.moduleTree;

import org.json.JSONObject;
import org.simpleframework.http.Query;

import preston.moduleTree.exceptions.ModuleNotFoundException;
import preston.moduleTree.exceptions.ModuleNullReturnException;
import preston.moduleTree.exceptions.PathFormatException;

public class ModuleTreeRoot extends ModuleTreeNode{
	
	public ModuleTreeRoot() {
		super("root");
	}
	
	@Override
	public void getResponse(JSONObject jsonObj, String reqPath, Query query) 
			throws ModuleNotFoundException, ModuleNullReturnException{
		if(jsonObj == null ) throw new NullPointerException("JSONObject cannot be null");
		if(reqPath == null ) throw new NullPointerException("Path parameter cannot be null");
		
		
		//if last '/' is missing add it
		String normalizedPath = PathUtils.addFinalSlash(reqPath);
		//System.out.println("@ requestedPath: "+reqPath+"\n@ normalizedPath: "+"/root"+normalizedPath);
		super.getResponse(jsonObj, "/root"+normalizedPath,query);
	}
	
	@Override
	public void addModule(String path, ModuleTreeLeaf leafModule)
			throws PathFormatException {
		if(leafModule == null){throw new NullPointerException("ModuleTreeLeaf cannot be null");}
		if(path == null){throw new NullPointerException("path string cannot be null");}
		String normalizedPath = PathUtils.addFinalSlash(path);
		if(normalizedPath.length()<3) throw new PathFormatException();
		super.addModule(normalizedPath, leafModule);
	}	
}
