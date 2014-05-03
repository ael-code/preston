package preston.modules;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import preston.json.JSONObject;

import preston.moduleTree.ModuleTreeLeaf;

public class MeminfoModule extends ModuleTreeLeaf{
	String file;
	
	public MeminfoModule(String name, String file) {
		super(name);
		this.file = file;
	}

	@Override
	protected Object generateResponse() {
		JSONObject result = new JSONObject();
		try {
			BufferedReader br = new BufferedReader( new FileReader(file));
			String line;
			while((line = br.readLine()) != null){
				
				String[] splitted = line.split("\\s|.", 3);
				//System.out.println(Arrays.toString(splitted));
				result.accumulate(splitted[0], splitted[1]);
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
