package preston.moduleTree;

import org.json.JSONObject;
import preston.moduleTree.exceptions.ModuleNotFoundException;
import preston.moduleTree.exceptions.ModuleNullReturnException;
import preston.moduleTree.exceptions.PathFormatException;

public class ModuleTreeRoot extends ModuleTreeNode{
	
	public ModuleTreeRoot() {
		super("root");
	}
	
	@Override
	public void getResponse(JSONObject jsonObj, String reqPath, String options) 
			throws ModuleNotFoundException, ModuleNullReturnException, PathFormatException{
		if(jsonObj == null ) throw new NullPointerException("JSONObject cannot be null");
		if(reqPath == null ) throw new NullPointerException("Path parameter cannot be null");
		//if last '/' is missing add it
		
		String normalizedPath = PathUtils.addFinalSlash(reqPath);
		if(normalizedPath.length()<3) throw new PathFormatException();
		//System.out.println("@ requestedPath: "+reqPath+"\n@ normalizedPath: "+"/root"+normalizedPath);
		super.getResponse(jsonObj, "/root"+normalizedPath,options);
	}
	
	@Override
	public void addModule(String path, ModuleTreeLeaf leafModule)
			throws PathFormatException {
		if(leafModule == null) throw new NullPointerException("ModuleTreeLeaf cannot be null");
		if(path == null) throw new NullPointerException("path string cannot be null");
		String normalizedPath = PathUtils.addFinalSlash(path);
		if(normalizedPath.length()<3) throw new PathFormatException();
		super.addModule(path, leafModule);
	}
	
}
