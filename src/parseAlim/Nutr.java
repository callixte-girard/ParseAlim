package parseAlim ;

import myClasses.* ;

import java.io.Serializable;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class Nutr implements Serializable
{
	public String nom ;
	public Double val ;
	public String unit ;
	
	public Nutr parent ;
	public ArrayList<Nutr> sub_nutr = new ArrayList<Nutr>() ; // if isEmpty, this is at the end
	
	
	public Nutr(String nom 
			,Double val 
			,String unit
		//	,Nutr parent
		//	,ArrayList<Nutr> sub_nutr
			)
	{
		this.nom = nom ;
		this.val = val ;
		this.unit = unit ;
		
	//	this.parent = this ;
	//	this.sub_nutr = sub_nutr ;
		
	//	System.out.println("- Parent : [" + this.parent.nom + "] | " + this.nom + " = " + this.val + " " + this.unit);
	}
	
	
	public boolean isMaster()
	{
		boolean is_master ;
		
		if (this.nom.equals(this.parent.nom))
		{
			is_master = true ;
		}
		else
		{
			is_master = false ;
		}
		
		return is_master ;
	}
	
	
	public boolean isMain()
	{
		boolean is_main ;
		
		if ( (this.isMaster() && this.val != null )
				|| this.nom.toLowerCase().contains("Energie")
				
			)
		{
			is_main = true ;
		}
		else
		{
			is_main = false ;
		}
		
		return is_main ;
	}


	public void dispWithSubNutr()
	{	
		// affiche juste plus indenté les familles !
		if (this.isMaster())
		{
			System.out.print("# ");
		}
		
		if (!sub_nutr.isEmpty())
		{
			//System.out.println("> [" + this.nom + "] contains :");
			this.dispMono();
			
			for (Nutr n : this.sub_nutr)
			{
				n.dispWithSubNutr();
			}	
		}
		else
		{
			this.dispMono();
		}
	}


	public void dispMono()
	{
		//dispParent();
		
		System.out.print("[" + this.nom + "]" + " = ");
		
		if (val != null)
		{
			System.out.println(this.val + " " + this.unit) ; 
		}
		else
		{
		//	System.out.println(" ? inconnu" );
			System.out.println(" ???" );
		}
		
	}


	public static void dispAllFull(ArrayList<Nutr> al)
	{
		for (Nutr n : al)
		{
			if (n.isMaster())
			{
			//	n.dispParent();
			//	System.out.print(n.isMaster());
				n.dispWithSubNutr();
				System.out.println(Disp.line);
			}
		}
	}


	protected static ArrayList<Nutr> postTraitement_energie(ArrayList<Nutr> al)
	{
		// compléter ceux qui sont vides et les convertir ainsi : 1 kcal = 4,184 kJ
		ArrayList<Nutr> out = new ArrayList<Nutr>() ;
		Double kcal = null ;
		
		for (Nutr n : al)
		{
			if (n.nom.contains("Energie"))
			{
				if (n.nom.contains("Energie - Calories"))
				{					
					if (n.val != null)
					{
						kcal = n.val ;
					}
				}
				else if (n.nom.contains("Energie - kilojoules")) // C'est lui qu'on modifie en fait
				{					
					n.nom = "Energie" ; // automatiquement considéré comme un master puisque n.nom == n.parent.nom
					n.unit = "kcal" ;
					
					if (kcal == null)
					{
						if (n.val != null)
						{
							n.val = n.val / 4.184 ;
						}
					}
					else
					{
						n.val = kcal ;
					}	
					
					out.add(n);
				}
			}
			else
			{
				out.add(n);
			}
		}
		
		return out ;
	}
	
	
	protected static ArrayList<Nutr> postTraitement_vitamineA(ArrayList<Nutr> al)
	{
		// Calcule la vitamine A totale = rétinol (en µg) + 1/6 bêta-carotène (en µg)
		ArrayList<Nutr> out = new ArrayList<Nutr>();
		double vitA = 0 ;
		
		for (Nutr n : al)
		{
			if (n.nom.contains("Vitamine A"))
			{
				if (n.nom.contains("Beta-Carotène"))
				{					
					if (n.val != null)
					{
						vitA = n.val / 6 ;
					}
				}
				else if (n.nom.contains("Rétinol"))
				{
					n.nom = "Vitamine A" ;
					
					if (n.val != null)
					{
						n.val += vitA ;
					}
				}
			}
			else
			{
				out.add(n);
			}
		}
		
		// maintenant il ne reste plus qu'à enlever manuellement la vitamine doublon (beta-car)
		ArrayList<Nutr> new_vit = new ArrayList<Nutr>();
		
		for (Nutr n : al)
		{
			if (n.nom.equals("Vitamines"))
			{
				for (Nutr vit : n.sub_nutr)
				{
					if (vit.nom.contains("Vitamine A") && !vit.nom.equals("Vitamine A"))
					{
						// ON NE LAJOUTE PAS 
					}
					else
					{
						new_vit.add(vit);
					}
				}
			}
		}
		// puis remplaçage dans la bonne sub_nutr al.
		for (Nutr n : al)
		{
			if (n.nom.equals("Vitamines"))
			{
				n.sub_nutr = new_vit ;
			}
		}
		
		return out ;
	}
	
	
	public static ArrayList<Nutr> getAllMasterInList(ArrayList<Nutr> al)
	{
		ArrayList<Nutr> out = new ArrayList<Nutr>();
		
		for (Nutr n : al)
		{
			if (n.isMaster())
			{
				out.add(n);
			}
		}
		
		return out ;
	}
	
	
	public static ArrayList<Nutr> getAllMainInList(ArrayList<Nutr> al)
	{
		ArrayList<Nutr> out = new ArrayList<Nutr>();
		
		for (Nutr n : al)
		{
			if (n.isMain())
			{
				if (n.nom.toLowerCase().contains("calories"))
				{
					n.nom = "Calories" ;
				}
				
				if (n.nom.toLowerCase().contains("alcool")
					|| n.nom.toLowerCase().contains("sodium")
					|| n.nom.toLowerCase().contains("eau")
					|| n.nom.toLowerCase().contains("fibres")
					)
				{
					
				}
				else
				{
					out.add(n);
				}
				
			}
		}
		
		return out ;
	}
	
	
	public static Nutr getByNameInList(ArrayList<Nutr> al, String name)
	{
		for (Nutr n : al)
		{
			if (n.nom.equals(name))
			{
				return n ;
			}
		}
		return null ;
	}
	



}
