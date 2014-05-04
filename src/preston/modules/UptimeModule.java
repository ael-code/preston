package preston.modules;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import preston.moduleTree.ModuleTreeLeaf;

public class UptimeModule extends ModuleTreeLeaf{
	public static final String DEFAULT_PATH ="/proc/uptime";
	private String file;
	
	public UptimeModule(String name) {
		super(name);
		this.file = DEFAULT_PATH;
	}
	public UptimeModule(String name,String file) {
		super(name);
		this.file = file;
	}

	@Override
	public String generateResponse(String options) {
		String result = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			result = line.substring(0, line.indexOf('.'));
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}
	

}
