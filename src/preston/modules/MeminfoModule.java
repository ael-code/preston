package preston.modules;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.simpleframework.http.Query;

import preston.moduleTree.ModuleTreeLeaf;

public class MeminfoModule extends ModuleTreeLeaf{
	private static final String[] VALUES = {"MemTotal","MemFree","MemAvailable","SwapTotal","SwapFree"};
	private static final String DEFAULT_PATH = "/proc/meminfo";
	
	private String file;
	private List<String> valuesColl;
	
	public MeminfoModule(String name) {
		this(name,DEFAULT_PATH);
	}
	public MeminfoModule(String name, String file) {
		super(name);
		this.file = file;
		valuesColl = Arrays.asList(VALUES);
	}

	@Override
	public Object generateResponse(Query query) {
		JSONObject result = new JSONObject();
		try {
			BufferedReader br = new BufferedReader( new FileReader(file));
			String line;
			while((line = br.readLine()) != null){
				
				String[] splitted = line.split("(:*\\s+)");
				if(valuesColl.contains(splitted[0]))
					result.put(splitted[0], splitted[1]);
			}
			br.close();		
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
