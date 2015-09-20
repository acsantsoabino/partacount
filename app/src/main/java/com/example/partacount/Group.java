/*******************************************************************************

 @file			Group.java
 @abstract		Définition de la classe Group pour gérer les groupes
 @author		SANTOS Arthur
 @author		COLLIOT Kévin
 @version		1.0

*******************************************************************************/

package com.example.partacount;

import java.util.ArrayList;

public class Group {
	
	// ATTRIBUTS
	private String nameGp;						// nom du groupe
	private ArrayList<Member> list_members;		// liste des membres du groupe
	private ArrayList<Bill> list_bills;			// liste des notes de frais du goupe
	private int nb_members;						// nombre de membres dans le groupe
	private float balance;						// total dépensé par le groupe
	private int id;								// identifiant du groupe
	
	// CONSTRUCTEUR
	public Group(String nameGp) {
		super();
		list_members = new ArrayList<Member>();
		list_bills = new ArrayList<Bill>();
		this.nameGp = nameGp;
		this.nb_members = 0;
		this.balance = 0;
	}

	// GETTERS AND SETTERS
	public String getNameGp() {
		return nameGp;
	}

	public void setNameGp(String nameGp) {
		this.nameGp = nameGp;
	}

	public ArrayList<Member> getList_members() {
		return list_members;
	}

	public void setList_members(ArrayList<Member> list_members) {
		this.list_members = list_members;
		this.setNb_members();
	}

	public ArrayList<Bill> getList_bills() {
		return list_bills;
	}

	public void setList_bills(ArrayList<Bill> list_bills) {
		this.list_bills = list_bills;
	}

	public int getNb_members() {
		return nb_members;
	}

	public void setNb_members() {
		this.nb_members = list_members.size();
	}
	
	public float getBalance() {
		return balance;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	// METHODES
	/*******************************************************************************
	 @function		memberAdd
	 @abstract		Méthode pour ajouter un nouveau membre au groupe
	 @param			Member newMember
	 @result 		newMember est ajouté à list_members et le nombre de membre dans
	  				le groupe est mis à jour.
	*******************************************************************************/
	public void memberAdd(Member newMember){
		
		list_members.add(newMember);
		this.setNb_members();
		
	}
	
	/*******************************************************************************
	 @function		billAdd
	 @abstract		Méthode pour ajouter une nouvelle note de frais au groupe
	 @param			Bill newBill
	*******************************************************************************/
	public void billAdd(Bill newbill){

		float debt;		// variable provisoire pour debtMember
		int i;			// variable d'incrémentation de boucle

		list_bills.add(newbill);		// ajout de la note à la liste des notes
		debt = newbill.debtMember();
		
		// on ajoute au total dépensé par le groupe la valeur de la note
		this.balance = this.balance + newbill.getValue();
		
		// Boucle de mise à jour de la dette de chaque membre
		for(i=0; i<nb_members; i++){
			// si le membre est concerné par la note
			if(newbill.getWhos_in()[i] == true){
				list_members.get(i).debtUpdate(-1*debt);					// mise à jour de sa dette
				list_members.get(i).setBills_count(list_members.get(i).getBills_count()+1);		// incrémentation de son compteur d'implication
			}
			// si le membre est celui qui a payé la note
			if(list_members.get(i).equals(newbill.getWho_paied())){
				list_members.get(i).debtUpdate(newbill.getValue());	//mise à jour de sa dette (retrait du total)
				if(newbill.getWhos_in()[i] != true){				// s'il ne devait rien payé :
					list_members.get(i).setBills_count(list_members.get(i).getBills_count()+1);	// incrémentation de son compteur d'implication
				}
			}
		}
		
	}	
	
	/*******************************************************************************
	 @function		billRemove
	 @abstract		Méthode pour retirer note de frais au groupe
	 @discussion	Méthode identique à la précédente avec des signes opposées
	 @param			Bill newBill note à enlever
	*******************************************************************************/
	public void billRemove(Bill newbill){

		float debt;
		int i;
		
		list_bills.remove(newbill);
		debt = newbill.debtMember();
		
		this.balance = this.balance - newbill.getValue();
		
		for(i=0;i<nb_members;i++){
			list_members.get(i).setBills_count(list_members.get(i).getBills_count()-1);
			if(newbill.getWhos_in()[i]==true){
				list_members.get(i).debtUpdate(debt);
			}
			if(list_members.get(i).getId()==newbill.getWho_paied().getId()){
				list_members.get(i).debtUpdate(-1*newbill.getValue());
			}
		}
		
		
	}

	/*******************************************************************************
	 @function		membersToString
	 @abstract		Méthode pour récupérer un tableau de string contenant le nom de 
	 				tous les membres du groupe.
	 @return		ArrayList<String> out tableau de string contenant le nom de tous
	 				les membres du groupe.
	*******************************************************************************/
	public ArrayList<String> membersToString() {

		ArrayList<String> out = new ArrayList<String>();	//variable de sortie
		int i;												// compteur de boucle
		
		for(i=0; i<nb_members; i++){
			out.add(list_members.get(i).getNameM());		// on met chaque membre dans l'ArrayList
		}
		
		return out;
	}

}
