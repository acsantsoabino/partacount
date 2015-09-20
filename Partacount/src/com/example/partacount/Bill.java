/*******************************************************************************

 @file			Bill.java
 @abstract		Définition de la classe Bill pour gérer les notes de frais
 @author		SANTOS Arthur
 @author		COLLIOT Kévin
 @version		1.0

*******************************************************************************/


package com.example.partacount;

public class Bill {
	
	//ATTRIBUTS
	private float value;			// valeur de la note
	private int nb_members;			// nombre de personne dans le groupe auquel elle appartient
	private Member who_paied;		// qui a payé
	private boolean[] whos_in;		// entre qui se partage la note
	private String what;			// nom de la note, ce à quoi elle correspond
	private int id;					// identifiant pour la BDD
	
	
	//CONSTRUCTEURS
	/*******************************************************************************
	 @Constructeur 	Bill
	 @abstract 		Constructeur avec seulement quelques paramètres créant la note
	 @result 		la note est créé
	*******************************************************************************/
	public Bill(String what, float value, int nb_members) {
		
		int i;
		
		this.what = what;
		this.value = value;
		this.nb_members = nb_members;
		whos_in = new boolean[nb_members];
		
		// Initialisation du tableau à faux partout
		for(i = 0; i < this.nb_members; i++) {
			this.whos_in[i] = false;
		}
		
	}
	/*******************************************************************************
	 @Constructeur 	Bill
	 @abstract 		Constructeur avec tous les paramètres créant la note complète
	 @result 		la note est créé
	*******************************************************************************/
	public Bill(int id, float value, int nb_members, Member who_paied,
			boolean[] whos_in, String what) {
		this.value = value;
		this.nb_members = nb_members;
		this.who_paied = who_paied;
		this.whos_in = whos_in;
		this.what = what;
		this.id = id;
	}

	//GETTERS ET SETTERS
	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public int getNb_members() {
		return nb_members;
	}

	public void setNb_members(int nb_members) {
		this.nb_members = nb_members;
	}

	public Member getWho_paied() {
		return who_paied;
	}

	public void setWho_paied(Member who_paied) {
		this.who_paied = who_paied;
	}

	public boolean[] getWhos_in() {
		return whos_in;
	}

	public void setWhos_in(boolean[] whos_in) {
		this.whos_in = whos_in;
	}

	public String getWhat() {
		return what;
	}

	public void setWhat(String what) {
		this.what = what;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	//METHODES
	/*******************************************************************************
	 @function		debtMember
	 @abstract		Méthode pour calculer ce que doit chaque débiteur
	 @return 		float la valeur de la note divisée par le nombre de débiteurs
	*******************************************************************************/
	public float debtMember() {
		int i, count=0;
		
		for(i=0;i<nb_members;i++){
			if(whos_in[i]) count++;
		}
		
		return this.value/count;
	}

	/*******************************************************************************
	 @function		whos_inTostring
	 @abstract		Méthode pour transformer le tableau de booléens whos_in en chaine
	  				de caractères composée de 0 et de 1
	 @return		String de 0 et de 1
	*******************************************************************************/
	public String whos_inTostring() {
		int i;
		String whos_inString = "";
		
		for(i=0;i<nb_members;i++){
			if(this.whos_in[i]){
				whos_inString = whos_inString + "1";
			} else {
				whos_inString = whos_inString + "0";
			}
		}
		
		return whos_inString;
	}

	/*******************************************************************************
	 @function		memberIn
	 @abstract		Méthode pour mettre à jour le tableau de boléens whos_in
	 @discussion	On invoque cette méthode à chaque fois que l'utilisateur coche ou
	 				décoche une case dans la liste des membres du groupe.
	 @param 		int position, position qu'il faut mettre à jour
	 @result 		
	*******************************************************************************/
	public void memberIn(int position) {
		// XOR : lorsque l'utilisateur appuie (coche ou décoche),
		// si le membre ne faisait pas partie de whos_in il en fait partie
		// si le membre en faisait partie, il n'en fait désormais plus partie.
		this.whos_in[position] = this.whos_in[position]^true;
		
	}

}
