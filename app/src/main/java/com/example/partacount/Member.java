/*******************************************************************************

 @file			Member.java
 @abstract		Définition de la classe Member pour gérer les membres
 @author		SANTOS Arthur
 @author		COLLIOT Kévin
 @version		1.0

*******************************************************************************/

package com.example.partacount;

public class Member {
	
	// ATTRIBUTS
	private String nameM;			// nom du membre
	private float debt;				// valeur de sa dette
	private int id;					// identifiant du membre pour la BDD
	private String ggl_address;		// adresse google (pour évolution future)
	private int bills_count;		// compteur de notes dans lequel ce membre est impliqué
	
	// CONSTRUCTEURS
	// sans paramètre
	public Member(){
		
	}
	
	// avec paramètres
	public Member(String nameM, int id) {
		this.nameM = nameM;
		this.debt = 0;
		this.id = id;
		this.bills_count = 0;
		this.ggl_address = null;
	}
	
	// GETTERS ET SETTERS
	public String getNameM() {
		return nameM;
	}
	public void setNameM(String name) {
		this.nameM = name;
	}
	public float getDebt() {
		return debt;
	}
	public void setDebt(float debt) {
		this.debt = debt;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getGgl_address() {
		return ggl_address;
	}
	public void setGgl_address(String ggl_address) {
		this.ggl_address = ggl_address;
	}
	public int getBills_count() {
		return bills_count;
	}
	public void setBills_count(int bills_count) {
		this.bills_count = bills_count;
	}

	// METHODE
	/*******************************************************************************
	 @function		debtUpdate
	 @abstract		Méthode pour mettre à jour la valeur de la dette
	 @param			float value valeur à ajouter à la dette
	*******************************************************************************/
	public void debtUpdate(float value){
		debt = debt + value;
	}

}
