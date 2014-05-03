import preston.http.Server;
import preston.moduleTree.exceptions.PathFormatException;
import preston.modules.MeminfoModule;
import preston.modules.SingleLineModule;
import preston.modules.UptimeModule;


public class Main{
	static final int PORT = 4445;
	
	public static void main(String[] args) throws PathFormatException{
		Server server = new Server(PORT);
		
		SingleLineModule tempNode1 = new SingleLineModule("core1","/sys/class/hwmon/hwmon1/device/temp1_input");
		server.addModule("/sensors/cputemp/", tempNode1);
		
		SingleLineModule fan = new SingleLineModule("fan_rpm", "/sys/class/hwmon/hwmon0/device/fan1_input");
		server.addModule("/sensors/", fan);
		
		UptimeModule uptime = new UptimeModule("uptime", "/proc/uptime");
		server.addModule("/sysinfo/", uptime);
		
		MeminfoModule meminfo = new MeminfoModule("meminfo", "/proc/meminfo");
		server.addModule("/sysinfo/", meminfo);
	}
}
