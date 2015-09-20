/*******************************************************************************

 @file			DataDbHelper.java
 @abstract		Définition de la classe DataDbHelper pour gérer la base de
 				données
 @author		SANTOS Arthur
 @author		COLLIOT Kévin
 @version		1.0

*******************************************************************************/

package com.example.partacount;

//IMPORTS
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataDbHelper extends SQLiteOpenHelper {

	//ATTRIBUTS
    // If you change the database schema, you must increment the database version.
	private static final String DATABASE_NAME = "Partacount.db";	// Nom de la base de donnée
	private static final String TABLE_GROUPS = "GROUPS";			// Tableau des groupes
	private static final String TABLE_MEMBERS = "MEMBERS";			// Tableau des membres
	private static final String TABLE_BILLS = "BILLS";				// Tableau des notes
	private static final String COL_GP_ID = "ID";					// Colonne des ids de groupe
	private static final String COL_MB_ID = "ID";					// Colonne des ids de membre
	private static final String COL_BL_ID = "ID";					// Colonne des ids de note
	private static final String COL_GP_NAME = "NAME";				// Colonne des noms de groupe
	private static final String COL_MB_NAME = "NAME";				// Colonne des noms de membres
	private static final String COL_GP_NB_MEMBER = "NB_MEMBER";		// Colonne des nombre de membre par groupe
	private static final String COL_BL_NB_MEMBER = "NB_MEMBER";		// Colonne des nombre de membre par note
	private static final String COL_BALANCE = "BALANCE";			// Colonne de la balance du groupe
	private static final String COL_MB_GROUP_ID = "GROUP_ID";		// Colonne des ids du groupe auquel le membre appartient
	private static final String COL_BL_GROUP_ID = "GROUP_ID";		// Colonne des ids du groupe auquel la note appartient
	private static final String COL_DEBT = "DEBT";					// Colonne des dettes des membres
	private static final String COL_GGL_ADDRESS = "GGL_ADDRESS";	// Colonne des adresses google des membres
	private static final String COL_WHO_PAYED_ID = "WHO_PAYED_ID";	// Colonne des membres ayant payer les notes
	private static final String COL_NAME_WHAT = "NAME_WHAT";		// Colonne des noms de note
	private static final String COL_BL_VALUE = "VALUE";				// Colonne des valeurs des notes
	private static final String COL_BL_WHO_IN = "WHO_IN";			// Colonne des membres concernés par la note
	private static final String COL_MB_BILLS_COUNT = "BILLS_COUNT";	// Colonne du nombre de note pour lesquelles un membre est impliqué
	
	// Definition du tableau de groupes
	private static final String  CREATE_GROUPS_TABLE = "CREATE TABLE " + TABLE_GROUPS + " ("
			+ COL_GP_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_GP_NAME+" TEXT NOT NULL, "
			+ COL_GP_NB_MEMBER+" NUMERIC, " + COL_BALANCE+" NUMERIC );";
	// Definition du tableau de membres
	private static final String  CREATE_GROUPS_MEMBERS ="CREATE TABLE " + TABLE_MEMBERS + " ("
			+ COL_MB_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_MB_NAME+" TEXT NOT NULL, "
			+ COL_DEBT+" NUMERIC, " + COL_GGL_ADDRESS+" TEXT, " + COL_MB_BILLS_COUNT+" NUMERIC, "
			+ COL_MB_GROUP_ID+" INTEGER, "
			+" FOREIGN KEY("+COL_MB_GROUP_ID+") REFERENCES " +TABLE_GROUPS+"("+ COL_GP_ID+") ON DELETE CASCADE"+ ");" ;
	// Definition du tableau de notes
	private static final String  CREATE_GROUPS_BILLS = "CREATE TABLE "+ TABLE_BILLS + " ("
			+ COL_BL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_NAME_WHAT+" TEXT NOT NULL, " 
			+ COL_BL_VALUE+" NUMERIC, " + COL_BL_GROUP_ID+" INTEGER, " 
			+ COL_BL_NB_MEMBER+" TEXT NOT NULL, " + COL_WHO_PAYED_ID+" INTEGER, " 
			+ COL_BL_WHO_IN+" TEXT NOT NULL, "
			+ " FOREIGN KEY("+COL_BL_GROUP_ID+") REFERENCES "+TABLE_GROUPS+"("+ COL_GP_ID+") ON DELETE CASCADE," 
			+" FOREIGN KEY("+COL_WHO_PAYED_ID+") REFERENCES "+TABLE_MEMBERS+"("+ COL_MB_ID+") ON DELETE CASCADE" +" );";
	// Definition de la string parametre pour effacer les tableaux
	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_GROUPS
			+"DROP TABLE IF EXISTS " + TABLE_MEMBERS +"DROP TABLE IF EXISTS " + TABLE_MEMBERS;

	// CONSTRUCTEUR
    public DataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }
	/*******************************************************************************
	 @function		onCreate
	 @abstract		Methode pour créer les tableau dans la base de données
	 @param			SQLiteDatabase db la BDD dans laquelle on va créer les tableaux
	*******************************************************************************/
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_GROUPS_TABLE);
        db.execSQL(CREATE_GROUPS_MEMBERS);
        db.execSQL(CREATE_GROUPS_BILLS);
    }
    
	/*******************************************************************************
	 @function		onUpgrade
	 @abstract		Methode pour refaire la base de données
	 @param			SQLiteDatabase db
	 @param			int oldVersion
	 @param			int newVersion
	*******************************************************************************/
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    /*******************************************************************************
	 @function		insertGroup
	 @abstract		Methode pour ajouter un groupe à la BDD
	 @param			Group group, groupe à ajouter
	*******************************************************************************/
	public void insertGroup(Group group){
		
		int i;
		// création d'un ContentValues (fonctionne comme une HashMap)
		ContentValues values = new ContentValues();
		Member member;
		Bill bill;
		
		SQLiteDatabase bdd = this.getWritableDatabase();
		// on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
		values.put(DataDbHelper.COL_GP_NAME, group.getNameGp());
		values.put(DataDbHelper.COL_GP_NB_MEMBER, group.getNb_members());
		values.put(DataDbHelper.COL_BALANCE, group.getBalance());
		// on insère l'objet dans la BDD via le ContentValues
		bdd.insert(DataDbHelper.TABLE_GROUPS, null, values);
		bdd.close();
		// on lui donne un id
		group.setId(this.getLastGroupId());
	    
		// on ajoute tous ses membres à la BDD
		for(i=0; i<group.getNb_members(); i++) {
			member = group.getList_members().get(i);
			this.insertMember(member, group.getId());
		}
		
		// on ajoute toutes ses notes à la BDD
		for(i=0; i<group.getList_bills().size(); i++) {
			bill = group.getList_bills().get(i);
			this.insertBill(bill, group.getId());
		}
	}
	/*******************************************************************************
	 @function		insertMember
	 @abstract		Methode pour ajouter un membre à la BDD
	 @param			Member member, membre à ajouter
	 @param			int groupId, id du groupe auquel on ajoute le membre
	*******************************************************************************/
	public void insertMember(Member member, int groupId){
		// donne l'authorisation d'écrire dans la BDD
		SQLiteDatabase bdd = this.getWritableDatabase();
		
		// création d'un ContentValues (fonctionne comme une HashMap)
		ContentValues values = new ContentValues();
		
		// remplissage des colonnes
		values.put(DataDbHelper.COL_MB_NAME, member.getNameM());
		values.put(DataDbHelper.COL_DEBT, member.getDebt());
		values.put(DataDbHelper.COL_GGL_ADDRESS, member.getGgl_address());
		values.put(DataDbHelper.COL_MB_GROUP_ID, groupId);
		values.put(DataDbHelper.COL_MB_BILLS_COUNT, member.getBills_count());
		
		// insertion du membre dans le tableau
		bdd.insert(DataDbHelper.TABLE_MEMBERS, null, values);
		
		// retrait du droit d'écriture		
		bdd.close();
	}
	/*******************************************************************************
	 @function		insertBill
	 @abstract		Methode pour ajouter une note à la BDD
	 @param			Bill bill, note à ajouter
	 @param			int groupId, id du groupe auquel on ajoute le membre
	*******************************************************************************/
	public void insertBill(Bill bill, int groupId){
		// donne l'authorisation d'écrire dans la BDD
		SQLiteDatabase bdd = this.getWritableDatabase();
		
		// création d'un ContentValues (fonctionne comme une HashMap)
		ContentValues values = new ContentValues();
		
		// remplissage des colonnes
		values.put(DataDbHelper.COL_NAME_WHAT, bill.getWhat());
		values.put(DataDbHelper.COL_BL_NB_MEMBER, bill.getNb_members());
		values.put(DataDbHelper.COL_BL_VALUE, bill.getValue());
		values.put(DataDbHelper.COL_WHO_PAYED_ID, bill.getWho_paied().getId());
		values.put(DataDbHelper.COL_BL_GROUP_ID, groupId);
		values.put(DataDbHelper.COL_BL_WHO_IN, bill.whos_inTostring());
		
		// insertion de la note dans le tableau
		bdd.insert(DataDbHelper.TABLE_BILLS, null, values);
		
		// retrait du droit d'écriture
		bdd.close();
	}
	/*******************************************************************************
	 @function		getGroup
	 @abstract		Methode pour récupérer un groupe dans la BDD
	 @param			int groupId, id du groupe que l'on veut récupérer
	 @return		Group
	*******************************************************************************/
	public Group getGroup(int id){
		
		// donne l'authorisation de lire dans la BDD
		SQLiteDatabase bdd = this.getReadableDatabase();
		// definition du curseur pour se déplacer dans le tableau
		Cursor cursor = bdd.query(TABLE_GROUPS, new String[] { DataDbHelper.COL_GP_ID,
	            DataDbHelper.COL_GP_NAME, DataDbHelper.COL_GP_NB_MEMBER,DataDbHelper.COL_BALANCE}, DataDbHelper.COL_GP_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);
	    
		if (cursor != null)
	        cursor.moveToFirst();
	    
		// création d'un groupe que l'on rempli avec les données que l'on lit dans la bdd
	    Group group = new Group(cursor.getString(1));
	    group.setId(Integer.parseInt(cursor.getString(0)));
	    group.setBalance(Float.parseFloat(cursor.getString(3)));
	    // s'il y a des notes, on les ajoute au groupe
	    if(Float.parseFloat(cursor.getString(3)) != 0) {
	    	group.setList_bills(getListBills(Integer.parseInt(cursor.getString(0))));
	    }
	    group.setList_members(getListMembers(Integer.parseInt(cursor.getString(0))));
	    // retrait du droit de lecture
	    bdd.close();
	    // renvoie le groupe récupéré
		return group;
	}
	/*******************************************************************************
	 @function		getMember
	 @abstract		Methode pour récupérer un membre dans la BDD
	 @param			int id, id du membre que l'on veut récupérer
	 @return		Member
	*******************************************************************************/
	private Member getMember(int id){
		
		// donne l'authorisation de lire dans la BDD
		SQLiteDatabase bdd = this.getReadableDatabase();
		// definition du curseur pour se déplacer dans le tableau	
		Cursor cursor = bdd.query(DataDbHelper.TABLE_MEMBERS, new String[] { DataDbHelper.COL_MB_ID,
	            DataDbHelper.COL_MB_NAME, DataDbHelper.COL_DEBT,DataDbHelper.COL_GGL_ADDRESS,
	            DataDbHelper.COL_MB_GROUP_ID,DataDbHelper.COL_MB_BILLS_COUNT}, DataDbHelper.COL_MB_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);
	    if (cursor != null)
	        cursor.moveToFirst();
	    
	    // création d'un membre que l'on rempli avec les données que l'on lit dans la bdd
	    Member member = new Member(cursor.getString(1),Integer.parseInt(cursor.getString(0)));
	    member.setDebt(Float.parseFloat(cursor.getString(2)));
	    member.setGgl_address(cursor.getString(3));
	    member.setBills_count(Integer.parseInt(cursor.getString(5)));
	    
	    // retrait du droit de lecture
	    bdd.close();
	    // renvoie le membre récupéré
		return member;
	}

	/*******************************************************************************
	 @function		getListMembers
	 @abstract		Methode pour récupérer la liste des membres d'un groupe
	 @param			int groupId, id du groupe auquel appartient la liste
	 @return		ArrayList<Member>
	*******************************************************************************/
	public ArrayList<Member> getListMembers(int groupId) {
		
		// création d'une liste de membres que l'on rempli avec les données que l'on lit dans la bdd
	    ArrayList<Member> memberList = new ArrayList<Member>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_MEMBERS;
	    // donne l'authorisation de lire dans la BDD
	    SQLiteDatabase bdd = this.getReadableDatabase();
	    
	    // definition du curseur pour se déplacer dans le tableau
	    Cursor cursor = bdd.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	if(Integer.parseInt(cursor.getString(5)) == groupId) {
		        	Member member = new Member(cursor.getString(1),Integer.parseInt(cursor.getString(0)));
		    	    member.setDebt(Float.parseFloat(cursor.getString(2)));
		    	    member.setGgl_address(cursor.getString(3));
		    	    member.setBills_count(Integer.parseInt(cursor.getString(4)));
		            // Adding contact to list
		    	    
		            memberList.add(member);
	            }
	        } while (cursor.moveToNext());
	    }
	 
	    // retrait du droit de lecture
	    bdd.close();
	    // renvoie la liste récupérée
	    return memberList;
	}
	/*******************************************************************************
	 @function		getListBills
	 @abstract		Methode pour récupérer la liste des notes d'un groupe
	 @param			int groupId, id du groupe auquel appartient la liste
	 @return		ArrayList<Bill>
	*******************************************************************************/
	public ArrayList<Bill> getListBills(int groupId) {
		
	    int i;	// variable de boucle 
		// création d'une liste de notes que l'on rempli avec les données que l'on lit dans la bdd
	    ArrayList<Bill> billList = new ArrayList<Bill>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_BILLS;

	    // donne l'authorisation de lire dans la BDD
	    SQLiteDatabase bdd = this.getReadableDatabase();
	    // definition du curseur pour se déplacer dans le tableau
	    Cursor cursor = bdd.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	if(Integer.parseInt(cursor.getString(3)) == groupId) {
	        		
	        		String who_inString = cursor.getString(6);
	        	    boolean[] who_in = new boolean[Integer.parseInt(cursor.getString(4))];
	        	    for(i=0; i<who_in.length; i++) {
	        	    	if(who_inString.charAt(i) == '0'){
	        	    		who_in[i] = false;
	        	    	} else if(who_inString.charAt(i) == '1'){
	        	    		who_in[i] = true;
	        	    	}
	        	    }
	        		
	        		Bill bill = new Bill(Integer.parseInt(cursor.getString(0)),Float.parseFloat(cursor.getString(2)),
	        	    		Integer.parseInt(cursor.getString(4)),this.getMember(Integer.parseInt(cursor.getString(5))),
	        	    		who_in, cursor.getString(1));
		            // Adding contact to list
		            billList.add(bill);
	            }
	        } while (cursor.moveToNext());
	    }
	 
	    // retrait du droit de lecture
	    bdd.close();
	    // renvoie la liste récupérée
	    return billList;
	}
	/*******************************************************************************
	 @function		getListGroups
	 @abstract		Methode pour récupérer la liste des groupes
	 @return		ArrayList<Group>
	*******************************************************************************/
	public ArrayList<Group> getListGroups() {
		
		// création d'une liste de groupes que l'on rempli avec les données que l'on lit dans la bdd
	    ArrayList<Group> groupList = new ArrayList<Group>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_GROUPS;
	    
	    // donne l'authorisation de lire dans la BDD
	    SQLiteDatabase bdd = this.getReadableDatabase();
	    // definition du curseur pour se déplacer dans le tableau
	    Cursor cursor = bdd.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	    	    Group group = new Group(cursor.getString(1));
	    	    group.setId(Integer.parseInt(cursor.getString(0)));
	    	    group.setBalance(Float.parseFloat(cursor.getString(3)));
	    	    if(Float.parseFloat(cursor.getString(3)) != 0) {
	    	    	group.setList_bills(getListBills(Integer.parseInt(cursor.getString(0))));
	    	    }
	    	    group.setList_members(getListMembers(Integer.parseInt(cursor.getString(0))));
	    	    if(!groupList.contains(group)) {
	    	    	groupList.add(group);
	    	    }
	    	    
	        } while (cursor.moveToNext());
	    }
	 
	    // retrait du droit de lecture
	    bdd.close();
	    // renvoie la liste récupérée
	    return groupList;
	}
	/*******************************************************************************
	 @function		updateGroup
	 @abstract		Methode pour mettre à jour un groupe dans la base de données
	 @param			Group group, groupe à mettre à jour
	*******************************************************************************/
	public void updateGroup(Group group) {
		
	    int i;	// variable de boucle
	    
	    // récupération de l'ancien groupe pour le modifier et le mettre à jour
	    Group oldGroup = this.getGroup(group.getId());
	    
	    // donne l'authorisation d'écrire dans la BDD
	    SQLiteDatabase db = this.getWritableDatabase();
	    // Remplissement de la variable de classe ContentValues necessair pour stocker un group dans la base de donnees
	    ContentValues values = new ContentValues();
	    values.put(DataDbHelper.COL_GP_NAME, group.getNameGp());
		values.put(DataDbHelper.COL_GP_NB_MEMBER, group.getNb_members());
		values.put(DataDbHelper.COL_BALANCE, group.getBalance());
	    // updating row
	    db.update(TABLE_GROUPS, values, COL_GP_ID + " = ?",new String[] { String.valueOf(group.getId()) });
	    
	    for(i=0; i<group.getNb_members(); i++){
	    	if(i < oldGroup.getNb_members()) {
	    		updateMember(group.getList_members().get(i), group.getId());
	    	} else {
	    		this.insertMember(group.getList_members().get(i), group.getId());
	    	}
	    }
	    // retrait du droit d'écriture
	    db.close();
	}
	/*******************************************************************************
	 @function		updateMember
	 @abstract		Methode pour mettre à jour un membre dans la base de données
	 @param			Member member, Membre à mettre à jour
	*******************************************************************************/
	public void updateMember(Member member, int groupId) {
		
		// donne l'authorisation d'écrire dans la BDD
	    SQLiteDatabase db = this.getWritableDatabase();
	    // Remplissement de la variable de classe ContentValues necessair pour stocker un group dans la base de donnees
	    ContentValues values = new ContentValues();
		values.put(DataDbHelper.COL_MB_NAME, member.getNameM());
		values.put(DataDbHelper.COL_DEBT, member.getDebt());
		values.put(DataDbHelper.COL_GGL_ADDRESS, member.getGgl_address());
		values.put(DataDbHelper.COL_MB_GROUP_ID, groupId);
		values.put(DataDbHelper.COL_MB_BILLS_COUNT, member.getBills_count());
		
	    // updating row
	    db.update(TABLE_MEMBERS, values, COL_MB_ID + " = ?",new String[] { String.valueOf(member.getId()) });
	    
	    // retrait du droit d'écriture
	    db.close();
	}
	/*******************************************************************************
	 @function		deleteGroup
	 @abstract		Methode pour supprimer un groupe de la base de données
	 @param			Group group, groupe à supprimer
	*******************************************************************************/
	public void deleteGroup(Group group) {
		
//	    int i;
		// donne l'authorisation d'écrire dans la BDD
		SQLiteDatabase db = this.getWritableDatabase();
		// suppression
	    db.delete(DataDbHelper.TABLE_GROUPS, DataDbHelper.COL_GP_ID + " LIKE ?",
	            new String[] { String.valueOf(group.getId()) });
	    // retrait du droit d'écriture
	    db.close();
	}
	/*******************************************************************************
	 @function		deleteMember
	 @abstract		Methode pour supprimer un membre de la base de données
	 @param			Member member, membre à supprimer
	*******************************************************************************/
	public void deleteMember(Member member) {
		// donne l'authorisation d'écrire dans la BDD
		SQLiteDatabase db = this.getWritableDatabase();
		// suppression
	    db.delete(TABLE_MEMBERS, COL_MB_ID+ " LIKE ?", new String[] { String.valueOf(member.getId()) });
	    // retrait du droit d'écriture
	    db.close();
	}
	/*******************************************************************************
	 @function		deleteBill
	 @abstract		Methode pour supprimer une note de la base de données
	 @param			Bill bill member, membre à supprimer
	*******************************************************************************/
	public void deleteBill(Bill bill) {
		// donne l'authorisation d'écrire dans la BDD
		SQLiteDatabase db = this.getWritableDatabase();
		// suppression
		db.delete(TABLE_BILLS, COL_MB_ID+ " LIKE ?", new String[] { String.valueOf(bill.getId()) });
		// retrait du droit d'écriture
	    db.close();
	}
	/*******************************************************************************
	 @function		getLastGroupId
	 @abstract		Methode pour récupérer l'ID du dernier groupe
	 @return		l'id du dernier groupe ou 0 si liste vide
	*******************************************************************************/
	public int getLastGroupId(){
		// definition de la string parametre le curseur on choisant la table qu'il doit parcourrir
	    String selectQuery = "SELECT  * FROM " + TABLE_GROUPS;
	    // donne l'authorisation de lire dans la BDD
	    SQLiteDatabase bdd = this.getReadableDatabase();
	    // definition du curseur pour se déplacer dans le tableau
	    Cursor cursor = bdd.rawQuery(selectQuery, null);
	    
	    if(cursor.moveToLast()) {
	    	return cursor.getInt(0);
	    } else return 0;
	}
}