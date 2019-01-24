package myClasses.utils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class SessionManage {
	
	private static String root_path ;


	public static String getSavePath()
    {
        return root_path ;
    }
	
	public static void setSavePath(String path)
	{
		root_path = path ;
		
		File file = new File(root_path);
		
		if (!file.exists())
		{
			file.mkdirs();
			
			System.out.println(">>> folder [" + path + "] created successfully.");
		}
	}


    public static void objectSave(String filename, Object to_save)
	{
	    try {

            System.out.println(">>> Saving to [" + filename + "] ... Please wait");

            FileOutputStream fos = new FileOutputStream(root_path + filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(to_save);
            oos.close();

            System.out.println(">>> Saved [" + filename + "] successfully :)");
            System.out.println(Disp.htag);

        } catch (NullPointerException np_ex) {

	        System.out.println("!!! CANNOT FIND SAVE PATH [" + root_path + filename + "]");

        } catch (IOException io_ex) {

            System.out.println("!!! other error");
        }

	}

	public static void test(String filename, ArrayList<?> al)
	{
		ArrayList<Field> out = new ArrayList<>();

		for (Object o : al) {
			for (Field f : ClassReader.returnFields(o, true)) {
				System.out.println(f.getName());
				out.add(f);
			}
		}
	}
	
	public static Object objectLoad(String filename, boolean showFullSavePath)
	{
		try {

            if (showFullSavePath)
                System.out.println(">>> Full Save Path is : " + root_path + filename);

            System.out.println(">>> Loading [" + filename + "] ... Please wait");

            FileInputStream fis = new FileInputStream(root_path + filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = (Object) ois.readObject();
            ois.close();

            System.out.println(">>> Loading [" + filename + "] complete. :)");
            System.out.println(Disp.htag);

            return obj;

        } catch (ClassNotFoundException cnf_ex) {

		    return null ;

        } catch (IOException io_ex) {

			System.out.println("!!! CANNOT FIND SAVE PATH [" + root_path + filename + "]");
			return null ;
        }


	}


	public static void dispEmptyObjectMessage(String objectName)
	{
		System.out.println(">>> Not saving anything because [" + objectName + "] is empty");
		System.out.println(Disp.htag);
	}
	
}
