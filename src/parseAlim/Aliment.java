package parseAlim ;

import java.util.ArrayList;
import java.io.Serializable;

import myClasses.utils.Disp;
import myClasses.utils.ParseHtml;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;



public class Aliment implements Serializable
{
	public static ArrayList<Aliment> al = new ArrayList<Aliment>() ;
	
	// SET-AT-CREATION ATTRIBUTES
	public String nom ;
	public String cat ; // categorie. Différent de type. On ne prend que le type "Aliment moyen"
	final public String url ;
	//final public String country_code ;
	
	// --------------------- NUTRIMENTS ----------------------------
	public ArrayList<Nutr> nutr = new ArrayList<Nutr>();
		
	//////////// ( à implémenter )
	/*
	public int portion = 0 ;
	public String qtite_portion = null ;	
	// for veg only :
	public boolean[] dispo = new boolean[12] ;
	public boolean[] haute_saison = new boolean[12] ;
	*/

	// #######################################################################################################

	public Aliment(String nom
			,String cat
			,String url
		//	,String country_code
		//	,Nutr proteines,	Nutr lipides, Nutr glucides, Nutr kcal, Nutr vitamines, Nutr mineraux
		//	,LinkedHashMap<String, Nutr> nutr
		//	,ArrayList<Nutr> nutr
			)
	{
		/*
		// correcter 3-in-1 pour csv, jsoup et pbs d'encodage
		this.nom = EncodingCorrecter.normaliseAzertySpecialLetters(nom
				.replace(",", "")
				.replace("°", "'")) ;
		*/
		this.nom = nom ;			 
		this.cat = cat ;
		this.url = url ;
	//	this.country_code = country_code ;
		
		//this.nutr = nutr ;
		
		if (!exists(this.nom, Aliment.al)
		//	&& !this.isEmpty()
				)
		{
			Aliment.al.add(this);
		}
	}
	
	
	public static boolean exists(String search, ArrayList<Aliment> l)
	{
		for (Aliment a : l)
		{
			if (search.equals(a.nom)) 
			{
				return true ;
			}
		}
		return false ;
	}


	public static boolean isContainedNotEquals(String search, ArrayList<Aliment> l) 
	{
		for (Aliment a : l) 
		{
			if (a.nom.equals(search)) 
			{
				return false ;
			}
			else if (a.nom.contains(search)) 
			{
				return true ;
			}
			else if (search.contains(a.nom))
			{
				return true ;
			}
		}
		return false ;
	}

	
	public boolean isEmpty()
	{
		if (this.nutr.isEmpty())
		{
			return true ;
		}
		else
		{
			return false ;
		}
	}

	/*
	public boolean isDispoNow() 
	{
		LocalDate ld = LocalDate.now();
		int month = ld.getMonthValue();
		
		if (this.haute_saison[month] || this.dispo[month])
		{
			return true ;
		}
		else
		{
			return false ;
		}
	}
	*/
	
	
	public void dispAttr(boolean with_nutr)
	{
		System.out.println("### " 
				+ this.cat + " | "
				+ this.nom + " | "
				+ this.url + " | "
		//		+ this.country_code 
			);
		
		System.out.println(Disp.line);
		
		if (with_nutr)
		{
			Nutr.dispAllFull(this.nutr);
		}	
	}
	
	
	public void dispBlock()
	{
		System.out.println("- nom : " + this.nom);
		System.out.println("- url : " + this.url);
		System.out.println("- cat : " + this.cat);
		System.out.println(Disp.line);
	}


	public static Aliment parseFromShortUrl(String url)
	{				
		String url_toFetch = ParseAlim3.url_main + url ;
		
	//	System.out.println(">>> Now fetching : [" + url_toFetch + "]");
	//	System.out.println(Disp.line);
		double start = System.currentTimeMillis();
		/////////// la mesure commence ici.
		
		Document doc = ParseHtml.fetchHtmlAsDocumentFromUrl(url_toFetch);
		
		// 1) ### parse name & cats
		
		String nom = doc.getElementsByTag("h1").first().html();
		String cat = doc.getElementsByTag("b").first().getElementsByTag("a").first().html();
		
		// 2) ### parse nutr list
		
		// --> converts " into ° so that it can be designed as string in java
		String html = ParseHtml.makeUsableInJavaStrings(doc.html(), '"', '°');
		
		String[] html_split = ParseHtml.splitStringIntoLinesArray(html);
		//for (String s : html_split) {System.out.println(s);}
		
		ArrayList<String> extracted = ParseHtml.extractOnlyNeededLines(
				html_split, 
				"<table", 
			//	"<tbody>"
				"</table>"
		);
	
		extracted = ParseHtml.recollageOneRequestPerLine(extracted, "tr", "td", "§");
		//for (String s : extracted) {System.out.println(s);}
		
		ArrayList<Nutr> fetched = parseNutrLineByLine(extracted);
		
		// puis fusionne kcal et kJ pour l'énergie et beta-carotène et rétinol pour vitamine A
		fetched = Nutr.postTraitement_energie(fetched);
		fetched = Nutr.postTraitement_vitamineA(fetched);
		
		// FINALLY, creates alim and adds nutr_al to it
		Aliment alim = new Aliment(nom, cat, url);
		
		alim.nutr = fetched ;
		
		///////////
		double end = System.currentTimeMillis();
		
		//Nutr.dispAllFull(fetched);
		
		//System.out.println("* Parsing for [" + url_toFetch + "] took : " + (end - start) + " ms. ***");	System.out.println(Main.star);
		
		return alim ;
	}
	
	
	private static ArrayList<Nutr> parseNutrLineByLine(ArrayList<String> al)
	//private static void parseNutrLineByLine(String[] al)
	{		
		ArrayList<Nutr> fetched = new ArrayList<Nutr>() ;
		
		Nutr n = null ;
		
		Nutr famille = null ;
		Nutr nutr = null ;
	
		String nom = null ;
		Double val = null ;
		String unit = "" ;
		
		for (String req : al)
		{
			req = req.trim();
			//System.out.println(req);
			
			Document doc = Jsoup.parse(req);
			
			
			// 2) AUTRE LIGNE : parse info indiquée selon condition.
			String[] splu = doc.body().text().split("§");
			// ici on parse les valeurs !!!! 
			{
				try
				{
					nom = splu[1];
					String[] spla = splu[2].split(" ");
					
					unit = spla[1];
					val = Double.parseDouble(spla[0]);
				}
				catch (NumberFormatException e1)
				{
					unit = "" ;
					val = null ;
				}
				catch (ArrayIndexOutOfBoundsException e)
				// si <td> vide
				{
					unit = "" ;
					val = null ;
				}
			}
			
			
			// 3) FINALLY c'est le moment de créer et ranger dans la lhm !!!
			if (nom != null)
			{
				n = new Nutr(nom, val, unit);
			
	
				// Inspect lines that change level
				if (req.contains("<tr class=°tr1"))
				{
					n.parent = n ;
					famille = n ;
				}
				else if (req.contains("<tr class=°tr2"))
				{
					if (req.contains("&nbsp; -"))
					{
						n.parent = nutr ;
						
						nutr.sub_nutr.add(n);
					}
					else
					{
						n.parent = famille ;
						nutr = n ;
						
						famille.sub_nutr.add(nutr);
					}
				}
				
				// DEBUGGING
				//n.dispParent();
				//n.dispWithSubNutr();
			
				fetched.add(n);
			}
		//	disp(star);
		//	disp(line);
			
		} // FIN BOUCLE
		
		return fetched ;
	}



	public static Aliment getFromName(String search, ArrayList<Aliment> l) 
	{
		if (Aliment.exists(search, l)) 
		{
			for (Aliment a : l) 
			{
				if (a.nom.equals(search)) 
				{
					return a ;
				}
			}
		}
		return null ;
	}
}