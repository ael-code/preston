package preston.moduleTree;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.simpleframework.http.Query;

import preston.moduleTree.exceptions.ModuleNotFoundException;
import preston.moduleTree.exceptions.ModuleNullReturnException;
import preston.moduleTree.exceptions.PathFormatException;

public class ModuleTreeNode extends ModuleTree{
	public static final String TREE_VIEW_PARAMETER = "treeView";
	protected Map<String,ModuleTree> childs;
	
	public ModuleTreeNode(String name){
		super(name);
		childs = new HashMap<String,ModuleTree>(2);
	}
	
	public Collection<ModuleTree> getChilds(){
		return childs.values();
	}
	
	public void getResponse(JSONObject jsonObj, String reqPath,Query query) 
			throws ModuleNotFoundException, ModuleNullReturnException{
		//System.out.println(name);
		
		String firstElementPath = PathUtils.getRoot(reqPath);
		if(firstElementPath == null || ! firstElementPath.equals(name)){ 
			throw new ModuleNotFoundException();
		}
		
		if(PathUtils.isLast(reqPath)){
			if(query.getBoolean(TREE_VIEW_PARAMETER)){
				this.getTreeView(jsonObj);
			}else {
				JSONObject jsonTemp = new JSONObject();
				
				for (ModuleTree child : childs.values()){
					child.getResponse(jsonTemp, '/'+child.getName()+'/',query);
				}
				jsonObj.accumulate(name, jsonTemp);
			}
			return;
		}
		String nextPath = PathUtils.trimRoot(reqPath);
		ModuleTree nextModule = childs.get(PathUtils.getRoot(nextPath));
		if(nextModule == null) throw new ModuleNotFoundException();
		nextModule.getResponse(jsonObj, nextPath,query);
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

	public void getTreeView(JSONObject jsonObj) {
		JSONObject temp = new JSONObject();
		Boolean empty = true;
		for (ModuleTree module : childs.values()) {
			if(module instanceof ModuleTreeLeaf){
				jsonObj.accumulate(this.name, module.name);
			}else{
				empty = false;
				((ModuleTreeNode)module).getTreeView(temp);
			}
		}
		if(!empty)
			jsonObj.accumulate(name, temp);
	}
}
