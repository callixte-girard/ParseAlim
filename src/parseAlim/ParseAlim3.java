package parseAlim ;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import myClasses.utils.Disp;
import myClasses.utils.EncodingCorrecter;
import myClasses.utils.ParseHtml;
import myClasses.utils.SessionManage;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

////// ###### !!!!!!! IF NOT WORKING, PUT ENCODING TO UTF-8 !!!!!!!! ######################

public class ParseAlim3 {
	
	public static final String url_main = "https://informationsnutritionnelles.fr" ;
	
	// paths
	private static final String save_path =
			"/Users/c/Google Drive/Code Archives/Java/saves/";
	private static final String export_path =
			"/Users/c/Google Drive/Docs vrac/Food/" ;

	private static final String filename = "alim_db";

	//////////////////////////////////////////////////////////:
	
	public static void main(String[] args) throws Exception
	{
		System.out.println("-------------------- ParseAlim3 --------------------");
		double start = System.currentTimeMillis();
		int nb_actuel = 0 ; // pour compter la prog

		SessionManage.setSavePath(save_path);
		EncodingCorrecter.refreshEncodingAtStartup("UTF-8");

		// ## FIRST OF ALL, start loading save, if it exists.
		try {

			Object saved_data = SessionManage.objectLoad
					(filename + ".sav", true);

			Aliment.al.addAll((ArrayList<Aliment>) saved_data);

			// now display it
			for (Aliment al : Aliment.al)
			{
				al.dispAttr(false);
//				al.dispBlock();
			}

		// ## ELSE, start fetching all aliments and export to csv.
		} catch (NullPointerException np_ex) {

			System.out.println("!!! COULD NOT LOAD FROM FILE [" + filename + "]");
			System.out.println(Disp.htag);
			System.out.println(Disp.htag);
			System.out.println(">>> Fetching aliment list... May take some time. Please wait");
			Document doc = ParseHtml.fetchHtmlAsDocumentFromUrl(url_main + "/aliments");
			System.out.println(Disp.htag);

			ArrayList<String> url_list = getLinksToParse(doc);

			int pipou = 0 ;
			// then parse them one by one.
			for (String url : url_list)
			{
				pipou ++ ;
//			if (pipou < 10) // possibility to limit the number of downloads with this line
				{
					Aliment al = Aliment.parseFromShortUrl(url);
					//	al.dispAttr(false); // true/false = with/without nutr
					al.dispBlock();

					// display progress
					nb_actuel += 1;
					Disp.displayProgress(nb_actuel, url_list.size());
				}
				// ### NB : la sauvegarde a lieu plus tard,
				// uniquement si tout le téléchargement
				// s'est passé sans problème
			}

		}

		/////// END : ### saves data in external text file
		System.out.println(Disp.htag);
		// write file for later quicker load
		SessionManage.objectSave(filename + ".sav", Aliment.al);
		// write file for exporting in excel, for example
		writeCSVRequests(filename + ".csv", true);
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
			BufferedWriter bw = outputForWrite(export_path + filename);
			
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
	{ /// #### ATTENTION : version spécifique en attendant.

		String save_to = export_path + filename ;
		System.out.println(save_to);

		try
		{
			BufferedWriter bw = outputForWrite(save_to);

			// I) makes header first
			bw.write("Aliment" + ",");
			bw.write("Catégorie" + ",");
			bw.write("URL" + ",");
//			bw.write("Country code" + ",");

			if (with_nutr)
			{
				for (Nutr n_header : Aliment.al.get(0).nutr)
				{
					bw.write(n_header.nom + ",");
				}
			}
			bw.newLine();

			// II) then writes data cell by cell
			for (Aliment al : Aliment.al)
			{
				bw.write(al.nom + ",");
				bw.write(al.cat + ",");
				bw.write(al.url + ",");
			//	bw.write(al.country_code + ",");
//				bw.write("" + Nutr.getByNameInList(al.nutr, "Energie").val);

				if (with_nutr)
				{
					for (Nutr n : al.nutr)
					{
						//save(path_main + "test");

						bw.write(n.val + ",");
					}
				}

				bw.newLine();
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
