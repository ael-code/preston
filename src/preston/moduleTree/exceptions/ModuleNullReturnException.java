package preston.moduleTree.exceptions;

@SuppressWarnings("serial")
public class ModuleNullReturnException extends Exception {
	public ModuleNullReturnException() {
		super("Module retuned null pointer");
	}
}
