package parseAlim ;

import myClasses.* ;

import java.awt.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

////// ###### !!!!!!! IF NOT WORKING, PUT ENCODING TO UTF-8 !!!!!!!! ######################

public class ParseAlim3 {
	
	public static String url_main = "https://informationsnutritionnelles.fr" ;
	
	// paths
	private static String path_js = "JS/" ;
	private static String path_csv = "VBA/" ;

	//////////////////////////////////////////////////////////:
	
	public static void main(String[] args) throws Exception
	{	
		System.out.println("-------------------- ParseAlim3 --------------------");
		
		double start = System.currentTimeMillis();
		int nb_actuel = 0 ; // pour compter la prog
	
	//	new Aliment("coucou", "coucou.com", "test");
		
		/////// INIT : ### download the page with all Aliment
		System.out.println(">>> Fetching aliment list... May take some time. Please wait");
		Document doc = ParseHtml.fetchHtmlAsDocument(url_main + "/aliments");
		System.out.println(Disp.htag);
		
		ArrayList<String> url_list = getLinksToParse(doc);
		
		// then parse them one by one.
		for (String url : url_list)
		{
			Aliment al = Aliment.parseFromShortUrl(url);
		//	al.dispAttr(false); // true/false = with/without nutr
			al.dispBlock();
			
			// display progress
			nb_actuel += 1 ;
			Disp.displayProgress(nb_actuel, url_list.size());
			
			// ### NB : la sauvegarde a lieu plus tard,
			// uniquement si tout le téléchargement
			// s'est passé sans problème
		}
	
		
		/////// END : ### saves data in external text file
		System.out.println(Disp.htag);
		
		SessionManage.setSavePath("parseAlim3/");
		SessionManage.objectSave("alim_db" , Aliment.al);
		
		System.out.println(Disp.htag);
		/////// saving finished.
		
		double end = System.currentTimeMillis();
		
		System.out.println(":D :D :D ------------ GOOD JOB ! Total time : " + (end-start) + " ms ------------ :D :D :D");
		
	} // fin du main
	
	
	private static ArrayList<String> getLinksToParse(Document doc)
	{		
		Element tbody = doc.getElementsByTag("tbody").first();
		Elements rows = tbody.getElementsByTag("tr");
		
		ArrayList<String> url_to_parse = new ArrayList<String>();
		
		for (Element el : rows)
		{
			// ne récupère que les aliments moyens (ou qui proviennent de Ciqual 2013)
			Elements elected = el.getElementsContainingText("Aliment moyen");
			
			for (Element td : elected)
			{
				Element link = td.getElementsByAttribute("href").first();

				if (link != null)
				{
					String url = link.attr("href");
					
					url_to_parse.add(url);
				}
			}
		}
		
		return url_to_parse ;
	}
	
	
	private static void writeJSRequests(String filename, boolean full)
	{ /// #### ATTENTION : version spécifique en attendant.
		try
		{
			BufferedWriter bw = outputForWrite(path_js + filename);
			
			bw.write("var alim_tab = ");
			bw.write("[");
			bw.newLine();
	
			for (Aliment al : Aliment.al)
			{
				
				bw.write("new Aliment(");
				bw.newLine();
				bw.write('"' + al.nom + '"' + ",");
				bw.newLine();
				bw.write('"' + al.cat + '"' + ",");
				bw.newLine();
				bw.write('"' + al.url + '"' + ",");
				bw.newLine();
			//	bw.write('"' + al.country_code + '"' + ",");
			//	bw.newLine();
				
				bw.write('[');
				bw.newLine();
			
				ArrayList<Nutr> to_write = new ArrayList<Nutr>() ;
				if (full)
				{
					to_write = al.nutr ;
				}
				else
				{
					to_write = Nutr.getAllMainInList(al.nutr);
					//to_write = Nutr.getAllMasterInList(al.nutr);
				}
				
				for (Nutr n : to_write)
				{
					//n.dispWithSubNutr();
					/*
					bw.write("new Nutr("
							+ '"' + n.nom + '"' + ","
							+ n.val + ","
							+ '"' + n.unit + '"' + ","
						//	+ '"' + n.parent.nom + '"'
							+ ") ,"
					); */
					bw.write("{ " 
							+ "nom:" + '"' + n.nom + '"' + ","
							+ "val:" + n.val + ","
							+ "unit:" + '"' + n.unit + '"' + ","
							+ " },");
					
					bw.newLine();
				}
				
				bw.write(']');
				bw.newLine();
				bw.write("),");
				bw.newLine();
			}
			
			bw.write("] ;");
			bw.close();
			
			System.out.println(" ************************** WRITING TO .js FILE FINISHED *************************");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	private static void writeCSVRequests(String filename, boolean with_nutr) throws Exception
	{ /// #### pareil
		try
		{
			BufferedWriter bw = outputForWrite(path_csv + filename);
	
			for (Aliment al : Aliment.al)
			{
				bw.write(al.nom + ",");
				bw.write(al.cat + ",");
				bw.write(al.url + ",");
			//	bw.write(al.country_code + ",");
				bw.write("" + Nutr.getByNameInList(al.nutr, "Energie").val);
				bw.newLine();
				
				if (with_nutr)
				{
					
					
					for (Nutr n : al.nutr)
					{
						//save(path_main + "test");
					}
				}
			}
			
			bw.close();
			
			System.out.println(" ************************** WRITING TO .csv FILE FINISHED *************************");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static BufferedWriter outputForWrite(String path)
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
	
	

}
