package preston.modules;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.simpleframework.http.Query;

import preston.moduleTree.ModuleTreeLeaf;

public class SingleLineModule extends ModuleTreeLeaf{
	private String file;
	
	public SingleLineModule(String name, String file) {
		super(name);
		this.file = file;
	}
	
	@Override
	public String generateResponse(Query query) {
		String temp = null;
		try {
			BufferedReader br = new BufferedReader( new FileReader(file));
			temp = br.readLine();
			br.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return temp;
	}
	
}
