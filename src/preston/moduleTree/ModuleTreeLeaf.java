package preston.moduleTree;

import preston.lib.json.JSONObject;
import preston.moduleTree.exceptions.ModuleNotFoundException;
import preston.moduleTree.exceptions.ModuleNullReturnException;

public abstract class ModuleTreeLeaf extends ModuleTree{
	
	public ModuleTreeLeaf(String name) {
		super(name);
	}
	
	@Override
	public void getResponse(JSONObject jsonObj, String reqPath)	throws ModuleNotFoundException, ModuleNullReturnException {
		Object response = generateResponse();
		if(response == null) throw new ModuleNullReturnException();
		jsonObj.accumulate(name, response);
	}
	
	protected abstract Object generateResponse();
}
