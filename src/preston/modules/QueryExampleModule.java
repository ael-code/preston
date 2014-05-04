package preston.modules;

import org.json.JSONObject;
import org.simpleframework.http.Query;

import preston.moduleTree.ModuleTreeLeaf;

public class QueryExampleModule extends ModuleTreeLeaf{

	public QueryExampleModule(String name) {
		super(name);
	}

	@Override
	public Object generateResponse(Query query) {
		if(query.isEmpty()) return "no parameters";
		JSONObject result = new JSONObject();
		for (String key : query.keySet()) {
			result.accumulate(key, query.get(key));
		}
		return result;
	}
	
}
