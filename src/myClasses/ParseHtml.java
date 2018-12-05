package myClasses;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class ParseHtml extends ParseText
{
	//////////////////////////////////////////////////////////////////

	public static Document fetchHtmlAsDocumentFromLocalFile(String full_path) throws IOException
	{
		// *** A TESTER
		File in = new File(full_path);
		Document doc = Jsoup.parse(in, null);
		return doc ;
	}

	public static Document fetchHtmlAsDocumentFromUrl(String url)
	{
		try
		{
			Document doc = Jsoup.connect(url).maxBodySize(6500000).get();
			// set to 6 500 000 bytes = 6.5 MB
			return doc ;
		}

		catch (IOException e)
		{
			System.out.println("!!! ERROR WHILE FETCHING FILE !!!");
			e.printStackTrace();
			return null ;
		}
	}

	public static ArrayList<String> extractOnlyNeededLines
			(String[] lines, String delim_top, String delim_bottom)
	{
		// NB1 : delim_top est inclus dans extracted, delim_bottom ne l'est pas
		// NB2 :  +++ l'input lines est directement interchangeable entre str[] et al<str>

		boolean cut = false ;
		ArrayList<String> extracted = new ArrayList<String>() ;

		for (String line : lines)
		{
			if (line.contains(delim_top))
			{
				cut = true ;
				extracted.add(line);
			}
			else if (line.contains(delim_bottom))
			{
				cut = false ;
			}
			else
			{
				if (cut)
				{
					extracted.add(line);
				}
			}
		}
		return extracted ;
	}


	public static String extractOnlyNeededSubline
			(String line, String delim_debut, String delim_fin)
	{ // extrait juste la partie comprise entre les deux délimiteurs.

		int index_debut = line.indexOf(delim_debut);
		int index_fin = line.indexOf(delim_fin);

		return line.substring(index_debut + delim_debut.length(), index_fin);

	}


	public static ArrayList<String> recollageOneRequestPerLine
		(ArrayList<String> al, String master_tag, String sub_tag, String delim
				)
	{
		String out = null ;
		ArrayList<String> main_out = new ArrayList<String>() ;

		String debut = "<" ;
		String fin = "</" ;

		for (String s : al)
		{
			s = s.trim();

			// First inspects beginning.
			if (s.contains(debut + master_tag))
			{
				out = "" ;
			}

			// then, add the line if contains mescouilles
			if (s.contains(debut + sub_tag) || s.contains(debut + master_tag) || s.contains(fin + master_tag))
			{
				out += s + delim ;
			}

			// finally, inspects ending.
			if (s.contains(fin + master_tag))
			{
				main_out.add(out);
				out = "" ;
			}
		}

		return main_out ;
	}

	public static String makeUsableInJavaStrings(String str, char find, char replace)
	// replaces all the " with ° to make things like : href="mateub" usable in java str
	// ex : if (str.contains("href=°/mateub°")) { do.... }
	{
		str = str.replace(find, replace);
		return str ;
	}


	public static Document makeUsableInJsoupDocument(String str, char find, char replace)
	// 1) makes the exact opposite of makeUsableInJavaStrings.
	// 2) + converts str into Jsoup.nodes.Document (or sthng like this)
	{
		str = str.replace(find, replace);
		Document doc = Jsoup.parse(str);
		return doc ;
	}



	public static String[] splitStringIntoLinesArray(String str)
	{
		String[] split = str.split("\\r?\\n");
		// pour output une al plutot qu'un str[], réactiver ce morceau :
		/*
		ArrayList<String> al = new ArrayList<String>();
		for (String s : split)
		{
			al.add(s);
		}
		*/
		return split ;
	}
}
