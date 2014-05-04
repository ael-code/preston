package preston.moduleTree;

import org.json.JSONObject;
import preston.moduleTree.exceptions.ModuleNotFoundException;
import preston.moduleTree.exceptions.ModuleNullReturnException;
import preston.moduleTree.exceptions.PathFormatException;

public abstract class ModuleTreeLeaf extends ModuleTree{
	
	public ModuleTreeLeaf(String name) {
		super(name);
	}
	
	@Override
	public void getResponse(JSONObject jsonObj, String reqPath,String options)	
			throws ModuleNotFoundException, ModuleNullReturnException, PathFormatException {
		Object response = generateResponse(options);
		if(response == null) throw new ModuleNullReturnException();
		jsonObj.accumulate(name, response);
	}
	
	public abstract Object generateResponse(String options);
}
