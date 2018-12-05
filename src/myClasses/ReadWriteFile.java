package myClasses;


import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;

public class ReadWriteFile
{


    public static BufferedWriter outputWriter(String path)
    {
        try
        {
            File f = new File(path);

            if (!f.exists())
            {
                f.createNewFile();
            }

            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);

            return bw ;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null ;
        }
    }

    public static BufferedReader outputReader(String path) throws FileNotFoundException
    {
        File f = new File(path);

        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);

        return br ;
    }

}
