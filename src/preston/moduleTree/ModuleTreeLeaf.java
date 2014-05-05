package preston.moduleTree;

import org.json.JSONObject;
import org.simpleframework.http.Query;

import preston.moduleTree.exceptions.ModuleNotFoundException;
import preston.moduleTree.exceptions.ModuleNullReturnException;

public abstract class ModuleTreeLeaf extends ModuleTree{
	
	public ModuleTreeLeaf(String name) {
		super(name);
	}
	
	@Override
	public void getResponse(JSONObject jsonObj, String reqPath,Query query)	
			throws ModuleNotFoundException, ModuleNullReturnException{
		Object response = generateResponse(query);
		if(response == null) throw new ModuleNullReturnException();
		jsonObj.accumulate(name, response);
	}
	
	public abstract Object generateResponse(Query query);
}
