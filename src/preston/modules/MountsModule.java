package preston.modules;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;
import org.simpleframework.http.Query;

import preston.moduleTree.ModuleTreeLeaf;

public class MountsModule extends ModuleTreeLeaf {
	/* Device MounPoint FileSystem Options Dump Pass */
	
	private static final String DEFAULT_PATH = "/proc/mounts";
	
	private String file;
	
	public MountsModule(String name) {
		this(name,DEFAULT_PATH);
	}
	public MountsModule(String name, String file) {
		super(name);
		this.file = file;
	}
	@Override
	public Object generateResponse(Query query) {
		JSONObject result = new JSONObject();
		try {
			BufferedReader br = new BufferedReader( new FileReader(file));
			String line;
			while((line = br.readLine()) != null){
				if(line.charAt(0) != '/') continue;
				String[] splitted = line.split("\\s+", 6);
				for (int i = 1; i < splitted.length; i++) {
					result.accumulate(splitted[0], splitted[i]);
				}			
			}
			br.close();		
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}
}
