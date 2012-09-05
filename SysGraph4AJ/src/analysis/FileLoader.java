package analysis;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class FileLoader{

	private static ClassLoader loader = null;

	public static synchronized ClassLoader getClassLoader(){
		if(loader==null){
			loader = ClassLoader.getSystemClassLoader();
		}
		return loader;
	}

	public static synchronized void setNewClassPath(File f){
		if(f!=null){
			if(!f.isDirectory()){
				f=f.getParentFile();
			}
			URL[] allLocations = new URL[0];
			try {
				allLocations = new URL[]{f.toURI().toURL()};
				loader = URLClassLoader.newInstance(allLocations, ClassLoader.getSystemClassLoader());
				if(loader == null){
					throw new Exception("ClassLoader is null");
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public static synchronized Class<?> forName(String fully) throws ClassNotFoundException{
		return getClassLoader().loadClass(fully);
	}

	public static void add(File f){
		setNewClassPath(f);
	}

}
