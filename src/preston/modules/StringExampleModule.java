package preston.modules;

import org.simpleframework.http.Query;

import preston.moduleTree.ModuleTreeLeaf;

public class StringExampleModule extends ModuleTreeLeaf{

	private String value;
	public StringExampleModule(String name,String value) {
		super(name);
		this.value = value;
	}

	@Override
	public Object generateResponse(Query query) {
		return value;
	}

}
