import preston.http.Server;
import preston.moduleTree.exceptions.PathFormatException;
import preston.modules.MeminfoModule;
import preston.modules.MountsModule;
import preston.modules.UptimeModule;


public class MainExample{
	public static void main(String[] args) throws PathFormatException{
		Server server = new Server();
		
		UptimeModule uptime = new UptimeModule("uptime");
		server.addModule("/data/sysinfo/", uptime);
		
		MeminfoModule meminfo = new MeminfoModule("meminfo");
		server.addModule("/data/sysinfo/", meminfo);
		
		MountsModule mounts = new MountsModule("mounts");
		server.addModule("/data/sysinfo/", mounts);
		
		server.start();		
	}
}
