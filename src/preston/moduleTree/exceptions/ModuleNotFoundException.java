package preston.moduleTree.exceptions;

@SuppressWarnings("serial")
public class ModuleNotFoundException extends Exception {
	public ModuleNotFoundException() {
		super("Module requested was not found");
	}
}
