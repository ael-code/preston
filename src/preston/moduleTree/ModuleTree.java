package preston.moduleTree;

import preston.lib.json.JSONObject;
import preston.moduleTree.exceptions.ModuleNotFoundException;
import preston.moduleTree.exceptions.ModuleNullReturnException;

public abstract class ModuleTree{
	protected String name;
	
	public ModuleTree(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public abstract void getResponse(JSONObject jsonObj, String reqPath) 
			throws ModuleNotFoundException, ModuleNullReturnException;
}
