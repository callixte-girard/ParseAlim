package myClasses;

import java.io.*;

public class SessionManage {
	
	// paths by default
	private static String path_main = "C:/Users/C/Dropbox/Code/Java/Saves/" ;
	private static String path_save ;
	
	
	public static void setSavePath(String path)
	{
		path_save = path ;
		
		File file = new File(path_main + path_save);
		
		if (!file.exists())
		{
			file.mkdirs();
			
			System.out.println(">>> folder [" + path + "] created successfully.");
		}
	}
	
	
	public static void objectSave(String filename, Object to_save) throws Exception
	{
		System.out.println(">>> Saving to [" + filename + "] ... Please wait");
		
		FileOutputStream fos = new FileOutputStream(path_main + path_save + filename);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(to_save);
		oos.close();
		
		System.out.println(">>> Saved [" + filename + "] successfully :)");
		System.out.println(Disp.htag);

	}
	
	public static Object objectLoad(String filename, boolean showFullSavePath) throws Exception
	{
		if (showFullSavePath)
		{
			System.out.println("Full Save Path : " + path_main + path_save + filename);
		}

		System.out.println(">>> Loading [" + filename + "] ... Please wait");
		
		FileInputStream fis = new FileInputStream(path_main + path_save + filename);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object obj = (Object) ois.readObject();
		ois.close();
		
		System.out.println(">>> Loading [" + filename + "] complete. :)");
		System.out.println(Disp.htag);

		return obj ;
		
	}
	
	public static void dispSavePath()
	{
		System.out.println(path_main + path_save);
		System.out.println(Disp.htag);
	}

	public static void dispEmptyObjectMessage(String objectName)
	{
		System.out.println(">>> Not saving anything because [" + objectName + "] is empty");
		System.out.println(Disp.htag);
	}
	
}
