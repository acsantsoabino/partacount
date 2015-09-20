/*******************************************************************************

 @file			DataDB.java
 @abstract		Définition de la classe DataDB pour définir notre BDD
 @author		SANTOS Arthur
 @author		COLLIOT Kévin
 @version		1.0

*******************************************************************************/

package com.example.partacount;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataDB {

	// ATTRIBUTS
	private SQLiteDatabase bdd;			// la base de donnée que l'on va utiliser
	private DataDbHelper mDataDbHelper;	// manipulatuer de la base de donnée
	
	// CONSTRUCTEUR
	public DataDB(Context context){
		mDataDbHelper = new DataDbHelper(context);
	}
	
	// GETTERS ET SETTERS
	public SQLiteDatabase getBDD(){
		return bdd;
	}
	
	// METHODES
	/*******************************************************************************
	 @function		openWrite
	 @abstract		Méthode pour pouvoir ecrire dans la base de donnée
	*******************************************************************************/
	public void openWrite(){
		bdd = mDataDbHelper.getWritableDatabase();
	}
	/*******************************************************************************
	 @function		openRead
	 @abstract		Méthode pour pouvoir lire dans la base de donnée
	*******************************************************************************/
	public void openRead(){
		bdd = mDataDbHelper.getReadableDatabase();
	}
	/*******************************************************************************
	 @function		close
	 @abstract		Méthode pour fermer la base de donnée
	*******************************************************************************/
	public void close(){
		bdd.close();
	}
}
