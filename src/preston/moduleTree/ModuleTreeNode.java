package preston.moduleTree;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import preston.lib.json.JSONObject;
import preston.moduleTree.exceptions.ModuleNotFoundException;
import preston.moduleTree.exceptions.ModuleNullReturnException;
import preston.moduleTree.exceptions.PathFormatException;

public class ModuleTreeNode extends ModuleTree{
	private Map<String,ModuleTree> childs;
	
	public ModuleTreeNode(String name){
		super(name);
		childs = new HashMap<String,ModuleTree>(2);
	}
	
	public Collection<ModuleTree> getChilds(){
		return childs.values();
	}
	
	public void getResponse(JSONObject jsonObj, String reqPath,String options) 
			throws ModuleNotFoundException, ModuleNullReturnException, PathFormatException{
		//System.out.println(name);
		
		String firstElementPath = PathUtils.getRoot(reqPath);
		if(firstElementPath == null || ! firstElementPath.equals(name)){ 
			throw new ModuleNotFoundException();
		}
		
		if(PathUtils.isLast(reqPath)){
			JSONObject jsonTemp = new JSONObject();
			
			for (ModuleTree child : childs.values()){
				child.getResponse(jsonTemp, '/'+child.getName()+'/',options);
			}
			jsonObj.accumulate(name, jsonTemp);
			return;
		}
		
		String nextPath = PathUtils.trimRoot(reqPath);
		ModuleTree nextModule = childs.get(PathUtils.getRoot(nextPath));
		if(nextModule == null) throw new ModuleNotFoundException();
		nextModule.getResponse(jsonObj, nextPath,options);
	}
	
	private void addChild(ModuleTree child){
		childs.put(child.getName(), child);
	}
	
	public void addModule(String path, ModuleTreeLeaf leafModule) throws PathFormatException{
		//System.out.println("adding \""+leafModule.getName()+"\" to \""+name+"\" with path \""+path+"\"");
		String rootPath = PathUtils.getRoot(path);
		if(rootPath == null) throw new PathFormatException();
		
		ModuleTreeNode nextModule = (ModuleTreeNode) childs.get(rootPath);
		if(nextModule == null){
			nextModule = new ModuleTreeNode(rootPath);
			childs.put(nextModule.getName(), nextModule);
		}
		
		String nextPath = PathUtils.trimRoot(path);
		if(PathUtils.isLast(path)){
			nextModule.addChild(leafModule);
		}else{
			nextModule.addModule(nextPath, leafModule);
		}
	}
	
}
