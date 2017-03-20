package com.sgck.dtu.analysis.utiils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.sgck.dtu.analysis.read.HandleMessageService;

public class ClassUtils
{

	public static void main(String[] args)
	{
		List<Class> list = getAllClassByInterface(HandleMessageService.class, "com.sgck.dtu.analysis.javastruct.toserver");
		System.out.println(list);
	}

	public static List<Class> getAllClassByInterface(Class c, String packageName)
	{
		List<Class> returnClassList = new ArrayList<Class>();
		if (c.isInterface()) {
			try {
				List<Class> allClass = getClasses(packageName);
				for (int i = 0; i < allClass.size(); i++) {
					if (c.isAssignableFrom(allClass.get(i))) {
						if (!c.equals(allClass.get(i))) {
							returnClassList.add(allClass.get(i));
						}
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return returnClassList;
	}

	public static List<Class> getAllClassByInterface(Class c)
	{
		List<Class> returnClassList = new ArrayList<Class>();

		if (c.isInterface()) {
			String packageName = c.getPackage().getName();
			try {
				List<Class> allClass = getClasses(packageName);
				for (int i = 0; i < allClass.size(); i++) {
					if (c.isAssignableFrom(allClass.get(i))) {
						if (!c.equals(allClass.get(i))) {
							returnClassList.add(allClass.get(i));
						}
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return returnClassList;
	}

	public static List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException
	{

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String path = packageName.replace(".", "/");

		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes;
	}

	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException
	{

		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}

		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert!file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + "." + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

	public static String[] findClassesImplementInterfaceFromJar(String interfaceName, String jarPath) throws ClassNotFoundException, IOException
	{
		URL url = new URL("jar:file:" + jarPath + "!/");
		JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
		JarFile jarFile = jarConnection.getJarFile();
		List fullNames = new ArrayList();
		Class interfaceClass = Class.forName(interfaceName);
		// 遍历
		for (JarEntry e : Collections.list(jarFile.entries())) {
			String n = e.getName();
			if (n.endsWith(".class")) {
				n = n.substring(0, n.length() - 6);
				n = n.replace('/', '.');
				Class currentClass = Class.forName(n);
				if (interfaceClass.isAssignableFrom(currentClass) && false == n.equals(interfaceName)) {
					fullNames.add(n);
				}
			}
		}
		// 返回结果
		return (String[]) fullNames.toArray(new String[fullNames.size()]);
	}
}
