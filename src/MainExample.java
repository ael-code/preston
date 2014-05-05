import preston.http.Server;
import preston.moduleTree.exceptions.PathFormatException;
import preston.modules.QueryExampleModule;
import preston.modules.StringExampleModule;


public class MainExample{
	public static void main(String[] args) throws PathFormatException{
		
		Server server = new Server(4445);
		
		StringExampleModule module1 = new StringExampleModule("module1","value1");
		server.addModule("/folder1/folder1.1/", module1);
		
		StringExampleModule module2 = new StringExampleModule("module2","value2");
		server.addModule("/folder1/folder1.2/", module2);
		
		StringExampleModule module3 = new StringExampleModule("module3","value3");
		server.addModule("/folder2/folder2.1/", module3);
		
		server.addModule("/folder3/folder3.1/", new QueryExampleModule("queryExample"));
		
		System.out.println("Installed Modules\n---------\n"+server.getTreeView().toString(3)+"\n---------\n");
		
		server.start();
	}
}
