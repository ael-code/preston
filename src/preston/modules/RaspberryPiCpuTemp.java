package preston.modules;

public class RaspberryPiCpuTemp extends SingleLineModule{
	private static final String PATH = "/sys/class/thermal/thermal_zone0/temp";

	public RaspberryPiCpuTemp(String name){
		super(name,PATH);
	}

}
