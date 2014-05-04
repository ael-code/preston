package preston.moduleTree;

import org.json.JSONObject;
import preston.moduleTree.exceptions.ModuleNotFoundException;
import preston.moduleTree.exceptions.ModuleNullReturnException;
import preston.moduleTree.exceptions.PathFormatException;

public abstract class ModuleTree{
	protected String name;
	
	public ModuleTree(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public abstract void getResponse(JSONObject jsonObj, String reqPath, String options) 
			throws ModuleNotFoundException, ModuleNullReturnException, PathFormatException;
}
