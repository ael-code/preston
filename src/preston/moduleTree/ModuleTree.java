package preston.moduleTree;

import org.json.JSONObject;
import org.simpleframework.http.Query;

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
	
	public abstract void getResponse(JSONObject jsonObj, String reqPath, Query query) 
			throws ModuleNotFoundException, ModuleNullReturnException;
}
