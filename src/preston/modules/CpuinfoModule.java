package preston.modules;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import preston.lib.json.JSONObject;
import preston.moduleTree.ModuleTreeLeaf;

public class CpuinfoModule extends ModuleTreeLeaf{
	private static final String DEFAULT_PATH = "/proc/cpuinfo";
	
	private String file;
	
	public CpuinfoModule(String name) {
		super(name);
		this.file = DEFAULT_PATH;
	}
	public CpuinfoModule(String name, String file) {
		super(name);
		this.file = file;
	}
	@Override
	public Object generateResponse(String options) {
		JSONObject result = new JSONObject();
		try {
			BufferedReader br = new BufferedReader( new FileReader(file));
			String line;
			while((line = br.readLine()) != null){
				String[] splitted = line.split("\\s+", 6);			
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
