package myClasses;

public class Disp 
{

	// petits raccourcis de branleur
	public static String line = "---------------------------------------------------------------------------------------------------------------------------------------------" ;
	public static String star = "*********************************************************************************************************************************************" ;
	public static String htag = "#############################################################################################################################################" ; 
	
	public static void displayProgress(int nb_actuel, int nb_total)
	{
		System.out.println(">>> Progression globale : " + nb_actuel + " / " + nb_total
				+ " | " + ( 100 * nb_actuel / nb_total ) + " %"
			);

		System.out.println(Disp.star);
	}
	
}
