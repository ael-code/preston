package preston.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;
import org.simpleframework.http.Query;

import preston.moduleTree.ModuleTreeLeaf;

public class CpuinfoModule extends ModuleTreeLeaf{
	private static final String DEFAULT_PATH = "/proc/cpuinfo";
	private static final String BASENAME_CPU = "cpu";
	//private static final String[] VALUES = {"MemTotal","MemFree","MemAvailable","SwapTotal","SwapFree"};
	
	//private List<String> valuesColl;
	
	private String file;
	
	public CpuinfoModule(String name) {
		this(name,DEFAULT_PATH);
	}
	public CpuinfoModule(String name, String file) {
		super(name);
		if(!(new File(file)).canRead()){throw new RuntimeException("\""+file+"\" doesn't exist or permission error occurred");}
		this.file = file;
		//valuesColl = Arrays.asList(VALUES);
	}
	@Override
	public Object generateResponse(Query query) {
		JSONObject result = new JSONObject();
		try {
			BufferedReader br = new BufferedReader( new FileReader(file));
			String line;
			JSONObject temp = null;
			String key = BASENAME_CPU;
			while((line = br.readLine()) != null){
				if(line.matches("\\s*")) continue;
				String[] splitted = line.split("\\s*: ");
				if(splitted.length<2)continue;
				if(splitted[0].equals("processor")){
					if(temp != null){
						result.accumulate(key, temp);
					}
					key = BASENAME_CPU+"_"+splitted[1];
					temp = new JSONObject();
				}
				if(temp != null) temp.accumulate(splitted[0],splitted[1]);
			}
			if(temp!=null){
				result.accumulate(key, temp);
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