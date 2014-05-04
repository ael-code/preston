package preston.moduleTree;

public class PathUtils {
	
	/**
	 * @param path to analyze
	 * @return the first node of this path
	 */
	public static String getRoot(String path){
		
		if(path.charAt(0) != '/') return null;
		int len = path.length();
		for (int i = 1; i < len; i++) {
			if(path.charAt(i) == '/'){
				//System.out.println("getRoot() [ "+path+" ] result [ "+path.substring(1,i)+" ]");
				return path.substring(1,i);			
			}
		}
		return null;
	}
	
	public static boolean isLast(String path){
		if(path.charAt(0) != '/') return false;
		//System.out.println("isLast() [ "+path+" ] result [ "+( path.indexOf('/', 1) == path.length()-1)+" ]");
		return ( path.indexOf('/', 1) == path.length()-1);
	}

	public static String trimRoot(String path){
		if(path.charAt(0) != '/') return null;
		//System.out.println("trimRoot() [ "+path+" ] to [ "+path.substring(path.indexOf('/', 1))+" ]");
		return path.substring(path.indexOf('/', 1));
	}

	public static String addFinalSlash(String reqPath) {
		if(reqPath.charAt(reqPath.length()-1) != '/')
			return reqPath + '/';
		return reqPath;
	}
}
